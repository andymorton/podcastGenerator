// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.

package com.morty.podcast.writer.validation;

import java.io.File;
import java.util.Set;

/**
 * Interface for folder validators
 * @author amorton
 */
public interface FolderValidator
{

    void setFolderToProcess(File file);

    void setExcludedFolders(Set set);

    void setFailOnError(boolean fail);


    void process() throws Exception;
}
