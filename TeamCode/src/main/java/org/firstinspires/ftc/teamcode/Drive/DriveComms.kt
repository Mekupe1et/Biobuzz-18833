package org.firstinspires.ftc.teamcode.Drive

import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.teamcode.Drive.DriveVars.maxspeed


object DriveComms {
    fun update(gamepad: Gamepad){
        if (gamepad.right_stick_button){
            maxspeed = 0.3
        }
        else if(gamepad.left_stick_button){
            maxspeed = 1.0
        }
    }
}