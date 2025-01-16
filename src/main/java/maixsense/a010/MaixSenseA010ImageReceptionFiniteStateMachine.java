package maixsense.a010;



/**
 * Manages the reception of images from a MaixSense-A010 ToF camera.
 * <p>
 * The received images are enqueued in a {@link MaixSenseA010ImageQueue} if it has been previously connected.
 * The only way for a user to access such images is:
 * <ul>
 *  <li> Implementing the {@link MaixSenseA010ImageConsumer} interface,
 *  <li> adding the {@link MaixSenseA010ImageConsumer} as a listener in a {@link MaixSenseA010ImageQueue}, and
 *  <li> connecting the {@link MaixSenseA010ImageQueue} to this class.
 * </ul>
 * <p>
 * The image consumption is implemented using the producer-consumer design pattern.
 * 
 * @see <a href>https://wiki.sipeed.com/hardware/en/maixsense/maixsense-a010/maixsense-a010.html</a>
 * @see <a href>https://wiki.sipeed.com/hardware/en/maixsense/maixsense-a010/at_command_en.html</a>
 * @see <a href>https://github.com/sipeed/MetaSense-ComTool</a>
 */
public class MaixSenseA010ImageReceptionFiniteStateMachine
{
    ////////////////////////////////////////////////////////////////
    // PRIVATE CONSTANTS
    ////////////////////////////////////////////////////////////////
    
    /**
     * Minimum {@link #pixels} length value.
     */
    private static final int PIXELS_LENGTH_MIN = 25 * 25;
    
    /**
     * Maximum {@link #pixels} length value.
     */
    private static final int PIXELS_LENGTH_MAX = 100 * 100;
    
    /**
     * Represents the state of the finite-state machine in which we are receiving the first header byte.
     */
    private static final int RECEIVING_FIRST_HEADER_BYTE_STATE = 1;
    
    /**
     * Represents the state of the finite-state machine in which we are receiving the second header byte.
     */
    private static final int RECEIVING_SECOND_HEADER_BYTE_STATE = 2;
    
    /**
     * Represents the state of the finite-state machine in which we are receiving the packet length.
     */
    private static final int RECEIVING_PACKET_LENGTH_STATE = 3;
    
    /**
     * Represents the state of the finite-state machine in which we are receiving the packet info byte.
     */
    private static final int RECEIVING_PACKET_INFO_STATE = 4;
    
    /**
     * Represents the state of the finite-state machine in which we are receiving the packet pixels.
     */
    private static final int RECEIVING_PACKET_PIXELS_STATE = 5;
    
    /**
     * Represents the state of the finite-state machine in which we are receiving the checksum.
     */
    private static final int RECEIVING_CHECKSUM_STATE = 6;
    
    /**
     * Represents the state of the finite-state machine in which we are receiving the tail byte.
     */
    private static final int RECEIVING_TAIL_BYTE_STATE = 0;
    
    /**
     * 2 bytes required to compose the packet length.
     * @see #receivePacketLengthBehavior(byte[], int, int)
     */
    private static final int BYTES_NEEDED_FOR_PACKET_LENGTH = 2;
    
    /**
     * 16 bytes required to build the packet info.
     * @see #receivePacketInfoBehavior(byte[], int, int)
     */
    private static final int BYTES_NEEDED_FOR_PACKET_INFO = 16;
    
    
    
    ////////////////////////////////////////////////////////////////
    // PRIVATE VARIABLES
    ////////////////////////////////////////////////////////////////
    
    /**
     * {@link MaixSenseA010ImageQueue} used to queue the {@link MaixSenseA010Image}s.
     */
    private MaixSenseA010ImageQueue queue;
    
    /**
     * Byte array used to store the received packet length.
     */
    private byte[] lengthBytes = new byte[ BYTES_NEEDED_FOR_PACKET_LENGTH ];
    
    /**
     * Byte array used to store the received packet info.
     */
    private byte[] packetInfoBytes = new byte[ BYTES_NEEDED_FOR_PACKET_INFO ];
    
    /**
     * Byte array used to store the values of the pixels received in the last image packet info.
     */
    private byte[] pixels;
    
