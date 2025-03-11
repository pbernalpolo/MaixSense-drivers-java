package maixsense.a010;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jssc.SerialPortException;



/**
 * Example on how to log MaixSense-A010 data.
 */
public class MaixSenseA010DataLogging
{
    ////////////////////////////////////////////////////////////////
    // PARAMETERS
    ////////////////////////////////////////////////////////////////
    
    /**
     * Quantization unit used for both the calibration and the driver.
     */
    static final int QUANTIZATION_UNIT = 0;
    
    
    
    ////////////////////////////////////////////////////////////////
    // MAIN: ENTRY POINT
    ////////////////////////////////////////////////////////////////
    
    /**
     * Entry point.
     * 
     * @param args  not used.
     * @throws IOException 
     * @throws SerialPortException 
     */
    public static void main(String[] args)
    {
        // Create the MaixSense-A010 data processing strategy.
        String timestamp = new SimpleDateFormat( "yyyyMMdd_HHmmss" ).format( new Date() );
        String fileName = "maixSenseA010_25x25_" + timestamp + ".log";
        MaixSenseA010DataLoggerStrategy dataLogger;
        try {
            dataLogger = new MaixSenseA010DataLoggerStrategy( fileName );
        } catch( IOException e ) {
            e.printStackTrace();
            return;
        }
        
        // Create the driver,
        MaixSenseA010Driver driver = new MaixSenseA010Driver( "/dev/serial/by-id/usb-SIPEED_SIPEED_Meta_Sense_Lite_202206_08C75B-if00-port0" );
        
        // initialize the driver communication,
        try {
            driver.initialize();
        } catch( SerialPortException e ) {
            e.printStackTrace();
            return;
        }
        
        // configure the MaixSense-A010 ToF cameras.
        driver.setImageSignalProcessorOn();
        
        driver.setLcdDisplayOff();
        driver.setUsbDisplayOn();
        driver.setUartDisplayOff();
        
        driver.setBinning25x25();
        //driver.setBinning100x100();
        driver.setFps( 20 );
        
        driver.setQuantizationUnit( QUANTIZATION_UNIT );
        driver.setAntiMultiMachineInterferenceOff();
        //driver.setAntiMultiMachineInterferenceOn();
        driver.setExposureTimeAutoOn();
        
        // and set the data processing strategy.
        driver.setDataProcessingStrategy( dataLogger );
    }
    
}
