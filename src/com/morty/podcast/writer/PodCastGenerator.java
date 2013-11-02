// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.

package com.morty.podcast.writer;

import com.morty.podcast.writer.excpt.PodCastCreationException;
import com.morty.podcast.writer.constants.PodCastConstants;
import com.morty.podcast.writer.file.PodCastFile;
import com.morty.podcast.writer.file.PodCastFileNameResolver;
import com.morty.podcast.writer.file.PodCastFileProperties;
import com.morty.podcast.writer.validation.FolderValidator;
import com.sun.syndication.feed.module.itunes.EntryInformation;
import com.sun.syndication.feed.module.itunes.EntryInformationImpl;
import com.sun.syndication.feed.module.itunes.FeedInformation;
import com.sun.syndication.feed.module.itunes.FeedInformationImpl;
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
import java.io.FilenameFilter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
 *
 * Modified 15/02/2011 to use the filename date of YYYYMMDD
 * Modified 25/07/2011 for major refactor and handle UTF8 filenames.
 * Modified September 2011 for new features.
 *
 * Major refactor to allow the custom naming, messaging and everything else.
 * Validation has been implemented, but will probably be used as a once off check, as its not very customisable.
 * Future implementations should use a wrapper around a FileFilter (see other project!)
 *
 * Copyright 2011
 * @author amorton
 */
public class PodCastGenerator
{
    
    private static final Log m_logger = LogFactory.getLog(PodCastGenerator.class);

    //Simple mode is no longer valid - if you want to run in simple mode, use
    //an appropriate supported file structure.
    @Deprecated
    private boolean m_simpleMode = false;

    private String m_fileToCreate = null;
    private String m_directoryToTraverse = null;
    private String m_urlSuffix = "";
    private String m_httpRoot = null;
    private Set<String> m_excludedFolders = new HashSet<String>();
    private PodCastFileNameResolver fileResolver = new PodCastFileNameResolver();

    //Validation
    private FolderValidator m_validator= null;
    private boolean m_failOnValidation = false;

    //Invalid files
    private Map m_ignoredFiles = new HashMap();


    //Override value for the http link for the feed.
    private String m_feedLink = null;

    /**
     * Set the feed link override text
     * @param link
     */ 
     publiv void setFeedLink(String link)
     {
         this.m_feedLink = link;
     }


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
     * Optional Folders
     * @param excludedFolders
     */
    public void setExcludedFolders(Set foldersToExclude)
    {
        this.m_excludedFolders = foldersToExclude;
    }


    /**
     * Allows for strict processing
     * @param folderValidator
     */
    public void setValidator(FolderValidator fv)
    {
        this.m_validator = fv;
    }

