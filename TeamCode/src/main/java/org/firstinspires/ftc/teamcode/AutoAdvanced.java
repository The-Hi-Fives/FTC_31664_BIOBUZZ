package org.firstinspires.ftc.teamcode;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

import java.util.Arrays;
import java.util.stream.IntStream;


@Autonomous(name = "Auto Test", group = "StarterBot")

@Disabled // comment out later!

public class AutoAdvanced extends LinearOpMode {

    private DcMotorEx backLeftDrive;
    private DcMotorEx backRightDrive;
    private DcMotorEx frontLeftDrive;
    private DcMotorEx frontRightDrive;

    private DcMotorEx launcher;
    private DcMotor intake;

    private CRServo leftIntakeServo;
    private CRServo rightIntakeServo;
    private CRServo windmillServo;

    private HuskyLens huskylens;

    private GoBildaPinpointDriver pinpoint;


    double targetHeading = 0; // in degrees

    public final int LAUNCHER_TARGET_VELOCITY = 1325;
    public final int LAUNCHER_MINIMUM_VELOCITY = 1275;

    public final int ROTATION_VELOCITY = 400;

    boolean is_alliance_red = false;

    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() throws InterruptedException {

        backLeftDrive    = hardwareMap.get(DcMotorEx.class, "back_left_drive");
        backRightDrive   = hardwareMap.get(DcMotorEx.class, "back_right_drive");
        frontLeftDrive   = hardwareMap.get(DcMotorEx.class, "front_left_drive");
        frontRightDrive  = hardwareMap.get(DcMotorEx.class, "front_right_drive");
        intake           = hardwareMap.get(DcMotor.class, "intake");
        launcher         = hardwareMap.get(DcMotorEx.class, "launcher");

        windmillServo    = hardwareMap.get(CRServo.class, "windmill");
        leftIntakeServo  = hardwareMap.get(CRServo.class, "left_intake_servo");
        rightIntakeServo = hardwareMap.get(CRServo.class, "right_intake_servo");

        backLeftDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        frontLeftDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        rightIntakeServo.setDirection(DcMotorSimple.Direction.REVERSE);

        backLeftDrive.setZeroPowerBehavior(BRAKE);
        backRightDrive.setZeroPowerBehavior(BRAKE);
        frontLeftDrive.setZeroPowerBehavior(BRAKE);
        frontRightDrive.setZeroPowerBehavior(BRAKE);
        intake.setZeroPowerBehavior(BRAKE);

        pinpoint.resetPosAndIMU();

        runtime.reset(); // reset the time so it starts at 0.

        // Sequence Code

        moveRobotTo(10,10,1000);

        moveRobotTo(12,12,1000);

        if (launch()) {
            moveRobotTo(10,10,1000);

        } else {
            moveRobotTo(10,10,1000);

        }

        launch();

        setIntake(true);
        moveRobotTo(10,10,1000);
        setIntake(false);

    }

    int[] getAprilTagIDs() {

        HuskyLens.Block[] blocks = huskylens.blocks();

        // If there are no visible AprilTags then return an empty array.
        if (blocks == null) {
            telemetry.addData("Block count", 0);
            return new int[0];
        }

        // Make a new array with the AprilTag IDs.
        int[] blockIDs = new int[blocks.length];
        telemetry.addData("Block count", blocks.length);

        // Loop through the blocks to get all the IDs.
        for (int i = 0; i < blocks.length; i++) {
            telemetry.addData("Block", blocks[i].toString());
            blockIDs[i] = blocks[i].id;
        }
        return blockIDs;
    }

    boolean isHiveReady() { // check if you can see the AprilTags on the hive

        int[] blockIDs = getAprilTagIDs();

        int verifiedBlocks = 0;

        IntStream streamedArray = Arrays.stream(blockIDs);

        // loop through the alliances AprilTags
        for (int i = is_alliance_red ? 1 : 9; i <= (is_alliance_red ? 8 : 16); i++) {
            if (streamedArrayHas(streamedArray,i)) {
                verifiedBlocks++;
            }
        }

        // if most of the blocks are there, then return true, otherwise false.
        return verifiedBlocks > 3;

    }

    boolean streamedArrayHas(IntStream streamedArray, int value) {
        return streamedArray.anyMatch(x -> x == value);
    }

    Pose2D getRobotPose() {
        return new Pose2D(DistanceUnit.INCH,0,0,AngleUnit.DEGREES,0); // set up later
    }

    double getDistanceBetweenPoints(Pose2D a, Pose2D b, DistanceUnit distanceUnit){
        return sqrt(pow(b.getX(distanceUnit) - a.getX(distanceUnit),2) + pow(b.getY(distanceUnit) - a.getY(distanceUnit),2));
    }

