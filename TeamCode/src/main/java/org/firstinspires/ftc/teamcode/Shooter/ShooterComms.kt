package org.firstinspires.ftc.teamcode.Shooter

import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.teamcode.Shooter.ShooterVars.Power


class ShooterComms {
    fun update(gamepad: Gamepad) {
        if (gamepad.right_stick_button) {
            Power = 0.7
        }
        else {
            Power = 0.0
        }
    }
}