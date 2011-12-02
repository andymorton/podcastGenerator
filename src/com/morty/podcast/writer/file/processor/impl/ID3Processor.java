// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.

package com.morty.podcast.writer.file.processor.impl;

import com.morty.podcast.writer.file.PodCastFile;
import com.mpatric.mp3agic.ID3Wrapper;
import com.mpatric.mp3agic.ID3v1Tag;
import com.mpatric.mp3agic.ID3v23Tag;
import com.mpatric.mp3agic.Mp3File;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.commons.io.FileUtils;

/**
 * This will take the file, and add on all the right id3 tags.
 * @author amorton
 */
public class ID3Processor extends AbstractFileProcessor
{

    //Private local variables.
    private String m_imageFile;
    private byte[] m_imageData;
    private Map m_mappings;
    private String m_fileRecord;
    private List m_fileRecordList = new ArrayList();

    public void setFileRecord(String file)
    {
        this.m_fileRecord = file;
    }

    @PostConstruct
    public void loadFileRecord()
    {
        if(m_fileRecord != null)
        {
            try
            {
                m_logger.info("Loading file record [" + m_fileRecord + "]");
                m_fileRecordList = FileUtils.readLines(new File(m_fileRecord));
            }
            catch (Exception ex)
            {
                m_logger.error("Unable to load the file record",ex);
                m_fileRecordList = new ArrayList();
            }

        }
    }


    public void setImage(String img)
    {
        try
        {
            m_imageFile = img;
            File image = new File(m_imageFile);
            //Load up image
            m_imageData = FileUtils.readFileToByteArray(image);
        }
        catch (IOException ex)
        {
            m_logger.error("Unable to obtain image data - setting to null",ex);
        }
    }




    public void setMappings(Map mpngs)
    {
        this.m_mappings = mpngs;
    }


    //have a name....
    public String getName()
    {
        return "ID3Processor";
    }


    public void processComponent(PodCastFile file)
    {
        if(m_fileRecordList.contains(file.getAbsolutePath()))
        {
            m_logger.info("Skipping pre-processed file ["+file.getAbsolutePath()+"]");
            return;
        }

        //Otherwise lets go and process the file.
        try
        {

            //Load the file up
            Mp3File mp3file = new Mp3File(file.getAbsolutePath());

            if(m_imageFile == null || m_imageFile.equals(""))
                m_logger.info("No image file set.");
            else
            {
                m_logger.info("Setting image");
                mp3file = setImage(mp3file);
            }
                

            //set mappings.
            if(m_mappings != null && !m_mappings.isEmpty())
                mp3file = setProperties(file,mp3file);


            mp3file.save(file.getAbsolutePath()+".tmp");
            FileUtils.forceDelete(new File(file.getAbsolutePath()));
            FileUtils.moveFile(new File(file.getAbsolutePath()+".tmp"), new File(file.getAbsolutePath()));

            if(m_fileRecord != null)
            {
                m_fileRecordList.add(file.getAbsolutePath());
                FileUtils.writeLines(new File(m_fileRecord), m_fileRecordList);
                m_logger.info("Added file to preprocessed file");
            }
            

        }
        catch (Exception ex)
        {
            m_logger.error("Unable to alter tags for ["+file.getAbsolutePath()+"]",ex);
        }



    }



    private Mp3File setImage(Mp3File mp3file)
    {
        ID3Wrapper wrapper;
        if(mp3file.hasId3v1Tag() || mp3file.hasId3v2Tag())
            wrapper = new ID3Wrapper(mp3file.getId3v1Tag(), mp3file.getId3v2Tag());
        else
            wrapper = new ID3Wrapper(new ID3v1Tag(), new ID3v23Tag());

        wrapper.setAlbumImage(m_imageData, "image/jpeg");

        mp3file.setId3v2Tag(wrapper.getId3v2Tag());
        mp3file.setId3v1Tag(wrapper.getId3v1Tag());


        return mp3file;
    }

    private Mp3File setProperties(PodCastFile file, Mp3File mp3file)
    {

        ID3Wrapper oldId3Wrapper;
        if(mp3file.hasId3v2Tag())
            oldId3Wrapper = new ID3Wrapper(mp3file.getId3v1Tag(), mp3file.getId3v2Tag());
        else
            oldId3Wrapper = new ID3Wrapper(new ID3v1Tag(), new ID3v23Tag());



        //look up the map values and pass them in.
        Iterator it = m_mappings.keySet().iterator();
        while(it.hasNext())
        {
            String key = (String) it.next();
            //now we have the key, get the value.
            String value = (String) m_mappings.get(key);

            //Replace any variables.
            String valueToWrite;
            if(file.hasProperty(value))
                valueToWrite = (String) file.getProperty(value);
            else
                valueToWrite = value;

            m_logger.info("About to write ["+key+"] with value ["+valueToWrite+"]");

            //Based on the key - we only handle a couple!
            if(key.equalsIgnoreCase("Artist"))
                mp3file.getId3v2Tag().setArtist(value);
            if(key.equalsIgnoreCase("Album"))
                mp3file.getId3v2Tag().setAlbum(value);
            
        }

        mp3file.setId3v1Tag(oldId3Wrapper.getId3v1Tag());
        mp3file.setId3v2Tag(oldId3Wrapper.getId3v2Tag());

        return mp3file;
    }
            
    
    
}
