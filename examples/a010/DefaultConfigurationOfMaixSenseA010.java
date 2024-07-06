package a010;

import jssc.SerialPortException;

public class DefaultConfigurationOfMaixSenseA010
{
    
    
    public static void main( String[] args )
    {
        MaixSenseA010Driver a010 = new MaixSenseA010Driver( "/dev/ttyUSB0" );
        
        try {
            
            a010.initialize();
            
            a010.setFps( 20 );
            
            a010.terminate();
            
        } catch( SerialPortException e ) {
            e.printStackTrace();
        }
        
        

    }

}