    /**
     * State of the finite-state machine used to manage the image packet reception.
     * <p>
     * <ul>
     *  <li> 0: receiving first header byte.
     *  <li> 1: receiving second header byte.
     *  <li> 2: receiving packet length.
     *  <li> 3: receiving packet info.
     *  <li> 4: receiving packet pixels.
     *  <li> 5: receiving checksum.
     *  <li> 6: receiving tail byte.
     * </ul>
     */
    private int receivingState;
    
    /**
     * Counter used to keep track of the received bytes.
     */
    private int byteCounter;
    
    /**
     * Exposure time received in the last image packet info.
     */
    private int exposureTimeReceived;
    
    /**
     * Frame Id received in the last image packet info.
     */
    private short frameIdReceived;
    
    //private byte commandReceived;
    //private byte outputModeReceived;
    
    /**
     * ToF sensor temperature received in the last image packet info.
     */
    private byte sensorTemperatureReceived;
    
    /**
     * Driver temperature received in the last image packet info.
     */
    private byte driverTemperatureReceived;
    
    /**
     * Error code received in the last image packet info.
     */
    private byte errorCodeReceived;
    
    //private byte reserved1ByteReceived;
    
    /**
     * Rows of the image received in the last image packet info.
     */
    private byte rowsReceived;
    
    /**
     * Columns of the image received in the last image packet info.
     */
    private byte colsReceived;
    
    //private byte ispVersionReceived;
    //private byte reserved3ByteReceived;
    
    /**
     * Checksum received in the last image packet.
     */
    private byte checksumComputed;
    
    
    
    ////////////////////////////////////////////////////////////////
    // PUBLIC CONSTRUCTORS
    ////////////////////////////////////////////////////////////////
    
    /**
     * Constructs a {@link MaixSenseA010ImageReceptionFiniteStateMachine}.
     */
    public MaixSenseA010ImageReceptionFiniteStateMachine()
    {
        // Initialize receivingState.
        this.receivingState = 0;
    }
    
    
    
    ////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ////////////////////////////////////////////////////////////////
    
    /**
     * Connects the queue in which the received images are stored.
     * 
     * @param imageQueue    queue in which the received images are stored.
     */
    public void connectQueue( MaixSenseA010ImageQueue imageQueue )
    {
        this.queue = imageQueue;
        this.queue.start();
    }
    
    
    /**
     * Updates the the state of the finite-state machine that manages the image packet reception.
     * <p>
     * The update is performed based on the sequence of bytes contained in the byte buffer, and the index interval defined by the inputs.
     * All bytes accessed with indices in the interval ({@code indexStart}, {@code indexStart+1}, ... , {@code indexStop-1}) will be processed.
     * 
     * @param buffer    byte buffer that contains the image data.
     * @param indexStart    index of the first byte to be processed.
     * @param indexStop     index of the byte at which processing must stop. The method will stop processing bytes before reaching this byte.
     * 
     * @throws IllegalArgumentException if {@code indexStop} is not greater than {@code indexStart}.
     */
    public void update( byte[] buffer , int indexStart , int indexStop )
    {
        if( indexStop <= indexStart ) {
            throw new IllegalArgumentException( "[" + this.getClass().getSimpleName() + "] indexStop must be greater than indexStart." );
        }
        while( indexStart < indexStop ) {
            int processedBytes = this.updateState( buffer , indexStart , indexStop );
            indexStart += processedBytes;
        }
    }
    
    
    
    ////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ////////////////////////////////////////////////////////////////
    
