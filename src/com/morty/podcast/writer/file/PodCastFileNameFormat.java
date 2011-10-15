package com.morty.podcast.writer.file;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Used to provide a filename format that can be used to discover
 * what fields are available.
 *
 * @author amorton
 */
public class PodCastFileNameFormat
{
    private final Log m_logger = LogFactory.getLog(getClass());
    private String formatName;
    private String formatPattern;
    private boolean delimited;
    private String splitCharacter;
    private Pattern regexPattern;
    private List fields;
    private Map formatMessages = new HashMap();


    public String getFormatName()
    {
        return formatName;
    }

    public void setFormatName(String formatName)
    {
        this.formatName = formatName;
    }

    public String getFormatPattern()
    {
        return formatPattern;
    }

    public void setFormatPattern(String formatPattern)
    {
        this.formatPattern = formatPattern;
        regexPattern = Pattern.compile(formatPattern);
    }

    public boolean isDelimited()
    {
        return delimited;
    }

    public void setDelimited(boolean delimited)
    {
        this.delimited = delimited;
    }

    public String getSplitCharacter()
    {
        return splitCharacter;
    }

    public void setSplitCharacter(String splitCharacter)
    {
        this.splitCharacter = splitCharacter;
    }


    public boolean matches(String filename)
    {
        Matcher matcher = regexPattern.matcher(filename);
        m_logger.info("Matching ["+filename+"] against ["+regexPattern.pattern()+"] result ["+matcher.matches()+"]");
        return matcher.matches();
    }

    public List getFields()
    {
        if(this.fields == null) return new ArrayList();
        return fields;
    }

    public void setFields(List fields)
    {
        this.fields = fields;
    }

    public Map getFormatMessages()
    {
        return formatMessages;
    }

    public void setFormatMessages(Map formatMessages)
    {
        this.formatMessages = formatMessages;
    }


    @Override
    public String toString()
    {
        return this.formatName+"("+this.formatPattern+")";
    }

    
}
