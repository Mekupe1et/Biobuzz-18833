package org.firstinspires.ftc.teamcode.subsystems.turret.bigTurret

import dev.nextftc.hardware.actuators.NextServo
import org.firstinspires.ftc.teamcode.subsystems.turret.TurretHardware
import org.firstinspires.ftc.teamcode.util.HtmlTelemetry

object BigTurretHardware : TurretHardware(BigTurretVars) {
    override val s: NextServo = NextServo("big_turret_servo")

    override fun telemetry() {
        HtmlTelemetry.log("Big Turret Target", vars.targetPosition)
        HtmlTelemetry.log("Big Turret Servo Pos", getPositionServo())
    }
}
