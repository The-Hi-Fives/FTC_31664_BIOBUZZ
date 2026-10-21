package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class RobotEngine {
    public DcMotorEx backLeftDrive;
    public DcMotorEx backRightDrive;
    public DcMotorEx frontLeftDrive;
    public DcMotorEx frontRightDrive;
    public DcMotorEx launcher;
    public DcMotor intake;

    public CRServo leftIntakeServo;
    public CRServo rightIntakeServo;
    public CRServo windmillServo;

    public HuskyLens huskyLens;
    public Limelight3A limelight;
    public GoBildaPinpointDriver pinpoint;

    public void init(HardwareMap hardMap) {

        // define and init motors
        frontLeftDrive   = hardMap.get(DcMotorEx.class, "front_left_drive");
        frontRightDrive  = hardMap.get(DcMotorEx.class, "front_right_drive");
        backLeftDrive    = hardMap.get(DcMotorEx.class, "back_left_drive");
        backRightDrive   = hardMap.get(DcMotorEx.class, "back_right_drive");
        intake           = hardMap.get(DcMotor.class, "intake");
        launcher         = hardMap.get(DcMotorEx.class, "launcher");

        //pinpoint         = hard_map.get(GoBildaPinpointDriver.class,"pinpoint");
        //huskyLens        = hard_map.get(HuskyLens.class, "huskylens");

        // reset motor velocity
        frontLeftDrive.setVelocity(0);
        frontRightDrive.setVelocity(0);
        backLeftDrive.setVelocity(0);
        backRightDrive.setVelocity(0);

        // set all of these motors to run with encoders
        frontLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // define and init servos
        windmillServo    = hardMap.get(CRServo.class, "windmill");
        leftIntakeServo  = hardMap.get(CRServo.class, "left_intake_servo");
        rightIntakeServo = hardMap.get(CRServo.class, "right_intake_servo");

        //limelight.pipelineSwitch(0);

        //limelight.start();

        //pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        //pinpoint.setOffsets();
        //pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD,GoBildaPinpointDriver.EncoderDirection.FORWARD);

        //pinpoint.resetPosAndIMU();

    }

    public void macanumDrive(double drive, double strafe, double rotate, double speed) {

        // multiply input values by speed.
        drive *= speed;
        strafe *= speed;
        rotate *= speed;

        // get the current robot rotation for
        // field centric.
        double heading = 0;//pinpoint.getHeading(AngleUnit.RADIANS);

        // Get the cosine and sine of the heading.
        double cosAngle = Math.cos((Math.PI / 2) - heading);
        double sinAngle = Math.sin((Math.PI / 2) - heading);

        // Adjust the strafe and drive accordingly
        double globalStrafe = drive * cosAngle + strafe * sinAngle;
        double globalDrive  = drive * cosAngle - strafe * sinAngle;

        // get the speeds for all the motors.
        double[] speeds = {
                (drive + strafe + rotate),
                (drive - strafe - rotate),
                (drive - strafe + rotate),
                (drive + strafe - rotate)
        };

        // Because we are adding vectors and motors only take values between
        // [-1,1] we may need to normalize them.

        // Loop through all values in the speeds[] array and find the greatest
        // *magnitude*.  Not the greatest velocity.
        double max = Math.abs(speeds[0]);
        for (double currentSpeed : speeds) {
            if (max < Math.abs(currentSpeed)) max = Math.abs(currentSpeed);
        }

        for (int i = 0; i <= 3; i++) {
            if (max > 1.0) {
                speeds[i] /= max;
            }
            speeds[i] *= speed;
        }

        // If and only if the maximum is outside the range we want it to be,
        // normalize all the other speeds based on the given speed value.

        // apply the calculated values to the motors.
        frontLeftDrive.setVelocity(speeds[0]);
        frontRightDrive.setPower(speeds[1]);
        backLeftDrive.setPower(speeds[2]);
        backRightDrive.setPower(speeds[3]);
    }
}
