package org.firstinspires.ftc.teamcode.subsystems.shooter.bigShooter

import dev.nextftc.hardware.actuators.NextMotor
import dev.nextftc.hardware.actuators.NextServo
import org.firstinspires.ftc.teamcode.subsystems.shooter.ShooterHardware
import org.firstinspires.ftc.teamcode.subsystems.turret.bigTurret.BigTurretHardware

object BigShooterHardware : ShooterHardware(BigTurretHardware, BigShooterVars) {
    override val shooterMotor = NextMotor("big_shooter_mtr")
    override val hoodServo = NextServo("big_hood_servo")
}
