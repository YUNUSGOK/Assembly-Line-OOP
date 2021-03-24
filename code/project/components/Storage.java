package project.components;

import project.utility.Drawable;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class Storage implements Drawable
{
    public int         maxCapacity ;
    public List<Robot> robots      ;

    public Storage ( int maxCapacity )
    {
        this.maxCapacity = maxCapacity       ;
        this.robots      = new ArrayList<>() ;
    }

    @Override public void draw ( Graphics2D g2d )
    {
        AffineTransform tOriginal = g2d.getTransform() ;
        g2d.setColor( Color.LIGHT_GRAY ) ;
        for ( int i = 200 ; i < maxCapacity * 200 ; i += 200 )  { g2d.drawLine( i , 0 , i , 200 ) ; }
        synchronized ( robots )
        {
            for ( Robot r : robots )  { r.draw( g2d ) ;  g2d.translate( 200 , 0 ) ; }
        }
        g2d.setTransform( tOriginal ) ;
    }
}