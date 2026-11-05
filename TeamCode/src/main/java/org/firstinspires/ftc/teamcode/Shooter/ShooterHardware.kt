package org.firstinspires.ftc.teamcode.Shooter
import dev.nextftc.hardware.actuators.NextMotor
import dev.nextftc.robot.Mechanism

object ShooterHardware: Mechanism {
    val shooter = lazy{NextMotor("shooter")}
    

}