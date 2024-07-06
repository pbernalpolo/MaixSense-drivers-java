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
    
    public void onImageQueued( MaixSenseA010Image image );
    
}
