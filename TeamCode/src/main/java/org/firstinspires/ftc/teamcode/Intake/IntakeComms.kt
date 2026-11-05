package org.firstinspires.ftc.teamcode.Intake

import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.teamcode.Intake.IntakeVars.speed


object IntakeComms {
    fun update(gamepad: Gamepad){
        if (gamepad.right_bumper){
            speed = -0.7
        }
        else if(gamepad.right_trigger >= 0.2){
            speed = 0.7
        }
        else {
            speed = 0.0
        }
    }
}