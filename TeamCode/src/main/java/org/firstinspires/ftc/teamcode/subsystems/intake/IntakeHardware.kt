package org.firstinspires.ftc.teamcode.subsystems.intake

import dev.nextftc.hardware.actuators.NextMotor
import dev.nextftc.hardware.actuators.NextServo
import dev.nextftc.robot.Telemetry
import org.firstinspires.ftc.teamcode.util.Subsystem

object IntakeHardware: Subsystem() {
    val intakeMotor = NextMotor("intk_mtr")
    val intakeArmServo = NextServo("intk_arm")

    //raw interactions
    fun setPower(power: Double) = methodWarper { intakeMotor.throttle = power }

    fun setPose(pose: Double) = methodWarper { intakeArmServo.position = pose }

    fun setIntakeState(state: IntakeState) = methodWarper { IntakeVars.intakeState = state }

    fun setIntakeArmState(state: IntakeArmState) = methodWarper { IntakeVars.intakeArmState = state }

    override fun periodic() {
        setPower(IntakeVars.intakePower)
        setPose(IntakeVars.intakeArmPos)
    }

    override fun telemetry() {
        Telemetry.log("Intake State: ${IntakeVars.intakeState}")
        Telemetry.log("Intake Power: ${IntakeVars.intakePower}")
        Telemetry.log("Intake Arm Position: ${IntakeVars.intakeArmPos}")
    }

}