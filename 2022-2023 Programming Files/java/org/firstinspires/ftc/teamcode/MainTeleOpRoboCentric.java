package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class MainTeleOpRoboCentric extends HardwareMapIter {
    boolean apressed, bpressed;
    double clawPosition = 0;
    double clawOpen = .75;
    double clawClosed = 1.0;
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

    }
    @Override
    public void loop(){

        double y = gamepad1.left_stick_y; // Remember, this is reversed!
        double x = -gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
        double rx = -gamepad1.right_stick_x;

        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio, but only when
        // at least one is out of the range [-1, 1]
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double frontLeftPower = (y + x + rx) / denominator;
        double backLeftPower = (y - x + rx) / denominator;
        double frontRightPower = (y - x - rx) / denominator;
        double backRightPower = (y + x - rx) / denominator;


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

        telemetry.addData("shaft position: ", shaft.getCurrentPosition());
        telemetry.addData("current time: ", runtime.time());
    }


}
