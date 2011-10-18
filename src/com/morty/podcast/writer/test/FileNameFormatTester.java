// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.

package com.morty.podcast.writer.test;

import com.morty.podcast.writer.file.PodCastFile;
import com.morty.podcast.writer.file.PodCastFileNameResolver;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author amorton
 */
public class FileNameFormatTester
{

    public static void main(String[] args)
    {
        System.out.println("Starting test");

        PodCastFileNameResolver fnf = new PodCastFileNameResolver();

        PodCastFile pcf = new PodCastFile("20111001.Unit02.Description1.Module1.mp3");
        fnf.formatFile(pcf);
        Map props = pcf.getProperties();

        System.out.println("Got map of length ["+props.size()+"]");
        Set s = props.keySet();
        Iterator it = s.iterator();
        while(it.hasNext())
        {
            String key = (String) it.next();
            System.out.println("Key["+key+"] has value ["+props.get(key)+"]");
        }


        System.out.println("Finished test");
    }


}
