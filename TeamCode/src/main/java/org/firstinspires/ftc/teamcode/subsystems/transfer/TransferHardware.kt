package org.firstinspires.ftc.teamcode.subsystems.transfer

import dev.nextftc.hardware.actuators.NextMotor
import dev.nextftc.hardware.actuators.NextServo
import dev.nextftc.robot.Telemetry
import org.firstinspires.ftc.teamcode.util.Subsystem

object TransferHardware: Subsystem() {
    val TransferMotor = NextMotor("TM")
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
        Telemetry.log("Transfer Power: ${TransferVars.transferPower}")
        Telemetry.log("Transfer Gate Small State: ${TransferVars.transferGateStateSmall}")
        Telemetry.log("Transfer Gate Small Position: ${TransferVars.transferGateModeSmall}")
        Telemetry.log("Transfer Gate Big State: ${TransferVars.transferGateStateBig}")
        Telemetry.log("Transfer Gate Big Position: ${TransferVars.transferGateModeBig}")
    }
}