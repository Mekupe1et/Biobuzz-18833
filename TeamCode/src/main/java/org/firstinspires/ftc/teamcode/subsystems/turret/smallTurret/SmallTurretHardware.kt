package org.firstinspires.ftc.teamcode.subsystems.turret.smallTurret

import dev.nextftc.hardware.actuators.NextServo
import org.firstinspires.ftc.teamcode.subsystems.turret.TurretHardware
import org.firstinspires.ftc.teamcode.util.HtmlTelemetry

object SmallTurretHardware : TurretHardware(SmallTurretVars) {
    override val s: NextServo = NextServo("small_turret_servo")

    override fun telemetry() {
        HtmlTelemetry.log("Small Turret Target", vars.targetPosition)
        HtmlTelemetry.log("Small Turret Servo Pos", getPositionServo())
    }
}
