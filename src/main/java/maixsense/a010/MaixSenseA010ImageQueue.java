package maixsense.a010;



/**
 * Represents a queue to which {@link MaixSenseA010Image}s can be added.
 */
public interface MaixSenseA010ImageQueue
{
    ////////////////////////////////////////////////////////////////
    // PUBLIC ABSTRACT METHODS
    ////////////////////////////////////////////////////////////////
    
    /**
     * Adds a new {@link MaixSenseA010Image} to the queue.
     * 
     * @param image     {@link MaixSenseA010Image} to be added to the queue.
     */
    public void add( MaixSenseA010Image image );
    
}
