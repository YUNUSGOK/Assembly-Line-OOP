package project.parts;

import project.SimulationRunner;
import project.utility.Drawable;

import java.awt.*;

public abstract class Part implements Drawable
{
    @Override public void draw ( Graphics2D g2d )
    {
        SimulationRunner.draw( g2d , this.getClass().getSimpleName() ) ;
    }
}