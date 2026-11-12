package org.firstinspires.ftc.teamcode.subsystems.turret.smallTurret

import com.bylazar.configurables.annotations.Configurable
import com.pedropathing.math.Vector
import org.firstinspires.ftc.teamcode.subsystems.turret.TurretVars

@Configurable
object SmallTurretVars : TurretVars {
    @JvmField var servoRangeField = 180.0
    override var servoRange: Double
        get() = servoRangeField
        set(value) { servoRangeField = value }

    @JvmField var rotationPredField = 0.0
    override var rotationPred: Double
        get() = rotationPredField
        set(value) { rotationPredField = value }

    @JvmField var runTurretField = true
    override var runTurret: Boolean
        get() = runTurretField
        set(value) { runTurretField = value }

    @JvmField var turretPosField = Vector(0.0, 0.0)
    override var turretPos: Vector
        get() = turretPosField
        set(value) { turretPosField = value }

    @JvmField var servoOffsetField = 0.0
    override var servoOffset: Double
        get() = servoOffsetField
        set(value) { servoOffsetField = value }

    @JvmField var targetPositionField = 180.0
    override var targetPosition: Double
        get() = targetPositionField
        set(value) { targetPositionField = value }
}