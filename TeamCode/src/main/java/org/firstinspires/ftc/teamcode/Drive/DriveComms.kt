package org.firstinspires.ftc.teamcode.Drive
import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.robotcore.external.Telemetry

object DriveComms {
    var vars = DriveVars()

    fun update(gamepad: Gamepad, telemetry: Telemetry) {
        vars.update(gamepad)

        vars.driveHardware.frontleft.power = vars.fl
        vars.driveHardware.frontright.power = vars.fr
        vars.driveHardware.backleft.power = vars.bl
        vars.driveHardware.backright.power = vars.br

        telemetry.addData("frontright speed:", vars.fr)
        telemetry.addData("frontleft speed:", vars.fl)
        telemetry.addData("backright speed:", vars.br)
        telemetry.addData("backleft speed:", vars.bl)
        telemetry.update()
    }
}