package org.firstinspires.ftc.teamcode.subsystems.turretBase.smallTurret

import com.bylazar.configurables.annotations.Configurable
import com.pedropathing.math.Vector
import org.firstinspires.ftc.teamcode.subsystems.turretBase.TurretState
import org.firstinspires.ftc.teamcode.subsystems.turretBase.TurretVars

@Configurable
object SmallTurretVars : TurretVars {
    override var servoRange: Double = 180.0
    override var rotationPred: Double = 0.0
    override var runTurret: Boolean = true
    override var turretPos: Vector = Vector(0.0, 0.0)
    override var servoOffset: Double = 0.0
    override var targetPosition: Double = 180.0
}
