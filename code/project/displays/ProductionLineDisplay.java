package project.displays;

import project.SimulationRunner;

import javax.swing.*;
import java.awt.*;

public class ProductionLineDisplay extends JPanel
{
    public ProductionLineDisplay ()
    {
        setBackground( Color.WHITE ) ;
    }

    @Override public Dimension getPreferredSize ()
    {
        return new Dimension( SimulationRunner.factory.productionLine.maxCapacity * 200 , 200 ) ;
    }

    @Override public void paintComponent ( Graphics g )
    {
        super.paintComponent( g ) ;
        Graphics2D g2d = (Graphics2D) g ;
        SimulationRunner.factory.productionLine.draw( g2d ) ;
    }
}