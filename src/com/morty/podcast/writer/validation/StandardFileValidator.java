// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.

package com.morty.podcast.writer.validation;

import java.io.File;
import java.io.FilenameFilter;

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
    public boolean process() throws Exception
    {
        boolean result = true;
        try
        {
            /*
             * 1) Are there modules?
             * 2) Is there a specific info.txt file for the podcast itself
             * 3) The modules - do they have a info.txt file?
             * 4) Do any of the filenames have spaces?
             */

            if(!m_folderToProcess.exists())
                throw new Exception("Podcast Directory does not exist");

            //Are there folders?
            if(m_folderToProcess.listFiles().length == 0)
                throw new Exception("No folders in the podcast directory");

            //Are there valid folders?
            File[] modules = m_folderToProcess.listFiles(new FilenameFilter() {

            public boolean accept(File file, String fileToCheck)
            {
                m_logger.debug("Checking module ["+fileToCheck+"]");
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
                throw new Exception("No valid folders in the podcast directory");

            //Is there an info.txt?



            //Does each module have an info.txt?


            //Check each filename - are there spaces?

            //Any files with no suffix?


        }
        catch(Exception e)
        {
            result = false;
        }
        finally
        {
            return result;
        }
        
    }



    public static void main(String[] args)
    {
        //Process a directory without exclusion...
        m_logger.info("Starting Validator");

        if(args.length != 1)
            throw new NullPointerException("No directory specified");

        String fileToProcess = args[0];
        m_logger.info("Processing ["+fileToProcess+"]");
        StandardFileValidator sfv = new StandardFileValidator();
        sfv.setFolderToProcess(new File(fileToProcess));
        boolean result=false;
        try
        {
            result = sfv.process();
        }
        catch (Exception ex)
        {
            m_logger.error("Error thrown",ex);
        }

        m_logger.info("Was the directory valid? ["+result+"]");
        m_logger.info("Finished Validator");
    }


}
