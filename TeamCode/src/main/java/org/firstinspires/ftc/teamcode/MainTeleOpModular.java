package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

// adb connect 192.168.43.1:5555

@TeleOp(name = "Main TeleOp Modular", group = "StarterBot")
public class MainTeleOpModular extends OpMode {
    RobotEngine robot = new RobotEngine();

    RobotEngine.BALL_TYPE ball_type = RobotEngine.BALL_TYPE.POLLEN;
    boolean ball_type_toggle_down = false;

    boolean slow_button_down = false;
    boolean slower_button_down = false;

    public final int DRIVE_VELOCITY = 2100;

    @Override
    public void init() {

        robot.init(hardwareMap);

    }

    @Override
    public void loop() {

        /// drive
        // get the joystick inputs
        double drive  = -gamepad1.left_stick_y;
        double strafe =  gamepad1.left_stick_x;
        double rotate =  gamepad1.right_stick_x;

        // if "y" is down, don't do "x"'s input, and vice versa
        boolean slow_button = gamepad1.y;
        boolean slower_button = gamepad1.x;

        int driveVelocity = DRIVE_VELOCITY;

        if (slow_button && !slower_button_down) { // slow

            // set the slow_down value to be true and decrease the driveVelocity by half
            slow_button_down = true;
            driveVelocity /= 2;

        } else if (slower_button && !slow_button_down) { // slower

            // set the slower_down value to be true and decrease the driveVelocity by a lot
            slower_button_down = true;
            driveVelocity /= 3;

        }
        if (!slow_button) { // reset slow
            slow_button_down = false;
        }
        if (!slower_button) { // reset slower
            slower_button_down = false;
        }

        telemetry.addData("slow: ", slow_button_down);
        telemetry.addData("slower: ", slower_button_down);

        robot.macanumDrive(drive, strafe, rotate, driveVelocity, robot.getRobotHeading());

        /// launch
        // get button inputs
        boolean toggle_button = (gamepad2.a || gamepad2.b);
        boolean launch_button = gamepad2.right_bumper;
        boolean windmill_reverse_button = gamepad2.dpad_left;

        // change ball_type if toggle button is just pressed
        if (toggle_button && !ball_type_toggle_down) {

            ball_type_toggle_down = true;

            /// runs 1 time
            // toggle the ball_type (between nectar and pollen)
            ball_type = (ball_type.equals(RobotEngine.BALL_TYPE.NECTAR))
                    ? RobotEngine.BALL_TYPE.POLLEN  // true
                    : RobotEngine.BALL_TYPE.NECTAR; // false

        } else if (!toggle_button) {

            ball_type_toggle_down = false;

        }

        robot.launchTeleOp(launch_button, ball_type, windmill_reverse_button, this);

        /// intake
        // get input
        double intake_trigger = (gamepad1.right_trigger >= 0.3)
                ? gamepad1.right_trigger  // true
                : gamepad2.right_trigger; // false
        double outtake_trigger = (gamepad1.left_trigger >= 0.3)
                ? gamepad1.left_trigger  // true
                : gamepad2.left_trigger; // false

        robot.intakeTeleOp(intake_trigger, outtake_trigger, launch_button);

        telemetry.update();

    }
}