     /**
     * Allows for processing to be stopped!
     * @param failOnValidation
     */
    public void setFailOnValidation(boolean fov)
    {
        this.m_failOnValidation = fov;
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

            outputIgnoredFiles();
            
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
       
        if(m_validator != null)
        {
            m_logger.info("Starting validation");
            m_validator.setExcludedFolders(m_excludedFolders);
            m_validator.setFolderToProcess(directory);
            m_validator.setFailOnError(m_failOnValidation);
            m_validator.process();
            m_logger.info("Finished validation");
        }


        //Allow folder exclusion
        File[] modules = directory.listFiles(new FilenameFilter() {

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
                        //Put these files in the list with an excluded reason
                        m_ignoredFiles.put(file.getAbsolutePath()+File.separator+fileToCheck,PodCastConstants.FILE_EXCLUDED);
                        return false;
                    }
                    else return true;
            }
        } );

        for (int i=0; i< modules.length; i++)
        {
            if(modules[i].isDirectory())
            {
                String category = modules[i].getName();

                m_logger.info("Processing module ["+category+"]");

                File categoryDirectory = new File(modules[i].getAbsolutePath());
                File[] originalFiles = categoryDirectory.listFiles();
                PodCastFile[] podcastFiles = new PodCastFile[originalFiles.length];
                Map parentDirectoryProperties = processInfoFile(categoryDirectory);


                //Convert all the files to PodCastFiles in the array.
                for(int counter=0;counter<podcastFiles.length;counter++)
                {
                    //Create a podcast file with a construct of file path (preconverted to UTF8).
                    //That way all files should exist, even if they have weird characters and every getName will
                    //also contain the right characters!
                    podcastFiles[counter] = new PodCastFile(PodCastUtils.convertToUTF8(originalFiles[counter].getAbsolutePath()));
                    podcastFiles[counter].setParentProperties(parentDirectoryProperties);
                    fileResolver.formatFile(podcastFiles[counter]);
                    
                }
                
               
                for (int p=0; p<podcastFiles.length;p++)
                { 
                    if(!podcastFiles[p].isValid())
                    {
                        m_logger.warn("Ignoring hidden/unresolved or info file ["+podcastFiles[p].getName()+"]");
                        m_ignoredFiles.put(podcastFiles[p].getAbsolutePath(),podcastFiles[p].getProperty(PodCastFileProperties.INVALID_REASON));

                    }
                    else
                        returnList.add(processFile(podcastFiles[p],category, parentDirectoryProperties));
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
        Date fileDate = podCastFile.getFileDate();
        String desc = podCastFile.getDescription();
        
        //Add the entry of the file
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
            if(m_feedLink == null)
                feed.setLink(PodCastUtils.generateHttpLink(m_httpRoot,m_fileToCreate,m_urlSuffix));
            else
                feed.setLink(m_feedLink);
            feed.setDescription(PodCastUtils.getMapValue(defaultFeedValues,PodCastConstants.FEED_DESCRIPTION_KEY, PodCastConstants.DEFAULT_FEED_DESCRIPTION));
            feed.setCopyright(PodCastUtils.getMapValue(defaultFeedValues,PodCastConstants.FEED_COPYRIGHT_KEY, PodCastConstants.DEFAULT_FEED_COPYRIGHT));
            
            //Set the image on the Feed.
            SyndImage img = new SyndImageImpl();
            img.setDescription(feed.getDescription());
            img.setLink(feed.getLink());
            img.setTitle(PodCastUtils.getMapValue(defaultFeedValues,PodCastConstants.FEED_IMAGE_TITLE, PodCastConstants.DEFAULT_IMAGE_TITLE));
            img.setUrl(PodCastUtils.generateHttpLink(m_httpRoot,PodCastUtils.getMapValue(defaultFeedValues,PodCastConstants.FEED_IMAGE_NAME, PodCastConstants.DEFAULT_IMAGE_NAME), m_urlSuffix));
            feed.setImage(img);

            //Set image on all episodes.
            FeedInformation episodeFeed = new FeedInformationImpl();
            episodeFeed.setImage(new URL(img.getUrl()));

            Iterator it = podcastFiles.iterator();
            while(it.hasNext())
            {
                SyndEntry podcastEntry = (SyndEntry) it.next();
                podcastEntry.getModules().add(episodeFeed);
            }
            
            //Generate the itunes image!
            FeedInformation itunesFeed = new FeedInformationImpl();
            itunesFeed.setImage(new URL(img.getUrl()));
            itunesFeed.setSummary(feed.getDescription());
            feed.getModules().add(itunesFeed);

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

    
    
    

    private SyndEntry generateEntry(String mp3link, Date fileDate, 
            String fileDescription, String fileCategory, String author, 
            PodCastFile originalFile,String httpSuffix) throws PodCastCreationException
    {
        SyndEntry podcastEntry;
        SyndContent description = new SyndContentImpl();
        SyndCategory category;
        try
        {

            podcastEntry = new SyndEntryImpl();
            podcastEntry.setAuthor(author);
            
            podcastEntry.setTitle(originalFile.getTitle());
            podcastEntry.setLink(mp3link+httpSuffix);
            podcastEntry.setPublishedDate(fileDate);

            //Set the right link type
            description.setType(originalFile.getMimeType());


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

    private void outputIgnoredFiles()
    {
        m_logger.info("=========================================================");
        m_logger.info("Ignored Files");

        Iterator it = m_ignoredFiles.keySet().iterator();
        while(it.hasNext())
        {
            String file = (String) it.next();
            String reason = (String) m_ignoredFiles.get(file);
            m_logger.info(reason+"\t\t"+file);
        }
        m_logger.info("=========================================================");
    }



    
}
