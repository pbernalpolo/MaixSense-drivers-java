package util.calibration;


import java.util.ArrayList;
import java.util.List;

import util.DepthImage;
import util.RealVector3;



/**
 * Implements the default calibration provided in the MaixSense-A010 ToF camera datasheet.
 * 
 * @see <a href>https://www.marutsu.co.jp/contents/shop/marutsu/datasheet/switchscience_DFROBOT-SEN0581.pdf</a>
 */
public class MaixSenseA010DefaultCalibration
    implements DepthCameraCalibration
{
    ////////////////////////////////////////////////////////////////
    // PRIVATE CONSTANTS
    ////////////////////////////////////////////////////////////////
    
    /**
     * Field of view of the camera in the horizontal direction.
     * <p>
     * Given in [rad].
     * 
     * @see <a href>https://www.marutsu.co.jp/contents/shop/marutsu/datasheet/switchscience_DFROBOT-SEN0581.pdf</a>, table under "Key Specifications" in page 6.
     */
    private static final double FOV_H = 70 * Math.PI/180.0;  // [rad] See table under "Key Specifications" in page 6
    
    /**
     * Field of view of the camera in the vertical direction.
     * <p>
     * Given in [rad].
     * 
     * @see <a href>https://www.marutsu.co.jp/contents/shop/marutsu/datasheet/switchscience_DFROBOT-SEN0581.pdf</a>, table under "Key Specifications" in page 6.
     */
    private static final double FOV_V = 60 * Math.PI/180.0;  // 
    
    
    
    ////////////////////////////////////////////////////////////////
    // PRIVATE DERIVED CONSTANTS
    ////////////////////////////////////////////////////////////////
    
    /**
     * Size of the projection screen in the x-direction when such screen is 1 meter apart from the camera focus.
     */
    private static final double X_SCREEN_SIZE_AT_1M = 2.0 * Math.tan( FOV_H / 2.0 );
    
    /**
     * Size of the projection screen in the y-direction when such screen is 1 meter apart from the camera focus.
     */
    private static final double Y_SCREEN_SIZE_AT_1M = 2.0 * Math.tan( FOV_V / 2.0 );
    
    
    
    ////////////////////////////////////////////////////////////////
    // PRIVATE VARIABLES
    ////////////////////////////////////////////////////////////////
    
    /**
     * Quantization unit used to compute the depth from a pixel value.
     * 
     * @see #depth(byte)
     */
    private int quantizationUnit;
    
    
    
    ////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ////////////////////////////////////////////////////////////////
    
    /**
     * Sets the quantization unit used to compute the depth from the pixel values.
     * 
     * @param unit  quantization unit.
     * 
     * @see #depth(byte)
     */
    public void setQuantizationUnit( int unit )
    {
        if( 0 <= unit  &&  unit <= 9 ) {
            this.quantizationUnit = unit;
        } else {
            this.quantizationUnit = 0;
            System.out.println( "Quantization unit must be in [0,10] interval; unit set to 0." );
        }
    }
    
    
    /**
     * Returns the depth in meters from the input pixel value.
     * <p>
     * It considers the quantization unit to compute the result as described in
     * <a href>https://wiki.sipeed.com/hardware/en/maixsense/maixsense-a010/at_command_en.html#Image-Packet-Description</a>.
     * That is,
     * <ul>
     *  <li> If quantization unit is 0,  depth = ( pixelValue / 5.1 )^2
     *  <li> Otherwise,  depth = pixelValue * quantizationUnit
     * </ul>
     * 
     * @param pixelValue    byte that holds the pixel value.
     * @return  depth in meters from the input pixel value.
     */
    public double depth( byte pixelValue )
    {
        // Take pixel byte and cast its unsigned representation to an int.
        int pixelValueUnsignedByte = pixelValue & 0xFF ;
        // The depth value depends on the quantization strategy.
        if( this.quantizationUnit == 0 ) {
            double depthSqrt = pixelValueUnsignedByte / 5.1;
            double depthInMillimiters = depthSqrt * depthSqrt;
            return depthInMillimiters * 1.0e-3;
        } else {
            return ( this.quantizationUnit * pixelValueUnsignedByte );
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    public List<RealVector3> imageToPointCloud( DepthImage image )
    {
        List<RealVector3> pointCloud = new ArrayList<RealVector3>();
        for( int i=0; i<image.rows(); i++ ) {
            for( int j=0; j<image.cols(); j++ ) {
                // Take pixel.
                byte pixelValue = image.pixel( i , j );
                // We skip it if it is saturated.
                if( pixelValue == (byte)0x00  ||  pixelValue == (byte)0xff ) {
                    continue;
                }
                // Otherwise, we compute its depth value.
                double depth = this.depth( pixelValue );
                // Build the RealVector3 from the (i,j)-th index and the depth value.
                RealVector3 point = new RealVector3(
                        (j-0.5*image.cols())/image.cols() * X_SCREEN_SIZE_AT_1M ,
                        (i-0.5*image.rows())/image.rows() * Y_SCREEN_SIZE_AT_1M ,
                        1.0 );
                point.scaleInplace( depth / point.norm() );
                // Add the point to the point cloud.
                pointCloud.add( point );
            }
        }
        return pointCloud;
    }
    
}
