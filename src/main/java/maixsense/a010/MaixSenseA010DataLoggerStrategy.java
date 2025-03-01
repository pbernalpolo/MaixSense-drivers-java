package maixsense.a010;


import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;



/**
 * Implements {@link MaixSenseA010DataProcessingStrategy} to store MaixSense-A010 data into a file.
 */
public class MaixSenseA010DataLoggerStrategy
    implements MaixSenseA010DataProcessingStrategy
{
    ////////////////////////////////////////////////////////////////
    // PRIVATE VARIABLES
    ////////////////////////////////////////////////////////////////
    
    /**
     * {@link BufferedOutputStream} used to write the bytes into a file.
     */
    private BufferedOutputStream bufferedOutputStream;
    
    
    
    ////////////////////////////////////////////////////////////////
    // PUBLIC CONSTRUCTORS
    ////////////////////////////////////////////////////////////////
    
    /**
     * Constructs a {@link MaixSenseA010DataLoggerStrategy}.
     * 
     * @param filePath    path to the file where the MaixSense-A010 data will be stored.
     */
    public MaixSenseA010DataLoggerStrategy( String filePath )
            throws IOException
    {
        this.bufferedOutputStream = new BufferedOutputStream( new FileOutputStream( filePath ) );
    }
    
    
    
    ////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ////////////////////////////////////////////////////////////////
    
    /**
     * {@inheritDoc}
     */
    public void process( byte[] buffer , int indexStart , int indexStop )
    {
        try {
            this.bufferedOutputStream.write( buffer , indexStart , indexStop-indexStart );
            //this.bufferedOutputStream.flush();
        } catch( IOException e ) {
            e.printStackTrace();
        }
    }
    
}
