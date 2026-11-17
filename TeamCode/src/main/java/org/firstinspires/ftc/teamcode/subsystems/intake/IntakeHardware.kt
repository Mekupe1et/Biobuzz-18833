package org.firstinspires.ftc.teamcode.subsystems.intake

import com.skeletonarmy.marrow.telemetry.FormatBuilder
import com.skeletonarmy.marrow.telemetry.modifiers.BoldModifier
import dev.nextftc.hardware.actuators.NextMotor
import dev.nextftc.hardware.actuators.NextServo
import org.firstinspires.ftc.teamcode.util.HtmlTelemetry
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
        HtmlTelemetry.log("── Intake ──", FormatBuilder().setColor("#EEFF00").bold())
        HtmlTelemetry.log("Intake State: ${IntakeVars.intakeState}", FormatBuilder().setColor("#EEFF00"))
        HtmlTelemetry.log("Intake Power: ${IntakeVars.intakePower}", FormatBuilder().setColor("#EEFF00"))
        HtmlTelemetry.log("Intake Arm State: ${IntakeVars.intakeArmState}", FormatBuilder().setColor("#EEFF00"))
        HtmlTelemetry.log("Intake Arm Position: ${IntakeVars.intakeArmPos}", FormatBuilder().setColor("#EEFF00"))
    }

}