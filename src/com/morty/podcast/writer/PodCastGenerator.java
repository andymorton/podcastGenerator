// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.

package com.morty.podcast.writer;

import com.sun.syndication.feed.module.itunes.EntryInformation;
import com.sun.syndication.feed.module.itunes.EntryInformationImpl;
import com.sun.syndication.feed.module.itunes.types.Duration;
import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndCategoryImpl;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEnclosureImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.feed.synd.SyndImage;
import com.sun.syndication.feed.synd.SyndImageImpl;
import com.sun.syndication.io.SyndFeedOutput;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This will traverse a directory and create a podcast based on it.
 * This can then be used to create the feed.
 * The structure should be as follows:
 * -Main Directory (suggest podcast201011), which then changes every year.
 *  -ModuleName (no spaces should be in this!)
 *      -MP3    filename should be YYYYMMDD.<UnitNumber>.<Description>.mp3
 *      -PDF    For notes
 *      -MPG    For video
 * 
 *      <UnitNumber> is available as %1 in the template
 *      <Description> is available as %2 in the template ( '_' will be replaced by space)
 *
 *      -Optional Info file (info.txt)
 *
 * This will create a podcast with categories of module name
 * Any file that starts '.' will be ignored
 *
 * In simple mode, it expects files with 'Unit1.mp3' filename format.
 * The filename without the suffix will be treated as the description
 *
 *
 * Modified 15/02/2011 to use the filename date of YYYYMMDD
 * Modified 25/07/2011 for major refactor and handle UTF8 filenames.
 * Modified September 2011 for new features.
 *
 * Copyright 2011
 * @author amorton
 */
public class PodCastGenerator
{
    
    private static final Log m_logger = LogFactory.getLog(PodCastGenerator.class);
    private static final ResourceBundle m_supportedTypes = ResourceBundle.getBundle("com.morty.podcast.writer.SupportFileTypes");


    private boolean m_simpleMode = false;
    private String m_fileToCreate = null;
    private String m_directoryToTraverse = null;
    private String m_urlSuffix = "";
    private String m_httpRoot = null;

    /**
     * Set the directory that we are looking at
     * @param dir
     */
    public void setDirectory(String dir)
    {
        this.m_directoryToTraverse = dir;
    }

    /**
     * Set the file that we are to create (absolute path!)
     * @param file
     */
    public void setFileToCreate(String file)
    {
        this.m_fileToCreate = file;
    }

    /**
     * Set the root URL (including http://)
     * @param httproot
     */
    public void setHttpRoot(String httproot)
    {
        this.m_httpRoot = httproot;
    }

    /**
     * Boolean to run in simple mode
     * @param simpleMode
     */
    public void setSimpleMode(boolean simpleMode)
    {
        this.m_simpleMode = simpleMode;
    }

    /**
     * Add in an optional url suffix
     * @param urlSuffix
     */
    public void setUrlSuffix(String urlSuffix)
    {
        this.m_urlSuffix = urlSuffix;
    }
    
    
    
    
    
    /**
     * The main method to be generate a podcast
     */
    public void generatePodCast()
    {
        try
        {
            m_logger.info("Starting");
            //verify the parameters
            checkValues();
            //Process the files
            List podcastFiles = processMainDirectory();
            generateCompleteFeed(podcastFiles);
            
            m_logger.info("Finished");
        } 
        catch (PodCastCreationException pce)
        {
            m_logger.error("Custom error",pce);
        }
        catch (Exception ex)
        {
            m_logger.error("Unable to Generate Podcast",ex);
        }
        
    }
    
    
    
    //Check all the variables to be valid.
    private void checkValues() throws PodCastCreationException
    {
        m_logger.info("Checking variables");
        
        if(m_fileToCreate == null ||
                m_directoryToTraverse == null ||
                m_urlSuffix == null ||
                m_httpRoot == null)
            throw new PodCastCreationException("Variables not specified");
        
        //Check the directory exists & is a directory!
        File dirCheck = new File(m_directoryToTraverse);
        if(!dirCheck.exists() || !dirCheck.isDirectory())
            throw new PodCastCreationException("Invalid directory specified");
        
        
        m_logger.info("Checked variables");
        
    }

