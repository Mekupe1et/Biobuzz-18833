package org.firstinspires.ftc.teamcode.Shooter
import dev.nextftc.hardware.actuators.NextMotor
import dev.nextftc.robot.Mechanism
import dev.nextftc.robot.Telemetry
import org.firstinspires.ftc.teamcode.Shooter.ShooterVars.Power

object ShooterHardware: Mechanism {
    val shooter = lazy{NextMotor("shooter")}
    fun setPower(power: Double){
        shooter.value.throttle = power
    }

    override fun periodic() {
        updatetelmtry()
        setPower(Power)
    }
    fun updatetelmtry(){
        Telemetry.log("shooer speed", Power)
    }

}