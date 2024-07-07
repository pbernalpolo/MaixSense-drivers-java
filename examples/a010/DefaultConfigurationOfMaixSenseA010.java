package a010;


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
        MaixSenseA010Driver a010 = new MaixSenseA010Driver( "/dev/ttyUSB0" );
        
        try {
            
            // Initialize serial communication.
            a010.initialize();
            
            // Enables image processing.
            a010.setImageSignalProcessorOn();
            
            // Disables LCD screen updating.
            a010.setLcdDisplayOff();
            // Enables communication of images through USB port.
            a010.setUsbDisplayOn();
            // Disables communication of images through UART.
            a010.setUartDisplayOff();
            
            // Captured images will be 100 x 100.
            a010.setBinning100x100();
            
            // Sets frames per second to 20 (images/second).
            a010.setFps( 20 );
            
            // Terminates the serial communication.
            a010.terminate();
            
        } catch( SerialPortException e ) {
            e.printStackTrace();
        }
        
    }

}
