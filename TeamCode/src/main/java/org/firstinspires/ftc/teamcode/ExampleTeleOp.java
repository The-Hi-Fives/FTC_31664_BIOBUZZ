package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class ExampleTeleOp extends OpMode {
    RobotEngine robot = new RobotEngine();

    RobotEngine.BALL_TYPE ball_type = RobotEngine.BALL_TYPE.POLLEN;
    boolean ball_type_toggle_down = false;

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

        robot.macanumDrive(drive,strafe,rotate,1000);

        /// launch
        // get button inputs
        boolean toggle_button = (gamepad1.a || gamepad1.b);
        boolean launch_button = gamepad1.right_bumper;

        // change ball_type if toggle button is just pressed
        if (toggle_button && !ball_type_toggle_down) {

            ball_type_toggle_down = true;

            // toggle the ball_type (between nectar and pollen)
            ball_type = (ball_type == RobotEngine.BALL_TYPE.NECTAR) ? RobotEngine.BALL_TYPE.POLLEN : RobotEngine.BALL_TYPE.NECTAR ;

        } else if (!toggle_button) {

            ball_type_toggle_down = false;

        }


        robot.launchTeleOp(launch_button, RobotEngine.BALL_TYPE.NECTAR);

    }
}
