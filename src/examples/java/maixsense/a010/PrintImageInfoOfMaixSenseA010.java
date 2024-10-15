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
        MaixSenseA010ImageQueue imageQueue = new MaixSenseA010ImageQueue();
        // and add the listener.
        imageQueue.addListener( printer );
        
        // Create the driver,
        MaixSenseA010Driver a010 = new MaixSenseA010Driver( "/dev/ttyUSB0" );
        // and connect the queue so that received images are added to it.
        a010.connectQueue( imageQueue );
        
        // Configure the MaixSense-A010 ToF camera.
        try {
            a010.initialize();
            
            a010.setImageSignalProcessorOn();
            
            a010.setLcdDisplayOff();
            a010.setUsbDisplayOn();
            a010.setUartDisplayOff();
            
            a010.setBinning100x100();
            a010.setFps( 20 );
            a010.setQuantizationUnit( 0 );
            a010.setAntiMultiMachineInterferenceOff();
            //a010.setAntiMultiMachineInterferenceOn();
            a010.setExposureTimeAutoOn();
            
        } catch( SerialPortException e ) {
            e.printStackTrace();
        }
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
    
}
