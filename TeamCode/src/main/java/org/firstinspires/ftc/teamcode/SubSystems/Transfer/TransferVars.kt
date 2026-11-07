package org.firstinspires.ftc.teamcode.SubSystems.Transfer

object TransferVars {

    // motor work power
    @JvmField var transferPower = 1.0
    // motor stop power
    @JvmField var transferStopPower = 0.0

    @JvmField var transferGateModeBig = 0.0
    @JvmField var transferGateModeSmall = 0.0
    @JvmField var transferGateModeOpen = 1.0
    @JvmField var transferGateModeClose = 0.0
    @JvmField var transferGateModeDisp = TransferGateState.CLOSE

    var transferState = TransferState.OFF
        set(value) {
            field = value
            transferPower = when (value) {
                TransferState.ON -> transferPower
                TransferState.OFF -> transferStopPower
            }
        }

    var transferGateStateBig = TransferGateState.OPEN
        set(value) {
            field = value
            transferGateModeBig = when (value) {
                TransferGateState.OPEN -> transferGateModeOpen
                TransferGateState.CLOSE -> transferGateModeClose
            }
            transferGateModeDisp = value
        }
    var transferGateStateSmall = TransferGateState.OPEN
        set(value) {
            field = value
            transferGateModeSmall = when (value) {
                TransferGateState.OPEN -> transferGateModeOpen
                TransferGateState.CLOSE -> transferGateModeClose
            }
            transferGateModeDisp = value
        }
}

enum class TransferState {
    ON,
    OFF
}
enum class TransferGateState {
    OPEN,
    CLOSE
}