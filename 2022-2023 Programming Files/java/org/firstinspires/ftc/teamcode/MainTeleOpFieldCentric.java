package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.hardware.bosch.BNO055IMU;


@TeleOp
public class MainTeleOpFieldCentric extends HardwareMapIter {
    double clawPosition = 0;
    double clawOpen = .75;
    double clawClosed = 1.0;
    boolean apressed, bpressed;
    double shaftOrigPos;
    public static double R = 2.54; // radius in cm
    public static double N = 8192; // ticks per rev
    public static double cm_per_tick = (2.0  * Math.PI * R) / N;
    @Override
    public void init(){
        initHwMap2();
        // Retrieve the IMU from the hardware map
        BNO055IMU imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        // Technically this is the default, however specifying it is clearer
        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        // Without this, data retrieving from the IMU throws an exception
        imu.initialize(parameters);

    }
    @Override
    public void init_loop(){

    }
    @Override
    public void start(){
       shaftOrigPos = shaft.getCurrentPosition();
    }
    @Override
    public void loop(){

        double y = gamepad1.left_stick_y;
        double x = -gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
        double rx = -gamepad1.right_stick_x;

        // Read inverse IMU heading, as the IMU heading is CW positive

        double botHeading = -imu.getAngularOrientation().firstAngle;


        double rotX = x * Math.cos(botHeading) - y * Math.sin(botHeading);
        double rotY = x * Math.sin(botHeading) + y * Math.cos(botHeading);

        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio, but only when
        // at least one is out of the range [-1, 1]
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double frontLeftPower = (rotY + rotX + rx) / denominator;
        double backLeftPower = (rotY - rotX + rx) / denominator;
        double frontRightPower = (rotY - rotX - rx) / denominator;
        double backRightPower = (rotY + rotX - rx) / denominator;

        if (gamepad1.left_trigger > .2){ // left trigger slow mode
            motorFrontLeft.setPower(.3 * frontLeftPower);
            motorBackLeft.setPower(.3 * backLeftPower);
            motorFrontRight.setPower(.3 * frontRightPower);
            motorBackRight.setPower(.3 * backRightPower);
        }
        else if (gamepad1.right_trigger > .2){ // right trigger fast mode
            motorFrontLeft.setPower(frontLeftPower);
            motorBackLeft.setPower(backLeftPower);
            motorFrontRight.setPower(frontRightPower);
            motorBackRight.setPower(backRightPower);
        }
        else {
            motorFrontLeft.setPower(.8 * frontLeftPower);
            motorBackLeft.setPower(.8 * backLeftPower);
            motorFrontRight.setPower(.8 * frontRightPower);
            motorBackRight.setPower(.8 * backRightPower);
        }

        if (gamepad1.y){
            initHwMap2();
            // Retrieve the IMU from the hardware map
            BNO055IMU imu = hardwareMap.get(BNO055IMU.class, "imu");
            BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
            // Technically this is the default, however specifying it is clearer
            parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
            // Without this, data retrieving from the IMU throws an exception
            imu.initialize(parameters);
            runtime.reset();
            while (runtime.time() < .5){
                shaft.setPower(0);
            }
        }
// Move shaft up and down

        shaft.setPower(-gamepad2.left_stick_y);

        if (gamepad2.left_stick_y == 0){
            shaft.setPower(0.01);
        }


// Open and close claw
        //close
        if (gamepad2.a && !apressed){
            claw.setPosition(clawClosed);
            apressed = true;
        }
        //open
        else if (gamepad2.b && !bpressed){
            claw.setPosition(clawOpen);
            bpressed = true;
        }

        if (gamepad2.right_bumper){
            claw.setPosition(claw.getPosition() - .01);
        }
        else if (gamepad2.left_bumper){
            claw.setPosition(claw.getPosition() + .01);
        }


        if (!gamepad2.a){
            apressed = false;
        }
        if (!gamepad2.b){
            bpressed = false;
        }


        telemetry.addData("Aux Distance: ", encodorAux.getCurrentPosition()/cm_per_tick);
        telemetry.addData("shaft position: ", shaft.getCurrentPosition());
        telemetry.addData("Claw Position: ", claw.getPosition());
    }

}
