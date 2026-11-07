package org.firstinspires.ftc.teamcode.subsystems.intake

import com.pedropathing.ivy.commands.Commands.instant
import org.firstinspires.ftc.teamcode.SubSystems.intake.IntakeArmState
import org.firstinspires.ftc.teamcode.SubSystems.intake.IntakeState
import org.firstinspires.ftc.teamcode.subsystems.intake.IntakeHardware.setIntakeState

object IntakeCommands {
    val runIntake = instant{setIntakeState(IntakeState.INTAKE)}
    val outtake = instant { setIntakeState(IntakeState.OUTTAKE) }
    val stopIntae = instant { setIntakeState(IntakeState.OFF) }

    val openIntakeArm = instant { IntakeHardware.setIntakeArmState(IntakeArmState.DOWN) }
    val closeIntakeArm = instant { IntakeHardware.setIntakeArmState(IntakeArmState.UP) }

}