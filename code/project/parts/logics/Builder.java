package project.parts.logics;

import project.SimulationRunner;
import project.components.ProductionLine;
import project.components.Robot;
import project.components.Storage;
import project.parts.Base;
import project.parts.Part;
import project.utility.Common;
import java.util.Arrays;
import java.util.List;

public class Builder extends Logic
{
    @Override public void run ( Robot robot )
    {
        boolean firstWait = true;


        while(!Common.newPart){ // While there is no new part in the productionLine builders wait
            if (firstWait){ //if it starts waiting, print waiting message
                System.out.printf("Robot %02d : Builder cannot build anything, waiting!%n", Common.get(robot, "serialNo"));
                firstWait = false;
            }
            if ( SimulationRunner.factory.stopProduction == true )  { return ; }//if production stops, Builder will stop waiting
            synchronized (this)
            {
                //builder will wait until a supplier notifies or certain amount of time passes for production stop check
                try                               { wait(SimulationRunner.robotSleepDurationConstant);          }
                catch ( InterruptedException ex ) { /* Do nothing */ }
            }
        }
        System.out.printf("Robot %02d : Builder woke up, going back to work.%n", Common.get(robot, "serialNo"));

        synchronized (Common.builder){ //one builder can work at a time
            ProductionLine pl = SimulationRunner.factory.productionLine; // production line
            List<Part> parts = pl.parts; //parts in production line
            boolean fixed = false; // Check If yhe possibility of fulfilling a production step
            Part r = null; //a base in production line
            Part x = null; // needed for selected base
            synchronized (parts){//ProductionLine can be accessed by one thread at a time
                for(int i = 0; i<parts.size();i++){ //searching for a base in production line
                    if(parts.get(i).getClass() == Base.class){ // check if the part[i] is base
                        r = parts.get(i);
                        synchronized (r) { // this base can be accesed by one thread at a time
                            Part arm = (Part) Common.get(r, "arm"); //arm of base
                            Part payload = (Part) Common.get(r, "payload"); //payload of base
                            Part logic = (Part) Common.get(r, "logic");//logic pf base
                            String[] neededPartClass = new String[]{}; //Set of possible parts can be fit the base
                            String neededPartField = ""; // which part is needed for base
                            if (arm == null) { // if arm is null,  look  for a Arm in production line
                                neededPartClass = new String[]{"Arm",};
                                neededPartField = "arm";
                            }
                            else if (payload  == null) {// if payload is null,  look for any type of payload
                                neededPartClass = new String[]{"Camera","Gripper","MaintenanceKit", "Welder"};
                                neededPartField = "payload";
                            }
                            else if (logic == null) {// if logic is null,  look logic for corresponding payload
                                neededPartClass = new String[]{Common.pairs.get(payload.getClass().getSimpleName()),};
                                neededPartField = "logic";
                            }
                            else{// if any of the parts of base is not null, robot is completed
                                fixed = true;
                                synchronized (parts){
                                    parts.remove(r);
                                }
                                //if is there a space in working robots place the completed robot in there
                                if(SimulationRunner.factory.robots.size()<SimulationRunner.factory.maxRobots){
                                    synchronized (SimulationRunner.factory.robots){
                                        SimulationRunner.factory.robots.add((Robot)r);
                                        synchronized (SimulationRunner.robotsDisplay){
                                            SimulationRunner.robotsDisplay.repaint();
                                        }
                                    }
                                    new Thread( (Robot)r ).start() ;// worker robots executes its logic when its added.
                                }
                                else {
                                    //if is there no space in working robots place it in the storage
                                    synchronized (SimulationRunner.factory.storage.robots){

                                        Storage s = SimulationRunner.factory.storage;
                                        if(s.robots.size()<s.maxCapacity){//check here is a space in storage

                                            SimulationRunner.factory.storage.robots.add((Robot)r);
                                            synchronized (SimulationRunner.storageDisplay){

                                                SimulationRunner.storageDisplay.repaint();
                                            }
                                        }
                                        if(s.robots.size()==s.maxCapacity){ // if storage is full stop production
                                            synchronized (SimulationRunner.factory){
                                                SimulationRunner.factory.stopProduction= true;

                                            }
                                        }
                                    }
                                }
                            }

                            List<String> list = Arrays.asList(neededPartClass);// list of possible needed parts
                            for(int j = 0; j<parts.size() && !fixed;j++){ //look for needed part
                                x = parts.get(j);
                                if(list.contains(x.getClass().getSimpleName())){//needed part found
                                    fixed = true;
                                    Common.set(r,neededPartField,x);//assign needed part to base
                                    parts.remove(x);// remove the needed part from productio line
                                    break; //stop searching needed part
                                }
                            }

                            if(fixed){// Builder  built something
                                System.out.printf(
                                        "Robot %02d : Builder attached some parts or relocated a completed robot.%n",
                                        Common.get(robot, "serialNo"),
                                        Common.get(r, "serialNo"));
                                break;
                            }
                        }
                    }
                }


            }
            synchronized (SimulationRunner.productionLineDisplay){
                SimulationRunner.productionLineDisplay.repaint();

            }
            synchronized (Common.newPart){
                Common.newPart = false;
            }
        }



    }
}