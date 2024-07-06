package a010;



public class MaixSenseA010Image
{
    ////////////////////////////////////////////////////////////////
    // PRIVATE VARIABLES
    ////////////////////////////////////////////////////////////////
    
    private final int rows;
    private final int cols;
    private final byte[] pixels;
    private int exposureTime;
    private short frameId;
    private byte sensorTemperature;
    private byte driverTemperature;
    private byte errorCode;
    
    
    
    public MaixSenseA010Image( int imageRows , int imageCols , byte[] imagePixels )
    {
        this.rows = imageRows;
        this.cols = imageCols;
        this.pixels = imagePixels;
    }
    
    
    public int rows()
    {
        return this.rows;
    }
    
    
    public int cols()
    {
        return this.cols;
    }
    
    
    public byte pixel( int i , int j )
    {
        return this.pixels[ i * this.cols() + j ];
    }
    
    
    public void setExposureTime( int theExposureTime )
    {
        this.exposureTime = theExposureTime;
    }
    
    
    public int exposureTime()
    {
        return this.exposureTime;
    }
    
    
    public void setFrameId( short id )
    {
        this.frameId = id;
    }
    
    
    public short frameId()
    {
        return this.frameId;
    }
    
    
    public void setSensorTemperature( byte temp )
    {
        this.sensorTemperature = temp;
    }
    
    
    public byte sensorTemperature()
    {
        return this.sensorTemperature;
    }
    
    
    public void setDriverTemperature( byte temp )
    {
        this.driverTemperature = temp;
    }
    
    
    public byte driverTemperature()
    {
        return this.driverTemperature;
    }
    
    
    public void setErrorCode( byte code )
    {
        this.errorCode = code;
    }
    
    
    public byte errorCode()
    {
        return this.errorCode;
    }
    
}
