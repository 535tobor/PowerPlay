package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;
@Autonomous
public class AutoMain extends HardwareMapLinear{
    public ElapsedTime runtime = new ElapsedTime();
    public ElapsedTime runtime2 = new ElapsedTime();
    double totalGreenValue, totalBlueValue, totalRedValue, count = 0;
    double avgGreen, avgBlue, avgRed;
    double newShaftPosition, origShaftPosition;
    @Override
    public void runOpMode() {
        initHwMap();
        waitForStart();


            if (opModeIsActive()){
                resetDriveEncoders();

                claw.setPosition(1);
                runtime.reset();
                while (runtime.time() < 1 && opModeIsActive()){        //close claw and wait one second
                    idle();
                }
                moveShaft(500);
                runtime.reset();
                while (runtime.time() < 1 && opModeIsActive()){        //close claw and wait one second
                    idle();
                }

                                                 //raise claw to avoid ground junction
                strafeRight(.8, 29);                      //drive to cone

              //  turn(-6);                                       //correct angle to accurately read cone

                runtime.reset();                                       /*find average value of each color over the course of two seconds*/
                while (runtime.time() < 2){
                    double green = colorSensor.green();
                    double blue = colorSensor.blue();
                    double red = colorSensor.red();
                    totalGreenValue = totalGreenValue + green - 25;
                    totalBlueValue = totalBlueValue + blue;
                    totalRedValue = totalRedValue + red;
                    count = count + 1;
                }

                avgGreen = totalGreenValue / count;
                avgRed  = totalRedValue / count;
                avgBlue = totalBlueValue / count;


                if (avgRed > avgBlue && avgRed > avgGreen){
                    telemetry.addData("", "zone 1");
                    //turn(7);        //correct angle
                    zone1();
                }

                else if (avgBlue > avgRed && avgBlue > avgGreen){
                    telemetry.addData("", "zone 2");
                    //turn(7);        //correct angle
                    zone2();
                }

                else if (avgGreen > avgRed && avgGreen > avgBlue){
                    telemetry.addData("", "zone 3");
                   // turn(7);        //correct angle
                    zone3();
                }



                while (opModeIsActive()){

                    //telemetry.addData("Zone: ", zone);
                    telemetry.addData("aux encoders:", encodorAux.getCurrentPosition());
                    telemetry.addData("Red:", avgRed);
                    telemetry.addData("Green: ", avgGreen);
                    telemetry.addData("Blue: ", avgBlue);
                    telemetry.update();

                }

            }





        }
    }


