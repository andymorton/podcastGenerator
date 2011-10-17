// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.

package com.morty.podcast.writer.file;

import java.util.Map;

/**
 * 
 * @author amorton
 */
public class PodCastFileNameField
{

    //Fields are either delimited, and therefore give a position.
    //Or they are fixed and give a start finish.
    //the mapped name is what property it is mapped to eg ${myvariable}
    //the valueMap is to allow the looking up of a value from a map, where the value from the filename is the key.
    //position = 0 based index based on split.
    //startpos & endpos are for field substrings.
    private String fieldName;
    private String mappedName;
    private Map valueMap;
    private int position;
    private int startpos;
    private int endpos;

    public int getEndpos()
    {
        return endpos;
    }

    public void setEndpos(int endpos)
    {
        this.endpos = endpos;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public void setFieldName(String fieldName)
    {
        this.fieldName = fieldName;
    }

    public String getMappedName()
    {
        return mappedName;
    }

    public void setMappedName(String mappedName)
    {
        this.mappedName = mappedName;
    }

    public int getPosition()
    {
        return position;
    }

    public void setPosition(int position)
    {
        this.position = position;
    }

    public int getStartpos()
    {
        return startpos;
    }

    public void setStartpos(int startpos)
    {
        this.startpos = startpos;
    }

    public Map getValueMap()
    {
        return valueMap;
    }

    public void setValueMap(Map valueMap)
    {
        this.valueMap = valueMap;
    }
    




}
