package project.parts;

import project.SimulationRunner;
import project.components.Robot;
import project.parts.logics.Logic;
import project.parts.payloads.Payload;
import project.utility.Common;

import java.awt.*;

public class Base extends Part implements Robot
{
    private int     serialNo       ;
    private Arm     arm            ;
    private Payload payload        ;
    private Logic   logic          ;
    private boolean isWaiting      ;
    private boolean isShutdown     ;
    private int     logicRunCount  ;
    private int     breakdownCount ;

    public Base ( int serialNo )
    {
        this.serialNo       = serialNo ;
        this.arm            = null     ;
        this.payload        = null     ;
        this.logic          = null     ;
        this.isWaiting      = false    ;
        this.isShutdown     = false    ;
        this.logicRunCount  = 0        ;
        this.breakdownCount = 0        ;
    }

    @Override public synchronized void draw ( Graphics2D g2d )
    {
        SimulationRunner.draw( g2d , (arm == null) ? "Base" : "BaseArm" ) ;
        g2d.setColor( Color.BLUE ) ;
        g2d.drawString( Integer.toString( serialNo ) , 10 , 30 ) ;

        if ( payload != null )  { payload.draw( g2d ) ; }
        if ( logic   != null )  { logic  .draw( g2d ) ; }

        if ( isWaiting               )  { g2d.setColor( Color.BLUE )          ; }
        if ( isShutdown              )  { g2d.setColor( Color.GRAY )          ; }
        if ( isWaiting || isShutdown )  { g2d.fillOval( 162 , 142 , 15 , 15 ) ; }

        if   ( (arm != null) && (payload != null) && (logic != null) )  { g2d.setColor( Color.GREEN ) ; }
        else                                                            { g2d.setColor( Color.RED   ) ; }

        g2d.fillOval( 162 , 172 , 15 , 15 ) ;
    }

    public synchronized void wearAndTear ()
    {
        boolean refresh = false ;

        switch ( Common.random.nextInt( SimulationRunner.breakdownProbabilityConstant ) )
        {
            case 0 : arm     = null ;  System.out.printf( "Robot %02d : Arm broken!%n"     , serialNo ) ;  refresh = true ;  break ;
            case 1 : payload = null ;  System.out.printf( "Robot %02d : Payload broken!%n" , serialNo ) ;  refresh = true ;  break ;
            case 2 : logic   = null ;  System.out.printf( "Robot %02d : Logic broken!%n"   , serialNo ) ;  refresh = true ;  break ;
        }

        if ( refresh == true )  { SimulationRunner.robotsDisplay.repaint() ; }
    }

    @Override public void run ()
    {
        while ( SimulationRunner.factory.stopProduction == false )
        {
            wearAndTear() ;

            if ( (arm != null) && (payload != null) && (logic != null) )
            {
                logicRunCount++ ;
                logic.run( this ) ;
            }
            else
            {
                breakdownCount++ ;
                System.out.printf( "Robot %02d : Waiting for maintenance!%n" , serialNo ) ;    isWaiting = true ;
                SimulationRunner.robotsDisplay.repaint() ;

                synchronized ( this )
                {
                    while ( (arm == null) | (payload == null) | (logic == null) )
                    {
                        try                               { wait() ;         }
                        catch ( InterruptedException ex ) { /* Do nothing */ }

                        if ( SimulationRunner.factory.stopProduction == true )  { break ; }
                    }
                }

                if ( SimulationRunner.factory.stopProduction == true )  { break ; }
                System.out.printf( "Robot %02d : Fixed, going back to work.%n" , serialNo ) ;    isWaiting = false ;
                SimulationRunner.robotsDisplay.repaint() ;
            }

            try                                { Thread.sleep( SimulationRunner.robotSleepDurationConstant ) ; }
            catch ( InterruptedException ex )  { /* Do nothing */                                    }
        }

        synchronized ( System.out )
        {
            System.out.printf( "Robot %02d : Reporting and shutting down%n" , serialNo       ) ;
            System.out.printf( "  Logic run count : %d%n"                   , logicRunCount  ) ;
            System.out.printf( "  Breakdown count : %d%n"                   , breakdownCount ) ;
        }

        isShutdown = true ;
        SimulationRunner.robotsDisplay.repaint() ;
        SimulationRunner.factory.initiateStop() ;
    }
}