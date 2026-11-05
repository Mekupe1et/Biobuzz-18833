package org.firstinspires.ftc.teamcode.subsystems.intake

import dev.nextftc.hardware.actuators.NextMotor
import dev.nextftc.hardware.actuators.NextServo
import dev.nextftc.robot.Mechanism
import dev.nextftc.robot.Telemetry
import org.firstinspires.ftc.teamcode.SubSystems.intake.IntakeArmState
import org.firstinspires.ftc.teamcode.SubSystems.intake.IntakeState
import org.firstinspires.ftc.teamcode.SubSystems.intake.IntakeVars
import org.firstinspires.ftc.teamcode.util.Subsystem

object IntakeHardware: Subsystem() {
    val intakeMotor = NextMotor("intk_mtr")
    val intakeArmServo = NextServo("intk_arm")

    //raw interactions
    fun setPower(power: Double){
        intakeMotor.throttle = power
    }

    fun setPose(pose: Double){
        intakeArmServo.position = pose
    }

    override fun periodic() {
        setPower(IntakeVars.intakePower)
        setPose(IntakeVars.intakeArmPos)
    }
    fun setIntakeState(state: IntakeState){
        IntakeVars.intakeState = state
    }

    fun setIntakeArmState(state: IntakeArmState){
        IntakeVars.intakeArmState = state
    }

    override fun telemetry() {
        Telemetry.log("Intake State: ${IntakeVars.intakeState}")
        Telemetry.log("Intake Power: ${IntakeVars.intakePower}")
        Telemetry.log("Intake Arm Position: ${IntakeVars.intakeArmPos}")
    }

}