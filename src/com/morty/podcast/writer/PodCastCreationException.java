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
