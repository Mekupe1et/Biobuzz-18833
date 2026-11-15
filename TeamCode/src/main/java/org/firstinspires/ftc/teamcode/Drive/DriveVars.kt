package org.firstinspires.ftc.teamcode.Drive

import com.qualcomm.robotcore.hardware.Gamepad
import dev.nextftc.robot.Mechanism
import org.firstinspires.ftc.teamcode.Drive.DriveHardware.imu

object DriveVars : Mechanism {
    // Custom getter so yaw is read dynamically
    val place get() = imu.value.yaw

    var drive = 0.0
    var Strafe = 0.0
    var turn = 0.0

    fun update(gamepad: Gamepad) {
        drive = gamepad.right_stick_y.toDouble()
        Strafe = gamepad.right_stick_x.toDouble()
        turn = gamepad.left_stick_x.toDouble()
    }
    var fl = drive + Strafe + turn
    var fr = drive - Strafe - turn
    var bl = drive - Strafe + turn
    var br = drive + Strafe + turn
}