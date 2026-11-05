package org.firstinspires.ftc.teamcode.Shooter
import dev.nextftc.hardware.actuators.NextMotor
import dev.nextftc.robot.Mechanism
import org.firstinspires.ftc.teamcode.Shooter.ShooterVars.speed

object ShooterHardware: Mechanism {
    val shooter = lazy{NextMotor("shooter")}
    fun setPower(power: Double){
        shooter.value.throttle = power
    }

    override fun periodic() {
        updatetelmtry()
        ShooterHardware.setPower(speed)
    }
    fun updatetelmtry(){

    }

}