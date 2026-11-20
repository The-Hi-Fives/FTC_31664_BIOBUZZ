package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Right Hive Launch Autonomous", group = "StarterBot Auto")

@Disabled // DELETE AFTER !!!!!!!

public class RightHiveLaunchAuto extends LinearOpMode {

    RobotEngine robot = new RobotEngine();
    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() throws InterruptedException {

        robot.init(hardwareMap);

        waitForStart();
        runtime.reset();

        // put auto code here

        // move slightly back

        // Launch pre-loaded pollen




    }
}
