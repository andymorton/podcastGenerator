// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.

package com.morty.podcast.writer.validation;

import java.io.File;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This allows for the folder to be validated in terms
 * of structure and info file.
 *
 * The things it should check:
 *
 * 1) Are there modules?
 * 2) Is there a specific info.txt file for the podcast itself
 * 3) The modules - do they have a info.txt file?
 * 4) Do any of the filenames have spaces?
 *
 * @author amorton
 */
public abstract class AbstractFolderValidator {

    //We need to look at the folder, and include any exclusion sets
    protected static final Log m_logger = LogFactory.getLog(AbstractFolderValidator.class);
    protected File m_folderToProcess;
    protected Set m_excludedFolders;
    protected boolean m_failOnError = false;

    public void setFolderToProcess(File folder)
    {
        m_folderToProcess = folder;
    }

    public void setExcludedFolders(Set exc)
    {
        m_excludedFolders = exc;
    }

    public void setFailOnError(boolean fail)
    {
        m_failOnError = fail;
    }

    public abstract boolean process() throws Exception;

}
