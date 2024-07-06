package a010;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;



public class MaixSenseA010ImageQueue
    implements Runnable
{
    ////////////////////////////////////////////////////////////////
    // PRIVATE VARIABLES
    ////////////////////////////////////////////////////////////////
    private Queue<MaixSenseA010Image> queue;
    private List<MaixSenseA010ImageConsumer> listeners;
    private boolean keepRunning;
    
    
    
    ////////////////////////////////////////////////////////////////
    // PUBLIC CONSTRUCTORS
    ////////////////////////////////////////////////////////////////
    
    public MaixSenseA010ImageQueue()
    {
        this.queue = new ArrayDeque<MaixSenseA010Image>();
        this.listeners = new ArrayList<MaixSenseA010ImageConsumer>();
        this.keepRunning = false;
    }
    
    
    
    ////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ////////////////////////////////////////////////////////////////
    
    public void addListener( MaixSenseA010ImageConsumer consumer )
    {
        this.listeners.add( consumer );
    }
    
    
    public void removeListener( MaixSenseA010ImageConsumer consumer )
    {
        this.listeners.remove( consumer );
    }
    
    
    public void add( MaixSenseA010Image image )
    {
        this.queue.add( image );
        synchronized( this ) {
            this.notify();
        }
    }
    
    
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
    
    
    public synchronized void stop()
    {
        this.keepRunning = false;
    }
    
    
    public void run()
    {
        while( this.keepRunning ) {
            while( !this.queue.isEmpty() ) {
                MaixSenseA010Image image = this.queue.poll();
                for( MaixSenseA010ImageConsumer consumer : this.listeners ) {
                    consumer.onImageQueued( image );
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
