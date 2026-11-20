package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Template Autonomous", group = "StarterBot Auto")
@Disabled // delete when copy/pasted
public class TemplateAuto extends LinearOpMode {

    RobotEngine robot = new RobotEngine();
    private ElapsedTime runtime = new ElapsedTime();
    private double current_runtime = 0;

    enum AutoStep {
        FIRST_DIRECTION,
        SECOND_DIRECTION,
        END,
    }

    AutoStep current_step = AutoStep.FIRST_DIRECTION;

    @Override
    public void runOpMode() throws InterruptedException {

        robot.init(hardwareMap);

        waitForStart();

        runtime.reset();
        current_runtime = 0;

        // put auto code here

        while (opModeIsActive() && runtime.seconds() < 30) {
            switch (current_step) {
                case FIRST_DIRECTION: // do the first direction

                    // direction code

                    if (runtime.milliseconds() > current_runtime + 200) { // wait 200 milliseconds
                        current_step = AutoStep.SECOND_DIRECTION; // go to the next step
                        current_runtime = runtime.milliseconds(); // set the current_runtime
                    }

                    break;

                case SECOND_DIRECTION: // do the second

                    // direction code

                    if (runtime.milliseconds() > current_runtime + 500) { // wait 500 milliseconds
                        current_step = AutoStep.END; // end the auto
                        current_runtime = runtime.milliseconds(); // set the current_runtime
                    }

                    break;

                case END:

                    robot.macanumDrive(0,0,0,0,0); // stop the robot

                    break;

            }
        }
    }
}
