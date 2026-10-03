package org.firstinspires.ftc.teamcode;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import static java.lang.Math.abs;
import static java.lang.Math.atan2;
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


@Autonomous(name = "Auto Test", group = "StarterBot")
@Disabled
public class AutoTest extends LinearOpMode {

    private DcMotor backLeftDrive;
    private DcMotor backRightDrive;
    private DcMotor frontLeftDrive;
    private DcMotor frontRightDrive;

    private DcMotorEx launcher;
    private DcMotor intake;

    private CRServo leftIntakeServo;
    private CRServo rightIntakeServo;
    private CRServo windmillServo;

    @Override
    public void runOpMode() throws InterruptedException {

        backLeftDrive    = hardwareMap.get(DcMotor.class, "back_left_drive");
        backRightDrive   = hardwareMap.get(DcMotor.class, "back_right_drive");
        frontLeftDrive   = hardwareMap.get(DcMotor.class, "front_left_drive");
        frontRightDrive  = hardwareMap.get(DcMotor.class, "front_right_drive");
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

        moveRobotTo(10,10,100);



    }

    void moveRobotTo(double TargetX,double TargetY,double Speed) { // in inches and rpm
        Pose2D TargetPosition = new Pose2D(DistanceUnit.INCH,TargetX,TargetY,AngleUnit.DEGREES,0);
        Pose2D CurrentPosition = new Pose2D(DistanceUnit.INCH,0,0,AngleUnit.DEGREES,0);
        double directionX = 0;
        double directionY = 0;
        while (abs(TargetPosition.getX(DistanceUnit.INCH) - CurrentPosition.getX(DistanceUnit.INCH)) > 1.0) {
            sleep(33);
            double dx = TargetPosition.getX(DistanceUnit.INCH) - CurrentPosition.getX(DistanceUnit.INCH);
            double dy = TargetPosition.getY(DistanceUnit.INCH) - CurrentPosition.getY(DistanceUnit.INCH);

            double L = sqrt(dx * dx + dy * dy);

            directionX = dx/L;
            directionY = dy/L;

            macanumDrive(directionY,directionX,0);
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
            speeds[0]  /= max;
            speeds[1] /= max;
            speeds[2]   /= max;
            speeds[3]  /= max;
        }


        // If and only if the maximum is outside the range we want it to be,
        // normalize all the other speeds based on the given speed value.

        // apply the calculated values to the motors.
        frontLeftDrive.setPower(speeds[0]);
        frontRightDrive.setPower(speeds[1]);
        backLeftDrive.setPower(speeds[2]);
        backRightDrive.setPower(speeds[3]);
    }

    void setRobotHeading(double heading,double speed) { // in degrees and rpm

    }

}
