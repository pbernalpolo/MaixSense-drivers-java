package maixsense.a010;



/**
 * Represents a source of MaixSense-A010 data.
 * <p>
 * Classes implementing this interface must be the Context in the Strategy Pattern.
 * The strategy used to process the MaixSense-A010 data is provided by a {@link MaixSenseA010DataProcessingStrategy}.
 */
public interface MaixSenseA010DataSource
{
    ////////////////////////////////////////////////////////////////
    // PUBLIC ABSTRACT METHODS
    ////////////////////////////////////////////////////////////////
    
    /**
     * Sets the {@link MaixSenseA010DataProcessingStrategy} that defines the way in which MaixSense-A010 data is processed.
     * 
     * @param maixSenseA010DataProcessingStrategy   {@link MaixSenseA010DataProcessingStrategy} that defines the way in which MaixSense-A010 data is processed.
     */
    public void setDataProcessingStrategy( MaixSenseA010DataProcessingStrategy maixSenseA010DataProcessingStrategy );
    
    
    /**
     * Initializes the MaixSense-A010 data reception.
     * 
     * @throws Exception    if some problem is encountered.
     */
    public void initialize() throws Exception;
    
    
    /**
     * Terminates the MaixSense-A010 data reception.
     * 
     * @throws Exception    if some problem is encountered.
     */
    public void terminate() throws Exception;
    
}
