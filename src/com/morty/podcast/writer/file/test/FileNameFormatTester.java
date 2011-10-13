package com.morty.podcast.writer.file.test;

import com.morty.podcast.writer.PodCastFile;
import com.morty.podcast.writer.file.PodCastFileNameFormatter;
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

        PodCastFileNameFormatter fnf = new PodCastFileNameFormatter();

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
