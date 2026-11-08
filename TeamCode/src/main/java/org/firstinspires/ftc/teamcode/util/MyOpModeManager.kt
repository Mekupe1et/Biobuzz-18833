package org.firstinspires.ftc.teamcode.util

import com.bylazar.opmodecontrol.OpModeStatus
import com.bylazar.opmodecontrol.Plugin.activeOpModeInfo
import com.bylazar.opmodecontrol.Plugin.activeOpModeStartTimestamp
import com.bylazar.opmodecontrol.Plugin.status
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.robot.RobotState
import com.skeletonarmy.marrow.OpModeManager
import dev.nextftc.robot.triggers.CommandGamepad
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta

object MyOpModeManager {
    val flavor: OpModeMeta.Flavor
        get(){
            return activeOpModeInfo.flavour
        }
    val phase: OpModeStatus
        get(){
            return status
        }
    val startTimeStamp: Long
        get(){
            return if (activeOpModeStartTimestamp == null) 0 else activeOpModeStartTimestamp!!
        }
    val runningOpMode: OpMode
        get(){
            return OpModeManager.getActiveOpMode();
        }
    val gp1: CommandGamepad
        get(){
            return CommandGamepad(runningOpMode.gamepad1)
        }
    val gp2: CommandGamepad
        get() {
            return CommandGamepad(runningOpMode.gamepad2)
        }
    val hm: HardwareMap
        get() {
            return OpModeManager.getHardwareMap()
        }
    val telemetry: Telemetry
        get() {
            return OpModeManager.getTelemetry()
        }

}