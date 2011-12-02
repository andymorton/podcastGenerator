// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package com.morty.podcast.writer.file.processor.test;

import com.morty.podcast.writer.file.PodCastFile;
import com.morty.podcast.writer.file.PodCastFileNameResolver;
import com.morty.podcast.writer.file.processor.PodCastFileProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * This allows a processor to be tested.
 * @author amorton
 */
public class FileProcessorTester
{

    private static final Log m_logger = LogFactory.getLog(FileProcessorTester.class);
    private static PodCastFileNameResolver m_fileResolver = new PodCastFileNameResolver();

    public static void main(String[] args)
    {
        m_logger.info("Starting processor testing");
        try
        {
            String contextFile = args[0];
            String processor = args[1];
            String file = args[2];
            
            m_logger.info("Loading ["+processor+"] from ["+contextFile+"] to process file ["+file+"]");
            
            
            final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(contextFile);
            final PodCastFileProcessor pfp = (PodCastFileProcessor) ctx.getBean(processor);

            //Open the file
            PodCastFile fileToProcess = new PodCastFile(file);
            m_fileResolver.formatFile(fileToProcess);

            m_logger.info("About to process");
            pfp.process(fileToProcess);
            
            
        }
        catch(Exception e)
        {
            m_logger.error("Problem in testing",e);
        }
        
        
        m_logger.info("Finished testing");
        
    }


}
