// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.

package com.morty.podcast.writer.validation;

import com.morty.podcast.writer.file.PodCastFileNameResolver;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Default validator... see the Abstract Validator
 * This is a package for this application.
 * Future implementation might make use of a customisable validator/processor
 * Keep an eye on repository listings....
 * @author amorton
 */
public class StandardFileValidator extends AbstractFolderValidator
{

    @Override
    public void process() throws Exception
    {
        
        List problems = new ArrayList();
        try
        {
            /*
             * 1) Are there modules?
             * 2) Is there a specific info.txt file for the podcast itself
             * 3) The modules - do they have a info.txt file?
             * 4) Do any of the filenames have spaces?
             */

            if(!m_folderToProcess.exists())
                problems.add("Podcast Directory does not exist");

            //Are there folders?
            if(m_folderToProcess.listFiles().length == 0)
                problems.add("No folders in the podcast directory");

            //Are there valid folders?
            File[] modules = m_folderToProcess.listFiles(new FilenameFilter() {

            public boolean accept(File file, String fileToCheck)
            {
                m_logger.debug("Checking module ["+fileToCheck+"]");

                File dirCheck = new File(file+File.separator+fileToCheck);
                if(fileToCheck.toLowerCase().equals("info.txt"))
                      return false;
                if(!dirCheck.isDirectory())
                    return false;

                //if not in list, then accept. If in list, then reject the folder
                if(m_excludedFolders == null || m_excludedFolders.isEmpty())
                    return true;
                else
                    if(m_excludedFolders.contains(fileToCheck))
                    {
                        m_logger.debug("Skipping excluded folder ["+fileToCheck+"]");
                        return false;
                    }
                    else return true;
            }
            } );

            if(modules.length ==0)
                problems.add("No valid folders in the podcast directory");

            //Is there an info.txt?
            File podcastInfo = new File(m_folderToProcess+File.separator+"info.txt");
            if(!podcastInfo.exists())
                problems.add("No podcast info.txt file.");

            //Does each module have an info.txt?
            for(int i=0; i<modules.length; i++)
            {
                File moduleInfo = new File(modules[i].getAbsolutePath()+File.separator+"info.txt");
                if(!moduleInfo.exists())
                    problems.add("No info file for module ["+modules[i]+"]");

            }

            //Filename resolver.
            PodCastFileNameResolver fnf = new PodCastFileNameResolver();

            //Check each filename - are there spaces?
            for(int i=0; i<modules.length; i++)
            {
                File moduleDir = new File(modules[i].getAbsolutePath());
                File[] files = moduleDir.listFiles();

                //Go through each file...
                for(int j=0; j< files.length; j++)
                {
                    //Get the file
                    File testFile = files[j];
                    if(testFile.getName().toLowerCase().equals("info.txt"))
                        m_logger.info("Ignoring text file in validation");
                    else if(!fnf.fileIsResolvable(testFile.getName()) )
                        problems.add("Unable to resolve file ["+testFile.getName()+"] in directory ["+moduleDir.getAbsolutePath()+"]");

                }


            }


            if(problems.isEmpty())
                m_logger.info("Podcast Directory seems to be valid");
            else
            {
                m_logger.error("\n\n");
                m_logger.error("==================================================================");
                m_logger.error("Podcast Directory is invalid for the following reasons");
                Iterator it = problems.iterator();
                while(it.hasNext())
                {
                    String problem = (String) it.next();
                    m_logger.error(problem);
                
                }
                m_logger.error("==================================================================");
                m_logger.error("\n\n");

                if(m_failOnError)
                       throw new Exception("Podcast Directory Invalid");
            }



        }
        catch(Exception e)
        {
            if(m_logger.isDebugEnabled())
                m_logger.debug("Unable to validate ["+e.getMessage()+"]",e);
            throw e;
        }
        
        
    }



    public static void main(String[] args)
    {
        //Process a directory without exclusion...
        Log m_logger = LogFactory.getLog(StandardFileValidator.class);
        m_logger.info("Starting Validator");

        if(args.length != 1)
            throw new NullPointerException("No directory specified");

        String fileToProcess = args[0];
        m_logger.info("Processing ["+fileToProcess+"]");
        StandardFileValidator sfv = new StandardFileValidator();
        sfv.setFolderToProcess(new File(fileToProcess));
        try
        {
            sfv.process();
            m_logger.info("Directory Valid");
        }
        catch (Exception ex)
        {
            m_logger.info("Directory InValid");
            m_logger.error("Error thrown",ex);
        }
        
        m_logger.info("Finished Validator");
    }


}
