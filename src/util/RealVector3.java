package util;


import java.util.Random;



public class RealVector3
{
    ////////////////////////////////////////////////////////////////
    // PRIVATE VARIABLES
    ////////////////////////////////////////////////////////////////
    private double vx;
    private double vy;
    private double vz;
    
    
    
    ////////////////////////////////////////////////////////////////
    // PUBLIC CONSTRUCTORS
    ////////////////////////////////////////////////////////////////
    
    public RealVector3( double x , double y , double z )
    {
        this.vx = x;
        this.vy = y;
        this.vz = z;
    }
    
    
    
    ////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ////////////////////////////////////////////////////////////////
    
    public void setX( double x )
    {
        this.vx = x;
    }
    
    
    public void setY( double y )
    {
        this.vy = y;
    }
    
    
    public void setZ( double z )
    {
        this.vz = z;
    }
    
    
    public double x()
    {
        return vx;
    }
    
    
    public double y()
    {
        return vy;
    }
    
    
    public double z()
    {
        return vz;
    }
    
    
    public String toString()
    {
        return String.format( "( %2.16f , %2.16f , %2.16f )" , this.x() , this.y() , this.z() );
    }
    
    
    public RealVector3 print()
    {
        System.out.println( this.toString() );
        return this;
    }
    
    
    public RealVector3 copy()
    {
        return new RealVector3( this.x() , this.y() , this.z() );
    }
    
    
    public RealVector3 setTo( RealVector3 other )
    {
        this.vx = other.x();
        this.vy = other.y();
        this.vz = other.z();
        return this;
    }
    
    
    public RealVector3 setToZero()
    {
        this.vx = 0.0;
        this.vy = 0.0;
        this.vz = 0.0;
        return this;
    }
    
    
    public boolean equals( RealVector3 other )
    {
        return (  this.x() == other.x()  &&
                  this.y() == other.y()  &&
                  this.z() == other.z()  );
    }
    
    
    public boolean equalsApproximately( RealVector3 other , double tolerance )
    {
        return ( this.distanceFrom( other ) < tolerance );
    }
    
    
    public RealVector3 add( RealVector3 other )
    {
        return new RealVector3( this.x() + other.x() ,
                                this.y() + other.y() ,
                                this.z() + other.z() );
    }


    public RealVector3 addInplace( RealVector3 other )
    {
        this.vx += other.x();
        this.vy += other.y();
        this.vz += other.z();
        return this;
    }
    
    
    public RealVector3 identityAdditive()
    {
        return RealVector3.zero();
    }

    
    public RealVector3 inverseAdditive()
    {
        return new RealVector3( -this.x() , -this.y() , -this.z() );
    }


    public RealVector3 inverseAdditiveInplace()
    {
        this.vx = -this.x();
        this.vy = -this.y();
        this.vz = -this.z();
        return this;
    }


    public RealVector3 subtract( RealVector3 other )
    {
        return new RealVector3( this.x() - other.x() ,
                                this.y() - other.y() ,
                                this.z() - other.z() );
    }


    public RealVector3 subtractInplace( RealVector3 other )
    {
        this.vx -= other.x();
        this.vy -= other.y();
        this.vz -= other.z();
        return this;
    }
    
    
    public RealVector3 scale( double scalar )
    {
        return new RealVector3( this.x() * scalar ,
                                this.y() * scalar ,
                                this.z() * scalar );
    }


    public RealVector3 scaleInplace( double scalar )
    {
        this.vx *= scalar;
        this.vy *= scalar;
        this.vz *= scalar;
        return this;
    }
    
    
    public double dot( RealVector3 other )
    {
        return ( this.x() * other.x() +
                 this.y() * other.y() +
                 this.z() * other.z() );
    }
    
    
    public double normSquared()
    {
        return this.dot( this );
    }
    
    
    public double norm()
    {
        return Math.sqrt( this.normSquared() );
    }


    public RealVector3 normalize()
    {
        return this.scale( 1.0/this.norm() );
    }


    public RealVector3 normalizeInplace()
    {
        return this.scaleInplace( 1.0/this.norm() );
    }



    public double distanceFrom( RealVector3 other )
    {
        return this.subtract( other ).norm();
    }
    
    
    public double angleFrom( RealVector3 other )
    {
        double dot = this.normalize().dot( other.normalize() );
        return ( ( dot < -1.0 )? Math.PI : ( ( 1.0 < dot )? 0.0 : Math.acos( dot ) ) );
    }
    
    
    public RealVector3 crossProduct( RealVector3 other )
    {
        return new RealVector3( this.y() * other.z() - this.z() * other.y() ,
                                this.z() * other.x() - this.x() * other.z() ,
                                this.x() * other.y() - this.y() * other.x() );
    }
    
    
    
    ////////////////////////////////////////////////////////////////
    // PUBLIC STATIC METHODS
    ////////////////////////////////////////////////////////////////
    
    public static RealVector3 zero()
    {
        return new RealVector3( 0.0 , 0.0 , 0.0 );
    }
    
    
    public static RealVector3 i()
    {
        return new RealVector3( 1.0 , 0.0 , 0.0 );
    }


    public static RealVector3 j()
    {
        return new RealVector3( 0.0 , 1.0 , 0.0 );
    }


    public static RealVector3 k()
    {
        return new RealVector3( 0.0 , 0.0 , 1.0 );
    }
    
    
    public static RealVector3 random( Random randomNumberGenerator )
    {
        return new RealVector3(
                randomNumberGenerator.nextGaussian() ,
                randomNumberGenerator.nextGaussian() ,
                randomNumberGenerator.nextGaussian() );
    }
    
}

