package project.parts.logics;

import project.SimulationRunner;
import project.components.Factory;
import project.components.ProductionLine;
import project.components.Robot;
import project.parts.Part;
import project.utility.Common;
import project.utility.SmartFactoryException;

import java.util.List;

public class Supplier extends Logic
{
    @Override public void run ( Robot robot )
    {
        synchronized (Common.supplier){//one supplier can work at a time

            ProductionLine pl = SimulationRunner.factory.productionLine;

            synchronized (pl.parts){//ProductionLine can be accessed by one thread at a time

                if(pl.maxCapacity >pl.parts.size()){//If production line is not full, create and add random part
                    //Creates a part by selecting a part name from Common part names.
                    Part p = Factory.createPart(Common.partNames[Common.random.nextInt(Common.partNames.length)]);
                    pl.parts.add(p); // Part is added to Production Line
                    System.out.printf(
                            "Robot %02d : Supplying a random part on production line.%n",
                            Common.get(robot, "serialNo"));
                }
                else if(pl.maxCapacity ==pl.parts.size()){//If production line is full, removes a random part
                    pl.parts.remove(Common.random.nextInt(pl.parts.size() ));
                    System.out.printf(
                            "Robot %02d : Production line is full, removing a random part from production line.%n",
                            Common.get(robot, "serialNo"));
                }
            }


            synchronized (Common.newPart){
                //Notifying waiting builders by telling there is a new part, if they are not already notified

                    Common.newPart = true;
                    List<Robot> robots = SimulationRunner.factory.robots;
                    System.out.printf( "Robot %02d : Waking up waiting builders.%n", Common.get(robot, "serialNo"));
                    for(int i=0; i<robots.size();i++){// look for builder in working robots
                        Robot r = robots.get(i);
                        try {//notify if robot is builder
                            Robot tmp = Factory.createBase();
                            Part logic = (Part) Common.get(tmp, "logic");
                            if(logic == null) continue;
                            if (logic.getClass() == Builder.class) {
                                synchronized (logic) {
                                    logic.notify();
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

            }
            synchronized (SimulationRunner.productionLineDisplay){ //Production Line will be repainted with changed parts
                SimulationRunner.productionLineDisplay.repaint();

            }

        }


    }
}