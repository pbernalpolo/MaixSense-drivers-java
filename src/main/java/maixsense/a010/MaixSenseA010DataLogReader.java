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
{
    ////////////////////////////////////////////////////////////////
    // PRIVATE VARIABLES
    ////////////////////////////////////////////////////////////////
    
	private MaixSenseA010ImagePlainQueue imageQueue;
	
    /**
     * Used to detect new frames in the received bytes.
     * @see Runner#run()
     */
    private MaixSenseA010ImageReceptionFiniteStateMachine imageReceptionFiniteStateMachine;
    
    /**
     * Path to the file that contains the MaixSense-A010 data.
     */
    private String filePath;
    
    /**
     * {@link BufferedInputStream} used to read the file that contains the MaixSense-A010 data.
     */
    private BufferedInputStream bufferedInputStream;
    
    
    /**
     * Byte buffer used to read from the log.
     */
    private byte[] buffer = new byte[1024];
    
    
    
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
        this.imageQueue = new MaixSenseA010ImagePlainQueue();
        this.imageReceptionFiniteStateMachine = new MaixSenseA010ImageReceptionFiniteStateMachine( imageQueue );
    }
    
    
    
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
        this.bufferedInputStream = new BufferedInputStream( new FileInputStream( this.filePath ) );
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
     * Returns the next {@link MaixSenseA010Image} in the log file.
     * 
     * @return	next {@link MaixSenseA010Image} in the log file.
     * @throws IOException	if an I/O error occurs.
     */
    public MaixSenseA010Image nextImage() throws IOException
    {
    	while( this.imageQueue.size() < 1 ) {
            int bytesRead = this.bufferedInputStream.read( buffer );
            if( bytesRead == -1 ) {
            	return null;
            }
        	this.imageReceptionFiniteStateMachine.update( buffer , 0 , bytesRead );
        }
    	return this.imageQueue.poll();
    }
    
}
