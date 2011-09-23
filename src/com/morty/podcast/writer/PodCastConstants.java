// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.

package com.morty.podcast.writer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.annotation.PostConstruct;

/**
 * The constants for the podcast application.
 * @author amorton
 */
public class PodCastConstants
{
    
    @PostConstruct
    private void init()
    {
        //Ensures that a InvalidDate Exception is thrown if it doesnt match the format!
        PodCastConstants.DATE_PARSER_OLD.setLenient(false);
        PodCastConstants.DATE_PARSER.setLenient(false);
    }

    /**
     * Used to parse dates of the format yyyymmdd. This is the preferred date.
     */
    public static final DateFormat DATE_PARSER = new SimpleDateFormat("yyyyMMdd");
    /**
     * Used to parse dates of the format ddmmyyyy.  This is an old supported format.
     * @deprecated
     */
    public static final DateFormat DATE_PARSER_OLD = new SimpleDateFormat("ddMMyyyy");
    /**
     * Name of info file
     */
    public static final String INFO_FILE = "info.txt";
    /**
     * MP3 suffix
     * @deprecated
     */
    public static final String MP3_SUFFIX = ".mp3";
    /**
     * PDF Suffix
     * @deprecated
     */
    public static final String PDF_SUFFIX = ".pdf";
    /**
     * Flash Video Suffix
     */
    public static final String FLV_SUFFIX = ".flv";
    /**
     * MP4 Suffix
     * @deprecated
     */
    public static final String MP4_SUFFIX = ".mp4";
    /**
     * MPEG Layer 1 Suffix
     * @deprecated
     */
    public static final String MPG_SUFFIX = ".mpg";
    /**
     * Upper case MPEG Layer 1 Suffix!
     * @deprecated
     */
    public static final String MPG_SUFFIX_UPPER = ".MPG";

    //Default values for the podcast.
    /**
     * Default value for Author
     */
    public static final String DEFAULT_AUTHOR="FMBC";
    /**
     * Default Desciption (no description available!)
     */
    public static final String DEFAULT_DESC = "No Description Available";
    /**
     * Default Podcast Title
     */
    public static final String DEFAULT_FEED_TITLE = "Default Podcast Title";
    /**
     * Default Copyright
     */
    public static final String DEFAULT_FEED_COPYRIGHT="Default Copyright Notice";
    /**
     * Default Podcast Description
     */
    public static final String DEFAULT_FEED_DESCRIPTION = "Default Podcast Description";
    /**
     * Default file duration.
     */
    public static final long DEFAULT_DURATION = 60000;


    //Key for the info file.
    /**
     * Default Key for Author Info
     */
    public static final String AUTHOR_KEY= "Author";
    /**
     * Default Key for Template Info
     */
    public static final String ITEM_DESC_TEMPLATE="Template";
    /**
     * Default Key for Feed Title Info
     */
    public static final String FEED_TITLE_KEY="Title";
    /**
     * Default Key for Feed Description
     */
    public static final String FEED_DESCRIPTION_KEY="Description";
    /**
     * Default Key for Feed Copyright
     */
    public static final String FEED_COPYRIGHT_KEY="Copyright";

    /**
     * Default RSS type
     */
    public static final String FEED_TYPE = "rss_2.0";
    
    
    /**
     * Default Key for Image Name
     */
    public static final String FEED_IMAGE_NAME="Image";
 
    /**
     * Default Key for Image Name
     */
    public static final String FEED_IMAGE_TITLE="ImageTitle";
    
    /**
     * Default Key for Image Name
     */
    public static final String DEFAULT_IMAGE_TITLE="PodcastImage";
    /**
     * Default Key for Image Name
     */
    public static final String DEFAULT_IMAGE_NAME="image.jpg";
    /**
     * Key name for podcast generator bean
     */
    public static final String PODCAST_GENERATOR_BEAN="podCastGenerator";

    /**
     * Default fields for parameters
     */
    public static final String PARSED_DATE="${date}";
    public static final String PARSED_UNIT="${unit}";
    public static final String PARSED_DESC="${description}";
    public static final String PARSED_MODULE="${module}";

    
    
}
