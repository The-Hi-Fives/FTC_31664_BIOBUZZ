package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Right Hive Autonomous", group = "StarterBot Auto")
public class RightHiveAuto extends LinearOpMode {

    RobotEngine robot = new RobotEngine();
    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() throws InterruptedException {

        robot.init(hardwareMap);

        waitForStart();
        runtime.reset();

        // put auto code here




    }
}
