package org.firstinspires.ftc.teamcode.util

import com.pedropathing.math.Vector
import dev.nextftc.robot.NextRobot

object FillerRobot: NextRobot {
    var deltaVec = Vector(0.0, 0.0)
    val voltage: Double
        get() {
            return MyOpModeManager.hm.voltageSensor.iterator().next().voltage
        }

}