package maixsense.a010;



/**
 * Implements {@link MaixSenseA010DataProcessingStrategy} to turn received bytes into {@link MaixSenseA010Image}s and enqueue them in a {@link MaixSenseA010ImageQueue}.
 * <p>
 * It is a strategy in the context of the Strategy Pattern.
 */
public class MaixSenseA010ImageEnqueuerStrategy
    implements MaixSenseA010DataProcessingStrategy
{
    ////////////////////////////////////////////////////////////////
    // PRIVATE VARIABLES
    ////////////////////////////////////////////////////////////////
    
    /**
     * Turns received bytes into {@link MaixSenseA010Image}s that are enqueued in a {@link MaixSenseA010ImageQueue}.
     */
    private MaixSenseA010ImageReceptionFiniteStateMachine imageReceptionFiniteStateMachine;
    
    
    
    ////////////////////////////////////////////////////////////////
    // PUBLIC CONSTRUCTORS
    ////////////////////////////////////////////////////////////////
    
    /**
     * Constructs a {@link MaixSenseA010ImageEnqueuerStrategy}.
     * 
     * @param maixSenseA010ImageQueue   {@link MaixSenseA010ImageQueue} to which the {@link MaixSenseA010Image}s will be enqueued.
     */
    public MaixSenseA010ImageEnqueuerStrategy( MaixSenseA010ImageQueue maixSenseA010ImageQueue )
    {
        this.imageReceptionFiniteStateMachine = new MaixSenseA010ImageReceptionFiniteStateMachine( maixSenseA010ImageQueue );
    }
    
    
    
    ////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ////////////////////////////////////////////////////////////////
    
    /**
     * {@inheritDoc}
     */
    public void process( byte[] buffer , int indexStart , int indexStop )
    {
        this.imageReceptionFiniteStateMachine.update( buffer , indexStart , indexStop );
    }
    
}
