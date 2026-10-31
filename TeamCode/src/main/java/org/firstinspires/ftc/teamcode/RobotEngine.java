package org.firstinspires.ftc.teamcode;

import android.hardware.input.InputManager;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.ImuOrientationOnRobot;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;

public class RobotEngine {

    enum BALL_TYPE {
        NECTAR,
        POLLEN
    }
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
    public IMU imu;
    public GoBildaPinpointDriver pinpoint;

    // Nectar
    public final int NECTAR_LAUNCHER_TARGET_VELOCITY = 1425;
    public final int NECTAR_LAUNCHER_MIN_VELOCITY = 1300;

    // Pollen
    public final int POLLEN_LAUNCHER_TARGET_VELOCITY = 1100;
    public final int POLLEN_LAUNCHER_MIN_VELOCITY = 975;
    public void init(HardwareMap hardMap) {

        // define and init motors
        frontLeftDrive   = hardMap.get(DcMotorEx.class, "front_left_drive");
        frontRightDrive  = hardMap.get(DcMotorEx.class, "front_right_drive");
        backLeftDrive    = hardMap.get(DcMotorEx.class, "back_left_drive");
        backRightDrive   = hardMap.get(DcMotorEx.class, "back_right_drive");
        intake           = hardMap.get(DcMotor.class, "intake");
        launcher         = hardMap.get(DcMotorEx.class, "launcher");

        //pinpoint         = hardMap.get(GoBildaPinpointDriver.class,"pinpoint");
        //huskyLens        = hardMap.get(HuskyLens.class, "huskylens");

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

        frontLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // set the direction of all macanum motors
        backLeftDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        frontLeftDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotorSimple.Direction.FORWARD);

        // define and init servos
        windmillServo    = hardMap.get(CRServo.class, "windmill");
        leftIntakeServo  = hardMap.get(CRServo.class, "left_intake_servo");
        rightIntakeServo = hardMap.get(CRServo.class, "right_intake_servo");

        // set the direction of all the servos
        rightIntakeServo.setDirection(DcMotorSimple.Direction.REVERSE);
        windmillServo.setDirection(DcMotorSimple.Direction.REVERSE);

        //limelight.pipelineSwitch(0);

        //limelight.start();

        //pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        //pinpoint.setOffsets();
        //pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD,GoBildaPinpointDriver.EncoderDirection.FORWARD);

        //pinpoint.resetPosAndIMU();

        //imu.resetYaw();

    }

    public void macanumDrive(double drive, double strafe, double rotate, double speed, double heading) {

        // get the current robot rotation for
        // field centric.

        // Get the cosine and sine of the heading.
        double cosAngle = Math.cos((Math.PI / 2) - heading);
        double sinAngle = Math.sin((Math.PI / 2) - heading);

        // Adjust the strafe and drive accordingly
        double globalStrafe = drive * cosAngle + strafe * sinAngle;
        double globalDrive  = drive * sinAngle - strafe * cosAngle;

        // get the speeds for all the motors.
        double[] speeds = {
                (globalDrive + globalStrafe + rotate),
                (globalDrive - globalStrafe - rotate),
                (globalDrive - globalStrafe + rotate),
                (globalDrive + globalStrafe - rotate)
        };

        // Because we are adding vectors and motors only take values between
        // [-1,1] we may need to normalize them.

        // Loop through all values in the speeds[] array and find the greatest
        // magnitude.  Not the greatest velocity.
        double max = Math.abs(speeds[0]);
        for (double currentSpeed : speeds) {
            if (max < Math.abs(currentSpeed)) max = Math.abs(currentSpeed);
        }

        //max = Math.max(Math.abs(speeds[0]), Math.abs(speeds[1]));
        //max = Math.max(max, Math.abs(speeds[2]));
        //max = Math.max(max, Math.abs(speeds[3]));

        if (max > 1.0) {
            speeds[0]  /= max;
            speeds[1] /= max;
            speeds[2]   /= max;
            speeds[3]  /= max;
        }

        // If and only if the maximum is outside the range we want it to be,
        // normalize all the other speeds based on the given speed value.

        // apply the calculated values to the motors.
        frontLeftDrive.setVelocity(speeds[0] * speed);
        frontRightDrive.setVelocity(speeds[1] * speed);
        backLeftDrive.setVelocity(speeds[2] * speed);
        backRightDrive.setVelocity(speeds[3] * speed);

    }

    public void launchTeleOp(boolean launch_button, BALL_TYPE ball_type, OpMode opmode) { // run every frame

        // get the target and minimum velocity.
        int target_velocity = (ball_type == BALL_TYPE.NECTAR) ? NECTAR_LAUNCHER_TARGET_VELOCITY : POLLEN_LAUNCHER_TARGET_VELOCITY;
        int minimum_velocity = (ball_type == BALL_TYPE.NECTAR) ? NECTAR_LAUNCHER_MIN_VELOCITY : POLLEN_LAUNCHER_MIN_VELOCITY;

        // start the motor if the launch button is down.
        if (launch_button) {
            launcher.setVelocity(target_velocity);
        } else {
            launcher.setVelocity(0);
        }

        // wait until the velocity is higher than the minimum velocity, then start the windmill.
        if (launch_button && launcher.getVelocity() > minimum_velocity) {
            windmillServo.setPower(1);
        } else {
            windmillServo.setPower(0);
        }

        opmode.telemetry.addData("Ball Type", ball_type);

    }

    public void intakeTeleOp(double intake_trigger, double outtake_trigger, boolean launch_button) {

        double intakePower = intake_trigger - outtake_trigger;

        if (launch_button) {
            intakePower += 0.5;
        }

        intake.setPower(intakePower);
        leftIntakeServo.setPower(intakePower);
        rightIntakeServo.setPower(intakePower);

    }
    public void intakeAuto(double intakePower) {

        intake.setPower(intakePower);
        leftIntakeServo.setPower(intakePower);
        rightIntakeServo.setPower(intakePower);

    }

    double getRobotHeading() {
        // get the yaw of the robot
        return 0;//imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
    }

    public void launchAuto(LinearOpMode opmode, BALL_TYPE ball_type) {

        // get the target and minimum velocities.
        int target_velocity = ball_type.equals(BALL_TYPE.NECTAR) ? NECTAR_LAUNCHER_TARGET_VELOCITY : POLLEN_LAUNCHER_TARGET_VELOCITY;
        int minimum_velocity = ball_type.equals(BALL_TYPE.NECTAR) ? NECTAR_LAUNCHER_MIN_VELOCITY : POLLEN_LAUNCHER_MIN_VELOCITY;

        // set the velocity
        launcher.setVelocity(target_velocity);

        // wait until the velocity is larger then the minimum velocity
        while (opmode.opModeIsActive() && launcher.getVelocity() > minimum_velocity) {
            opmode.idle();
        }

        // turn on the windmill servo.
        windmillServo.setPower(1);

        // wait a second
        opmode.sleep(1000);

        // stop everything
        windmillServo.setPower(0);
        launcher.setVelocity(0);

    }
}
