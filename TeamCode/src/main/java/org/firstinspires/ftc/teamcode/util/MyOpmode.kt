package org.firstinspires.ftc.teamcode.util

import com.bylazar.panels.Panels
import com.bylazar.telemetry.PanelsTelemetry
import dev.nextftc.robot.NextRobot
import dev.nextftc.robot.Telemetry
import dev.nextftc.robot.opmode.BlazeOpMode
import gay.zharel.fateweaver.flight.FlightRecorder

class MyOpmode(robot: NextRobot) : BlazeOpMode(robot) {
    override fun disabledPeriodic() {
        Telemetry.addBackend(FlightRecorder)
        Telemetry.addBackend(PanelsTelemetry.ftcTelemetry)
        Subsystem.subsystems.forEach {
            it.disabledPeriodic()
        }
        playLoop()
        Subsystem.subsystems.forEach {
            it.telemetry()
        }
        Telemetry.update()
    }
    override fun start() {
        Subsystem.subsystems.forEach {
            it.start()
        }
        pressStart()
        Subsystem.subsystems.forEach {
            it.telemetry()
        }
        Telemetry.update()
    }
    override fun periodic() {
        Subsystem.subsystems.forEach {
            it.periodic()
        }
        loop()
        Subsystem.subsystems.forEach {
            it.telemetry()
        }
        Telemetry.update()
    }
    override fun end() {
        Subsystem.subsystems.forEach {
            it.end()
        }
        finish()
        Subsystem.subsystems.forEach {
            it.telemetry()
        }
        Telemetry.update()
    }
    /** Called repeatedly while the OpMode is in the INIT phase. */
    open fun playLoop() {}

    /** Called exactly once after the PLAY button is pressed. */
    open fun pressStart() {}

    /** Called repeatedly while the OpMode is actively running. */
    open fun loop() {}

    /** Called exactly once when the OpMode finishes execution. */
    open fun finish() {}
}