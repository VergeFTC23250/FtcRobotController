package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp
public class JavaCodeforFTC extends LinearOpMode {

    //Variables/objects
    DcMotor FrontLeft;
    DcMotor FrontRight;
    DcMotor BackLeft;
    DcMotor BackRight;


    //Method, will run when you press start
    @Override
    public void runOpMode() throws InterruptedException {

        FrontLeft = hardwareMap.get(DcMotor.class, "FrontLeft");
        FrontRight = hardwareMap.get(DcMotor.class, "FrontRight");
        BackLeft = hardwareMap.get(DcMotor.class, "BackLeft");
        BackRight = hardwareMap.get(DcMotor.class, "BackRight");
        //Will run counterclockwise
        FrontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        BackLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();


        //Loops when you press start
            while(opModeIsActive()){
                    controlDrive();
            }


    }


    //Controls how the controller controls the robot
    public void controlDrive(){
        double drive = gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double turn = gamepad1.right_stick_x;


        FrontLeft.setPower(drive + strafe + turn);
        FrontRight.setPower(drive - strafe - turn);
        BackLeft.setPower(drive - strafe + turn);
        BackRight.setPower(drive + strafe - turn);



    }
}