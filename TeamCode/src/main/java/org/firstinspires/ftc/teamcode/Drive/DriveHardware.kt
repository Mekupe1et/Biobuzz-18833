package org.firstinspires.ftc.teamcode.Drive
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.LogoFacingDirection
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.UsbFacingDirection
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot
import dev.nextftc.hardware.actuators.NextMotor
import dev.nextftc.robot.Mechanism
import dev.nextftc.robot.Telemetry
import dev.nextftc.hardware.sensors.NextIMU
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.IMU
import org.firstinspires.ftc.teamcode.Drive.DriveVars.bl
import org.firstinspires.ftc.teamcode.Drive.DriveVars.br
import org.firstinspires.ftc.teamcode.Drive.DriveVars.fl
import org.firstinspires.ftc.teamcode.Drive.DriveVars.fr

object DriveHardware : Mechanism{
    val frontright by lazy{NextMotor("fr")}
    val frontleft by lazy{NextMotor("fl")}
    val backright by lazy{NextMotor("br")}
    val backleft by lazy { NextMotor("bl") }
    val imu = lazy{ NextIMU("imu") }
    fun drivePower(){
        backleft.throttle = bl
        backright.throttle = br
        frontright.throttle = fr
        frontleft.throttle = fl
    }
    fun setimu(gamepad: Gamepad){
        imu.value.initialize(IMU.Parameters(RevHubOrientationOnRobot(LogoFacingDirection.UP, UsbFacingDirection.RIGHT)))
    }
}