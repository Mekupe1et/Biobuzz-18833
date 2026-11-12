package org.firstinspires.ftc.teamcode.Drive


import dev.nextftc.hardware.sensors.NextIMU
import dev.nextftc.robot.Mechanism
import org.firstinspires.ftc.teamcode.Drive.DriveHardware.imu



object DriveVars: Mechanism {
    val place = imu.value.yaw
    var turnPower = 1.0
    var drivePower = 0.0
    var getPosition = 0.0
}