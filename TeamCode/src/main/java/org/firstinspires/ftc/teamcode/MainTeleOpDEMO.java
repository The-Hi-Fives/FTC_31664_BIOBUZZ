package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

// adb connect 192.168.43.1:5555

@TeleOp(name = "TeleOp DEMO", group = "StarterBot DEMO")
@Disabled
public class MainTeleOpDEMO extends OpMode {
    RobotEngine robot = new RobotEngine();

    boolean slow_button_down = false;
    boolean slower_button_down = false;

    public final int DRIVE_VELOCITY = 2800; // max is 2800

    // Nectar
    public final int LAUNCHER_TARGET_VELOCITY = 2000;
    public final int LAUNCHER_MIN_VELOCITY = 1700;


    @Override
    public void init() {

        robot.init(hardwareMap);
        robot.initGamepad(gamepad1, gamepad2);

    }

    @Override
    public void loop() {

        /// drive
        // get the joystick inputs
        double drive  = -robot.gamepad1.left_stick_y;
        double strafe =  robot.gamepad1.left_stick_x;
        double rotate =  robot.gamepad1.right_stick_x;

        // if "y" is down, don't do "x"'s input, and vice versa
        boolean slow_button = robot.gamepad1.y;
        boolean slower_button = robot.gamepad1.x;

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
        boolean launch_button = robot.gamepad2.right_bumper;

        launchDEMO();

        /// intake
        // get input
        double intake_trigger = (gamepad1.right_trigger >= 0.3)
                ? robot.gamepad1.right_trigger  // true
                : robot.gamepad2.right_trigger; // false
        double outtake_trigger = (gamepad1.left_trigger >= 0.3)
                ? robot.gamepad1.left_trigger  // true
                : robot.gamepad2.left_trigger; // false

        robot.intakeTeleOp(intake_trigger, outtake_trigger, launch_button);

        telemetry.update();

    }

    public void launchDEMO() { // run every frame

        boolean launch_button = robot.gamepad2.right_bumper;
        boolean windmill_reverse_button = robot.gamepad2.dpad_left;


        // get the target, minimum, and maximum velocity.

        // start the motor if the launch button is down.
        if (launch_button) {

            robot.launcher.setVelocity(LAUNCHER_TARGET_VELOCITY);

        } else {

            robot.launcher.setVelocity(0);

        }

        // wait until the velocity is higher than the minimum velocity, then start the windmill.
        if (launch_button && robot.launcher.getVelocity() > LAUNCHER_MIN_VELOCITY) {

            robot.leftWindmillServo.setPower(1);
            robot.rightWindmillServo.setPower(1);

        } else if (windmill_reverse_button) {

            robot.leftWindmillServo.setPower(-1);
            robot.rightWindmillServo.setPower(-1);

        } else {

            robot.leftWindmillServo.setPower(0);
            robot.rightWindmillServo.setPower(0);

        }

        telemetry.addData("launcher Velocity", robot.launcher.getVelocity());

    }

}
