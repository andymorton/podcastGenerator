// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.

package com.morty.podcast.writer.file.processor;

import com.morty.podcast.writer.file.PodCastFile;

/**
 * Allows for files to be processed once they have been identified as valid
 * @author amorton
 */
public interface PodCastFileProcessor 
{
    void process(PodCastFile file) throws Exception;
    String getName();
}
