package org.firstinspires.ftc.teamcode.Intake
import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.robotcore.external.Telemetry


object IntakeComms {
    var intake = IntakeHardware()
    var vars = IntakeVars()
    fun update(gamepad: Gamepad, telemetry: Telemetry) {
        telemetry.addData("intake vars", vars.speed)
        intake.intake.power = vars.speed
        if (gamepad.right_bumper) {
            vars.speed = -0.7
        }
        else if (gamepad.right_trigger >= 0.3){
            vars.speed = 0.7
        }
        else {
            vars.speed = 0.0
        }
        telemetry.update()


    }
}