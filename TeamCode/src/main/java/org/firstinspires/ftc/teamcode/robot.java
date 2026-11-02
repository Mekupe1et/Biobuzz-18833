package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Drive.DriveComms;
import org.firstinspires.ftc.teamcode.Intake.IntakeComms;


@TeleOp
public class robot extends OpMode {
    @Override
    public void init() {

    }

    @Override
    public void loop() {
        IntakeComms.INSTANCE.update(gamepad1, telemetry);
        DriveComms.INSTANCE.update(gamepad1, telemetry);
    }
}
