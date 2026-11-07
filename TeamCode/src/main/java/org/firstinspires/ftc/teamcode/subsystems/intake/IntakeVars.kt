package org.firstinspires.ftc.teamcode.subsystems.intake

import com.bylazar.configurables.annotations.Configurable

@Configurable
object IntakeVars {
    @JvmField var intakePower = 0.0
    @JvmField var intakingPower = 1.0
    @JvmField var outtakingPower = -1.0
    @JvmField var stoppingPower = 0.0
    @JvmField var intakeStateDisp = IntakeState.OFF
    var intakeState = IntakeState.OFF
        set(value)  {
            field = value
            intakePower = when(value){
                IntakeState.INTAKE -> intakingPower
                IntakeState.OUTTAKE -> outtakingPower
                IntakeState.OFF -> stoppingPower
            }
            intakeStateDisp = value
        }
    @JvmField var intakeArmPos = 0.0
    @JvmField var intakeArmPosUp = 1.0
    @JvmField var intakeArmPosDown = 0.0
    @JvmField var intakeArmStateDisp = IntakeArmState.UP
    var intakeArmState = IntakeArmState.UP
        set(value) {
            field = value
            intakeArmPos = when (value) {
                IntakeArmState.UP -> intakeArmPosUp
                IntakeArmState.DOWN -> intakeArmPosDown
            }
            intakeArmStateDisp = value
        }
}

enum class IntakeState {
    INTAKE,
    OUTTAKE,
    OFF
}
enum class IntakeArmState {
    UP,
    DOWN
}