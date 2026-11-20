package org.firstinspires.ftc.teamcode;

import android.hardware.input.InputManager;

import androidx.annotation.NonNull;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/// The RobotEngine class is to easily make Opmodes without typing in the motor and servo declarations every time.
public class RobotEngine {

    enum BALL_TYPE {
        NECTAR,
        POLLEN,
        FLOWER,
    }
    public DcMotorEx backLeftDrive, backRightDrive, frontLeftDrive, frontRightDrive;
    public DcMotorEx launcher;
    public DcMotor intake;
    public CRServo leftIntakeServo, rightIntakeServo, leftWindmillServo, rightWindmillServo;
    
    //public Servo angularServo;

    //public HuskyLens huskyLens;
    public Limelight3A limelight;
    public SparkFunOTOS sparkFunOTOS;
    //public GoBildaPinpointDriver pinpoint;

    Gamepad gamepad1;
    Gamepad gamepad2;

    public final int MACANUM_SPEED = 2000;

    // Nectar
    public final int NECTAR_LAUNCHER_TARGET_VELOCITY = 1525;
    public final int NECTAR_LAUNCHER_MIN_VELOCITY = 1400;
    public final int NECTAR_LAUNCHER_MAX_VELOCITY = 1550;

    // Pollen
    public final int POLLEN_LAUNCHER_TARGET_VELOCITY = 1350;
    public final int POLLEN_LAUNCHER_MIN_VELOCITY = 1225;
    public final int POLLEN_LAUNCHER_MAX_VELOCITY = 1375;

    // Flower
    public final int FLOWER_LAUNCHER_TARGET_VELOCITY = 950;
    public final int FLOWER_LAUNCHER_MIN_VELOCITY = 825;
    public final int FLOWER_LAUNCHER_MAX_VELOCITY = 1100;

    // Angular Servo Positions
    //public final double NECTAR_ANGLE_POSITION = 0.6;
    //public final double POLLEN_ANGLE_POSITION = 0.0;

    public void init(HardwareMap hardMap) {

        // define and init motors
        frontLeftDrive   = hardMap.get(DcMotorEx.class, "front_left_drive");
        frontRightDrive  = hardMap.get(DcMotorEx.class, "front_right_drive");
        backLeftDrive    = hardMap.get(DcMotorEx.class, "back_left_drive");
        backRightDrive   = hardMap.get(DcMotorEx.class, "back_right_drive");
        intake           = hardMap.get(DcMotor.class, "intake");
        launcher         = hardMap.get(DcMotorEx.class, "launcher");

        // reset motor velocity/power
        frontLeftDrive.setVelocity(0);
        frontRightDrive.setVelocity(0);
        backLeftDrive.setVelocity(0);
        backRightDrive.setVelocity(0);

        intake.setPower(0);
        launcher.setVelocity(0);

        // set all of these motors to run with encoders
        frontLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // make sure that the drive motors brake when at zero power
        frontLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // set the direction of all macanum motors
        backLeftDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        frontLeftDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotorSimple.Direction.FORWARD);

        // set up the OTOS for field oriented driving
        //sparkFunOTOS = hardMap.get(SparkFunOTOS.class, "sensor_otos");
        //configureOTOS();

        // set up limelight to have accurate autos
        // limelight = hardMap.get(Limelight3A.class,"limelight");

        //configureLimelight();


        // define and init servos
        leftWindmillServo   = hardMap.get(CRServo.class, "left_windmill");
        rightWindmillServo  = hardMap.get(CRServo.class, "right_windmill");
        leftIntakeServo     = hardMap.get(CRServo.class, "left_intake_servo");
        rightIntakeServo    = hardMap.get(CRServo.class, "right_intake_servo");

        //angularServo        = hardMap.get(Servo.class, "angular_servo");

        // reset the power of the servos
        leftWindmillServo.setPower(0);
        rightWindmillServo.setPower(0);
        leftIntakeServo.setPower(0);
        rightIntakeServo.setPower(0);

        // set the direction of all the servos
        rightIntakeServo.setDirection(DcMotorSimple.Direction.REVERSE);
        rightWindmillServo.setDirection(DcMotorSimple.Direction.REVERSE);
        leftWindmillServo.setDirection(DcMotorSimple.Direction.REVERSE);
        leftIntakeServo.setDirection(DcMotorSimple.Direction.FORWARD);

        //angularServo.setDirection(Servo.Direction.REVERSE);