    void moveRobotTo(double TargetX,double TargetY,double speed) { // in inches and rpm

        Pose2D TargetPose = new Pose2D(DistanceUnit.INCH,TargetX,TargetY,AngleUnit.DEGREES,0);
        Pose2D CurrentPose = getRobotPose();

        double directionX;
        double directionY;

        while (getDistanceBetweenPoints(TargetPose,CurrentPose,DistanceUnit.INCH) > 1.0) {
            sleep(33); // Wait 1/30 of a second

            double dx = (TargetPose.getX(DistanceUnit.INCH) - TargetPose.getX(DistanceUnit.INCH));
            double dy = TargetPose.getY(DistanceUnit.INCH) - TargetPose.getY(DistanceUnit.INCH);

            // normalize the direction
            double l = sqrt(pow(dx,2) + pow(dy,2));

            directionX = (dx/l);
            directionY = (dy/l);

            double rotate = CurrentPose.getHeading(AngleUnit.DEGREES) - targetHeading;

            macanumDrive(directionY * speed,directionX * speed,rotate * speed);

            CurrentPose = getRobotPose();

        }

        macanumDrive(0,0,0);

    }

    void macanumDrive(double forward, double sideways, double rotate) {

        // Assign the input values to the variables

        // get the current robot rotation for
        // field centric.
        double heading = 0;//pinpoint.getHeading(AngleUnit.RADIANS);

        // Get the cosine and sine of the heading.
        double cosAngle = Math.cos((Math.PI / 2) - heading);
        double sinAngle = Math.sin((Math.PI / 2) - heading);

        // Adjust the strafe and drive accordingly
        double globalStrafe = forward * cosAngle + sideways * sinAngle;
        double globalDrive  = forward * cosAngle - sideways * sinAngle;

        // get the speeds for all the motors.
        double[] speeds = {
                (forward + sideways + rotate),
                (forward - sideways - rotate),
                (forward - sideways + rotate),
                (forward + sideways - rotate)
        };

        // Because we are adding vectors and motors only take values between
        // [-1,1] we may need to normalize them.

        // Loop through all values in the speeds[] array and find the greatest
        // *magnitude*.  Not the greatest velocity.
        double max = Math.abs(speeds[0]);
        for (double speed : speeds) {
            if (max < Math.abs(speed)) max = Math.abs(speed);
        }

        max = Math.max(Math.abs(speeds[0]), Math.abs(speeds[1]));
        max = Math.max(max, Math.abs(speeds[2]));
        max = Math.max(max, Math.abs(speeds[3]));

        if (max > 1.0) {
            speeds[0]   /= max;
            speeds[1]   /= max;
            speeds[2]   /= max;
            speeds[3]   /= max;
        }


        // If and only if the maximum is outside the range we want it to be,
        // normalize all the other speeds based on the given speed value.

        // apply the calculated values to the motors.
        frontLeftDrive.setVelocity(speeds[0]);
        frontRightDrive.setVelocity(speeds[1]);
        backLeftDrive.setVelocity(speeds[2]);
        backRightDrive.setVelocity(speeds[3]);

    }

    boolean launch() {

        boolean hiveReady = isHiveReady();

        for (int i = 0; i <= 60; i++) {
            sleep(33);
            if (hiveReady) {
                break;
            } else {
                hiveReady = isHiveReady();
            }
            //idle();
        }

        if (!hiveReady) {
            return false;
        }

        launcher.setVelocity(LAUNCHER_TARGET_VELOCITY);

        while (launcher.getVelocity() <= LAUNCHER_MINIMUM_VELOCITY) {
            sleep(33);
        }

        double recordedVelocity = launcher.getVelocity();

        windmillServo.setPower(1);

        while (recordedVelocity - launcher.getVelocity() > 200) {
            sleep(33);
        }

        sleep(200);

        launcher.setVelocity(0);
        windmillServo.setPower(0);

        return true;

    }

    void setIntake(boolean isIntaking) {

        double intakeSpeed = isIntaking ? 1.0 : 0.0; // ternary operator (do not question)

        intake.setPower(intakeSpeed);
        leftIntakeServo.setPower(intakeSpeed);
        rightIntakeServo.setPower(intakeSpeed);

    }

    void setRobotHeading(double heading,double speed) { // in degrees and rpm

        targetHeading = heading;
        double currentHeading = getRobotPose().getHeading(AngleUnit.DEGREES);

        while (abs(currentHeading - targetHeading) > 10) {
            sleep(33);
            macanumDrive(0,0,currentHeading - targetHeading * ROTATION_VELOCITY);
        }

        macanumDrive(0,0,0);

    }
}
