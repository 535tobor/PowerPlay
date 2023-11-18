package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.List;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;


@Autonomous(name = "Main Auto With Custom")
public class AutoModeCameraCustom extends HardwareMapLinear {

    private static final String TFOD_MODEL_ASSET = "PowerPlayCustom2.tflite";
    // private static final String TFOD_MODEL_FILE  = "/sdcard/FIRST/tflitemodels/CustomTeamModel.tflite";

    private static final String[] LABELS = {
            "Zone 1",
            "Zone 2",
            "Zone 3"
    };

    private static final String VUFORIA_KEY = "AWwcYJb/////AAABmdJjPbY0RUeFuaTQ5+I2oKEuhS8KFjtBswsI6wsaaPcy1Q5DqeHfaQfXoi+rslrJbgY9nvZ6loSiW9d/84i3GzHzJh0MBvZ09peEI+xG59MrgmfEvYPkCNUfGb5TifmxgQMUowzfgZm2ILe4c3fLe9thMYZ4K4iD8QyPQmjBgMROEdzZeKE7R9/2+d7vbMLNJs8U9u/r3Zo1t3TsY+nN+VWrCXY83wASJOVyV9R6CcxJkNAcyf+9kNRFqF9ZMmvi+XbDr/Btl4Laycbq3fnxumfLj2ohBDKL37HykPd/QzRGpjYkDRew+aejJSKE+oHp3e3bJLPOstDS0OUa1TuOLNbDOFbb4UwrNRrRsre+VC6c";
    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;
    public boolean alreadyRan = false;
    public boolean usedFailsafe = false;
    public ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode(){
        //initHwMap();
        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.
        initVuforia();
        initTfod();
        initHwMap();


        if (tfod != null) {
            tfod.activate();

            tfod.setZoom(1, 16.0/9.0);
        }

        /** Wait for the game to begin **/
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();

        waitForStart();
        if(opModeIsActive()){
            runtime.reset();
            while (opModeIsActive()) {
                if (tfod != null) {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();

                    if (updatedRecognitions != null) {
                        telemetry.addData("# Objects Detected", updatedRecognitions.size());
                        telemetry.addData("time: ", runtime.time());

                        for (Recognition recognition : updatedRecognitions) {
                            if (!alreadyRan){
                                double col = (recognition.getLeft() + recognition.getRight()) / 2 ;
                                double row = (recognition.getTop()  + recognition.getBottom()) / 2 ;
                                double width  = Math.abs(recognition.getRight() - recognition.getLeft()) ;
                                double height = Math.abs(recognition.getTop()  - recognition.getBottom()) ;

                                telemetry.addData(""," ");
                                telemetry.addData("Image", "%s (%.0f %% Conf.)", recognition.getLabel(), recognition.getConfidence() * 100 );
                                telemetry.addData("- Position (Row/Col)","%.0f / %.0f", row, col);
                                telemetry.addData("- Size (Width/Height)","%.0f / %.0f", width, height);


                                if (recognition.getLabel() == "Zone 1"){
                                    zone1();
                                    alreadyRan = true;
                                }

                                else if (recognition.getLabel() == "Zone 2"){
                                    zone2();
                                    alreadyRan=true;

                                }
                                else if (recognition.getLabel() == "Zone 3"){
                                    zone3();
                                    alreadyRan=true;
                                }

                                else {
                                    setPowerAllZero();
                                }
                            }


                        }
                        telemetry.update();



                    }
                }
                if ((runtime.time() > 7) & (!usedFailsafe) & (!alreadyRan)){
                    zone3();
                    alreadyRan = true;
                    usedFailsafe = true;

                }

            }
        }

    }
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);
    }

    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.75f;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 300;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);

        // Use loadModelFromAsset() if the TF Model is built in as an asset by Android Studio
        // Use loadModelFromFile() if you have downloaded a custom team model to the Robot Controller's FLASH.
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
        // tfod.loadModelFromFile(TFOD_MODEL_FILE, LABELS);
    }
}

