package project.displays;

import project.SimulationRunner;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import project.components.Robot;

public class RobotsDisplay extends JPanel
{
    public RobotsDisplay ()
    {
        setBackground( Color.WHITE ) ;
    }

    @Override public Dimension getPreferredSize ()
    {
        return new Dimension( SimulationRunner.factory.maxRobots * 200 , 200 ) ;
    }

    @Override public void paintComponent ( Graphics g )
    {
        super.paintComponent( g ) ;
        Graphics2D g2d = (Graphics2D) g ;
        AffineTransform tOriginal = g2d.getTransform() ;
        g2d.setColor( Color.LIGHT_GRAY ) ;
        for (int i = 200; i < SimulationRunner.factory.maxRobots * 200 ; i += 200 )  { g2d.drawLine( i , 0 , i , 200 ) ; }
        synchronized ( SimulationRunner.factory.robots )
        {
            for ( Robot r : SimulationRunner.factory.robots )  { r.draw( g2d ) ;  g2d.translate( 200 , 0 ) ; }
        }
        g2d.setTransform( tOriginal ) ;
    }
}