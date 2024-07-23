package util.calibration;


import java.util.List;

import util.DepthImage;
import util.RealVector3;



/**
 * Represents the calibration of a depth camera.
 */
public interface DepthCameraCalibration
{
    ////////////////////////////////////////////////////////////////
    // PUBLIC ABSTRACT METHODS
    ////////////////////////////////////////////////////////////////
    
    /**
     * Returns the point cloud that results from the raw depth image.
     * 
     * @param image     raw depth image.
     * @return  point cloud that results from the raw depth image.
     */
    public List<RealVector3> imageToPointCloud( DepthImage image );
    
}
