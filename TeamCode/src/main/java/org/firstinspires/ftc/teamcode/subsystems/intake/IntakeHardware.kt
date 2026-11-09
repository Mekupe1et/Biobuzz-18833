package org.firstinspires.ftc.teamcode.subsystems.intake

import com.skeletonarmy.marrow.telemetry.FormatBuilder
import com.skeletonarmy.marrow.telemetry.HtmlColor
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
        // log marrow HTML telemetry Dev Tool
        super.telemetry()

        //the title of the subsystem
        logHtml(
            FormatBuilder("── Intake ──").italic().setColor(HtmlColor.GREEN).format()
        )

        // all of the data that will be show
        logHtml(
            "<font color='green'><b>Intake Power:  ${IntakeVars.intakePower}</b></font>"
        )
        logHtml(
            "<font color='green'><b>Intake State: ${IntakeVars.intakeState}</b></font>"
        )
        logHtml(
            "<font color='green'><b>Intake Arm Position: ${IntakeVars.intakeArmPos}</b></font>"
        )
    }

}