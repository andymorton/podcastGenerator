// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.

package com.morty.podcast.writer;

import com.morty.podcast.writer.file.PodCastFileProperties;
import java.io.File;
import java.net.URI;
import java.util.Map;

/**
 * Allows for UTF8 filenames.
 * @author amorton
 */
public class PodCastFile extends File
{
    Map m_internalProperties;

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

    
    //Ability to get file properties
    public boolean isValid()
    {
        if(!m_internalProperties.containsKey(PodCastFileProperties.FILE_VALID))
            return true;
        else if(m_internalProperties.get(PodCastFileProperties.FILE_VALID) == Boolean.TRUE)
            return true;
        else return false;
    }





}
