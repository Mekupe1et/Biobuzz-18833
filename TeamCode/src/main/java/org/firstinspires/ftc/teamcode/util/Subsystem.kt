package org.firstinspires.ftc.teamcode.util

abstract class Subsystem {
    companion object{
        val subsystems = ArrayList<Subsystem>()
    }
    init {
        subsystems.add(this)
    }
    /** Called repeatedly while the OpMode is in the INIT phase. */
    open fun disabledPeriodic() {}

    /** Called exactly once after the PLAY button is pressed. */
    open fun start() {}

    /** Called repeatedly while the OpMode is actively running. */
    open fun periodic() {}

    /** Called exactly once when the OpMode finishes execution. */
    open fun end() {}

    open fun telemetry() {}

}