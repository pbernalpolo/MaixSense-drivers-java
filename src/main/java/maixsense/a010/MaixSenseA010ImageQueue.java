package maixsense.a010;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;



/**
 * Implements a queue to which {@link MaixSenseA010Image}s are added and consumed by {@link MaixSenseA010ImageConsumer}s in parallel.
 * <p>
 * The producer-consumer design pattern is used.
 */
public class MaixSenseA010ImageQueue
    implements Runnable
{
    ////////////////////////////////////////////////////////////////
    // PRIVATE VARIABLES
    ////////////////////////////////////////////////////////////////
    
    /**
     * Queue to which {@link MaixSenseA010Image}s are added, and consumed by {@link MaixSenseA010ImageConsumer}s.
     */
    private Queue<MaixSenseA010Image> queue;
    
    /**
     * List of {@link MaixSenseA010ImageConsumer}s that consume the {@link MaixSenseA010Image}s.
     */
    private List<MaixSenseA010ImageConsumer> listeners;
    
    /**
     * True if the thread that consumes the queue must be kept running.
     */
    private boolean keepRunning;
    
    
    
    ////////////////////////////////////////////////////////////////
    // PUBLIC CONSTRUCTORS
    ////////////////////////////////////////////////////////////////
    
    /**
     * Constructs a {@link MaixSenseA010ImageQueue}.
     */
    public MaixSenseA010ImageQueue()
    {
        this.queue = new ArrayDeque<MaixSenseA010Image>();
        this.listeners = new ArrayList<MaixSenseA010ImageConsumer>();
        this.keepRunning = false;
    }
    
    
    
    ////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ////////////////////////////////////////////////////////////////
    
    /**
     * Adds a {@link MaixSenseA010ImageConsumer} to the list of {@link MaixSenseA010Image} consumers.
     * 
     * @param consumer  {@link MaixSenseA010ImageConsumer} to be added.
     */
    public void addListener( MaixSenseA010ImageConsumer consumer )
    {
        this.listeners.add( consumer );
    }
    
    
    /**
     * Removes a {@link MaixSenseA010ImageConsumer} from the list of {@link MaixSenseA010Image} consumers.
     * 
     * @param consumer  {@link MaixSenseA010ImageConsumer} to be removed.
     */
    public void removeListener( MaixSenseA010ImageConsumer consumer )
    {
        this.listeners.remove( consumer );
    }
    
    
    /**
     * Adds a new {@link MaixSenseA010Image} to the queue.
     * 
     * @param image     {@link MaixSenseA010Image} to be added to the queue.
     */
    public synchronized void add( MaixSenseA010Image image )
    {
        this.queue.add( image );
        this.notify();
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
    
    
    /**
     * Starts a thread in which the {@link MaixSenseA010Image}s will be consumed by the {@link MaixSenseA010ImageConsumer}s.
     */
    public synchronized void start()
    {
        if( !this.keepRunning ) {
            Thread thread = new Thread( this );
            this.keepRunning = true;
            thread.start();
        } else {
            throw new RuntimeException( "Tried to start, while already running." );
        }
    }
    
    
    /**
     * Stops the thread in which the {@link MaixSenseA010Image}s are consumed by the {@link MaixSenseA010ImageConsumer}s.
     */
    public synchronized void stop()
    {
        this.keepRunning = false;
        this.notify();
    }
    
    
    /**
     * Runs in a {@link Thread}, polling the {@link MaixSenseA010Image}s and consuming them by the {@link MaixSenseA010ImageConsumer}s.
     */
    public void run()
    {
        while( this.keepRunning ) {
            while( !this.queue.isEmpty() ) {
                synchronized( this ) {
                    MaixSenseA010Image image = this.queue.poll();
                    for( MaixSenseA010ImageConsumer consumer : this.listeners ) {
                        consumer.consumeImage( image );
                    }
                }
            }
            try {
                synchronized( this ) {
                    this.wait();
                }
            } catch( InterruptedException e ) {
                e.printStackTrace();
            }
        }
    }
    
}