        // set the launcher coefficients
        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(40, 0, 0, 12.5));

    }

    public void initGamepad(Gamepad controller1, Gamepad controller2) {

        gamepad1 = controller1;

        if (isGamepadConnected(controller2)) {
            gamepad2 = controller2;
        } else {
            gamepad2 = controller1;
        }

    }

    void configureOTOS() { // configure

        // use inches and degrees
        sparkFunOTOS.setLinearUnit(DistanceUnit.INCH);
        sparkFunOTOS.setAngularUnit(AngleUnit.DEGREES);

        // adjust the offset of the sensor from the center of the robot
        SparkFunOTOS.Pose2D offset = new SparkFunOTOS.Pose2D(0, 0, 0);
        sparkFunOTOS.setOffset(offset);

        // set the scalar to have more accurate results
        sparkFunOTOS.setLinearScalar(1.0);
        sparkFunOTOS.setAngularScalar(1.0);

        // calibrate so it calibrates
        sparkFunOTOS.calibrateImu();

        // reset the tracking to be at the origin
        sparkFunOTOS.resetTracking();

        // starting position
        SparkFunOTOS.Pose2D currentPosition = new SparkFunOTOS.Pose2D(0, 0, 0);
        sparkFunOTOS.setPosition(currentPosition);

        // get the version
        SparkFunOTOS.Version hwVersion = new SparkFunOTOS.Version();
        SparkFunOTOS.Version fwVersion = new SparkFunOTOS.Version();
        sparkFunOTOS.getVersionInfo(hwVersion, fwVersion);

    }
    public void configureLimelight() {

        limelight.setPollRateHz(100);
        limelight.start();

    }

    public void macanumDrive(double drive, double strafe, double rotate, double speed, double heading) {

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

    public void launchTeleOp(boolean launch_button, BALL_TYPE ball_type, boolean windmill_reverse_button , OpMode opmode) { // run every frame

        // set the target position
        //double target_position = (ball_type == BALL_TYPE.NECTAR) ? NECTAR_ANGLE_POSITION : POLLEN_ANGLE_POSITION;
        //angularServo.setPosition(target_position);

        // get the target, minimum, and maximum velocity.

        int target_velocity = (ball_type == BALL_TYPE.NECTAR)
                ? NECTAR_LAUNCHER_TARGET_VELOCITY  // NECTAR
                : (ball_type == BALL_TYPE.POLLEN)
                ? POLLEN_LAUNCHER_TARGET_VELOCITY  // Pollen
                : FLOWER_LAUNCHER_TARGET_VELOCITY; // FLOWER
        int minimum_velocity = (ball_type == BALL_TYPE.NECTAR)
                ? NECTAR_LAUNCHER_MIN_VELOCITY  // NECTAR
                : (ball_type == BALL_TYPE.POLLEN)
                ? POLLEN_LAUNCHER_MIN_VELOCITY  // Pollen
                : FLOWER_LAUNCHER_MIN_VELOCITY; // FLOWER
        int maximum_velocity = (ball_type == BALL_TYPE.NECTAR)
                ? NECTAR_LAUNCHER_MAX_VELOCITY  // NECTAR
                : (ball_type == BALL_TYPE.POLLEN)
                ? POLLEN_LAUNCHER_MAX_VELOCITY  // Pollen
                : FLOWER_LAUNCHER_MAX_VELOCITY; // FLOWER

        // start the motor if the launch button is down.
        if (launch_button) {

            launcher.setVelocity(target_velocity);

        } else {

            launcher.setVelocity(0);

        }

        // wait until the velocity is higher than the minimum velocity, then start the windmill.
        if (launch_button && launcher.getVelocity() > minimum_velocity && launcher.getVelocity() < maximum_velocity) {

            leftWindmillServo.setPower(1);
            rightWindmillServo.setPower(1);

        } else if (windmill_reverse_button) {

            leftWindmillServo.setPower(-1);
            rightWindmillServo.setPower(-1);

        } else {

            leftWindmillServo.setPower(0);
            rightWindmillServo.setPower(0);

        }

        opmode.telemetry.addData("Ball Type", ball_type);

    }

    public void intakeTeleOp(double intake_trigger, double outtake_trigger, boolean launch_button) {

        // get the intake power
        double intakePower = intake_trigger - outtake_trigger;

        // add 0.5 to the intake power if launching // maybe delete
        //if (launch_button) {
        //    intakePower += 0.5;
        //}

        // set the intake servos/motors power
        intake.setPower(intakePower);
        leftIntakeServo.setPower(intakePower);
        rightIntakeServo.setPower(intakePower);

    }
    public void intakeAuto(double intakePower) {

        // set the powers of all the intake motors/servos
        intake.setPower(intakePower);
        leftIntakeServo.setPower(intakePower);
        rightIntakeServo.setPower(intakePower);

    }

    public double getRobotHeading() {

        // get the yaw of the robot
        return 0;//imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

    }

    public void launchAuto(LinearOpMode opmode, BALL_TYPE ball_type) {

        // set the target position
        //double target_position = (ball_type == BALL_TYPE.NECTAR) ? NECTAR_ANGLE_POSITION : POLLEN_ANGLE_POSITION;
        //angularServo.setPosition(target_position);

        // get the target, minimum, and maximum velocity.
        int target_velocity = (ball_type == BALL_TYPE.NECTAR)
                ? NECTAR_LAUNCHER_TARGET_VELOCITY  // true
                : POLLEN_LAUNCHER_TARGET_VELOCITY; // false
        int minimum_velocity = (ball_type == BALL_TYPE.NECTAR)
                ? NECTAR_LAUNCHER_MIN_VELOCITY  // true
                : POLLEN_LAUNCHER_MIN_VELOCITY; // false
        int maximum_velocity = (ball_type == BALL_TYPE.NECTAR)
                ? NECTAR_LAUNCHER_MAX_VELOCITY  // true
                : POLLEN_LAUNCHER_MAX_VELOCITY; // false

        // set the velocity
        launcher.setVelocity(target_velocity);

        // wait until the velocity is larger than the minimum velocity
        while (opmode.opModeIsActive() && launcher.getVelocity() > minimum_velocity && launcher.getVelocity() < maximum_velocity) {

            // notify the robot that the code didn't go rogue.
            opmode.idle();

        }

        // turn on the windmill servo
        leftWindmillServo.setPower(1);
        rightWindmillServo.setPower(1);

        // wait a second
        opmode.sleep(1000);

        // stop everything
        leftWindmillServo.setPower(0);
        rightWindmillServo.setPower(0);
        launcher.setVelocity(0);

    }

    public boolean isGamepadConnected(Gamepad gamepad) {
        return (gamepad.getGamepadId() != Gamepad.ID_UNASSOCIATED);
    }

}