    private List processMainDirectory() throws Exception
    {
        m_logger.info("Processing directories");
        
        List returnList = new ArrayList();
        
        //Process the directory
        File directory = new File(m_directoryToTraverse);
       
        //We get a list of modules from the directory names.
        File[] modules = directory.listFiles();
        for (int i=0; i< modules.length; i++)
        {
            if(modules[i].isDirectory())
            {
                String category = modules[i].getName();

                File categoryDirectory = new File(modules[i].getAbsolutePath());
                File[] originalFiles = categoryDirectory.listFiles();
                PodCastFile[] podcastFiles = new PodCastFile[originalFiles.length];

                //Convert all the files to PodCastFiles in the array.
                for(int counter=0;counter<podcastFiles.length;counter++)
                {
                    //Create a podcast file with a construct of file path (preconverted to UTF8).
                    //That way all files should exist, even if they have weird characters and every getName will
                    //also contain the right characters!
                    podcastFiles[counter] = new PodCastFile(PodCastUtils.convertToUTF8(originalFiles[counter].getAbsolutePath()));
                }
                
                //Overriding values for this module.
                HashMap customValues = processInfoFile(categoryDirectory);
                
                for (int p=0; p<podcastFiles.length;p++)
                { 
                    if(podcastFiles[p].getName().equalsIgnoreCase(PodCastConstants.INFO_FILE) || podcastFiles[p].getName().startsWith("."))
                        m_logger.warn("Ignoring hidden or info file ["+podcastFiles[p].getName()+"]");
                    else
                        returnList.add(processFile(podcastFiles[p],category, customValues));
                }

            }
            else m_logger.info("Skipping Loose File ["+modules[i]+"]");
        }
        
        return returnList;
           
    }
    
    

    private HashMap processInfoFile(File categoryDirectory)
    {
        HashMap returnValues = new HashMap();
        //Check for the info file. (overrides the author and description!)
        File infoFile = new File(categoryDirectory+File.separator+PodCastConstants.INFO_FILE);
        if(infoFile.exists())
        {
            returnValues = PodCastUtils.parseInfoFile(new File(categoryDirectory+File.separator+PodCastConstants.INFO_FILE));
            m_logger.info("Found info file for ["+categoryDirectory+"]");
        }
        else m_logger.warn("No info file at level ["+categoryDirectory+File.separator+PodCastConstants.INFO_FILE+"]");

        return returnValues;

    }


    private SyndEntry processFile(PodCastFile podCastFile,String category, Map customValues) throws Exception
    {
        m_logger.info("Processing file ["+podCastFile.getName()+"]");
        Date fileDate = null;
        String desc = null;
        
        if(m_simpleMode)
        {
            //Use the system date.
            fileDate= new Date(System.currentTimeMillis());

            String filename = podCastFile.getName();
            String[] parts = filename.split("\\.");

            //We have just got the filename without the extension
            desc = parts[0];
        }
        else
        {
            fileDate = getfileDate(podCastFile);
            desc = getDescription(podCastFile,customValues);

        }
        //Add the entry of the mp3 file.
        return generateEntry(  
                        m_httpRoot +"/"+category+"/"+ podCastFile.getName(),
                        fileDate,
                        desc,
                        category,
                        PodCastUtils.getMapValue(customValues, 
                                                 PodCastConstants.AUTHOR_KEY, 
                                                 PodCastConstants.DEFAULT_AUTHOR), 
                        podCastFile,
                        m_urlSuffix);

    }

