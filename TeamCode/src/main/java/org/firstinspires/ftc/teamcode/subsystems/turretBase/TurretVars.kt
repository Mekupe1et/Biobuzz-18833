package org.firstinspires.ftc.teamcode.subsystems.turretBase

import com.bylazar.configurables.annotations.Configurable


abstract class TurretVars {
    abstract var servoRange: Double
    abstract var p: Double
    abstract var rotationPred: Double
    abstract var runTurret: Boolean
    abstract var offset: Double
    abstract var offsetLL: Double
    abstract var reducer: Double
    abstract var servoOffset: Double
    abstract var state: TurretState
    abstract var targetPosition: Double
}
enum class TurretState{
    TrackingAprilTags,
    ResetEncoder,
    Disabled
}