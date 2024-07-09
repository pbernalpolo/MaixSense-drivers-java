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
    private static final double FOV_H = 70 * Math.PI/180.0;  // [rad] See table under "Key Specifications" in page 6
    private static final double FOV_V = 60 * Math.PI/180.0;  // [rad] See table under "Key Specifications" in page 6
    private static final double DEPTH_RANGE_MIN = 0.0;  // [m] See table under "Key Specifications" in page 6
    private static final double DEPTH_RANGE_MAX = 2.5;  // [m] See table under "Key Specifications" in page 6
    
    ////////////////////////////////////////////////////////////////
    // PRIVATE DERIVED CONSTANTS
    ////////////////////////////////////////////////////////////////
    private static final double X_SCREEN_SIZE_AT_1M = 2.0 * Math.tan( FOV_H / 2.0 );
    private static final double Y_SCREEN_SIZE_AT_1M = 2.0 * Math.tan( FOV_V / 2.0 );
    
    
    
    ////////////////////////////////////////////////////////////////
    // PUBLIC ABSTRACT METHODS
    ////////////////////////////////////////////////////////////////
    
    /**
     * {@inheritDoc}
     */
    public List<RealVector3> imageToPointCloud( DepthImage image )
    {
        List<RealVector3> pointCloud = new ArrayList<RealVector3>();
        for( int i=0; i<image.rows(); i++ ) {
            for( int j=0; j<image.cols(); j++ ) {
                int depthValue = image.pixel(i,j) & 0xff ;
                double depth = DEPTH_RANGE_MIN + depthValue/255.0 * ( DEPTH_RANGE_MAX - DEPTH_RANGE_MIN );
                RealVector3 point = new RealVector3(
                        (j-0.5*image.cols())/image.cols() * X_SCREEN_SIZE_AT_1M ,
                        (i-0.5*image.rows())/image.rows() * Y_SCREEN_SIZE_AT_1M ,
                        1.0 );
                point.scaleInplace( depth / point.norm() );
                pointCloud.add( point );
            }
        }
        return pointCloud;
    }
    
}
