package a010;


import java.util.ArrayDeque;
import java.util.Queue;

import jssc.SerialPortException;



public class PrintDepthMapOfMaixSenseA010
    implements MaixSenseA010ImageConsumer
{
    
    
    public static void main( String[] args )
    {
        PrintDepthMapOfMaixSenseA010 printer = new PrintDepthMapOfMaixSenseA010();
        /*
        QueueNotifierDecorator<ImageOfMaixSenseA010> imageQueue = new QueueNotifierDecorator<ImageOfMaixSenseA010>( new ArrayDeque<ImageOfMaixSenseA010>() );
        imageQueue.addListener( printer );
        */
        MaixSenseA010ImageQueue imageQueue = new MaixSenseA010ImageQueue();
        imageQueue.addListener( printer );
        
        MaixSenseA010Driver a010 = new MaixSenseA010Driver( "/dev/ttyUSB0" );
        a010.connectQueue( imageQueue );
        
        try {
            
            a010.initialize();
            
            a010.setImageSignalProcessorOn();
            
            a010.setLcdDisplayOff();
            a010.setUsbDisplayOn();
            a010.setUartDisplayOff();
            
            a010.setBinning100x100();
            a010.setFps( 20 );
            
            Thread.sleep( 4000 );
            
            //a010.terminate();
            
        } catch( SerialPortException | InterruptedException e ) {
            e.printStackTrace();
        }
    }
    
    
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
