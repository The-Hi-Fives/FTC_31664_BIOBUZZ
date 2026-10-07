package org.firstinspires.ftc.teamcode;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.Rotation;


@Autonomous(name = "Auto Test", group = "StarterBot")
@Disabled
public class AutoTest extends LinearOpMode {

    private DcMotorEx backLeftDrive;
    private DcMotorEx backRightDrive;
    private DcMotorEx frontLeftDrive;
    private DcMotorEx frontRightDrive;

    private DcMotorEx launcher;
    private DcMotor intake;

    private CRServo leftIntakeServo;
    private CRServo rightIntakeServo;
    private CRServo windmillServo;

    double currentRotation = 0; // in degrees

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

        backLeftDrive.setZeroPowerBehavior(BRAKE);
        backRightDrive.setZeroPowerBehavior(BRAKE);
        frontLeftDrive.setZeroPowerBehavior(BRAKE);
        frontRightDrive.setZeroPowerBehavior(BRAKE);
        intake.setZeroPowerBehavior(BRAKE);

        moveRobotTo(10,10,1000);

        moveRobotTo(12,12,1000);

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

            double dx = TargetPose.getX(DistanceUnit.INCH) - TargetPose.getX(DistanceUnit.INCH);
            double dy = TargetPose.getY(DistanceUnit.INCH) - TargetPose.getY(DistanceUnit.INCH);

            double L = sqrt(pow(dx,2) + pow(dy,2));

            directionX = (dx/L);
            directionY = (dy/L);

            double rotate = CurrentPose.getHeading(AngleUnit.DEGREES) - currentRotation;

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

    void setRobotHeading(double heading,double speed) { // in degrees and rpm

    }

}
