package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name = "Simple Motor Auto", group = "Autonomous")
public class TestBenchMotor extends LinearOpMode {

    @Override
    public void runOpMode() {
        // 2. Initialize the motor from the hardware map
        // The string "leftMotor" must match the name given in your Robot Controller configuration
        // 1. Declare the DC motor object
        DcMotor leftMotor = hardwareMap.get(DcMotor.class, "leftMotor");

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Wait for the driver to press START on the Driver Station
        waitForStart();

        if (opModeIsActive()) {
            // 3. Run the motor at 50% power
            leftMotor.setPower(0.5);

            // Keep the motor running for 10000 milliseconds (10 seconds)
            sleep(10000);

            // 4. Stop the motor
            leftMotor.setPower(0);

            // Wait a moment and finish
            sleep(500);
        }
    }
}