    /**
     * Updates the the state of the finite-state machine that manages the image packet reception.
     * <p>
     * The update is performed based on the sequence of bytes contained in the byte buffer, and the index interval defined by the inputs.
     * Although the inputs define the index range of the bytes that could be processed ({@code indexStart}, {@code indexStart+1}, ... , {@code indexStop-1}), the processing can stop before reaching {@code indexStop-1}.
     * The output of this method is the number of bytes finally processed in the current method call.
     * 
     * @param buffer    byte buffer that contains the image data.
     * @param indexStart    index of the first byte to be processed.
     * @param indexStop     index of the byte at which processing must stop. The method could stop processing bytes before reaching this byte.
     * @return  number of bytes finally processed in the current method call.
     */
    private int updateState( byte[] buffer , int indexStart , int indexStop )
    {
        switch( this.receivingState ) {
            case RECEIVING_FIRST_HEADER_BYTE_STATE:
                return this.receiveFirstHeaderByteBehavior( buffer , indexStart , indexStop );
            case RECEIVING_SECOND_HEADER_BYTE_STATE:
                return this.receiveSecondHeaderByteBehavior( buffer , indexStart , indexStop );
            case RECEIVING_PACKET_LENGTH_STATE:
                return this.receivePacketLengthBehavior( buffer , indexStart , indexStop );
            case RECEIVING_PACKET_INFO_STATE:
                return this.receivePacketInfoBehavior( buffer , indexStart , indexStop );
            case RECEIVING_PACKET_PIXELS_STATE:
                return this.receivePixelsBehavior( buffer , indexStart , indexStop );
            case RECEIVING_CHECKSUM_STATE:
                return this.receiveChecksumBehavior( buffer , indexStart , indexStop );
            case RECEIVING_TAIL_BYTE_STATE:
                return this.receiveTailByteBehavior( buffer , indexStart , indexStop );
            default:
                // If we are in some other state, go to receive tail byte.
                this.receivingState = RECEIVING_TAIL_BYTE_STATE;
                // Return the number of processed bytes.
                return 0;
        }
    }
    
    
    /**
     * Implements behavior of finite-state machine when first header byte is being received.
     * 
     * @param buffer    byte buffer that contains the image data.
     * @param indexStart    index of the first byte to be processed.
     * @param indexStop     index of the byte at which processing must stop. The method could stop processing bytes before reaching this byte.
     * @return  number of bytes finally processed in the current method call.
     * 
     * @see #updateState(byte[], int, int)
     */
    private int receiveFirstHeaderByteBehavior( byte[] buffer , int indexStart , int indexStop )
    {
        // Get next byte.
        byte nextByte = buffer[ indexStart ];
        // If the received byte is 0x00, then we go to receive second header byte.
        if( nextByte == (byte)0x00 ) {
            // Go to receive second header byte.
            this.receivingState = RECEIVING_SECOND_HEADER_BYTE_STATE;
        } else {
            // If not, fall back to receive tail byte.
            this.receivingState = RECEIVING_TAIL_BYTE_STATE;
        }
        // Return the number of processed bytes.
        return 1;
    }
    
    
    /**
     * Implements behavior of finite-state machine when second header byte is being received.
     * 
     * @param buffer    byte buffer that contains the image data.
     * @param indexStart    index of the first byte to be processed.
     * @param indexStop     index of the byte at which processing must stop. The method could stop processing bytes before reaching this byte.
     * @return  number of bytes finally processed in the current method call.
     * 
     * @see #updateState(byte[], int, int)
     */
    private int receiveSecondHeaderByteBehavior( byte[] buffer , int indexStart , int indexStop )
    {
        // Get next byte.
        byte nextByte = buffer[ indexStart ];
        // If the received byte is 0xFF, then we go to receive packet length.
        if( nextByte == (byte)0xFF ) {
            // Initialize checksum.
            this.checksumComputed = (byte)0xFF;
            // Reset byte counter.
            this.byteCounter = 0;
            // Go to receive packet length.
            this.receivingState = RECEIVING_PACKET_LENGTH_STATE;
        } else {
            // If not 0xFF, go back to receive tail byte.
            this.receivingState = RECEIVING_TAIL_BYTE_STATE;
        }
        // Return the number of processed bytes.
        return 1;
    }
    
    
    /**
     * Implements behavior of finite-state machine when packet length is being received.
     * 
     * @param buffer    byte buffer that contains the image data.
     * @param indexStart    index of the first byte to be processed.
     * @param indexStop     index of the byte at which processing must stop. The method could stop processing bytes before reaching this byte.
     * @return  number of bytes finally processed in the current method call.
     * 
     * @see #updateState(byte[], int, int)
     */
    private int receivePacketLengthBehavior( byte[] buffer , int indexStart , int indexStop )
    {
        // Fill buffer with available bytes, or until the bytes required to compose the packet length.
        int lengthOfCopy = Math.min(  BYTES_NEEDED_FOR_PACKET_LENGTH - this.byteCounter  ,  indexStop - indexStart  );
        System.arraycopy( buffer , indexStart , this.lengthBytes , this.byteCounter , lengthOfCopy );
        this.byteCounter += lengthOfCopy;
        // Return if we still don't have the bytes needed to compose the packet length.
        if( this.byteCounter < BYTES_NEEDED_FOR_PACKET_LENGTH ) {
            return lengthOfCopy;
        }
        // Build packet length from them.
        int receivedLength = ( ( this.lengthBytes[1] << 8 ) | ( this.lengthBytes[0] & 0xFF ) );
        // Compute number of pixels to receive.
        int pixelsLength = receivedLength - BYTES_NEEDED_FOR_PACKET_INFO;
        // Check that pixelsLength is within bounds.
        if(  pixelsLength < PIXELS_LENGTH_MIN  ||  PIXELS_LENGTH_MAX < pixelsLength  ) {
            // If pixelsLength is not in bounds, go to receive tail byte.
            this.receivingState = RECEIVING_TAIL_BYTE_STATE;
            return lengthOfCopy;
        }
        // Update pixels variable if necessary.
        if(  this.pixels == null  ||  this.pixels.length != pixelsLength  ) {
            this.pixels = new byte[ pixelsLength ];
        }
        // Update checksum.
        this.checksumComputed += this.lengthBytes[0];
        this.checksumComputed += this.lengthBytes[1];
        // Reset byte counter.
        this.byteCounter = 0;
        // And go to receive the packet info.
        this.receivingState = RECEIVING_PACKET_INFO_STATE;
        return lengthOfCopy;
    }
    
    
    /**
     * Implements behavior of finite-state machine when packet info is being received.
     * 
     * @param buffer    byte buffer that contains the image data.
     * @param indexStart    index of the first byte to be processed.
     * @param indexStop     index of the byte at which processing must stop. The method could stop processing bytes before reaching this byte.
     * @return  number of bytes finally processed in the current method call.
     * 
     * @see #updateState(byte[], int, int)
     */
    private int receivePacketInfoBehavior( byte[] buffer , int indexStart , int indexStop )
    {
        // Fill buffer with available bytes, or until the bytes required to compose the packet info.
        int lengthOfCopy = Math.min(  BYTES_NEEDED_FOR_PACKET_INFO - this.byteCounter  ,  indexStop - indexStart  );
        System.arraycopy( buffer , indexStart , this.packetInfoBytes , this.byteCounter , lengthOfCopy );
        this.byteCounter += lengthOfCopy;
        // Return if we still don't have the bytes needed to compose the packet info.
        if( this.byteCounter < BYTES_NEEDED_FOR_PACKET_INFO ) {
            return lengthOfCopy;
        }
        // Extract the info according to the documentation.
        //this.commandReceived = this.packetInfoBytes[0];
        //this.outputModeReceived = this.packetInfoBytes[1];
        this.sensorTemperatureReceived = this.packetInfoBytes[2];
        this.driverTemperatureReceived = this.packetInfoBytes[3];
        this.exposureTimeReceived = (
                ( this.packetInfoBytes[7] << 24 ) |
                ( (this.packetInfoBytes[6] & 0xFF) << 16 ) |
                ( (this.packetInfoBytes[5] & 0xFF) << 8 ) |
                (this.packetInfoBytes[4] & 0xFF) );
        this.errorCodeReceived = this.packetInfoBytes[8];
        //this.reserved1ByteReceived = this.packetInfoBytes[9];
        this.rowsReceived = this.packetInfoBytes[10];
        this.colsReceived = this.packetInfoBytes[11];
        short frameIdNew = (short)( ( this.packetInfoBytes[13] << 8 ) | (this.packetInfoBytes[12] & 0xFF) );
        // Sometimes same image is received multiple times;
        // if this happens, we save resources by ignoring the rest of the package.
        if( frameIdNew != this.frameIdReceived ) {
            this.frameIdReceived = frameIdNew;
        } else {
            this.receivingState = RECEIVING_TAIL_BYTE_STATE;
            return lengthOfCopy;
        }
        //this.ispVersionReceived = this.packetInfoBytes[14];
        //this.reserved3ByteReceived = this.packetInfoBytes[15];
        // Update checksum.
        for( int i=0; i<BYTES_NEEDED_FOR_PACKET_INFO; i++ ) {
            this.checksumComputed += this.packetInfoBytes[i];
        }
        // Reset byte counter.
        this.byteCounter = 0;
        // And go to receive pixels.
        this.receivingState = RECEIVING_PACKET_PIXELS_STATE;
        return lengthOfCopy;
    }
    
    
    /**
     * Implements behavior of finite-state machine when packet pixels are being received.
     * 
     * @param buffer    byte buffer that contains the image data.
     * @param indexStart    index of the first byte to be processed.
     * @param indexStop     index of the byte at which processing must stop. The method could stop processing bytes before reaching this byte.
     * @return  number of bytes finally processed in the current method call.
     * 
     * @see #updateState(byte[], int, int)
     */
    private int receivePixelsBehavior( byte[] buffer , int indexStart , int indexStop )
    {
        // Fill buffer with available bytes, or until the bytes required to compose the packet info.
        int lengthOfCopy = Math.min(  this.pixels.length - this.byteCounter  ,  indexStop - indexStart  );
        System.arraycopy( buffer , indexStart , this.pixels , this.byteCounter , lengthOfCopy );
        this.byteCounter += lengthOfCopy;
        // Update the checksum.
        for( int i=this.byteCounter-lengthOfCopy; i<this.byteCounter; i++ ) {
            this.checksumComputed += this.pixels[ i ];
        }
        // If we completed the pixels, go to receive the checksum.
        if( this.byteCounter >= this.pixels.length ) {
            this.receivingState = RECEIVING_CHECKSUM_STATE;
        }
        // Return the number of processed bytes.
        return lengthOfCopy;
    }
    
    
    /**
     * Implements behavior of finite-state machine when checksum is being received.
     * 
     * @param buffer    byte buffer that contains the image data.
     * @param indexStart    index of the first byte to be processed.
     * @param indexStop     index of the byte at which processing must stop. The method could stop processing bytes before reaching this byte.
     * @return  number of bytes finally processed in the current method call.
     * 
     * @see #updateState(byte[], int, int)
     */
    private int receiveChecksumBehavior( byte[] buffer , int indexStart , int indexStop )
    {
        // Get next byte.
        byte nextByte = buffer[ indexStart ];
        // If the checksum computed matches the received byte,
        if( this.checksumComputed == nextByte ) {
            // create image and add it to the queue (if connected),
            if( this.queue != null ) {
                MaixSenseA010Image image = this.getImageCurrent();
                this.queue.add( image );
            }
        }
        // Whatever the result of the checksum, we go to receive the tail byte.
        this.receivingState = RECEIVING_TAIL_BYTE_STATE;
        // Return the number of processed bytes.
        return 1;
    }
    
    
    /**
     * Implements behavior of finite-state machine when tail byte is being received.
     * 
     * @param buffer    byte buffer that contains the image data.
     * @param indexStart    index of the first byte to be processed.
     * @param indexStop     index of the byte at which processing must stop. The method could stop processing bytes before reaching this byte.
     * @return  number of bytes finally processed in the current method call.
     * 
     * @see #updateState(byte[], int, int)
     */
    private int receiveTailByteBehavior( byte[] buffer , int indexStart , int indexStop )
    {
        for( int i=indexStart; i<indexStop; i++ ) {
            // Get next byte.
            byte nextByte = buffer[ i ];
            // If the received byte is 0xDD, go to receive first header byte.
            if( nextByte == (byte)0xDD ) {
                this.receivingState = RECEIVING_FIRST_HEADER_BYTE_STATE;
                return ( i - indexStart + 1 );
            }
        }
        // Return the number of processed bytes.
        return ( indexStop - indexStart );
    }
    
    
    /**
     * Creates image from last image packet.
     * 
     * @return  image from last image packet.
     */
    private MaixSenseA010Image getImageCurrent()
    {
        MaixSenseA010Image output = new MaixSenseA010Image( this.rowsReceived , this.colsReceived , this.pixels.clone() );
        output.setFrameId( this.frameIdReceived );
        output.setExposureTime( this.exposureTimeReceived );
        output.setSensorTemperature( this.sensorTemperatureReceived );
        output.setDriverTemperature( this.driverTemperatureReceived );
        output.setErrorCode( this.errorCodeReceived );
        return output;
    }
    
}
