package org.firstinspires.ftc.teamcode.util

import com.pedropathing.math.Pose
import com.pedropathing.math.Vector
import com.pedropathing.math.Vector2D
import dev.nextftc.robot.Telemetry
import kotlin.math.atan2

object Util {
    fun roughlyEquals(pos1: Pose, pos2: Pose, threshold: Double): Boolean {
        return pos1.distance(pos2) < threshold
    }
    fun getTimeStamp(): Long {
        return System.currentTimeMillis()
    }
    fun wrap360(angle: Double): Double {
        return ((angle % 360) + 360) % 360
    }
    fun wrap360(angle: Int): Int {
        return ((angle % 360) + 360) % 360
    }
    fun wrap2Pi(radians: Double): Double {
        var radians = radians
        while (radians > Math.PI) {
            radians -= 2 * Math.PI
        }
        while (radians < -Math.PI) {
            radians += 2 * Math.PI
        }

        return radians
    }
    fun vectorAngle(vec: Vector2D): Double {
        return atan2(vec.y(), vec.x())
    }
    fun vectorAngle(vec: Vector): Double {
        return atan2(vec.toVector2D().y(), vec.toVector2D().x())
    }
}