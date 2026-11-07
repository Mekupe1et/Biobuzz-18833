package org.firstinspires.ftc.teamcode.subsystems.transfer

object TransferVars {

    // motor work power
    @JvmField var transferPower = 1.0
    // motor stop power
    @JvmField var transferStopPower = 0.0
    var transferState = TransferState.OFF
        set(value) {
            field = value
            transferPower = when (value) {
                TransferState.ON -> transferPower
                TransferState.OFF -> transferStopPower
            }
        }

    @JvmField var transferGateModeBig = 0.0
    @JvmField var transferGateModeOpenBig = 1.0
    @JvmField var transferGateModeCloseBig = 0.0
    @JvmField var transferGateModeDispBig = TransferGateState.CLOSE

    var transferGateStateBig = TransferGateState.OPEN
        set(value) {
            field = value
            transferGateModeBig = when (value) {
                TransferGateState.OPEN -> transferGateModeOpenBig
                TransferGateState.CLOSE -> transferGateModeCloseBig
            }
            transferGateModeDispBig = value
        }

    @JvmField var transferGateModeSmall = 0.0
    @JvmField var transferGateModeOpenSmall = 1.0
    @JvmField var transferGateModeCloseSmall = 0.0
    @JvmField var transferGateModeDispSmall = TransferGateState.CLOSE

    var transferGateStateSmall = TransferGateState.OPEN
        set(value) {
            field = value
            transferGateModeSmall = when (value) {
                TransferGateState.OPEN -> transferGateModeOpenSmall
                TransferGateState.CLOSE -> transferGateModeCloseSmall
            }
            transferGateModeDispSmall = value
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