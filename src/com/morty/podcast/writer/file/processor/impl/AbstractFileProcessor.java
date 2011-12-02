// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.

package com.morty.podcast.writer.file.processor.impl;

import com.morty.podcast.writer.file.PodCastFile;
import com.morty.podcast.writer.file.processor.PodCastFileProcessor;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base class...
 * @author amorton
 */
public abstract class AbstractFileProcessor implements PodCastFileProcessor
{
    protected static final Log m_logger = LogFactory.getLog(AbstractFileProcessor.class);

    private Set m_validTypes;

    public void setValidTypes(Set vt)
    {
        m_validTypes = vt;
    }


    @Override
    public void process(PodCastFile file) throws Exception
    {
        if(m_validTypes == null || m_validTypes.contains(file.getSuffix()) )
            processComponent(file);
        else
            m_logger.info("File is not a valid type ["+file.getAbsolutePath()+"]");
        
    }

    @Override
    public abstract String getName();

    //Every sub class will process...
    public abstract void processComponent(PodCastFile file);
    
}
