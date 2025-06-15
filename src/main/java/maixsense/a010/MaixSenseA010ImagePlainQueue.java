package maixsense.a010;


import java.util.ArrayDeque;
import java.util.Queue;



/**
 * Implements a queue to which {@link MaixSenseA010Image}s are added and polled.
 */
public class MaixSenseA010ImagePlainQueue
	implements MaixSenseA010ImageQueue
{
    ////////////////////////////////////////////////////////////////
    // PRIVATE VARIABLES
    ////////////////////////////////////////////////////////////////
    
    /**
     * Queue to which {@link MaixSenseA010Image}s are added.
     */
    private Queue<MaixSenseA010Image> queue;
    
    
    
    ////////////////////////////////////////////////////////////////
    // PUBLIC CONSTRUCTORS
    ////////////////////////////////////////////////////////////////
    
    /**
     * Constructs a {@link MaixSenseA010ImagePlainQueue}.
     */
    public MaixSenseA010ImagePlainQueue()
    {
        this.queue = new ArrayDeque<MaixSenseA010Image>();
    }
    
    
    
    ////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ////////////////////////////////////////////////////////////////
    
    /**
     * Adds a new {@link MaixSenseA010Image} to the queue.
     * 
     * @param image     {@link MaixSenseA010Image} to be added to the queue.
     */
    public void add( MaixSenseA010Image image )
    {
        this.queue.add( image );
    }
    
    
    /**
     * Retrieves and removes the head of this queue, or returns null if this queue is empty.
     * 
     * @return	{@link MaixSenseA010Image} at the head of the queue.
     */
    public MaixSenseA010Image poll()
    {
    	return this.queue.poll();
    }
    
    
    /**
     * Returns the current size of the queue.
     * 
     * @return  current size of the queue.
     */
    public int size()
    {
        return this.queue.size();
    }
    
}
