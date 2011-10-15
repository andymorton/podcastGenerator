// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.

package com.morty.podcast.writer;

/**
 * This is a class that can be populated in Spring context to put settings in
 * to allow multiple settings to be passed in.
 * @author amorton
 */
public class PodCastSettings
{
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
     * Add in an optional url suffix
     * @param urlSuffix
     */
    public void setUrlSuffix(String urlSuffix)
    {
        this.m_urlSuffix = urlSuffix;
    }


    //Getters...
    public String getDirectory()
    {
        return m_directoryToTraverse;
    }

    public String getFileToCreate()
    {
        return m_fileToCreate;
    }

    public String getHttpRoot()
    {
        return m_httpRoot;
    }

    public String getUrlSuffix()
    {
        return m_urlSuffix;
    }




}
