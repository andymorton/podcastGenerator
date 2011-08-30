// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.

package com.morty.podcast.writer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tritonus.share.sampled.file.TAudioFileFormat;

/**
 * Basic Utility class for the podcasts
 * @author amorton
 */
public class PodCastUtils
{
    
    //Standard Logger
    private static final Log m_logger = LogFactory.getLog(PodCastUtils.class);
    

    /**
     * Converts the String to a UFT-8 compliant String, re-encoding any
     * non-ascii characters in UTF-8
     * @param filepath
     * @return UTF8 Compatible filename
     */
    public static String convertToUTF8(String filepath)
    {
        byte[] originalBytes = filepath.getBytes();
        try
        {
            String newName = new String(originalBytes, "UTF-8");
            return newName;
        }
        catch(Exception e)
        {
            if(m_logger.isDebugEnabled())
                m_logger.error("We could not return a UTF-8 based string for the name. Returning ["+filepath+"]",e);
            else
                m_logger.error("We could not return a UTF-8 based string for the name. Returning ["+filepath+"]");
            return filepath;
        }
                
    }


    
    
    /**
     * Gets everything except the date and suffix.
     * Of course, if there is only filename.suffix, then it doesnt bother.
     * @param name
     * @return filename without date and suffix (ie the first and last sections)
     */
    public static String getFileName(String name)
    {
        m_logger.debug("Splitting string ["+name+"]");

        String[] values = name.split("\\.");

        //value 0 = date
        //value 1 = unit number
        //value 2 = description
        //value 3 = module.
        //value 4 = suffix.
        //ignore the last and first one.
        String returnValue = "";
        if(values.length !=2 )// no description (maybe simple mode!)
        {
            //remove last.
            for(int i=0; i < values.length-1; i++ )
            {
                returnValue += values[i]+".";
            }
            //remove last .
            returnValue = returnValue.substring(0,returnValue.length()-1);

            //Remove '_'
            returnValue = returnValue.replace('_', ' ');
        }
        return returnValue;


    }


    
    /**
     * Get the duration of an mp3 file...
     * @param file
     * @return the number of milliseconds in the file or default if not valid audio file
     */
    public static long getFileDuration(File file)
    {
        try
        {
            if(file.getName().endsWith(PodCastConstants.MP3_SUFFIX))
                return getDurationWithMp3Spi(file);
            else return PodCastConstants.DEFAULT_DURATION;
        }
        catch(Exception e)
        {
            m_logger.error("Exception found of ["+e+"] getting length of file.");
            //Couldnt find the duration - use default.
            return PodCastConstants.DEFAULT_DURATION;
        }
    }

    /**
     * Gets the duration of an audio file.
     * @param file
     * @return the number of milliseconds in the file
     * @throws UnsupportedAudioFileException
     * @throws IOException
     */
    public static long getDurationWithMp3Spi(File file) throws UnsupportedAudioFileException, IOException
    {
        m_logger.info("Determining length of MP3 file");

        AudioFileFormat audioFile = AudioSystem.getAudioFileFormat(file);
        if (audioFile instanceof TAudioFileFormat)
        {
            m_logger.debug("TAudioFileFormat file found");
            Map<?, ?> properties = ((TAudioFileFormat) audioFile).properties();
            String key = "duration";
            Long numberOfMicroseconds = (Long) properties.get(key);
            m_logger.info("Duration of ["+numberOfMicroseconds+"] microseconds");
            return (numberOfMicroseconds.longValue()/1000);
        }
        else 
        {
            m_logger.info("Unsupported Mp3 fileformat.");
            throw new UnsupportedAudioFileException();
        }

    }

    
     
    /**
     * Gets an info file and parses it for key-value pairs, that
     * can be accessed as a map
     * 
     * (This be changed to be a resource bundle - might be easier.... )
     * 
     * @param file
     * @return
     */
    public static HashMap parseInfoFile(File file)
    {
        BufferedReader in = null;
        HashMap returnMap = new HashMap();
        try
        {
            in = new BufferedReader(new FileReader(file));
            String line = "";

            while( (line = in.readLine()) != null)
            {
                String[] values = line.split("=");
                returnMap.put(values[0], values[1]);
            }

        }
        catch (FileNotFoundException ex)
        {
            returnMap = new HashMap();
        }
        finally
        {
            try
            { 
                in.close();
            }
            catch (IOException ex)
            {    
                returnMap = new HashMap();
            }
            finally
            {return returnMap;}
        }
    }
    
    
    
    
    
    /**
     * Gets a value from a hashmap, and if it doesnt exist, returns the default.
     * @param customValues
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getMapValue(Map customValues,String key,String defaultValue)
    {
        if(customValues.containsKey(key))
            return customValues.get(key).toString();
        else
            return defaultValue;
    }
    
    
    
    
    /**
     * Just concatenates everything...(making sure the filepath is held to)
     * @param httpRoot
     * @param filename
     * @param httpSuffix
     * @return
     */
    public static String generateHttpLink(String httpRoot, String filename, String httpSuffix)
    {
        //Look at the filename and take the actual name.
        File file = new File(filename);
        return httpRoot+"/"+file.getName()+httpSuffix;
    }


    /*
     * Allow for parameters in Template.
     * ${date}
     * ${unit}
     * ${description}
     * ${module}
     */
    public static String replaceParameters(String original, Map<String,String> values)
    {
        //Replace the strings...
        String returnValue = original;
        Iterator it =values.keySet().iterator();
        while(it.hasNext())
        {
            String key= (String) it.next();
            if(StringUtils.contains(returnValue, key))
            {
                StringUtils.replace(returnValue, key, values.get(key));
            }
        }
        return returnValue;
    }
    


}
