package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

public class HardwareMapLinearTest extends LinearOpMode {
    public ColorSensor cs;

    public void initHwMap() {

        cs = hardwareMap.get(ColorSensor.class, "cs");



    }

    @Override
    public void runOpMode() {
    }


}
