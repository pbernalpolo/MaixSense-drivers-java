package a010;



/**
 * Represents entities that will consume images from a queue.
 * <p>
 * {@link MaixSenseA010ImageConsumer} is used to implement the Producer-Consumer Pattern.
 */
public interface MaixSenseA010ImageConsumer
{
    ////////////////////////////////////////////////////////////////
    // PUBLIC ABSTRACT METHODS
    ////////////////////////////////////////////////////////////////
    
    /**
     * Consumes the {@link MaixSenseA010Image} polled from the queue.
     * 
     * @param image     {@link MaixSenseA010Image} to be consumed.
     */
    public void consumeImage( MaixSenseA010Image image );
    
}
