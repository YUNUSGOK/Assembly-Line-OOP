package project.parts.logics;

import project.SimulationRunner;
import project.components.ProductionLine;
import project.components.Robot;
import project.parts.Part;
import project.utility.Common;

import java.util.List;

public class Inspector extends Logic
{
    @Override public void run ( Robot robot )
    {

        synchronized (Common.inspector){//one inspector can work at a time

            List<Robot> robots = SimulationRunner.factory.robots;
            synchronized (robots){
                for(Robot r : robots){ // inspect every robot in working robots
                    // if robot is already in broken list skip
                    if(SimulationRunner.factory.brokenRobots.contains(r)) continue;

                    Part rarm = (Part)Common.get(r,"arm");
                    Part rpayload = (Part)Common.get(r,"payload");
                    Part rlogic = (Part)Common.get(r,"logic");
                    if((rarm == null) || (rpayload == null) || (rlogic == null))
                    {
                        System.out.printf(
                                "Robot %02d : Detected a broken robot (%02d), adding it to broken robots list.%n",
                                Common.get(robot, "serialNo"),
                                Common.get(r, "serialNo"));
                        //add detected broken robot to broken list
                        synchronized (SimulationRunner.factory.brokenRobots)
                        {
                            SimulationRunner.factory.brokenRobots.add(r);
                        }
                        //Notifying waiting fixers by telling there is a new broken
                        synchronized (Common.newBroken){
                            Common.newBroken = true;
                            List<Robot> workingRobots = SimulationRunner.factory.robots;
                            System.out.printf("Robot %02d : Notifying waiting fixers.%n", Common.get(robot, "serialNo"));
                            for(int i=0; i<workingRobots.size();i++){// look for fixer in working robots
                                Robot w = workingRobots.get(i);
                                try{//notify if robot is fixer
                                    Part logic = (Part) Common.get(w,"logic");
                                    if(logic.getClass()==Fixer.class){
                                        synchronized (logic){
                                            logic.notify();
                                        }
                                    }
                                }catch (Exception e){
                                    //Can not notify(do nothing)
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}