package org.firstinspires.ftc.teamcode.Drive

import org.firstinspires.ftc.teamcode.Drive.DriveVars.place
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
        else if((place <= 274) && (place >= 266)){
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
        else {
            if(place <= 135){
                turn = -0.6
            }
           else{
               turn = 0.6
            }
        }
    }
}