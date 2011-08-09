// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.

package com.morty.podcast.writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Main starting point. 
 * @author amorton
 */
public class PodCastCreationRunner
{

    private static final Log m_logger = LogFactory.getLog(PodCastCreationRunner.class);

    /**
     * This is the main runner for this application.
     * It takes the parameter of the spring context file to use,
     * then looks up the generator (which should be populated with the right
     * parameter values and in the classpath) and runs it.
     * @param spring context file to use
     */
    public static void main(String[] args)
    {
        try
        {
            //Get the spring bean, and run
            m_logger.info("Starting PodCast Generator");
            String springFile = args[0];
            m_logger.info("Using Spring File ["+springFile+"]");

            final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(springFile);
            final PodCastGenerator pcg = (PodCastGenerator) ctx.getBean(PodCastConstants.PODCAST_GENERATOR_BEAN);

            pcg.generatePodCast();
        }
        catch(Exception e)
        {
            m_logger.error("Unable to get bean from spring context. Please confirm file and bean details.",e);
        }
        
    }
}
