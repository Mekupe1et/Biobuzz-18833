package org.firstinspires.ftc.teamcode.Drive

import com.qualcomm.robotcore.hardware.Gamepad

object DriveVars {

    var maxspeed = 1.0

    var fl = 0.0
    var fr = 0.0
    var bl = 0.0
    var br = 0.0

    fun update(gamepad: Gamepad) {
        val drive = gamepad.right_stick_y.toDouble()
        val strafe = gamepad.right_stick_x.toDouble()
        val turn = gamepad.left_stick_x.toDouble()

        fl = (drive + strafe + turn) * maxspeed
        bl = (drive - strafe + turn) * maxspeed
        fr = (drive - strafe - turn) * maxspeed
        br = (drive + strafe - turn) * maxspeed
    }
}