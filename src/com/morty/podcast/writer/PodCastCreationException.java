// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.

package com.morty.podcast.writer;

/**
 * Custom Exception for podcast Creation
 * @author amorton
 */
class PodCastCreationException extends Exception
{

    public PodCastCreationException()
    {
        super();
    }
    
    public PodCastCreationException(String msg)
    {
        super(msg);
    }
    
}
