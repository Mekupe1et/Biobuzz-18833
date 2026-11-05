package org.firstinspires.ftc.teamcode.Intake

import dev.nextftc.hardware.actuators.NextMotor
import dev.nextftc.robot.Mechanism
import dev.nextftc.robot.Telemetry
import org.firstinspires.ftc.teamcode.Intake.IntakeVars.speed


object IntakeHardware : Mechanism{
    val intake = lazy { NextMotor("intake") }
    fun setPower(power: Double){
        intake.value.throttle = power
    }
    override fun periodic() {
        updateTelemetry()
        IntakeHardware.setPower(speed)
    }

    fun updateTelemetry(){
        Telemetry.log("Intake speed", speed)
    }

}