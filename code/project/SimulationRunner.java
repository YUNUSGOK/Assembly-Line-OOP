package project;

import project.displays.ProductionLineDisplay;
import project.displays.RobotsDisplay;
import project.displays.StorageDisplay;
import project.components.Factory;
import project.utility.SmartFactoryException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class SimulationRunner
{
    public static Factory factory                      = null ;
    public static int     breakdownProbabilityConstant = 200 ;  // higher -> less breakdowns
    public static int     robotSleepDurationConstant   = 200 ;  // higher -> slower animation

    public static RobotsDisplay robotsDisplay         = null ;
    public static JFrame robotsWindow          = null ;

    public static ProductionLineDisplay productionLineDisplay = null ;
    public static JFrame productionLineWindow  = null ;

    public static StorageDisplay storageDisplay        = null ;
    public static JFrame storageWindow         = null ;

    public static final Map<String, BufferedImage> robotImageFiles   = new HashMap<>() ;
    public static final Map<String, BufferedImage> payloadImageFiles = new HashMap<>() ;
    public static final Map<String, BufferedImage> logicImageFiles   = new HashMap<>() ;

    static
    {
        try
        {
            robotImageFiles  .put( "Base"           , ImageIO.read( new File( "images/Base.jpg" ) ) ) ;
            robotImageFiles  .put( "Arm"            , ImageIO.read( new File("images/Arm.jpg") ) ) ;
            robotImageFiles  .put( "BaseArm"        , ImageIO.read( new File("images/BaseArm.jpg") ) ) ;
            payloadImageFiles.put( "Gripper"        , ImageIO.read( new File("images/Gripper.jpg") ) ) ;
            payloadImageFiles.put( "Welder"         , ImageIO.read( new File("images/Welder.jpg") ) ) ;
            payloadImageFiles.put( "Camera"         , ImageIO.read( new File("images/Camera.jpg") ) ) ;
            payloadImageFiles.put( "MaintenanceKit" , ImageIO.read( new File( "images/MaintenanceKit.jpg" ) ) ) ;
            logicImageFiles  .put( "Supplier"       , ImageIO.read( new File( "images/Supplier.jpg" ) ) ) ;
            logicImageFiles  .put( "Builder"        , ImageIO.read( new File("images/Builder.jpg") ) ) ;
            logicImageFiles  .put( "Inspector"      , ImageIO.read( new File("images/Inspector.jpg") ) ) ;
            logicImageFiles  .put( "Fixer"          , ImageIO.read( new File("images/Fixer.jpg") ) ) ;
        }
        catch ( IOException e )  { throw new SmartFactoryException( "Failed: images!" ) ; }
    }

    public static void draw (Graphics2D g2d , String imageName )
    {
        BufferedImage image  = null ;
        int           width  = 0    ;
        int           height = 0    ;
        int           x      = 0    ;
        int           y      = 0    ;

        if ( robotImageFiles  .containsKey( imageName ) )
        {
            image  = robotImageFiles  .get( imageName ) ;
            width  = image.getWidth ()                  ;
            height = image.getHeight()                  ;
            x      = 1                                  ;
            y      = 0                                  ;
        }
        else if ( payloadImageFiles.containsKey( imageName ) )
        {
            image  = payloadImageFiles.get( imageName ) ;
            width  = image.getWidth () * 11 / 15        ;
            height = image.getHeight() * 11 / 15        ;
            x      = 200 - width                        ;
            y      = 0                                  ;
        }
        else if ( logicImageFiles  .containsKey( imageName ) )
        {
            image  = logicImageFiles  .get( imageName ) ;
            width  = image.getWidth () *  9 / 15        ;
            height = image.getHeight() *  9 / 15        ;
            x      = 200 - width - 10                   ;
            y      =  70                                ;
        }

        if ( image != null )  { g2d.drawImage( image , x , y , width , height , null ) ; }
    }

    public SimulationRunner(int maxRobots , int maxProductionLineCapacity , int maxStorageCapacity )
    {
        factory = new Factory( maxRobots , maxProductionLineCapacity , maxStorageCapacity ) ;

        robotsDisplay         = new RobotsDisplay() ;
        robotsWindow         = new JFrame( "Robots"          ) ;

        productionLineDisplay = new ProductionLineDisplay() ;
        productionLineWindow = new JFrame( "Production Line" ) ;

        storageDisplay        = new StorageDisplay() ;
        storageWindow        = new JFrame( "Storage"         ) ;

        robotsWindow        .add                     ( robotsDisplay         ) ;
        robotsWindow        .setLocation             ( 50 , 50               ) ;
        robotsWindow        .setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE  ) ;
        robotsWindow        .pack                    (                       ) ;

        productionLineWindow.add                     ( productionLineDisplay ) ;
        productionLineWindow.setLocation             ( 50 , 350              ) ;
        productionLineWindow.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE  ) ;
        productionLineWindow.pack                    (                       ) ;

        storageWindow       .add                     ( storageDisplay        ) ;
        storageWindow       .setLocation             ( 50 , 650              ) ;
        storageWindow       .setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE  ) ;
        storageWindow       .pack                    (                       ) ;

        SwingUtilities.invokeLater(() -> robotsWindow        .setVisible( true )) ;
        SwingUtilities.invokeLater(() -> productionLineWindow.setVisible( true )) ;
        SwingUtilities.invokeLater(() -> storageWindow       .setVisible( true )) ;

        // Press Q to terminate the program
        java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher
                (
                        event -> {
                            String key = KeyStroke.getKeyStrokeForEvent(event).toString() ;

                            if ( key.equals( "pressed Q" ) )  { System.exit(0) ;  return true ; }

                            return false ;
                        }
                ) ;

        System.out.println( "Press Q to quit" ) ;
        System.out.println(                   ) ;

        factory.start() ;
    }

    public static void main ( String [] args )
    {
        try                    { UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() ) ; }
        catch ( Exception e )  { /* Do nothing */                                                        }

        int maxRobots                 = 8 ;
        int maxProductionLineCapacity = 9 ;
        int maxStorageCapacity        = 9 ;

        if ( args.length >= 1 )  { maxRobots                    = Integer.parseInt( args[0] ) ; }
        if ( args.length >= 2 )  { maxProductionLineCapacity    = Integer.parseInt( args[1] ) ; }
        if ( args.length >= 3 )  { maxStorageCapacity           = Integer.parseInt( args[2] ) ; }
        if ( args.length >= 4 )  { breakdownProbabilityConstant = Integer.parseInt( args[3] ) ; }
        if ( args.length >= 5 )  { robotSleepDurationConstant   = Integer.parseInt( args[4] ) ; }

        try { PrintStream fileStream = new PrintStream("output.txt"); System.setOut(fileStream); }
        catch (FileNotFoundException e) { e.printStackTrace(); }

        new SimulationRunner( maxRobots , maxProductionLineCapacity , maxStorageCapacity ) ;
    }
}