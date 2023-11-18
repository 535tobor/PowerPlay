package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.hardware.bosch.BNO055IMU;
@TeleOp
public class TeleTest extends HardwareMapIter {
    double apressed = 0;
    double bpressed = 0;
    double clawPosition = 0;
    @Override
    public void init(){
        initHwMap2();
        // Retrieve the IMU from the hardware map
       // BNO055IMU imu = hardwareMap.get(BNO055IMU.class, "imu");
       // BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        // Technically this is the default, however specifying it is clearer
       // parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        // Without this, data retrieving from the IMU throws an exception
       // imu.initialize(parameters);


    }
    @Override
    public void init_loop(){

    }
    @Override
    public void start(){

    }
    @Override
    public void loop(){

        if (gamepad1.a && apressed != 1){
            clawPosition = clawPosition + .65; // .65
            claw.setPosition(clawPosition);
            apressed = 1;
        }

        else if (gamepad1.b && bpressed != 1){
            clawPosition = clawPosition - .65;
            claw.setPosition(0);
            bpressed = 1;
        }

        if (!gamepad1.a){
            apressed = 0;
        }
        if (!gamepad1.b){
            bpressed = 0;
        }

        telemetry.addData("claw position: ", claw.getPosition());
        telemetry.addData("red: ", cs.red());
        telemetry.addData("green", cs.green());
        telemetry.addData("blue", cs.blue());
    }

}
