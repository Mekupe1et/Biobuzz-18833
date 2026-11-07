package org.firstinspires.ftc.teamcode.util

import com.bylazar.panels.Panels
import com.bylazar.telemetry.PanelsTelemetry
import com.qualcomm.robotcore.robot.Robot
import dev.nextftc.hardware.RobotController
import dev.nextftc.hardware.RobotController.hardwareMap
import dev.nextftc.hardware.lynx.NextLynxModule
import dev.nextftc.robot.NextRobot
import dev.nextftc.robot.Telemetry
import dev.nextftc.robot.opmode.BlazeBulkReadHook
import dev.nextftc.robot.opmode.BlazeOpMode
import dev.nextftc.robot.opmode.NextOpMode
import gay.zharel.fateweaver.flight.FlightRecorder

class MyOpmode : BlazeOpMode(
    FillerRobot(),
    BlazeBulkReadHook(NextLynxModule.Type.CONTROL_HUB, false),
    BlazeBulkReadHook(NextLynxModule.Type.EXPANSION_HUB, false),
) {
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

    override fun onControlHubBulkData() {
        onCtrlHubBulk()
    }

    override fun onExpansionHubBulkData() {
        Subsystem.subsystems.forEach {
            it.pid()
        }
        onExpHubBulk()
    }

    override fun onLocalizationData() {
        onLocData()
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