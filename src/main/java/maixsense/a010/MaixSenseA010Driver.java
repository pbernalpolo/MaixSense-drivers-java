package maixsense.a010;


import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;



/**
 * Controls the MaixSense-A010 ToF camera.
 * <p>
 * The driver is used to send commands over the {@link SerialPort}.
 * It also implements {@link SerialPortEventListener} to receive MaixSense-A010 data over the serial port.
 * <p>
 * The way in which the received MaixSense-A010 data is processed is defined by a {@link MaixSenseA010DataProcessingStrategy}.
 * This class is the Context in the Strategy Pattern.
 * 
 * @see <a href>https://wiki.sipeed.com/hardware/en/maixsense/maixsense-a010/maixsense-a010.html</a>
 * @see <a href>https://wiki.sipeed.com/hardware/en/maixsense/maixsense-a010/at_command_en.html</a>
 */
public class MaixSenseA010Driver
    implements
        SerialPortEventListener
{
    ////////////////////////////////////////////////////////////////
    // PRIVATE VARIABLES
    ////////////////////////////////////////////////////////////////
    
    /**
     * {@link MaixSenseA010DataProcessingStrategy} used to process the data received through the serial port.
     */
    private MaixSenseA010DataProcessingStrategy dataProcessingStrategy;
    
    /**
     * {@link SerialPort} over which the communication is established.
     */
    private SerialPort serialPort;
    
    /**
     * Used to define the value that is sent with the AT+DISP command.
     * Such command is used to enable/disable the LCD, USB, and UART communication.
     */
    private byte disp;
    
    
    
    ////////////////////////////////////////////////////////////////
    // PUBLIC CONSTRUCTORS
    ////////////////////////////////////////////////////////////////
    
    /**
     * Constructs a {@link MaixSenseA010Driver}.
     * 
     * @param serialPortPath    path to the serial port where the MaixSense-A010 is connected.
     */
    public MaixSenseA010Driver( String serialPortPath )
    {
        this.serialPort = new SerialPort( serialPortPath );
        // Default initial value is LCD display on.
        this.disp = (byte)0b00000001;
    }
    
    
    
    ////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ////////////////////////////////////////////////////////////////
    
    /**
     * {@inheritDoc}
     */
    public void setDataProcessingStrategy( MaixSenseA010DataProcessingStrategy maixSenseA010DataProcessingStrategy )
    {
        this.dataProcessingStrategy = maixSenseA010DataProcessingStrategy;
    }
    
    
    /**
     * Initializes communication through the serial port.
     * 
     * @throws SerialPortException  if the initialization fails.
     */
    public void initialize()
            throws SerialPortException
    {
        // Open serial port.
        this.serialPort.openPort();
        // Configuration must be set after opening the SerialPort.
        this.serialPort.setParams(
                SerialPort.BAUDRATE_115200 ,
                SerialPort.DATABITS_8 ,
                SerialPort.STOPBITS_1 ,
                SerialPort.PARITY_NONE );
        this.serialPort.addEventListener( this , SerialPort.MASK_RXCHAR );
    }
    
    
    /**
     * Terminates communication through the serial port.
     * 
     * @throws SerialPortException  if the termination fails.
     */
    public void terminate()
            throws SerialPortException
    {
        this.serialPort.removeEventListener();
        this.serialPort.purgePort( SerialPort.PURGE_RXCLEAR | SerialPort.PURGE_TXCLEAR );
        this.serialPort.closePort();
    }
    
    
    /**
     * Activates Image Signal Processor.
     * <p>
     * "It is planned to start the module ISP, and the actual drawing needs to wait 1-2 seconds".
     */
    public void setImageSignalProcessorOn()
    {
        this.sendCommand( "AT+ISP=1\r" , "Failed setImageSignalProcessorOn." );
    }
    
    
    /**
     * Activates Image Signal Processor.
     * <p>
     * "Close the module ISP immediately, stop the IR transmitter".
     */
    public void setImageSignalProcessorOff()
    {
        this.sendCommand( "AT+ISP=0\r" , "Failed setImageSignalProcessorOff." );
    }
    
    
    /**
     * Sets the size of depth images to be received to 100 x 100 pixels^2.
     */
    public void setBinning100x100()
    {
        // The image signal processor must be on to execute the change in binning.
        this.setImageSignalProcessorOn();
        this.sendCommand( "AT+BINN=1\r" , "Failed setBinning100x100." );
    }
    
    
    /**
     * Sets the size of depth images to be received to 50 x 50 pixels^2.
     */
    public void setBinning50x50()
    {
        // The image signal processor must be on to execute the change in binning.
        this.setImageSignalProcessorOn();
        this.sendCommand( "AT+BINN=2\r" , "Failed setBinning50x50." );
    }
    
    
    /**
     * Sets the size of depth images to be received to 25 x 25 pixels^2.
     */
    public void setBinning25x25()
    {
        // The image signal processor must be on to execute the change in binning.
        this.setImageSignalProcessorOn();
        this.sendCommand( "AT+BINN=4\r" , "Failed setBinning25x25." );
    }
    
    
    /**
     * Activates LCD display.
     */
    public void setLcdDisplayOn()
    {
        this.disp |= (byte)0b00000001;
        this.updateDisp( "Failed setLcdDisplayOn." );
    }
    
    
    /**
     * Deactivates LCD display.
     */
    public void setLcdDisplayOff()
    {
        this.disp &= (byte)0b11111110;
        this.updateDisp( "Failed setLcdDisplayOff." );
    }
    
    
    /**
     * Activates USB communication.
     */
    public void setUsbDisplayOn()
    {
        this.disp |= (byte)0b00000010;
        this.updateDisp( "Failed setUsbDisplayOn." );
    }
    
    
    /**
     * Deactivates USB communication.
     */
    public void setUsbDisplayOff()
    {
        this.disp &= (byte)0b11111101;
        this.updateDisp( "Failed setUsbDisplayOff." );
    }
    
    
    /**
     * Activates UART communication.
     */
    public void setUartDisplayOn()
    {
        this.disp |= (byte)0b00000100;
        this.updateDisp( "Failed setUartDisplayOn." );
    }
    
    
    /**
     * Deactivates UART communication.
     */
    public void setUartDisplayOff()
    {
        this.disp &= (byte)0b11111011;
        this.updateDisp( "Failed setUartDisplayOff." );
    }
    
    
    /**
     * Sets the UART baudrate to 9600.
     */
    public void setUartBaudrate9600()
    {
        this.sendCommand( "AT+BAUD=0\r" , "Failed setUartBaudrate9600." );
    }
    
    
    /**
     * Sets the UART baudrate to 57600.
     */
    public void setUartBaudrate57600()
    {
        this.sendCommand( "AT+BAUD=1\r" , "Failed setUartBaudrate57600." );
    }
    
    
    /**
     * Sets the UART baudrate to 115200.
     */
    public void setUartBaudrate115200()
    {
        this.sendCommand( "AT+BAUD=2\r" , "Failed setUartBaudrate115200." );
    }
    
    
    /**
     * Sets the UART baudrate to 230400.
     */
    public void setUartBaudrate230400()
    {
        this.sendCommand( "AT+BAUD=3\r" , "Failed setUartBaudrate230400." );
    }
    
    
    /**
     * Sets the UART baudrate to 460800.
     */
    public void setUartBaudrate460800()
    {
        this.sendCommand( "AT+BAUD=4\r" , "Failed setUartBaudrate460800." );
    }
    
    
    /**
     * Sets the UART baudrate to 921600.
     */
    public void setUartBaudrate921600()
    {
        this.sendCommand( "AT+BAUD=5\r" , "Failed setUartBaudrate921600." );
    }
    
    
    /**
     * Sets the UART baudrate to 1000000.
     */
    public void setUartBaudrate1000000()
    {
        this.sendCommand( "AT+BAUD=6\r" , "Failed setUartBaudrate1000000." );
    }
    
    
    /**
     * Sets the UART baudrate to 2000000.
     */
    public void setUartBaudrate2000000()
    {
        this.sendCommand( "AT+BAUD=7\r" , "Failed setUartBaudrate2000000." );
    }
    
    
    /**
     * Sets the UART baudrate to 3000000.
     */
    public void setUartBaudrate3000000()
    {
        this.sendCommand( "AT+BAUD=8\r" , "Failed setUartBaudrate3000000." );
    }
    
    
    /**
     * Sets the quantization unit.
     * <p>
     * The quantization unit is used to compute the pixel value from a depth measurement as described in
     * <a href>https://wiki.sipeed.com/hardware/en/maixsense/maixsense-a010/at_command_en.html#UNIT-directive</a>.
     * That is,
     * <ul>
     *  <li> If quantization unit is 0,  pixelValue = 5.1 * sqrt( depth )
     *  <li> Otherwise,  pixelValue = depth / quantizationUnit
     * </ul>
     * The quantization unit can take values in the interval [0,9].
     * If the introduced value is outside of the interval [0,9], then the quantization unit is set to 0.
     * 
     * @param unit  quantization unit used to compute the pixel value from depth measurements.
     */
    public void setQuantizationUnit( int quantizationUnit )
    {
        // The image signal processor must be on to execute the change in quantization unit.
        this.setImageSignalProcessorOn();
        if(  0 <= quantizationUnit  &&  quantizationUnit <= 9  ) {
            this.sendCommand( "AT+UNIT=" + quantizationUnit + "\r" , "Failed setQuantizationUnit." );
        } else {
            this.sendCommand( "AT+UNIT=0\r" , "Failed setQuantizationUnit." );
            System.out.println( "Quantization unit must be in [0,10] interval; unit set to 0." );
        }
    }
    
    
    /**
     * Sets the Frames Per Second.
     * <p>
     * Input values must be in the range [1,20].
     * 
     * @param fps   Frames Per Second.
     */
    public void setFps( int fps )
    {
        // The image signal processor must be off to execute the change in fps.
        this.setImageSignalProcessorOff();
        if( fps < 1 ) {
            fps = 1;
            System.out.println( "fps must be in the interval [1,20]; fps set to 1." );
        }
        if( fps > 20 ) {
            fps = 20;
            System.out.println( "fps must be in the interval [1,20]; fps set to 20." );
        }
        this.sendCommand( "AT+FPS=" + fps + "\r" , "Failed setFps." );
    }
    
    
    /**
     * Activates the anti-multi-machine interference.
     */
    public void setAntiMultiMachineInterferenceOff()
    {
        this.sendCommand( "AT+ANTIMMI=0\r" , "Failed setAntiMultiMachineInterferenceOff." );
    }
    
    
    /**
     * Deactivates the anti-multi-machine interference.
     */
    public void setAntiMultiMachineInterferenceOn()
    {
        this.sendCommand( "AT+ANTIMMI=1\r" , "Failed setAntiMultiMachineInterferenceOn." );
    }
    
    
    /**
     * Activates the auto exposure time algorithm.
     * <p>
     * The exposure time will be automatically set depending on the environment conditions.
     */
    public void setExposureTimeAutoOn()
    {
        this.sendCommand( "AT+AE=1\r" , "Failed setExposureTimeAuto AE." );
        //this.sendCommand( "AT+EV=0\r" , "Failed setExposureTimeAuto EV." );
    }
    
    
    /**
     * Deactivates the auto exposure time algorithm.
     * <p>
     * The exposure time will take the last value set by the auto-exposure algorithm.
     */
    public void setExposureTimeAutoOff()
    {
        this.sendCommand( "AT+AE=0\r" , "Failed setExposureTimeAuto AE." );
    }
    
    
    /**
     * Sets the exposure time.
     * <p>
     * The exposure time is fixed, and its value is the input value.
     * 
     * @param value     exposure time.
     */
    // TODO: implement working setExposureTime
    /*public void setExposureTime( int value )
    {
        if(  value < 1  ||  40000 < value  ) {
            value = 1;
            System.out.println( "Exposure time must be in the interval [1,40000]; exposure value set to 1." );
        }
        this.sendCommand( "AT+AE=0\r" , "Failed setExposureTime AE." );
        this.sendCommand( "AT+EV=" + value + "\r" , "Failed setExposureTime EV." );
    }*/
    
    
    /**
     * Saves current configuration in MaixSense-A010 ToF camera.
     */
    public void saveConfiguration()
    {
        this.sendCommand( "AT+SAVE\r" , "Failed saveConfiguration." );
    }
    
    
    /**
     * Executed whenever there is a serial event.
     */
    @Override
    public void serialEvent( SerialPortEvent event )
    {
        if(  event.isRXCHAR()  &&  this.dataProcessingStrategy != null  ) {
            try {
                byte[] receivedBytes = this.serialPort.readBytes();
                if( receivedBytes == null ) {
                    return;
                }
                this.dataProcessingStrategy.process( receivedBytes , 0 , receivedBytes.length );
            } catch( SerialPortException e ) {
                e.printStackTrace();
            }
        }
    }
    
    
    
    ////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ////////////////////////////////////////////////////////////////
    
    /**
     * Sends an AT command through the serial port.
     * 
     * @param atCommand     AT command to be sent.
     * @param errorMessageOnFailure     error message printed if there is a failure on sending the AT command.
     * @return  true if the AT command is successfully sent; false otherwise.
     */
    private void sendCommand( String atCommand , String errorMessageOnFailure )
    {
        try {
            this.serialPort.writeString( atCommand );
        } catch( SerialPortException e ) {
            System.out.println( errorMessageOnFailure );
        }
    }
    
    
    /**
     * Sends a display command to activate/deactivate the communication through LCD display, USB, or UART.
     * 
     * @param errorMessageOnFailure     error message printed if there is a failure on sending the AT command.
     */
    private void updateDisp( String errorMessageOnFailure )
    {
        this.sendCommand( "AT+DISP=" + this.disp + "\r" , errorMessageOnFailure );
    }
    
}
