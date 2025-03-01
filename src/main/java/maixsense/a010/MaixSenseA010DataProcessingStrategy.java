package maixsense.a010;



/**
 * Represents strategies used to process the byte stream produced by the MaixSense-A010 TOF camera.
 * <p>
 * {@link MaixSenseA010DataProcessingStrategy} represents the strategy in the Strategy Pattern.
 */
public interface MaixSenseA010DataProcessingStrategy
{
    ////////////////////////////////////////////////////////////////
    // PUBLIC ABSTRACT METHODS
    ////////////////////////////////////////////////////////////////
    
    /**
     * Processes a sequence of bytes produced by the MaixSense-A010.
     * 
     * @param buffer    byte buffer that contains the image data.
     * @param indexStart    index of the first byte to be processed.
     * @param indexStop     index of the byte at which processing must stop. The method will stop processing bytes before reaching this byte.
     */
    public void process( byte[] buffer , int indexStart , int indexStop );
    
}
