package org.firstinspires.ftc.teamcode;

import android.media.MediaPlayer;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.Servo;

public class HardwareMapLinear extends LinearOpMode {
    public DcMotor motorFrontLeft, motorFrontRight, motorBackLeft, motorBackRight, shaft;
    public DcMotor encoderLeft, encoderRight, encodorAux;
    public ColorSensor colorSensor;
    public DistanceSensor distanceSensor;
    public Servo claw;
    public ElapsedTime runtime = new ElapsedTime();
    public BNO055IMU imu;
    // values for odometry
    public static double L;
    public static double B;
    public static double R = 2.54; // radius in cm
    public static double N = 8192; // ticks per rev
    public static double cm_per_tick = (2.0  * Math.PI * R) / N;
    public static double auxDistanceFromCenter = 15.748; //PLACEHOLDER
    // keep track of odometry values
    public int currentRightPos = 0;
    public int curentLeftPos = 0;
    public int currentAuxPos = 0;

    public int oldRightPos = 0;
    public int oldLeftPos = 0;
    public int oldAuxPos = 0;

    public void initHwMap() {
        motorFrontLeft = hardwareMap.dcMotor.get("fl");
        motorFrontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motorBackLeft = hardwareMap.dcMotor.get("bl");
        motorBackLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motorFrontRight = hardwareMap.dcMotor.get("fr");
        motorFrontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motorBackRight = hardwareMap.dcMotor.get("br");
        motorBackRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        shaft = hardwareMap.dcMotor.get("shaft");
        claw = hardwareMap.servo.get("claw");
        shaft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        shaft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        encoderLeft = motorBackLeft;
        encoderRight = motorBackRight;
        encodorAux = motorFrontRight;
        colorSensor = hardwareMap.get(ColorSensor.class, "cs");
        distanceSensor = hardwareMap.get(DistanceSensor.class, "cs");

        motorFrontRight.setDirection(DcMotor.Direction.REVERSE);
        motorBackRight.setDirection(DcMotor.Direction.REVERSE);

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        // Technically this is the default, however specifying it is clearer
        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        // Without this, data retrieving from the IMU throws an exception
        imu.initialize(parameters);
    }

