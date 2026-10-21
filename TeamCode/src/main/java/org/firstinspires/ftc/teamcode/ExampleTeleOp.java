package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class ExampleTeleOp extends OpMode {
    RobotEngine robot = new RobotEngine();

    @Override
    public void init() {

        robot.init(hardwareMap);

    }

    @Override
    public void loop() {

        double drive  = -gamepad1.left_stick_y;
        double strafe =  gamepad1.left_stick_x;
        double rotate =  gamepad1.right_stick_x;

        robot.macanumDrive(drive,strafe,rotate,1000);

    }
}
