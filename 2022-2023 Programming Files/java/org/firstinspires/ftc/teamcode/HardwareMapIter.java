package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import java.util.ArrayList;

public class HardwareMapIter extends OpMode {

    @Override
    public void init() {

    }
    public DcMotor motorFrontLeft, motorFrontRight, motorBackLeft, motorBackRight, shaft;

    public DcMotor encoderLeft, encoderRight, encodorAux;
    public Servo claw;
    public ColorSensor cs;
    public ElapsedTime runtime = new ElapsedTime();
    public BNO055IMU imu;

    public static double R = 2.54;
    public static double N = 8192; // ticks per rev
    public static double cm_per_tick = 2.0  * Math.PI * R / N;
    public void initHwMap2() {
        motorFrontLeft = hardwareMap.dcMotor.get("fl");
        motorBackLeft = hardwareMap.dcMotor.get("bl");
        motorFrontRight = hardwareMap.dcMotor.get("fr");
        motorBackRight = hardwareMap.dcMotor.get("br");

        shaft = hardwareMap.dcMotor.get("shaft");
        shaft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        shaft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        claw = hardwareMap.servo.get("claw");

        cs = hardwareMap.get(ColorSensor.class, "cs");

        encoderLeft = motorBackLeft;
        encoderRight = motorBackRight;
        encodorAux = motorFrontRight;
        // Reverse the right side motors
        // Reverse left motors if you are using NeveRests
        motorFrontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        motorBackRight.setDirection(DcMotorSimple.Direction.REVERSE);
        motorFrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFrontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorFrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFrontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Retrieve the IMU from the hardware map

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        // Technically this is the default, however specifying it is clearer
       parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
       imu = hardwareMap.get(BNO055IMU.class, "imu");
        // Without this, data retrieving from the IMU throws an exception
        imu.initialize(parameters);

    }


    @Override
    public void loop() {

    }
}