    private void generateCompleteFeed(List podcastFiles) throws Exception
    {

        try
        {
        
            //Get the default info file from the podcast directory, and see if there are any overrides
            File defaultInfo =  new File(m_directoryToTraverse + File.separator+ PodCastConstants.INFO_FILE);
            HashMap defaultFeedValues = new HashMap();
            if(defaultInfo.exists())
                   defaultFeedValues = PodCastUtils.parseInfoFile(defaultInfo);


            final SyndFeed feed = new SyndFeedImpl();
            feed.setFeedType(PodCastConstants.FEED_TYPE);

            feed.setTitle(PodCastUtils.getMapValue(defaultFeedValues,PodCastConstants.FEED_TITLE_KEY, PodCastConstants.DEFAULT_FEED_TITLE));
            feed.setLink(PodCastUtils.generateHttpLink(m_httpRoot,m_fileToCreate,m_urlSuffix));
            feed.setDescription(PodCastUtils.getMapValue(defaultFeedValues,PodCastConstants.FEED_DESCRIPTION_KEY, PodCastConstants.DEFAULT_FEED_DESCRIPTION));
            feed.setCopyright(PodCastUtils.getMapValue(defaultFeedValues,PodCastConstants.FEED_COPYRIGHT_KEY, PodCastConstants.DEFAULT_FEED_COPYRIGHT));
            
            //Set the image on the Feed.
            SyndImage img = new SyndImageImpl();
            img.setDescription(feed.getDescription());
            img.setLink(feed.getLink());
            img.setTitle(PodCastUtils.getMapValue(defaultFeedValues,PodCastConstants.FEED_IMAGE_TITLE, PodCastConstants.DEFAULT_IMAGE_TITLE));
            img.setUrl(PodCastUtils.generateHttpLink(m_httpRoot,PodCastUtils.getMapValue(defaultFeedValues,PodCastConstants.FEED_IMAGE_NAME, PodCastConstants.DEFAULT_IMAGE_NAME), m_urlSuffix));
            
            feed.setImage(img);
            
            feed.setEntries(podcastFiles);
            

            final Writer fileWriter = new FileWriter(m_fileToCreate);
            final SyndFeedOutput feedOutput = new SyndFeedOutput();
            feedOutput.output(feed, fileWriter);
            fileWriter.flush();
            fileWriter.close();

            m_logger.info("Podcast created [" + m_fileToCreate + "]");

        } 
        catch (Exception ex)
        {
            m_logger.error("ERROR: " + ex.getMessage());
            throw ex;
        }


    }

    
    //Get the filedate from the filename, or otherwise...
    private Date getfileDate(PodCastFile podCastFile)
    {
        //Look at the first 8 characters. If valid date, then use it, otherwise, the last
        //created date
        String potentialDate = podCastFile.getName().substring(0,8);
        m_logger.info("Parsing filename date of  ["+potentialDate+"]");
        Date fileDate= null;
        try
        {
            //try parsing YYYYMMDD
            fileDate = PodCastConstants.DATE_PARSER.parse(potentialDate);
            m_logger.info("Parsed Date using new parser [yyyymmdd]");
        }
        catch(Exception de)
        {

            try
            {
                //If that doesnt work, then try DDMMYYYY
                fileDate = PodCastConstants.DATE_PARSER_OLD.parse(potentialDate);
                m_logger.info("Parsed Date using old parser [ddmmyyyy]");
            }
            catch(Exception e)
            {
                m_logger.info("Parsed Date from last modified date");
                fileDate = new Date(podCastFile.lastModified());
            }
        }
        return fileDate;
    }
    
    
    //Get the description from the template.
    private String getDescription(PodCastFile podCastFile,Map customValues)
    {
        //Get the description for the file.
        //Is there a custom description - use if there is.
        //Is there a template. Use if there is and you can parse the filename.
        //Otherwise just use the default
        String desc = PodCastUtils.getMapValue(customValues, podCastFile.getName(), PodCastConstants.DEFAULT_DESC);
        if(desc.equals(PodCastConstants.DEFAULT_DESC)) //look for template (in case thats configured)
        {
            String descriptionFromName = PodCastUtils.getFileName(podCastFile.getName());
            m_logger.debug("Description from name is ["+descriptionFromName+"]");
            if(PodCastUtils.getMapValue(customValues, PodCastConstants.ITEM_DESC_TEMPLATE, "").equals(""))
            {
                m_logger.debug("No template in custom values");
                //we dont have a template setup - so use the default.
                desc = PodCastConstants.DEFAULT_DESC;
            }
            else
            {
                m_logger.debug("Using template for description");
                // lets get the description and parse into %1,and %2
                if(descriptionFromName.indexOf(".") == -1) //no split in description
                {
                    m_logger.debug("No splittable values from name ["+descriptionFromName+"]");
                    desc = String.format(PodCastUtils.getMapValue(customValues, PodCastConstants.ITEM_DESC_TEMPLATE, PodCastConstants.DEFAULT_DESC),descriptionFromName,"UNKNOWN");
                }
                else
                {
                    m_logger.debug("Splitting description from filename");
                    //split the desc (ie use the unit, description and the module)
                    String[] descParts = descriptionFromName.split("\\.");

                    if(descParts.length == 2)
                        //Use [1] and [2] as we now return the date...
                        desc = String.format(PodCastUtils.getMapValue(customValues, PodCastConstants.ITEM_DESC_TEMPLATE, PodCastConstants.DEFAULT_DESC),descParts[1],descParts[2]);

                    if(descParts.length == 4)
                    {
                        Map parameterValues = new HashMap();
                        parameterValues.put(PodCastConstants.PARSED_DATE, descParts[0]);
                        parameterValues.put(PodCastConstants.PARSED_UNIT, descParts[1]);
                        parameterValues.put(PodCastConstants.PARSED_DESC, descParts[2]);
                        parameterValues.put(PodCastConstants.PARSED_MODULE, descParts[3]);

                        desc = PodCastUtils.replaceParameters(desc, customValues);
                    }

                }

            }

        }
        else m_logger.debug("Has custom value for file");
        m_logger.info("Description is ["+desc+"]");
        
        return desc;
    }
    
    
    //Get the title of the file
    private String getTitle(String module, String link,String desc, Date fileDate)
    {
        String title = module;
        String suffix = PodCastUtils.getSuffix(link);
        if(!m_simpleMode)
        {
            if ( m_supportedTypes.containsKey(suffix))
            {
                String fileDesc = (m_supportedTypes.getString(suffix).split("~"))[0];
                title += fileDesc + " from "+PodCastConstants.DATE_PARSER.format(fileDate);
            }
            else
                title += " Misc Document from "+PodCastConstants.DATE_PARSER.format(fileDate);
        }
        else
        {
            if ( m_supportedTypes.containsKey(suffix))
            {
                String fileDesc = (m_supportedTypes.getString(suffix).split("~"))[0];
                title +=  " " + desc + " - "+ fileDesc;
            }
            else
                title += " " + desc + " - Misc Document";
        }
        return title;

        
    }
    
