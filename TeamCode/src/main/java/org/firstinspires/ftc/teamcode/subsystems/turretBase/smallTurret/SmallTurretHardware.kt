package org.firstinspires.ftc.teamcode.subsystems.turretBase.smallTurret

import dev.nextftc.hardware.actuators.NextServo
import org.firstinspires.ftc.teamcode.subsystems.turretBase.TurretHardware
import org.firstinspires.ftc.teamcode.util.HtmlTelemetry

object SmallTurretHardware : TurretHardware(SmallTurretVars) {
    override val s: NextServo = NextServo("small_turret_servo")

    override fun telemetry() {
        HtmlTelemetry.log("Small Turret Target", vars.targetPosition)
        HtmlTelemetry.log("Small Turret Servo Pos", getPositionServo())
    }
}
