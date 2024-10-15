package maixsense.a010;



/**
 * Contains an image provided by the MaixSenseA010 ToF camera.
 * <p>
 * It contains both, the pixel data, and more info such as the frame ID.
 */
public class MaixSenseA010Image
{
    ////////////////////////////////////////////////////////////////
    // PRIVATE VARIABLES
    ////////////////////////////////////////////////////////////////
    
    /**
     * Rows of the image.
     */
    private int rows;
    
    /**
     * Columns of the image.
     */
    private int cols;
    
    /**
     * Array that contains the pixel values.
     */
    private byte[] pixels;
    
    /**
     * Exposure time with which the image was captured.
     */
    private int exposureTime;
    
    /**
     * Frame ID associated to the image.
     */
    private short frameId;
    
    /**
     * Temperature of the sensor when the image was captured.
     */
    private byte sensorTemperature;
    
    /**
     * Temperature of the driver when the image was captured.
     */
    private byte driverTemperature;
    
    /**
     * Error code received with the image.
     */
    private byte errorCode;
    
    
    
    ////////////////////////////////////////////////////////////////
    // PUBLIC CONSTRUCTORS
    ////////////////////////////////////////////////////////////////
    
    /**
     * Constructs a {@link MaixSenseA010Image}.
     */
    public MaixSenseA010Image()
    {
    }
    
    
    
    /**
     * Constructs a {@link MaixSenseA010Image}.
     * 
     * @param imageRows     rows of the {@link MaixSenseA010Image}.
     * @param imageCols     columns of the {@link MaixSenseA010Image}-
     * @param imagePixels   array that contains the pixel values of the {@link MaixSenseA010Image}.
     */
    public MaixSenseA010Image( int imageRows , int imageCols , byte[] imagePixels )
    {
        this.rows = imageRows;
        this.cols = imageCols;
        this.pixels = imagePixels;
    }
    
    
    
    ////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ////////////////////////////////////////////////////////////////
    
    /**
     * Returns the rows of the image.
     * 
     * @return  rows of the image.
     */
    public int rows()
    {
        return this.rows;
    }
    
    
    /**
     * Returns the columns of the image.
     * 
     * @return  columns of the image.
     */
    public int cols()
    {
        return this.cols;
    }
    
    
    /**
     * Returns the value of the pixel located at (i,j).
     * 
     * @param i     x-coordinate of the pixel.
     * @param j     y-coordinate of the pixel.
     * @return  value of the pixel located at (i,j).
     */
    public int pixel( int i , int j )
    {
        return ( this.pixels[ i * this.cols() + j ] & 0xFF );
    }
    
    
    /**
     * Sets the exposure time with which the image was captured.
     * 
     * @param theExposureTime   exposure time with which the image was captured.
     */
    public void setExposureTime( int theExposureTime )
    {
        this.exposureTime = theExposureTime;
    }
    
    
    /**
     * Returns the exposure time with which the image was captured.
     * 
     * @return  exposure time with which the image was captured.
     */
    public int exposureTime()
    {
        return this.exposureTime;
    }
    
    
    /**
     * Sets the frame ID of the image.
     * 
     * @param id    frame ID to be set.
     */
    public void setFrameId( short id )
    {
        this.frameId = id;
    }
    
    
    /**
     * Returns the frame ID of the image.
     * 
     * @return  frame ID of the image.
     */
    public short frameId()
    {
        return this.frameId;
    }
    
    
    /**
     * Sets the temperature of the sensor when the image was captured.
     * 
     * @param temp  temperature to be set.
     */
    public void setSensorTemperature( byte temp )
    {
        this.sensorTemperature = temp;
    }
    
    
    /**
     * Returns the temperature of the sensor when the image was captured.
     * 
     * @return  temperature of the sensor when the image was captured.
     */
    public byte sensorTemperature()
    {
        return this.sensorTemperature;
    }
    
    
    /**
     * Sets the temperature of the driver when the image was captured.
     * 
     * @param temp  temperature to be set.
     */
    public void setDriverTemperature( byte temp )
    {
        this.driverTemperature = temp;
    }
    
    
    /**
     * Returns the temperature of the driver when the image was captured.
     * 
     * @return  temperature of the driver when the image was captured.
     */
    public byte driverTemperature()
    {
        return this.driverTemperature;
    }
    
    
    /**
     * Sets the error code received with the image.
     * 
     * @param code  error code received with the image.
     */
    public void setErrorCode( byte code )
    {
        this.errorCode = code;
    }
    
    
    /**
     * Returns the error code received with the image.
     * 
     * @return  error code received with the image.
     */
    public byte errorCode()
    {
        return this.errorCode;
    }
    
}
