package org.firstinspires.ftc.teamcode.util

import com.bylazar.telemetry.PanelsTelemetry
import dev.nextftc.hardware.lynx.NextLynxModule
import dev.nextftc.robot.Telemetry
import dev.nextftc.robot.opmode.BlazeBulkReadHook
import dev.nextftc.robot.opmode.BlazeOpMode
import gay.zharel.fateweaver.flight.FlightRecorder

class MyOpmode : BlazeOpMode(
    FillerRobot(),
    BlazeBulkReadHook(NextLynxModule.Type.CONTROL_HUB, false),
    BlazeBulkReadHook(NextLynxModule.Type.EXPANSION_HUB, false),
) {
    override fun disabledPeriodic() {
        //TODO: add support for marrow
        Telemetry.addBackend(FlightRecorder)
        Telemetry.addBackend(PanelsTelemetry.ftcTelemetry)
        forEachSubsystem { disabledPeriodic() }
        playLoop()
        forEachTelemetry()
    }
    override fun start() {
        forEachSubsystem { start() }
        pressStart()
        forEachTelemetry()
    }
    override fun periodic() {
        forEachSubsystem { periodic() }
        loop()
        forEachTelemetry()
    }
    override fun end() {
        forEachSubsystem { end() }
        finish()
        forEachTelemetry()
    }

    override fun onExpansionHubBulkData() {
        forEachSubsystem { pid() }
        onExpHubBulk()
    }

    override fun onControlHubBulkData() {
        onCtrlHubBulk()
    }

    override fun onLocalizationData() {
        onLocData()
    }

    private fun forEachSubsystem(action: Subsystem.() -> Unit) {
        Subsystem.subsystems.forEach { subsystem ->
            subsystem.methodWarper { subsystem.action() }
        }
    }
    private fun forEachTelemetry(){
        Subsystem.subsystems.forEach {
            it.telemetry()
        }
        Telemetry.update()
    }
//TODO: add runtime telemetry
    /** Called repeatedly while the OpMode is in the INIT phase. */
    open fun playLoop() {}

    /** Called exactly once after the PLAY button is pressed. */
    open fun pressStart() {}

    /** Called repeatedly while the OpMode is actively running. */
    open fun loop() {}

    /** Called exactly once when the OpMode finishes execution. */
    open fun finish() {}

    open fun onCtrlHubBulk() {}

    open fun onExpHubBulk() {}

    open fun onLocData() {}
}