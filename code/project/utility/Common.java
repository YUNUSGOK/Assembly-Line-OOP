package project.utility;

import project.SimulationRunner;
import project.components.Factory;
import project.parts.Arm;
import project.parts.Base;
import project.parts.Part;
import project.parts.logics.Builder;
import project.parts.logics.Fixer;
import project.parts.logics.Inspector;
import project.parts.logics.Supplier;
import project.parts.payloads.Camera;
import project.parts.payloads.Gripper;
import project.parts.payloads.MaintenanceKit;
import project.parts.payloads.Welder;

import java.lang.reflect.Field;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Common
{
    public static Random random = new Random() ;

    public static synchronized Object get (Object object , String fieldName )
    {

        Field field = null;
        try{
            field = object.getClass().getDeclaredField(fieldName); //get given object's field attribute
            field.setAccessible(true); // set to public to access
            Object ret  = field.get(object); // get object's specified field value
            return ret; // return the acquired value

        } catch (Exception e){
            e.printStackTrace();
            throw new SmartFactoryException( "Failed: get!" );
        }
        // This function retrieves (gets) the private field of an object by using reflection
        // In case the function needs to throw an exception, throw this: SmartFactoryException( "Failed: get!" )
    }

    public static synchronized void set ( Object object , String fieldName , Object value )
    {


        Field field = null;
        try{
            field = object.getClass().getDeclaredField(fieldName);//get given object's field attribute
            field.setAccessible(true);// set to public to access
            field.set(object,value);// set object's specified field value to given value
        } catch (Exception e){
            throw new SmartFactoryException( "Failed: set!->" );
        }

    }
    // only one robot with each logic can work at a time
    public static Object supplier = new Object(); //supplier lock
    public static Object fixer = new Object(); //fixer lock
    public static Object builder = new Object(); // builder lock
    public static Object inspector = new Object(); //inspector lock

    public static Boolean newPart = false; // Condition for builder wait
    public static Boolean newBroken = false;// Condition for fixer wait

    public static String[] partNames = { //part names for a robot
            "Supplier", "Gripper",
            "Builder","Welder",
            "Inspector", "Camera",
            "Fixer", "MaintenanceKit",
            "Arm","Base"};

    // payload - logic combinations
    public static HashMap<String, String> pairs  = new HashMap<String, String>() {{
        put("Supplier", "Gripper");
        put("Builder", "Welder");
        put("Camera", "Inspector");
        put("Fixer", "MaintenanceKit");
        put("Gripper","Supplier");
        put("Welder","Builder");
        put("Inspector","Camera");
        put("MaintenanceKit","Fixer");
    }};
    //Base factory for
    public static Part createBase(int nextSerialNo){
        return new Base(nextSerialNo);
    }
    //Returns factory for given part name
    public static Method AbstractFactory(String name){
        Method PartFactory = null;
        try{
            PartFactory
                    = Common.class.getMethod(name+"Factory");
        }catch (Exception e){
            e.printStackTrace();
            throw new SmartFactoryException("Failed: createPart!" );
        }
        return PartFactory;
    }
    // Part factories creating a specific part
    public static Part ArmFactory(){
        return new Arm();
    }
    public static Part BuilderFactory(){
        return new Builder();
    }
    public static Part FixerFactory(){
        return new Fixer();
    }
    public static Part InspectorFactory(){
        return new Inspector();
    }
    public static Part SupplierFactory(){
        return new Supplier();
    }
    public static Part CameraFactory(){
        return new Camera();
    }
    public static Part GripperFactory(){
        return new Gripper();
    }
    public static Part MaintenanceKitFactory(){
        return new MaintenanceKit();
    }
    public static Part WelderFactory(){
        return new Welder();
    }
    public static Part BaseFactory(){
        return Factory.createBase();
    }


}