package util;



/**
 * Represents a depth image.
 */
public interface DepthImage
{
    ////////////////////////////////////////////////////////////////
    // PUBLIC ABSTRACT METHODS
    ////////////////////////////////////////////////////////////////
    
    /**
     * Returns the rows of the image.
     * 
     * @return  rows of the image.
     */
    public int rows();
    
    
    /**
     * Returns the columns of the image.
     * 
     * @return  columns of the image.
     */
    public int cols();
    
    
    /**
     * Returns the value of the pixel located at (i,j).
     * 
     * @param i     x-coordinate of the pixel.
     * @param j     y-coordinate of the pixel.
     * @return  value of the pixel located at (i,j).
     */
    public byte pixel( int i , int j );
    
}
