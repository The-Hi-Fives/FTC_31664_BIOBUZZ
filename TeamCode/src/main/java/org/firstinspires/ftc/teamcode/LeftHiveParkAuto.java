package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Left Hive Park Autonomous", group = "StarterBot Auto")
public class LeftHiveParkAuto extends LinearOpMode {

    RobotEngine robot = new RobotEngine();
    private ElapsedTime runtime = new ElapsedTime();
    private double current_runtime = 0;

    enum AutoStep {
        DRIVE_FORWARD,
        DRIVE_RIGHT_INTO_PARK,
        END,
    }

    AutoStep current_step = AutoStep.DRIVE_FORWARD;

    @Override
    public void runOpMode() throws InterruptedException {

        robot.init(hardwareMap);

        waitForStart();

        runtime.reset();
        current_runtime = 0;

        // put auto code here

        while (opModeIsActive() && runtime.seconds() < 30) {
            switch (current_step) {
                case DRIVE_FORWARD: // drive forward slightly to not get caught on the walls

                    robot.macanumDrive(1.0,0,0,500,0);

                    if (runtime.milliseconds() > current_runtime + 1000) { // wait
                        current_step = AutoStep.DRIVE_RIGHT_INTO_PARK;
                        current_runtime = runtime.milliseconds();
                    }

                    break;

                case DRIVE_RIGHT_INTO_PARK: // drive into park

                    robot.macanumDrive(0,1.0,0,700,0);

                    if (runtime.milliseconds() > current_runtime + 3000) { // wait
                        current_step = AutoStep.END; // end the auto
                        current_runtime = runtime.milliseconds();
                    }

                    break;

                case END:

                    robot.macanumDrive(0,0,0,0,0); // stop the robot

                    break;

            }
        }
    }
}