    @Override
    public void runOpMode() {
    }
    public void resetDriveEncoders(){
        motorFrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFrontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorFrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFrontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);



    }

    public void setPowerAll(double speed) {
        motorBackLeft.setPower(-speed);
        motorBackRight.setPower(-speed);
        motorFrontRight.setPower(-speed);
        motorFrontLeft.setPower(-speed);
    }
    public void setPowerAllZero() {
        motorBackLeft.setPower(0);
        motorBackRight.setPower(0);
        motorFrontRight.setPower(0);
        motorFrontLeft.setPower(0);
    }
    public void turnRight(double power){
        motorBackLeft.setPower(-power);
        motorBackRight.setPower(power);
        motorFrontRight.setPower(power);
        motorFrontLeft.setPower(-power);
    }
    public void turnLeft(double power){
        motorBackLeft.setPower(power);
        motorBackRight.setPower(-power);
        motorFrontRight.setPower(-power);
        motorFrontLeft.setPower(power);
    }
    public void strafeLeft(double power, double distance) {
        double newAuxPos = encodorAux.getCurrentPosition() - (distance/cm_per_tick);

        while (encodorAux.getCurrentPosition() > newAuxPos){
            motorBackLeft.setPower(-power);
            motorBackRight.setPower(power);
            motorFrontRight.setPower(-power);
            motorFrontLeft.setPower(power);
        }
        setPowerAllZero();
    }
    public void strafeRight(double power, double distance) {
        double newAuxPos = encodorAux.getCurrentPosition() + (distance / cm_per_tick);

        while (encodorAux.getCurrentPosition() < newAuxPos) {
            motorBackLeft.setPower(power);
            motorBackRight.setPower(-power);
            motorFrontRight.setPower(power);
            motorFrontLeft.setPower(-power);
        }
        setPowerAllZero();
    }

    public void turn(double radians){
        double turnAmountRadians = radians; //Math.PI/48;
        double objAngle = -imu.getAngularOrientation().firstAngle + turnAmountRadians;

        if (turnAmountRadians > 0){
            while (-imu.getAngularOrientation().firstAngle < objAngle){
                turnRight(.4);
            }
        }
        if (turnAmountRadians < 0){
            while (-imu.getAngularOrientation().firstAngle >= objAngle){
                turnLeft(.4);
            }
        }
        setPowerAllZero();
    }
    public double detectColor(){
        double red = colorSensor.red();
        double green = colorSensor.green();
        double blue = colorSensor.blue();
        double zone;
        if ((red > green) && (red > blue)) {
            zone = 2;
            return zone;
        }
        else if ((blue > green) && (blue > red)) {
            zone = 1;
            return zone;
        }
        else if ((green > blue) && (green > red)){
            zone = 3;
            return zone;
        }
        else {
            zone = 0;
            return zone;
        }
    }
    public void scoreCone(){
        strafeRight(.8, 11);
        turn(Math.PI/2);
        driveForward(.8, 40);
        turn(Math.PI/4);
        moveShaft(3300);
        driveForward(.4, 11);
        waitfortime(1);
        claw.setPosition(0.75);
        shaft.setPower(.05);
        waitfortime(1);
        // moveShaft(1500);
        driveBackward(.8, 11);

        turn(-Math.PI/3.92);
    }
    public void scoreConeMedium(){
        strafeRight(.4, 12); // get to center of square
        turn(3*Math.PI/4); // turn to medium pole
        moveShaft(2350); // move shaft to top of pole
        driveForward(.4, 9); //drive to pole
        waitfortime(1);
        claw.setPosition(.75); //open claw
        waitfortime(1);
        driveBackward(.8, 10);// drive back to center of pole
        turn(-Math.PI/4); // face forward
    }
    public void zone1(){
        scoreConeMedium();
        strafeLeft(.6, 46);
        //driveBackward(.6, );

    }
    public void zone2(){
        scoreConeMedium();
        driveForward(.6, 5);
    }
    public void zone3(){
        scoreConeMedium();
        strafeRight(.6, 46);
    }
    public void newTurn(int degrees){
        double rads = (degrees*Math.PI)/2;
        double auxMovementDistance = rads*auxDistanceFromCenter;
        double newAuxPosition = encodorAux.getCurrentPosition() + (auxMovementDistance/cm_per_tick);
        if (degrees > 0){
            while (opModeIsActive() && newAuxPosition > encodorAux.getCurrentPosition()){
                turnRight(.3);
            }
            setPowerAllZero();
        }
        else if (degrees < 0){
            while (opModeIsActive() && newAuxPosition < encodorAux.getCurrentPosition()){
                turnLeft(.3);
            }
            setPowerAllZero();
        }
    }
    public void waitfortime(double duration){
        runtime.reset();
        while (runtime.time() < duration && opModeIsActive()){        //close claw and wait one second
            idle();
        }
    }
    public void moveShaft(double position){
        if (position > shaft.getCurrentPosition()){
            while (shaft.getCurrentPosition() < position){
                shaft.setPower(1);
            }
            shaft.setPower(.01);
        }
        else if (position < shaft.getCurrentPosition()){
            while (shaft.getCurrentPosition() > position){
                shaft.setPower(-.1);
            }
            shaft.setPower(1);
        }
    }
    public void driveByTime(double speed, double time) {
        runtime.reset();
        while (opModeIsActive() && runtime.time() < time) {
            setPowerAll(speed);
        }
        setPowerAll(0.0);

    }
    public void driveForwardNew(double speed, double distance){
        double newPosLeft = encoderLeft.getCurrentPosition() + (distance/cm_per_tick);
        double newPosRight = encoderRight.getCurrentPosition() + (distance/cm_per_tick);
        //double newPosRight = encoderRight.getCurrentPosition() + (distance/cm_per_tick);
        while ((opModeIsActive()) && (encoderLeft.getCurrentPosition() < newPosLeft) || (encoderRight.getCurrentPosition() < newPosRight)){
            if (encoderLeft.getCurrentPosition() < newPosLeft){
                motorBackLeft.setPower(-speed);
                motorFrontLeft.setPower(-speed);
            }
            else{
                motorBackLeft.setPower(0);
                motorFrontLeft.setPower(0);
            }

            if (encoderLeft.getCurrentPosition() < newPosLeft){
                motorBackLeft.setPower(-speed);
                motorFrontLeft.setPower(-speed);
            }
            else{
                motorBackLeft.setPower(0);
                motorFrontLeft.setPower(0);
            }
            //setPowerAll(speed);
        }
        setPowerAllZero();

    }
    public void driveForward(double speed, double distance){
        double newPosLeft = encoderLeft.getCurrentPosition() + (distance/cm_per_tick);
;
        while ((opModeIsActive()) && (encoderLeft.getCurrentPosition() < newPosLeft)){
            setPowerAll(speed);
        }
        setPowerAllZero();
    }
    public void driveBackward(double speed, double distance){
        double newPosLeft = encoderLeft.getCurrentPosition() - (distance/cm_per_tick);
        //double newPosRight = encoderRight.getCurrentPosition() + (distance/cm_per_tick);
        while (encoderLeft.getCurrentPosition() > newPosLeft){
            setPowerAll(-speed);
        }
        setPowerAllZero();
    }

}
