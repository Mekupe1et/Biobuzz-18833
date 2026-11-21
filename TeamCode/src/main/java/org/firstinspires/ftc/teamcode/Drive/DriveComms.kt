package org.firstinspires.ftc.teamcode.Drive

import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.teamcode.Drive.DriveVars.place
import org.firstinspires.ftc.teamcode.Drive.DriveVars.Strafe
import org.firstinspires.ftc.teamcode.Drive.DriveVars.drive



object DriveComms {
    fun update(gamepad: Gamepad){
        if ((place > 45) && (place <= 135)){
            if(drive > 0.3) {
                Strafe = gamepad.right_stick_y.toDouble()
                drive = 0.0
            }
            else if(drive < -0.3){
                Strafe = gamepad.right_stick_y.toDouble()
                drive = 0.0
            }
            else if (Strafe > 0.3){
                drive = gamepad.right_stick_x.toDouble()
                Strafe = 0.0
            }
            else if(Strafe < -0.3){
                drive = gamepad.right_stick_x.toDouble()
                Strafe - 0.0

            }
        }
        else if((place > 135) && (place <= 225)){
            if (drive > 0.3){
                drive = gamepad.right_stick_y.toDouble()
                Strafe  = 0.0
            }
            else if(drive < -0.3){
                drive = gamepad.right_stick_y.toDouble()
                Strafe = 0.0
            }
            else if(Strafe > 0.3){
                Strafe = gamepad.right_stick_x.toDouble()
                drive = 0.0
            }
            else if(Strafe < -0.3){
                Strafe = gamepad.right_stick_x.toDouble()
                drive = 0.0
            }
        }
        else if((place > 225) && (place <= 315)){
            if(drive > 0.3){
                Strafe = gamepad.right_stick_y.toDouble()
                drive = 0.0
            }
            else if(drive < -0.3){
                Strafe = gamepad.right_stick_y.toDouble()
                drive = 0.0
            }
            else if(Strafe > 0.3){
                drive = gamepad.right_stick_x.toDouble()
                Strafe = 0.0
            }
            else if(Strafe < -0.3){
                drive = gamepad.right_stick_x.toDouble()
            }
        }
    }
}