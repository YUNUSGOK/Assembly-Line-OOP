package project.components;

import project.parts.Base;
import project.parts.Part;
import project.parts.logics.Supplier;
import project.utility.Common;
import project.utility.SmartFactoryException;
import sun.reflect.Reflection;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Factory
{
    private static int nextSerialNo = 1 ;

    public static Base createBase ()
    {
        return (Base)Common.createBase(nextSerialNo++);
    }

    public static Part createPart (String name )
    {
        Part temp = null;
        //get the specific part factory by given name
        Method PartFactory = Common.AbstractFactory(name);
        try{
            //create the specific part from that factory
            temp = (Part) PartFactory.invoke(null);

        }catch (Exception e){
            throw new SmartFactoryException("Failed: createPart!" );
        }
        return temp;
    }

    public  int            maxRobots      ;
    public List<Robot>     robots         ;
    public ProductionLine  productionLine ;
    public  Storage        storage        ;
    public  List<Robot>    brokenRobots   ;
    public  boolean        stopProduction ;

    public Factory ( int maxRobots , int maxProductionLineCapacity , int maxStorageCapacity )
    {
        this.maxRobots      = maxRobots                                       ;
        this.robots         = new ArrayList<>()                               ;
        this.productionLine = new ProductionLine( maxProductionLineCapacity ) ;
        this.storage        = new Storage( maxStorageCapacity        )        ;
        this.brokenRobots   = new ArrayList<>()                               ;
        this.stopProduction = false                                           ;

        Base robot ;

        robot = createBase()                                             ;
        Common.set( robot , "arm"     , createPart( "Arm"            ) ) ;
        Common.set( robot , "payload" , createPart( "Gripper"        ) ) ;
        Common.set( robot , "logic"   , createPart( "Supplier"       ) ) ;
        robots.add(robot ) ;

        robot = createBase()                                             ;
        Common.set( robot , "arm"     , createPart( "Arm"            ) ) ;
        Common.set( robot , "payload" , createPart( "Welder"         ) ) ;
        Common.set( robot , "logic"   , createPart( "Builder"        ) ) ;
        robots.add(robot ) ;

        robot = createBase()                                             ;
        Common.set( robot , "arm"     , createPart( "Arm"            ) ) ;
        Common.set( robot , "payload" , createPart( "Camera"         ) ) ;
        Common.set( robot , "logic"   , createPart( "Inspector"      ) ) ;
        robots.add(robot ) ;

        robot = createBase()                                             ;
        Common.set( robot , "arm"     , createPart( "Arm"            ) ) ;
        Common.set( robot , "payload" , createPart( "Camera"         ) ) ;
        Common.set( robot , "logic"   , createPart( "Inspector"      ) ) ;
        robots.add(robot ) ;

        robot = createBase()                                             ;
        Common.set( robot , "arm"     , createPart( "Arm"            ) ) ;
        Common.set( robot , "payload" , createPart( "MaintenanceKit" ) ) ;
        Common.set( robot , "logic"   , createPart( "Fixer"          ) ) ;
        robots.add(robot ) ;

        robot = createBase()                                             ;
        Common.set( robot , "arm"     , createPart( "Arm"            ) ) ;
        Common.set( robot , "payload" , createPart( "MaintenanceKit" ) ) ;
        Common.set( robot , "logic"   , createPart( "Fixer"          ) ) ;
        robots.add(robot ) ;
    }

    public void start ()
    {
        for ( Robot r : robots )  { new Thread( r ).start() ; }
    }

    public void initiateStop ()
    {
        stopProduction = true ;

        synchronized ( robots )
        {
            for ( Robot r : robots )  { synchronized ( r )  { r.notifyAll() ; } }
        }

        synchronized ( productionLine )  { productionLine.notifyAll() ; }
        synchronized ( brokenRobots   )  { brokenRobots  .notifyAll() ; }
    }
}