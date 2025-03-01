package maixsense.a010;


import jssc.SerialPortException;



/**
 * Example on how to configure the MaixSense-A010 ToF camera using {@link MaixSenseA010Driver}.
 */
public class DefaultConfigurationOfMaixSenseA010
{
    
    /**
     * Entry point of the example.
     */
    public static void main( String[] args )
    {
        MaixSenseA010Driver driver = new MaixSenseA010Driver( "/dev/ttyUSB0" );
        
        // Initialize serial communication.
        try {
            driver.initialize();
        } catch( SerialPortException e ) {
            e.printStackTrace();
            return;
        }
        
        // Enables image processing.
        driver.setImageSignalProcessorOn();
        
        // Disables LCD screen updating.
        driver.setLcdDisplayOff();
        // Enables communication of images through USB port.
        driver.setUsbDisplayOn();
        // Disables communication of images through UART.
        driver.setUartDisplayOff();
        
        // Captured images will be 100 x 100.
        driver.setBinning100x100();
        
        // Sets frames per second to 20 (images/second).
        driver.setFps( 20 );
        
        // Sets the quantization unit used to relate the pixel value to a depth measurement.
        driver.setQuantizationUnit( 0 );
        
        // Deactivates the anti-multi-machine interference.
        driver.setAntiMultiMachineInterferenceOff();
        
        // Sets the exposure value to automatic exposure.
        driver.setExposureTimeAutoOn();
        
        // Terminates the serial communication.
        try {
            driver.terminate();
        } catch( SerialPortException e ) {
            e.printStackTrace();
            return;
        }
        
        System.out.println( "Configuration successful." );
    }

}
