package maixsense.a010;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;



/**
 * Implements functionality to read and process MaixSense-A010 data from a file.
 * <p>
 * This class implements the Context in the Strategy Pattern, and the strategy is defined by a {@link MaixSenseA010DataProcessingStrategy}.
 * 
 * @see MaixSenseA010DataProcessingStrategy
 * @see MaixSenseA010DataLoggerStrategy
 */
public class MaixSenseA010DataLogReader
    implements MaixSenseA010DataSource
{
    ////////////////////////////////////////////////////////////////
    // PRIVATE VARIABLES
    ////////////////////////////////////////////////////////////////
    
    /**
     * {@link MaixSenseA010DataProcessingStrategy} used to process the data read from the file.
     */
    private MaixSenseA010DataProcessingStrategy dataProcessingStrategy;
    
    /**
     * Used to detect new frames in the received bytes.
     * @see Runner#run()
     */
    private MaixSenseA010ImageReceptionFiniteStateMachine imageReceptionFiniteStateMachine;
    
    /**
     * {@link Runner} that holds the implementation.
     */
    private Runner runner;
    
    /**
     * Path to the file that contains the MaixSense-A010 data.
     */
    private String filePath;
    
    /**
     * Holds the period configured through the {@link #setReadingSpeedFps(double)}, or -1 if {@link #setReadingSpeedMaximum()} was called last.
     */
    private int readingPeriodMilliseconds;
    
    /**
     * True if {@link #initialize()} was last called; false if {@link #terminate()} was last called.
     */
    private boolean keepRunning;
    
    
    
    ////////////////////////////////////////////////////////////////
    // PUBLIC CONSTRUCTORS
    ////////////////////////////////////////////////////////////////
    
    /**
     * Constructs a {@link MaixSenseA010DataLogReader}.
     * 
     * @param filePath  path to the file where the MaixSense-A010 data is stored.
     */
    public MaixSenseA010DataLogReader( String filePath )
    {
        this.filePath = filePath;
        MaixSenseA010ImageQueue imageQueue = new MaixSenseA010ImageQueue();
        this.imageReceptionFiniteStateMachine = new MaixSenseA010ImageReceptionFiniteStateMachine( imageQueue );
        this.runner = new Runner();
        imageQueue.addListener( this.runner );
        this.keepRunning = false;
    }
    
    
    
    ////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ////////////////////////////////////////////////////////////////
    
    /**
     * Sets the data reading speed.
     * 
     * @param framesPerSecond   frames per second that define the data reading speed.
     */
    public void setReadingSpeedFps( double framesPerSecond )
    {
        this.readingPeriodMilliseconds = (int)( 1.0/framesPerSecond * 1.0e3 );
    }
    
    
    /**
     * Sets the data reading speed to the maximum.
     */
    public void setReadingSpeedMaximum()
    {
        this.readingPeriodMilliseconds = -1;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void setDataProcessingStrategy( MaixSenseA010DataProcessingStrategy maixSenseA010DataProcessingStrategy )
    {
        this.dataProcessingStrategy = maixSenseA010DataProcessingStrategy;
    }
    
    
    /**
     * {@inheritDoc}
     * 
     * @throws FileNotFoundException    if the file that contains the MaixSense-A010 data is not found.
     */
    public void initialize()
            throws FileNotFoundException
    {
        this.runner.initialize();
        this.keepRunning = true;
        Thread thread = new Thread( this.runner );
        thread.start();
    }
    
    
    /**
     * {@inheritDoc}
     * 
     * @throws IOException  if failed to close the file that contains the MaixSense-A010 data.
     */
    public void terminate()
            throws IOException
    {
        this.keepRunning = false;
        this.runner.terminate();
    }
    
    
    
    ////////////////////////////////////////////////////////////////
    // PRIVATE CLASSES
    ////////////////////////////////////////////////////////////////
    
    /**
     * Implements functionality needed by {@link MaixSenseA010DataLogReader}.
     * <p>
     * This class acts as a Proxy, hiding the implementation from the user.
     * In particular, it hides the methods {@link #run()} and {@link #consumeImage(MaixSenseA010Image)} so that the user doesn't mess with them.
     */
    private class Runner
        implements
            Runnable,
            MaixSenseA010ImageConsumer
    {
        ////////////////////////////////////////////////////////////////
        // PRIVATE VARIABLES
        ////////////////////////////////////////////////////////////////
        
        /**
         * {@link BufferedInputStream} used to read the file that contains the MaixSense-A010 data.
         */
        private BufferedInputStream bufferedInputStream;
        
        /**
         * True if we need to pause to accomplish the configured frames per second.
         * 
         * @see MaixSenseA010DataLogReader#setReadingSpeedFps(double)
         * @see #run()
         */
        private boolean pause;
        
        
        
        ////////////////////////////////////////////////////////////////
        // PUBLIC METHODS
        ////////////////////////////////////////////////////////////////
        
        /**
         * Initializes {@link #bufferedInputStream} using the specified {@link MaixSenseA010DataLogReader#filePath}.
         * 
         * @throws FileNotFoundException    if the file containing the MaixSense-A010 data is not found.
         */
        public void initialize()
                throws FileNotFoundException
        {
            this.bufferedInputStream = new BufferedInputStream( new FileInputStream( filePath ) );
        }
        
        
        /**
         * Closes {@link #bufferedInputStream}.
         * 
         * @throws IOException  if failed to close {@link #bufferedInputStream}.
         */
        public void terminate()
                throws IOException
        {
            this.bufferedInputStream.close();
        }
        
        
        /**
         * Runs in a {@link Thread}, reading the MaixSense-A010 data, and processing it using {@link MaixSenseA010DataLogReader#dataProcessingStrategy}.
         * <p>
         * It also implements pauses to accomplish the frames per second defined by {@link MaixSenseA010DataLogReader#readingPeriodMilliseconds}.
         */
        public void run()
        {
            try {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while(  keepRunning  &&  (bytesRead = this.bufferedInputStream.read(buffer) ) != -1  ) {
                    dataProcessingStrategy.process( buffer , 0 , bytesRead );
                    if( readingPeriodMilliseconds < 0 ) {
                        continue;
                    }
                    imageReceptionFiniteStateMachine.update( buffer , 0 , bytesRead );
                    if( !pause ) {
                        continue;
                    }
                    try {
                        Thread.sleep( readingPeriodMilliseconds );
                    } catch( InterruptedException e ) {
                        e.printStackTrace();
                    }
                    pause = false;
                }
            } catch( IOException e ) {
                e.printStackTrace();
            }
        }
        
        
        /**
         * {@inheritDoc}
         * 
         * Each time a new image is received we need to pause.
         */
        public void consumeImage( MaixSenseA010Image image )
        {
            this.pause = true;
        }
        
    }
    
}
