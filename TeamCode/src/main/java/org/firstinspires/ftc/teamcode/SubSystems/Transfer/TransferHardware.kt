package org.firstinspires.ftc.teamcode.SubSystems.Transfer

import dev.nextftc.hardware.actuators.NextMotor
import dev.nextftc.hardware.actuators.NextServo
import dev.nextftc.robot.Telemetry
import org.firstinspires.ftc.teamcode.util.Subsystem
import org.firstinspires.ftc.teamcode.SubSystems.Transfer.TransferVars
import org.firstinspires.ftc.teamcode.SubSystems.Transfer.TransferState

object TransferHardware: Subsystem() {
    val TransferMotor = NextMotor("tM")
    val TransferGateServoSmall = NextServo("SS")
    val TransferGateServoBig = NextServo("SB")

    fun setPower(Power: Double) {
        TransferMotor.throttle = Power
    }

    fun setTransferState(state: TransferState){
        TransferVars.transferState = state
    }

    fun setTransferGateModeBig(state: TransferGateState){
        TransferVars.transferGateStateBig = state
    }

    fun setTransferGateModeSmall(state: TransferGateState){
        TransferVars.transferGateStateSmall = state
    }

    override fun telemetry() {
        Telemetry.log("Transfer Power: ${TransferVars.transferPower}")
        Telemetry.log("Transfer Gate Small State: ${TransferVars.transferGateStateSmall}")
        Telemetry.log("Transfer Gate Big State ${TransferVars.transferGateStateBig}")
    }
}