package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


@TeleOp(name = "Verge Practice Station")
public class VergePracticeStation extends OpMode {

    @Override
    public void init() {
        addTeamInfo();

    }

    @Override
    public void loop() {

    }

    private void addTeamInfo(){
        telemetry.addData("Team Name","Verge Team"); // My First Comment
        telemetry.addData("City","Ballwin");
        telemetry.addData("ZipCode","63011");
        telemetry.addData("Country","USA");
    }
}

