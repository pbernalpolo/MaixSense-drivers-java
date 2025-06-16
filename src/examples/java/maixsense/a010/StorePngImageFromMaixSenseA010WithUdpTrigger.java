package maixsense.a010;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import javax.imageio.ImageIO;

import jssc.SerialPortException;



/**
 * Example on how to store depth images in png format if a trigger is received through UDP.
 */
public class StorePngImageFromMaixSenseA010WithUdpTrigger
    implements MaixSenseA010ImageConsumer
{
    ////////////////////////////////////////////////////////////////
    // PARAMETERS
    ////////////////////////////////////////////////////////////////
	
	/**
	 * UDP port through which UDP packets will be received.
	 */
	static final int UDP_PORT = 5002;
    
	
	
	////////////////////////////////////////////////////////////////
	// VARIABLES
	////////////////////////////////////////////////////////////////
	
	/**
	 * Socket through which the images will be received.
	 */
	DatagramSocket udpSocket;
	
	
	
	////////////////////////////////////////////////////////////////
	// MAIN: ENTRY POINT
	////////////////////////////////////////////////////////////////
	
    /**
     * Entry point of the example.
     * @throws SocketException 
     */
    public static void main( String[] args )
    {
        // Instantiate a MaixSenseA010ImageConsumer; in this case it is the StoreImageFromMaixSenseA010 itself.
        StorePngImageFromMaixSenseA010WithUdpTrigger storer = new StorePngImageFromMaixSenseA010WithUdpTrigger();
        
        try {
			storer.udpSocket = new DatagramSocket( UDP_PORT );
			storer.udpSocket.setReuseAddress( true );
			storer.udpSocket.setSoTimeout(1); // 1 ms timeout for non-blocking receive
			System.out.println(" Listening for UDP packets on port " + UDP_PORT );
		} catch( SocketException e ) {
			e.printStackTrace();
			System.exit( 1 );
		}
        
        // Create the image queue,
        MaixSenseA010ImagePublisherQueue imageQueue = new MaixSenseA010ImagePublisherQueue();
        // and add the listeners.
        imageQueue.addListener( storer );
        
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
     * In this example, we store the image as a bmp only if a trigger is received through the UDP port.
     */
    public void consumeImage( MaixSenseA010Image image )
    {
    	byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        
    	try {
    		// Next line will throw exception if timeout.
			this.udpSocket.receive( packet );
			
	    	int width = image.cols();
	        int height = image.rows();
	        BufferedImage bufferedImage = new BufferedImage( width , height , BufferedImage.TYPE_BYTE_GRAY );
	        
	        for( int i = 0; i < height; i++ ) {
	            for( int j = 0; j < width; j++ ) {
	                int pixelValue = image.pixel( i , j );
	                bufferedImage.setRGB( j,i , (pixelValue << 16) | (pixelValue << 8) | pixelValue );
	            }
	        }
	        
	        try {
	            // Create the output directory if it does not exist.
	            File outputDir = new File( "./data/output/maixsenseA010/" );
	            if( !outputDir.exists() ) {
	                outputDir.mkdirs();
	            }
	            
	            // Create the output file using the content of the UDP packet to compose the name.
	            String receivedDateString = new String(packet.getData(), 0, packet.getLength()).trim();
	            File outputfile = new File( outputDir , "image_" + receivedDateString + ".png" );
	            ImageIO.write( bufferedImage , "png" , outputfile );
	            System.out.println( "Image saved successfully: " + outputfile.getAbsolutePath() );
	        } catch( IOException e ) {
	            e.printStackTrace();
	        }
    	} catch( SocketTimeoutException e ) {
            // No packet received; skip storing the image.
    	} catch( IOException e ) {
			e.printStackTrace();
		}
    	
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
