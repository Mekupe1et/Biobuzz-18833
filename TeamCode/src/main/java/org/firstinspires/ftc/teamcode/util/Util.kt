package org.firstinspires.ftc.teamcode.util

import com.pedropathing.math.Pose

object Util {
    fun roughlyEquals(pos1: Pose, pos2: Pose, threshold: Double): Boolean {
        return pos1.distance(pos2) < threshold
    }
}