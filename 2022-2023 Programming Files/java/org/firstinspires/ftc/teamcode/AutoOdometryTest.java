package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;
@Autonomous
public class AutoOdometryTest extends HardwareMapLinear{
    @Override
    public void runOpMode(){
        initHwMap();
        waitForStart();
        driveForwardNew(6, 40);

    }
}
