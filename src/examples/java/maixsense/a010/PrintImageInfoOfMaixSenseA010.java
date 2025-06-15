package maixsense.a010;


import jssc.SerialPortException;



/**
 * Example of how to process images received from the MaixSense-A010 ToF camera.
 */
public class PrintImageInfoOfMaixSenseA010
    implements MaixSenseA010ImageConsumer
{
    
    /**
     * Entry point of the example.
     */
    public static void main( String[] args )
    {
        // Instantiate a MaixSenseA010ImageConsumer; in this case it is the PrintImageInfoOfMaixSenseA010 itself.
        PrintImageInfoOfMaixSenseA010 printer = new PrintImageInfoOfMaixSenseA010();
        
        // Create the image queue,
        MaixSenseA010ImagePublisherQueue imageQueue = new MaixSenseA010ImagePublisherQueue();
        // and add the listeners.
        imageQueue.addListener( printer );
        
        // Create the MaixSense-A010 data processing strategy.
        MaixSenseA010ImageEnqueuerStrategy imageEnqueuer = new MaixSenseA010ImageEnqueuerStrategy( imageQueue );
        
        // Create the driver,
        MaixSenseA010Driver driver = new MaixSenseA010Driver( "/dev/ttyUSB0" );
        
        // initialize the driver communication,
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
    
    
    /**
     * {@inheritDoc}
     * <p>
     * In this example, we jut print the image info.
     */
    public void consumeImage( MaixSenseA010Image image )
    {
        System.out.println( "Frame ID: " + image.frameId() );
        System.out.println( "Image size: " + image.rows() + " x " + image.cols() );
        System.out.println( "Exposure time: " + image.exposureTime() );
        System.out.println( "Temperature sensor: " + image.sensorTemperature() );
        System.out.println( "Temperature driver: " + image.driverTemperature() );
        System.out.println( "Error code: " + image.errorCode() );
        System.out.println();
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
        
        driver.setQuantizationUnit( 0 );
        driver.setAntiMultiMachineInterferenceOff();
        //driver.setAntiMultiMachineInterferenceOn();
        driver.setExposureTimeAutoOn();
    }
    
}
