package org.firstinspires.ftc.teamcode.subsystems.shooter.smallShooter

import dev.nextftc.hardware.actuators.NextMotor
import dev.nextftc.hardware.actuators.NextServo
import org.firstinspires.ftc.teamcode.subsystems.shooter.ShooterHardware
import org.firstinspires.ftc.teamcode.subsystems.turret.smallTurret.SmallTurretHardware

object SmallShooterHardware : ShooterHardware(SmallTurretHardware, SmallShooterVars) {
    override val shooterMotor = NextMotor("small_shooter_mtr")
    override val hoodServo = NextServo("small_hood_servo")
}
