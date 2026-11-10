package org.firstinspires.ftc.teamcode.util

import com.bylazar.telemetry.PanelsTelemetry
import com.skeletonarmy.marrow.telemetry.modifiers.BoldModifier
import com.skeletonarmy.marrow.telemetry.modifiers.ItalicModifier
import dev.nextftc.hardware.lynx.NextLynxModule
import dev.nextftc.robot.opmode.BlazeBulkReadHook
import dev.nextftc.robot.opmode.BlazeOpMode
import gay.zharel.fateweaver.flight.FlightRecorder
import java.util.Locale

class MyOpmode : BlazeOpMode(
    FillerRobot,
    BlazeBulkReadHook(NextLynxModule.Type.CONTROL_HUB, false),
    BlazeBulkReadHook(NextLynxModule.Type.EXPANSION_HUB, false),
) {
    companion object {
        var totalLoopTime = 0.0
    }
    init {
        HtmlTelemetry.addBackend(FlightRecorder)
        HtmlTelemetry.addBackend(PanelsTelemetry.ftcTelemetry)
    }

    override fun disabledPeriodic() = runLoopPhase({ disabledPeriodic() }) { playLoop() }
    override fun start() = runPhase({ start() }) { pressStart() }
    override fun periodic() = runLoopPhase({ periodic() }) { loop() }
    override fun end() = runPhase({ end() }) { finish() }

    override fun onExpansionHubBulkData() {
        forEachSubsystem { pid() }
        onExpHubBulk()
    }

    override fun onControlHubBulkData() = onCtrlHubBulk()
    override fun onLocalizationData() = onLocData()

    private inline fun runLoopPhase(
        noinline subsystemAction: Subsystem.() -> Unit,
        userAction: () -> Unit
    ) {
        val startNano = System.nanoTime()
        forEachSubsystem(subsystemAction)
        userAction()
        totalLoopTime = (System.nanoTime() - startNano) / 1_000_000.0
        forEachTelemetry()
    }

    private inline fun runPhase(
        noinline subsystemAction: Subsystem.() -> Unit,
        userAction: () -> Unit
    ) {
        forEachSubsystem(subsystemAction)
        userAction()
        forEachTelemetry()
    }

    private fun forEachSubsystem(action: Subsystem.() -> Unit) {
        Subsystem.subsystems.forEach { subsystem ->
            subsystem.methodWarper { subsystem.action() }
        }
    }

    private fun forEachTelemetry() {
        Subsystem.subsystems.forEach {
            HtmlTelemetry.log("--${it.javaClass.simpleName}--", BoldModifier())
            it.telemetry()
            HtmlTelemetry.log("Subsystem Loop Time: ${it.looptime} ms", ItalicModifier())
        }
        HtmlTelemetry.log("Total Loop Time: ${String.format(Locale.US, "%.2f", totalLoopTime)} ms", BoldModifier())
        HtmlTelemetry.update()
    }

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