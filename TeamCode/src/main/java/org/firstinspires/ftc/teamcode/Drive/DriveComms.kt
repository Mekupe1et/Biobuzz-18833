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
import org.firstinspires.ftc.teamcode.Drive.DriveVars.drivePower
import org.firstinspires.ftc.teamcode.Drive.DriveVars.getPosition
import org.firstinspires.ftc.teamcode.Drive.DriveVars.turnPower

object DriveComms {
    fun rightTurn(){
        backleft.throttle = turnPower
        backright.throttle = -turnPower
        frontright.throttle = -turnPower
        frontleft.throttle = turnPower
    }
    fun leftTurn(){
        backleft.throttle = -turnPower
        backright.throttle = turnPower
        frontright.throttle = turnPower
        frontleft.throttle = -turnPower
    }
    fun forward(){
        backleft.throttle = drivePower
        backright.throttle = drivePower
        frontleft.throttle = drivePower
        frontright.throttle = drivePower
    }
    fun backword(){
        backright.throttle = -drivePower
        backleft.throttle = -drivePower
        frontright.throttle = -drivePower
        frontleft.throttle = -drivePower
    }
    fun update(gamepad: Gamepad){

        if (gamepad.left_stick_button){
            imu.value.resetYaw()
        }
        if (gamepad.right_stick_y > 0.3){
            getPosition = 0.0
            if (place == 0.0){
                forward()
            }
            else if (place <= 180){
            leftTurn()
            }
            else {
                rightTurn()
            }
        }
       else if(gamepad.right_stick_y <= -0.3) {
           getPosition = 180.0
           if (place == 180.0){
               backword()
           }
            else if (place >= 0 && place > 180.0){
                rightTurn()

           }
        }
    }
}