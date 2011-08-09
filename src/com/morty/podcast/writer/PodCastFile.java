package com.morty.podcast.writer;

import java.io.File;
import java.net.URI;

/**
 * Allows for UTF8 filenames.
 * @author amorton
 */
public class PodCastFile extends File
{

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


}
