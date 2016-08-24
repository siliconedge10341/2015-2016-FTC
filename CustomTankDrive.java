package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;
/**
 * Created by Wei on 10/23/2015.
 */
public class CustomTankDrive extends K9TankDrive {

    //master motor direction control
    boolean robotMotionReversed = false;

    //motor names as seen by the hardware
    String lMotor1_Name = "lmotor_1";
    String lMotor2_Name = "lmotor_2";
    String rMotor1_Name = "rmotor_1";
    String rMotor2_Name = "rmotor_2";

    //create motor objects
    //two per side called in sync
    DcMotor lMotor1;
    DcMotor lMotor2;
    DcMotor rMotor1;
    DcMotor rMotor2;

    //need a constructor
    public CustomTankDrive()
    {
        //superconstructor call since no real changes are needed
        //it's basically a duplicate and modified K9TankDrive
        //super();
    }

    //interfaces for controlling motor speed during driving
    public void drive(float rSpeed, float lSpeed)
    {
        lMotor1.setPower(lSpeed);
        lMotor2.setPower(lSpeed);
        rMotor1.setPower(rSpeed);
        rMotor2.setPower(rSpeed);
    }

    //initialization routine
    //called before the match
    @Override
    public void init()
    {
        //get the motors from the hardware map
        lMotor1 = hardwareMap.dcMotor.get(lMotor1_Name);
        lMotor2 = hardwareMap.dcMotor.get(lMotor2_Name);
        rMotor1 = hardwareMap.dcMotor.get(rMotor1_Name);
        rMotor2 = hardwareMap.dcMotor.get(rMotor2_Name);

        //ternaries ensure that opposing motors always run in opposite directions
        DcMotor.Direction lDriveDir = (!robotMotionReversed ? DcMotor.Direction.REVERSE : DcMotor.Direction.FORWARD); //left-side
        DcMotor.Direction rDriveDir = (!robotMotionReversed ? DcMotor.Direction.FORWARD : DcMotor.Direction.REVERSE); //right-side
        //set the correct motor direction
        lMotor1.setDirection(lDriveDir);
        lMotor2.setDirection(lDriveDir);
        rMotor1.setDirection(rDriveDir);
        rMotor2.setDirection(rDriveDir);
    }

    //loop function which oddly enough loops during match
    //TODO see if you need to manually end this part with a method call
    @Override
    public void loop()
    {
        //for reference:
        //gamepad1, gamepad2 can be referenced directly

        //get the values on each desired joystick
        float left = -gamepad1.left_stick_y;
        float right = -gamepad1.right_stick_y;

        //Range.clip "clips" the values
        //so they're never beyond +/- 1
        //Range.clip(number, lowerBound, upperBound)
        right = Range.clip(right, -1, 1);
        left = Range.clip(left, -1, 1);

        //scale the inputs correctly, then cast to float
        left = (float)scaleInput(left);
        right = (float)scaleInput(right);

        //drive the motors
        drive(right,left);

        //10/23/2015 - determined that the drive() issue was caused by hardware, not software

        telemetry.addData("left tgt pwr",  "left  pwr: " + String.format("%.2f", left));
        telemetry.addData("right tgt pwr", "right pwr: " + String.format("%.2f", right));
    }

    @Override
    public void stop()
    {

    }
}
