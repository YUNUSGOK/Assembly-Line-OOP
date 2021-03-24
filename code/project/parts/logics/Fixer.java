package project.parts.logics;

import project.SimulationRunner;
import project.components.Factory;
import project.components.Robot;
import project.parts.Part;
import project.utility.Common;

import java.util.List;

public class Fixer extends Logic
{
    @Override public void run ( Robot robot )
    {
        boolean firstWait = true;

        while (!Common.newBroken) { // While there is no new broken part, fixers wait
            if (firstWait) {
                System.out.printf("Robot %02d : Nothing to fix, waiting!%n", Common.get(robot, "serialNo"));
                firstWait = false;
            }
            if (SimulationRunner.factory.stopProduction == true) {
                return;
            }//if production stops, Builder will stop waiting
            synchronized (this) {
                //fixer will wait until a inspector notifies or certain amount of time passes for production stop check
                try { wait(SimulationRunner.robotSleepDurationConstant); }
                catch (InterruptedException ex) { /* Do nothing */ }
            }
        }
        System.out.printf("Robot %02d : Fixer woke up, going back to work.%n", Common.get(robot, "serialNo"));


        synchronized (Common.fixer){//one fixer can work at a time

            List<Robot> brokenRobots = SimulationRunner.factory.brokenRobots;
            synchronized (brokenRobots)
            {
                for(int i = 0; i<brokenRobots.size();i++){
                    Robot r = brokenRobots.get(i);
                    synchronized (r){
                        Part arm = (Part)Common.get(r,"arm");
                        Part payload = (Part)Common.get(r,"payload");
                        Part logic = (Part)Common.get(r,"logic");
                        if(arm == null){ //if arm is borken create a new arm
                            Common.set(r,"arm", Factory.createPart("Arm"));
                        }
                        if(logic == null){// if logic is borken find and create logic for corresponding payload
                            String logicName = Common.pairs.get(payload.getClass().getSimpleName());
                            Common.set(r,"logic",Factory.createPart(logicName));
                        }
                        if(payload == null){// if payload is broken find and create payload for corresponding logic
                            String payloadName = Common.pairs.get(logic.getClass().getSimpleName());
                            Common.set(r,"payload",Factory.createPart(payloadName));
                        }
                        System.out.printf("Robot %02d : Fixed and waken up robot (%02d).%n",
                                Common.get(robot, "serialNo"),
                                Common.get(r, "serialNo"));
                        r.notify();//Robot is fixed, and notified to wake up
                        brokenRobots.remove(r);// remove from broken robots list
                        if(brokenRobots.isEmpty()){// if no broken robots, fixer will wait.

                        }

                    }
                    break;

                }

            }
            synchronized (Common.newBroken ){
                Common.newBroken = false;
            }
        }


    }
}