    private String getLinkType(String link)
    {
        String linkType = new String();
        // Find the mime type from the supported Files.
        String suffix = PodCastUtils.getSuffix(link);
        if(m_supportedTypes.containsKey(suffix))
            linkType = m_supportedTypes.getString(suffix).split("~")[1];
        else
            linkType = "text/plain";
        
        return linkType;

    }
    

    private SyndEntry generateEntry(String mp3link, Date fileDate, 
            String fileDescription, String fileCategory, String author, 
            File originalFile,String httpSuffix) throws PodCastCreationException
    {
        SyndEntry podcastEntry;
        SyndContent description = new SyndContentImpl();
        SyndCategory category;
        try
        {

            podcastEntry = new SyndEntryImpl();
            podcastEntry.setAuthor(author);
            
            podcastEntry.setTitle(getTitle(fileCategory,mp3link,fileDescription, fileDate));
            podcastEntry.setLink(mp3link+httpSuffix);
            podcastEntry.setPublishedDate(fileDate);

            //Set the right link type
            description.setType(getLinkType(mp3link));

            //Setting description
            m_logger.info("Setting description as ["+fileDescription+"]");

            description.setValue(fileDescription);
            podcastEntry.setDescription(description);

            //Do some crazy category stuff.
            category = new SyndCategoryImpl();
            category.setName(fileCategory);
            List categories = new ArrayList();
            categories.add(category);
            podcastEntry.setCategories(categories);


            ArrayList modules = new ArrayList();
            EntryInformation e = new EntryInformationImpl();
            //Duration in milliseconds... set to 1 minute if not an audio file or cant be found.
            e.setDuration( new Duration( PodCastUtils.getFileDuration(originalFile) ) );

            e.setAuthor(podcastEntry.getAuthor());
            e.setSummary(podcastEntry.getDescription().getValue());
            e.setKeywords(new String[]{category.getName()});
            modules.add( e );
            podcastEntry.setModules( modules );

            //Set the actual Link.
            SyndEnclosure enc = new SyndEnclosureImpl();
            enc.setType(description.getType());
            enc.setUrl(mp3link+httpSuffix);
            List encs = new ArrayList();
            encs.add(enc);
            podcastEntry.setEnclosures(encs);

            
            return podcastEntry;


        } 
        catch (Exception ex)
        {
            m_logger.error("ERROR: " + ex.getMessage());
            throw new PodCastCreationException(ex.getLocalizedMessage());
        }
    }



    
}
