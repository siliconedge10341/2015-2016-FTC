package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;
//Pavan Vattyam CREATED THIS
//Wei Gao refactored this
//Dival Banerjee revised this
////Magicnumbers are evil
public class SiEdgeOpModeArm extends OpMode {

    //to avoid the overuse of magic numbers
    static final double tapeSpeed = 0.5; //speed of tape reel extension/retraction
    static final double brushSpeed = 1; // speed of collector brush
    static final double slideSpeed = 0.3; // speed of slide extension
    static final double conveyorSpeed = 1; // speed of debris conveyor
    static final double tapedelta = .01; // resolution of tape servo arm
    static final double bucketdelta = .01;
    static final double ziplinedelta = .01;

    //drive motors
    DcMotor motorLeft1;
    DcMotor motorRight1;

    //function motors
    DcMotor motorTape;
    DcMotor motorBrush;
    DcMotor motorSlide;
    DcMotor motorConveyor;

    //servomotors
    Servo tapearm;
    Servo bucket;
    Servo zipline;

    double servopos1;
    double servopos2;
    double servopos3;
    public SiEdgeOpModeArm() {

    }

    //initialization routine
    @Override
    public void init() {

        servopos1 = 0.0;
        servopos2= 0.5;
        servopos3 = 0.0;

        motorLeft1 = hardwareMap.dcMotor.get("lmotor_1");
        motorRight1 = hardwareMap.dcMotor.get("rmotor_1");
        //lmotor_1 ----> LeftMotor
        //rmotor_1 ----> RightMotor

        motorTape = hardwareMap.dcMotor.get("motor_tape");
        motorBrush = hardwareMap.dcMotor.get("motor_brush");

        motorSlide = hardwareMap.dcMotor.get("motor_slide");
        motorConveyor = hardwareMap.dcMotor.get("motor_conveyor");

        tapearm = hardwareMap.servo.get("s_tapepos");
        bucket = hardwareMap.servo.get("s_bucket");
        zipline = hardwareMap.servo.get("s_zipline");

        motorLeft1.setDirection(DcMotor.Direction.FORWARD);
        motorRight1.setDirection(DcMotor.Direction.FORWARD);

        //Turn the collector
        motorBrush.setDirection(DcMotor.Direction.FORWARD);
        motorSlide.setDirection(DcMotor.Direction.FORWARD); //may need to be reversed
        //MotorTape Initialization //// FIXME: This is a potential fix, since occasionally the tape measure extents automatically
        motorTape.setDirection(DcMotor.Direction.FORWARD);
        motorTape.setPower(0);

        //TODO design a power-on self test
        //Power ON Self Test

    }

    //main function body
    @Override
    public void loop() {
        /*
            DRIVE MOTOR CODE
         */
        // throttle: left_stick_y ranges from -1 to 1, where -1 is full up, and
        // 1 is full down
        // direction: left_stick_x ranges from -1 to 1, where -1 is full left
        //FIXME kind of a kludge, recheck hardware to see if motors were mixed up
        //FIXME b/c x and y axes are fliped
        float throttle = -gamepad1.left_stick_x; //Switched to negitive to fix direction
        float direction = gamepad1.left_stick_y;

        float right = throttle - direction;//Updated to flip direction
        float left = throttle + direction;//Updated to flip direction
        // clip the right/left values so that the values never exceed +/- 1
        right = Range.clip(right, -1, 1);
        left = Range.clip(left, -1, 1);
        // scale the joystick value to make it easier to control
        // the robot more precisely at slower speedy
        right = (float)scaleInput(right);
        left =  (float)scaleInput(left);
        //Sets theo
        // write the values to the motors
        motorLeft1.setPower(left);
        motorRight1.setPower(right);

        /*
            OTHER MOTOR STUFF
         */

        /*Using the button a on controller 2, when pressed, Tape motor turns,
         on release, motor doesn't turn
         */

        /*
            GAMEPAD BUTTON EVENTS
         */
        //tape measure
            //Left trigger retracts; right trigger extends
            if(gamepad1.right_trigger >= .5) {
                motorTape.setPower(tapeSpeed);
            }
            else if(gamepad1.left_trigger >= .5) {
                motorTape.setPower(-tapeSpeed);
            }
            else{
                motorTape.setPower(0);

            }

        //turn sweeper when a is pressed reverse when b is pressed.
        if(gamepad2.a){
            motorBrush.setPower(brushSpeed);
        }else if(gamepad2.b){
            motorBrush.setPower(-brushSpeed);
        }
        else{
            motorBrush.setPower(0);
        }

        //conveyor motion
        if (gamepad2.x) {
            motorConveyor.setPower(conveyorSpeed);
        }
        else{
            motorConveyor.setPower(0); //Shuts down conveyor
        }

        //slide motion
        if (gamepad2.left_bumper) {
            //extend
            motorSlide.setPower(slideSpeed);
        } else if (gamepad2.right_bumper) {
            //retract
            motorSlide.setPower(-slideSpeed);
        }
        else{
            motorSlide.setPower(0);
        }

        //SERVO CODE:
        //TODO Automate climbing maneuver by auto-retracting the tape as the bot climbs
        if(gamepad1.left_bumper){
            servopos1 -= tapedelta;
        }
        if (gamepad1.right_bumper){
            servopos1 += tapedelta;
        }
        servopos1 = Range.clip(servopos1,0.0,1.0);
        tapearm.setPosition(servopos1);

        //Bucket
        if(gamepad2.dpad_right){
            servopos2 -= bucketdelta;
        }
        if (gamepad2.dpad_left){
            servopos2 += bucketdelta;
        }
        servopos2 = Range.clip(servopos2,0.0,1.0);
        bucket.setPosition(servopos2);

        //ZIPLINE

        if (gamepad2.left_trigger >=.5){
            servopos3 -= ziplinedelta;
        }if (gamepad2.right_trigger >=.5){
            servopos3 += ziplinedelta;
        }
        servopos3 = Range.clip(servopos3,0.0,1.0);
        zipline.setPosition(servopos3);

    }


    @Override
    public void stop() {

    }

    void pushToTelemetry (String key, String data) {
        telemetry.addData(key, data);
    }
    void pushToTelemetry (String key, double data) {
        telemetry.addData(key, data);
    }
    void pushToTelemetry (String key, float data) {
        telemetry.addData(key, data);
    }

    /*
     * This method scales the joystick input so for low joystick values, the
     * scaled value is less than linear.  This is to make it easier to drive
     * the robot more precisely at slower speeds.
     */
    double scaleInput(double dVal)  {
        double[] scaleArray = { 0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
                0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00 };

        // get the corresponding index for the scaleInput array.
        int index = (int) (dVal * 16.0);

        // index should be positive.
        if (index < 0) {
            index = -index;
        }

        // index cannot exceed size of array minus 1.
        if (index > 16) {
            index = 16;
        }

        // get value from the array.
        double dScale = 0.0;
        if (dVal < 0) {
            dScale = -scaleArray[index];
        } else {
            dScale = scaleArray[index];
        }

        // return scaled value.
        return dScale;
    }

}

