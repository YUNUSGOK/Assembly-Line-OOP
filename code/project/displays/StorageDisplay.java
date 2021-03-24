package project.displays;

import project.SimulationRunner;

import javax.swing.*;
import java.awt.*;

public class StorageDisplay extends JPanel
{
    public StorageDisplay ()
    {
        setBackground( Color.WHITE ) ;
    }

    @Override public Dimension getPreferredSize ()
    {
        return new Dimension( SimulationRunner.factory.storage.maxCapacity * 200 , 200 ) ;
    }

    @Override public void paintComponent ( Graphics g )
    {
        super.paintComponent( g ) ;
        Graphics2D g2d = (Graphics2D) g ;
        SimulationRunner.factory.storage.draw( g2d ) ;
    }
}