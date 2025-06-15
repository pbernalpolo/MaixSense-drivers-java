package maixsense.a010;


import jssc.SerialPortException;



/**
 * Example on how to process images received from the MaixSense-A010 ToF camera, and print them using ASCII art.
 */
public class MaixSenseA010AsciiArtImageViewer
    implements MaixSenseA010ImageConsumer
{
    ////////////////////////////////////////////////////////////////
    // PARAMETERS
    ////////////////////////////////////////////////////////////////
    
    /**
     * Quantization unit used for both the calibration and the driver.
     */
    static final int QUANTIZATION_UNIT = 0;
    
    /**
     * Defines box of pixels used to compute the gray scale value that will be mapped to a character.
     */
    static final int PIXELS_PER_ASCII_CHAR = 2;
    
    /**
     * {@link String} that defines the map between gray scale values ASCII characters.
     * (Choose one from the following 2 options).
     */
    // https://stackoverflow.com/questions/30097953/ascii-art-sorting-an-array-of-ascii-characters-by-brightness-levels-c-c
    //static final String pixelToChar = new StringBuilder( " `.-':_,^=;><+!rc*/z?sLTv)J7(|Fi{C}fI31tlu[neoZ5Yxjya]2ESwqkP6h9d4VpOGbUAKXHm8RD#$Bg0MNWQ%&@" ).toString();
    // https://github.com/ebenpack/laboratory/blob/master/ASCII/ASCII-grayscale-values.txt
    static final String pixelToChar = "@MBHENR#KWXDFPQASUZbdehx*8Gm&04LOVYkpq5Tagns69owz$CIu23Jcfry%1v7l+it[] {}?j|()=~!-/<>\\\"^_';,:`. ";
    
    
    
    
    ////////////////////////////////////////////////////////////////
    // MAIN: ENTRY POINT
    ////////////////////////////////////////////////////////////////

    /**
     * Entry point.
     * <p>
     * The argument passed to "main" must match the class name.
     * 
     * @param args  not used.
     */
    public static void main(String[] args)
    {
        MaixSenseA010AsciiArtImageViewer viewer = new MaixSenseA010AsciiArtImageViewer();
        
        // Create the image queue,
        MaixSenseA010ImagePublisherQueue imageQueue = new MaixSenseA010ImagePublisherQueue();
        // and add the listeners.
        imageQueue.addListener( viewer );
        
        // Create the MaixSense-A010 data processing strategy.
        MaixSenseA010ImageEnqueuerStrategy imageEnqueuer = new MaixSenseA010ImageEnqueuerStrategy( imageQueue );
        
        // Create the driver,
        MaixSenseA010Driver driver = new MaixSenseA010Driver( "/dev/ttyUSB0" );
        // initialize the communication,
        try {
            driver.initialize();
        } catch( SerialPortException e ) {
            e.printStackTrace();
            return;
        }
        // configure the MaixSense-A010 ToF camera.
        configureCamera( driver );
        // and set the data processing strategy.
        driver.setDataProcessingStrategy( imageEnqueuer );
        
    }
    
    
    
    ////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ////////////////////////////////////////////////////////////////
    
    /**
     * {@inheritDoc}
     * <p>
     * In this case, we map the gray scale values that go from 0 to 255, to ASCII characters, so that we can print the image in the console.
     */
    public void consumeImage( MaixSenseA010Image image )
    {
        System.out.println( image.frameId() );
        StringBuilder asciiArtString = new StringBuilder();
        for( int i=0; i<image.rows(); i+=PIXELS_PER_ASCII_CHAR ) {
            for( int j=0; j<image.cols(); j+=PIXELS_PER_ASCII_CHAR ) {
                double pixelAverage = 0.0;
                for( int ii=0; ii<PIXELS_PER_ASCII_CHAR; ii++ ) {
                    for( int jj=0; jj<PIXELS_PER_ASCII_CHAR; jj++ ) {
                        pixelAverage += image.pixel( i + ii , j + jj );
                    }
                }
                pixelAverage /= PIXELS_PER_ASCII_CHAR * PIXELS_PER_ASCII_CHAR;
                int charIndex = (int)( pixelAverage / 256.0 * pixelToChar.length() );
                asciiArtString.append( " " );
                asciiArtString.append( pixelToChar.charAt( charIndex ) );
            }
            asciiArtString.append( "\n" );
        }
        System.out.println( asciiArtString );
    }
    
    
    
    ////////////////////////////////////////////////////////////////
    // PUBLIC STATIC METHODS
    ////////////////////////////////////////////////////////////////
    
    /**
     * Sets the configuration of a MaixSenseA010Driver.
     * 
     * @param driver    {@link MaixSenseA010Driver} to be configured.
     */
    public static void configureCamera( MaixSenseA010Driver driver )
    {
        driver.setImageSignalProcessorOn();
        
        driver.setLcdDisplayOff();
        driver.setUsbDisplayOn();
        driver.setUartDisplayOff();
        
        //driver.setBinning25x25();
        driver.setBinning100x100();
        driver.setFps( 20 );
        
        driver.setQuantizationUnit( QUANTIZATION_UNIT );
        driver.setAntiMultiMachineInterferenceOff();
        //driver.setAntiMultiMachineInterferenceOn();
        driver.setExposureTimeAutoOn();
    }
    
}
