package org.firstinspires.ftc.teamcode.util

import org.firstinspires.ftc.teamcode.util.Util.getTimeStamp

abstract class Subsystem {
    companion object{
        val subsystems = ArrayList<Subsystem>()
    }
    init {
        subsystems.add(this)
    }
    var looptime = 0.0
    /** Called repeatedly while the OpMode is in the INIT phase. */
    open fun disabledPeriodic() {}

    /** Called exactly once after the PLAY button is pressed. */
    open fun start() {}

    /** Called repeatedly while the OpMode is actively running. */
    open fun periodic() {}

    /** Called exactly once when the OpMode finishes execution. */
    open fun end() {}

    open fun telemetry() {}

    open fun pid(){}

    inline fun <T> methodWarper(action: () -> T): T {
        val startTime = getTimeStamp()
        return try {
            action()
        } finally {
            addToTime(startTime)
        }
    }

    open fun addToTime(startTime: Long) {
        looptime += getTimeStamp() - startTime
    }

}