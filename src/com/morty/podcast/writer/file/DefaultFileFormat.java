// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.

package com.morty.podcast.writer.file;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Allows a default file format to be used, so that if you cant be bothered
 * to configure, this is what it will enforce.
 * @author amorton
 */
public class DefaultFileFormat extends PodCastFileNameFormat
{
    public DefaultFileFormat()
    {
        //Set the defaults in the constructor
        this.setDelimited(true);
        this.setSplitCharacter("\\.");
        this.setFormatName("Default Format");
        this.setFormatPattern("\\d{8}\\.[-|\\w]+\\.[-|\\w]+\\.\\w+\\.\\w+");
        
        //create the fields.
        PodCastFileNameField date = new PodCastFileNameField();
        date.setPosition(0);
        date.setFieldName("date");
        date.setMappedName("${date}");
        
        PodCastFileNameField unit = new PodCastFileNameField();
        unit.setPosition(1);
        unit.setFieldName("unit");
        unit.setMappedName("${unit}");
        
        PodCastFileNameField desc = new PodCastFileNameField();
        desc.setPosition(2);
        desc.setFieldName("desc");
        desc.setMappedName("${description}");
        
        PodCastFileNameField module = new PodCastFileNameField();
        module.setPosition(3);
        module.setFieldName("module");
        module.setMappedName("${module}");
        
        PodCastFileNameField suffix = new PodCastFileNameField();
        suffix.setPosition(4);
        suffix.setFieldName("suffix");
        suffix.setMappedName("${suffix}");
        
        List fields = new ArrayList();
        fields.add(date);
        fields.add(unit);
        fields.add(desc);
        fields.add(module);
        fields.add(suffix);
        
        this.setFields(fields);


        //Set the filetypes.
        Map fileMessages = new HashMap();
        String mp3 = "${module} Lecture Audio from ${date}~audio/mpeg";
        String m4a = "${module} Lecture Audio ffrom ${date}~audio/mp4a-latm";
        String mp4 = "${module} Lecture Video from ${date}~video/mpeg";
        String pdf = "${module} Notes from ${date}~application/pdf";
        String flv = "${module} Lecture Video from ${date}~video/x-flv";
        String mpg = "${module} Lecture Video from ${date}~video/mpeg";

        fileMessages.put("mp3", mp3);
        fileMessages.put("m4a", m4a);
        fileMessages.put("mp4", mp4);
        fileMessages.put("pdf", pdf);
        fileMessages.put("flv", flv);
        fileMessages.put("mpg", mpg);


        this.setFormatMessages(fileMessages);
        
        
    }




}
