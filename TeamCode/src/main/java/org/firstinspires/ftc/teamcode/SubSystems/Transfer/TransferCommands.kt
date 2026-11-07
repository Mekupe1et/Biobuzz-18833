package org.firstinspires.ftc.teamcode.SubSystems.Transfer

import com.pedropathing.ivy.commands.Commands.instant
import org.firstinspires.ftc.teamcode.SubSystems.Transfer.TransferHardware.setTransferGateModeBig
import org.firstinspires.ftc.teamcode.SubSystems.Transfer.TransferHardware.setTransferGateModeSmall
import org.firstinspires.ftc.teamcode.SubSystems.Transfer.TransferHardware.setTransferState

object TransferCommands {
    val runTransfer = instant { setTransferState(TransferState.ON) }
    val stopTransfer = instant { setTransferState(TransferState.OFF) }

    val openGateBig = instant { setTransferGateModeBig(TransferGateState.OPEN) }
    val closeGateBig = instant { setTransferGateModeBig(TransferGateState.CLOSE) }

    val openGateSmall = instant { setTransferGateModeSmall(TransferGateState.OPEN) }
    val closeGateSmall = instant { setTransferGateModeSmall(TransferGateState.CLOSE) }
}