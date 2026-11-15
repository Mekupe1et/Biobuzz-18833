package org.firstinspires.ftc.teamcode.Drive

import com.pedropathing.controllers.PIDController
import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.teamcode.Drive.DriveHardware.backleft
import org.firstinspires.ftc.teamcode.Drive.DriveHardware.backright
import org.firstinspires.ftc.teamcode.Drive.DriveHardware.frontleft
import org.firstinspires.ftc.teamcode.Drive.DriveHardware.frontright
import org.firstinspires.ftc.teamcode.Drive.DriveHardware.imu

import org.firstinspires.ftc.teamcode.Drive.DriveVars.place
import com.qualcomm.robotcore.hardware.PIDCoefficients
import org.firstinspires.ftc.teamcode.Drive.DriveVars.Strafe
import org.firstinspires.ftc.teamcode.Drive.DriveVars.drive
import org.firstinspires.ftc.teamcode.Drive.DriveVars.turn


object DriveComms {
    fun update(){
        if ((place <= 94) && (place >= 86)){
            if(drive > 0.3) {
                Strafe = -0.8
            }
            else if(drive < -0.3){
                Strafe = 0.8
            }
            else if (Strafe > 0.3){
                drive = 0.8

            }
            else if(Strafe < -0.3){
                drive = -0.8

            }
        }
        else if((place <= 184) && (place >= 176)){
            if (drive > 0.3){
                drive = -0.8
            }
            else if(drive < -0.3){
                drive = 0.8
            }
            else if(Strafe > 0.3){
                Strafe = -0.8
            }
            else if(Strafe < -0.3){
                Strafe = 0.8
            }
        }
        else if(place <= 274){
            if(drive > 0.3){
                Strafe = 0.8
            }
            else if(drive < -0.3){
                Strafe = -0.8
            }
            else if(Strafe > 0.3){
                drive = -0.8
            }
            else if(Strafe < -0.3){
                drive = 0.8
            }
        }
    }
}