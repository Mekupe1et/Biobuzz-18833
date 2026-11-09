package org.firstinspires.ftc.teamcode.subsystems.transfer

import com.skeletonarmy.marrow.telemetry.FormatBuilder
import com.skeletonarmy.marrow.telemetry.HtmlColor
import dev.nextftc.hardware.actuators.NextMotor
import dev.nextftc.hardware.actuators.NextServo
import dev.nextftc.robot.Telemetry
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
        // log marrow HTML telemetry Dev Tool
        super.telemetry()

        //the title of the subsystem
        logHtml(
            FormatBuilder("── Transfer ──").italic().setColor(HtmlColor.BLUE).format()
        )

        // all of the data that will be show
        logHtml(
            "<font color='blue'><b>Transfer Power:</b> ${TransferVars.transferPower}</font>"
        )
        logHtml(
            "<font color='blue'><b>Transfer Gate Small State:</b> ${TransferVars.transferGateStateSmall}</font>"
        )
        logHtml(
            "<font color='blue'><b>Transfer Gate Small Position:</b> ${TransferVars.transferGateModeSmall}</font>"
        )
        logHtml(
            "<font color='blue'><b>Transfer Gate Small Open Mode Posision:<b> ${TransferVars.transferGateModeOpenSmall}</font>"
        )
        logHtml(
            "<font color='blue'><b>Transfer Gate Small Close Mode Posision:<b> ${TransferVars.transferGateModeCloseSmall}</font>"
        )
        logHtml(
            "<font color='blue'><b>Transfer Gate Big State:</b> ${TransferVars.transferGateStateBig}</font>"
        )
        logHtml(
            "<font color='blue'><b>Transfer Gate Big Position:</b> ${TransferVars.transferGateModeBig}</font>"
        )
        logHtml(
            "<font color='blue'><b>Transfer Gate Big Open Mode Position:</b> ${TransferVars.transferGateModeOpenBig}</font>"
        )
        logHtml(
            "<font color='blue'><b>Transfer Gate Big Close Mode Posision:</b> ${TransferVars.transferGateModeCloseBig}</font>"
        )
    }
}