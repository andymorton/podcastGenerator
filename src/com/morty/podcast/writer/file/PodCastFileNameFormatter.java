// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.

package com.morty.podcast.writer.file;

import com.morty.podcast.writer.PodCastConstants;
import com.morty.podcast.writer.PodCastFile;
import com.morty.podcast.writer.PodCastUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * This looks at all the available file formats,
 * find the right one, and returns a list of the values in a map.
 *
 * TODO - add system properties (like size/folder/parent)
 * @author amorton
 */
public class PodCastFileNameFormatter
{

    private final Log m_logger = LogFactory.getLog(getClass());
    private static List<PodCastFileNameFormat> listOfFormats;
    
    public PodCastFileNameFormatter()
    {
        //look for xml file. if not there, then just have list of 1
        //Which will only contain the default format.
        try
        {
            m_logger.info("Setting up resolvers");
            //Look up the xml file in the classpath
            final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(PodCastConstants.SUPPORTED_FILE_FORMATS);
            listOfFormats = (List) ctx.getBean(PodCastConstants.SUPPORTED_FILE_FORMATS_LIST);

            //Log the list of resovlers.
            m_logger.info("Finished Setting up resolvers ["+listOfFormats+"]");

        }
        catch(Exception e)
        {
            m_logger.info("Default Format being used ["+e.getMessage()+"]");
           List defaultList = new ArrayList();
           defaultList.add(new DefaultFileFormat());
           listOfFormats = defaultList;
        }


    }



    //Allow for file properties to be retrieved with file object
    public void formatFile(PodCastFile file)
    {
        Map props = getFileProperties(file.getName(), file.getParentProperties());
        PodCastUtils.printProperties(props);
        file.setProperties(props);
    }

    //Parse the filename to get properties from that.
    private Map getFileProperties(String filename, Map parentProps)
    {
        //Look at our list of filename formats, and find the one that matches.
        //Its a sequencial list...
        Iterator formats = listOfFormats.iterator();
        while(formats.hasNext())
        {
            PodCastFileNameFormat format = (PodCastFileNameFormat) formats.next();
            m_logger.info("Resolving file ["+filename+"] using format ["+format.getFormatName()+"]");
            if(format.matches(filename))
            {
                //Get the map, and return it.
                m_logger.info("Matched using ["+format.getFormatName()+"]");
                return splitFilename(filename, format,parentProps);
            }
            else 
                m_logger.debug("File does not match using ["+format.getFormatName()+"]");


        }

        m_logger.warn("File does not match any resolver. Returning invalid file.");
        //if it doesnt match a format, then just return an empty map
        Map invalidFile = new HashMap();
        invalidFile.put(PodCastFileProperties.FILE_VALID, Boolean.FALSE);
        m_logger.warn("Invalid file set");
        return invalidFile;

    }


    //This is where all the work is done!
    private Map splitFilename(String filename, PodCastFileNameFormat format, Map parentProps)
    {
        Map returnValues = new HashMap();
        //Allow access to parent props in messages.
        returnValues.putAll(parentProps);

        returnValues.put(PodCastFileProperties.FILE_VALID, Boolean.TRUE);

        //If there are no fields configured, just return them
        if(format.getFields().isEmpty()) return returnValues;

        //Otherwise, lets get all the parts we have to!
        if(format.isDelimited())
        {
            m_logger.info("Using delimited format");
            //Split it on the character and get the parts.
            String[] parts = filename.split(format.getSplitCharacter());

            //put all the values in the map
            List fields = format.getFields();
            Iterator it = fields.iterator();
            while(it.hasNext())
            {
                PodCastFileNameField fnf = (PodCastFileNameField) it.next();
                m_logger.info("splitFilename: Setting key["+fnf.getMappedName()+"] value["+parts[fnf.getPosition()]+"]");
                returnValues.put(fnf.getMappedName(), parts[fnf.getPosition()]);
            }

        }
        else
        {
            //Substring the filename positions.
            List fields = format.getFields();
            Iterator it = fields.iterator();
            while(it.hasNext())
            {
                PodCastFileNameField fnf = (PodCastFileNameField) it.next();
                m_logger.info("Setting key["+fnf.getMappedName()+"] value["+filename.substring(fnf.getStartpos(),fnf.getEndpos())+"]");
                returnValues.put(fnf.getMappedName(), filename.substring(fnf.getStartpos(),fnf.getEndpos()));
            }
        }

        //Go through each format, and if mapped, then look up the right value
        Iterator mappedValuesIterator = format.getFields().iterator();
        while(mappedValuesIterator.hasNext())
        {
            PodCastFileNameField fnf = (PodCastFileNameField) mappedValuesIterator.next();
            if(fnf.getValueMap() != null)
            {
                m_logger.info("Using a map to get value for ["+fnf.getMappedName()+"]");
                //Take the value, and use it as a key in the map.
                returnValues.put(fnf.getMappedName(),
                        fnf.getValueMap().get(returnValues.get(fnf.getMappedName())));
            }

        }

        //Now get the suffix and get the specific message for that,
        //setting the mime type and message
        String messageAndMime;
        if(format.getFormatMessages().containsKey( returnValues.get(PodCastFileProperties.FILE_SUFFIX) ) )
        {
            m_logger.info("File Suffix ["+returnValues.get(PodCastFileProperties.FILE_SUFFIX)+"] found");
            messageAndMime = (String)format.getFormatMessages().get(returnValues.get(PodCastFileProperties.FILE_SUFFIX));
        }
        else
        {
            //This is an unsupported file type, so we ignore
            m_logger.warn("Ignoring Unsupported File Type ["+filename+"]");
            returnValues.put(PodCastFileProperties.FILE_VALID, Boolean.FALSE);
            return returnValues;
        }

        
        String[] parts = messageAndMime.split("~");
        //Set the mime type
        returnValues.put(PodCastFileProperties.FILE_MIME_TYPE, parts[1]);
        //Set the desc
        String desc = parts[0];
        desc = PodCastUtils.replaceParameters(desc, returnValues);

        returnValues.put(PodCastFileProperties.FILE_TITLE, desc);



        //Finally return
        return returnValues;

    }

}
