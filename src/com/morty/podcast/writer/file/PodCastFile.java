// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.

package com.morty.podcast.writer.file;

import com.morty.podcast.writer.constants.PodCastConstants;
import com.morty.podcast.writer.PodCastUtils;
import com.morty.podcast.writer.file.PodCastFileProperties;
import java.io.File;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.StringUtils;

/**
 * Allows for UTF8 filenames.
 * @author amorton
 */
public class PodCastFile extends File
{
    private static final Log m_logger = LogFactory.getLog(PodCastFile.class);
    Map m_internalProperties;
    Map m_parentPropeties;

    /**
     * Constructor - see related java.io.File Constructor
     * @param name
     */
    public PodCastFile(String name)
    {
        super(name);
    }


    /**
     * Constructor - see related java.io.File Constructor
     * @param uri
     */
    public PodCastFile(URI uri)
    {
        super(uri);
    }

    /**
     * Constructor - see related java.io.File Constructor
     * @param file
     * @param name
     */
    public PodCastFile(File file,String name)
    {
        super(file,name);
    }


    /**
     * Constructor - see related java.io.File Constructor
     * @param string1
     * @param string2
     */
    public PodCastFile(String string1, String string2)
    {
        super(string1,string2);
    }


    /**
     * Gets the file original name and applies UTF-8 encoding
     * @return UTF-8 compliant name
     */
    @Override
    public String getName()
    {
        return PodCastUtils.convertToUTF8(super.getName());
    }


    /*
     * Set propert - allows for the properties of files to be retrieved.
     */
    public void setProperties(Map props)
    {
        m_internalProperties = props;
    }

    public Map getProperties()
    {
        return m_internalProperties;
    }

    public boolean hasProperty(String key)
    {
        return (m_internalProperties != null && m_internalProperties.containsKey(key)) ||
                (m_parentPropeties != null && m_parentPropeties.containsKey(key));
    }

    public Object getProperty(String key)
    {
        //Look for the props in the file, then the parent.
        if(m_internalProperties.containsKey(key))
            return m_internalProperties.get(key);
        else if(m_parentPropeties.containsKey(key))
            return m_parentPropeties.get(key);
        else return null;
    }


    //Parent props are those that affect us, but we dont allow access to them outside of this class!
    public void setParentProperties(Map props)
    {
        m_parentPropeties = props;
    }

    public Map getParentProperties()
    {
        return m_parentPropeties;
    }


    
    //Ability to get file properties
    public boolean isValid()
    {
        if(!m_internalProperties.containsKey(PodCastFileProperties.FILE_VALID))
        {
            m_logger.info("isValid: No flag set");
            return true;
        }
        else if(m_internalProperties.get(PodCastFileProperties.FILE_VALID) == Boolean.TRUE)
        {
            m_logger.info("isValid: Flag set to true");
            return true;
        }
        else return false;
    }


    public Date getFileDate()
    {
        //Look at the props. If not there use the lastModified
         //Look at the first 8 characters. If valid date, then use it, otherwise, the last
        //created date
        String potentialDate;
        if(this.hasProperty(PodCastFileProperties.FILE_DATE))
            potentialDate = (String) this.getProperty(PodCastFileProperties.FILE_DATE);
        else
        {
            m_logger.info("No date field found - using the last modified date");
            Date lmd = new Date(this.lastModified());
            return lmd;
        }

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
                m_logger.info("Unparsable date - Using Parsed Date from last modified date");
                fileDate = new Date(this.lastModified());
            }
        }
        return fileDate;
    }

    public String getFormattedFileDate(String format)
    {
        //Get the date and format it in the right format.
        DateFormat parser = new SimpleDateFormat(format);
        return parser.format(this.getFileDate());
    }


    public String getDescription()
    {
        //This one allows for a custom template description from the category
        //Get the description for the file.
        //Is there a custom description - use if there is.
        //Is there a template. Use if there is and you can parse the filename.
        //Otherwise just use the default
        String desc = PodCastUtils.getMapValue(m_parentPropeties, this.getName(), PodCastConstants.DEFAULT_DESC);
        if(desc.equals(PodCastConstants.DEFAULT_DESC)) //look for template (in case thats configured)
        {
            if(PodCastUtils.getMapValue(m_parentPropeties, PodCastConstants.ITEM_DESC_TEMPLATE, "").equals(""))
            {
                m_logger.debug("No template in custom values");
                //we dont have a template setup - so use the default.
                desc = PodCastConstants.DEFAULT_DESC;
            }
            else
            {
                m_logger.debug("Using template for description");
                
                //Use the %s parameters first, to get rid of them (legacy templates) and then apply the new one
                //This is for backwards compatibility. if the template is still using %s, then we pass in the unit and description
                desc = String.format(PodCastUtils.getMapValue(m_parentPropeties, PodCastConstants.ITEM_DESC_TEMPLATE, PodCastConstants.DEFAULT_DESC),
                        this.getProperty(PodCastFileProperties.FILE_UNIT),this.getProperty(PodCastFileProperties.FILE_DESC));

                m_logger.info("About to replace parameters in desc ["+desc+"] ");

                //Then we replace any other parameters that might be set.
                desc = PodCastUtils.replaceParameters(desc, m_internalProperties);
                m_logger.debug("Custom description applied ["+desc+"]");
                
            }

        }
        else m_logger.debug("Has custom value for file");

        //Replace any '_' characters with space the original description will still have the underscores!
        desc = StringUtils.replace(desc,"_", " ");
        m_logger.info("Description is ["+desc+"]");
        
        return desc;

    }

    public String getOriginalDescription()
    {
        if(m_internalProperties.containsKey(PodCastFileProperties.FILE_DESC))
            return (String) this.getProperty(PodCastFileProperties.FILE_DESC);
        else return "";


    }

    public String getTitle()
    {
        //This is a taken from the formatter.
        return (String) this.getProperty(PodCastFileProperties.FILE_TITLE);
    }


    public String getMimeType()
    {
        //Set during the formatter
        return (String) this.getProperty(PodCastFileProperties.FILE_MIME_TYPE);
    }

    public String getSuffix()
    {
        if(this.hasProperty(PodCastFileProperties.FILE_SUFFIX))
            return (String) this.getProperty(PodCastFileProperties.FILE_SUFFIX);
        else return null;
    }




}
