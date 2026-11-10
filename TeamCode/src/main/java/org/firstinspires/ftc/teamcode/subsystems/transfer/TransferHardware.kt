package org.firstinspires.ftc.teamcode.subsystems.transfer

import com.skeletonarmy.marrow.telemetry.FormatBuilder
import com.skeletonarmy.marrow.telemetry.HtmlColor
import com.skeletonarmy.marrow.telemetry.MarrowTelemetry
import com.skeletonarmy.marrow.telemetry.modifiers.BoldModifier
import dev.nextftc.hardware.actuators.NextMotor
import dev.nextftc.hardware.actuators.NextServo
import dev.nextftc.robot.Telemetry
import org.firstinspires.ftc.teamcode.util.HtmlTelemetry
import org.firstinspires.ftc.teamcode.util.Subsystem

object TransferHardware: Subsystem() {
    val TransferMotor = NextMotor("tM")
    val TransferGateServoSmall = NextServo("SS")
    val TransferGateServoBig = NextServo("SB")

    fun setPower(power: Double) =
        methodWarper { TransferMotor.throttle = power }

    fun setTransferState(state: TransferState) =
        methodWarper { TransferVars.transferState = state }

    fun setTransferGateModeBig(state: TransferGateState) =
        methodWarper { TransferVars.transferGateStateBig = state }

    fun setTransferGateModeSmall(state: TransferGateState) =
        methodWarper { TransferVars.transferGateStateSmall = state }

    override fun telemetry() {
        HtmlTelemetry.log("Transfer Power: ${TransferVars.transferPower}")
        HtmlTelemetry.log("Transfer Gate Small State: ${TransferVars.transferGateStateSmall}")
        HtmlTelemetry.log("Transfer Gate Small Position: ${TransferVars.transferGateModeSmall}")
        HtmlTelemetry.log("Transfer Gate Big State: ${TransferVars.transferGateStateBig}")
        HtmlTelemetry.log("Transfer Gate Big Position: ${TransferVars.transferGateModeBig}")
    }
}