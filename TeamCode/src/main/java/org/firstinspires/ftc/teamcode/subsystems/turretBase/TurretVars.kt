package org.firstinspires.ftc.teamcode.subsystems.turretBase

import com.bylazar.configurables.annotations.Configurable


interface TurretVars {
    var servoRange: Double
    var p: Double
    var rotationPred: Double
    var runTurret: Boolean
    var offset: Double
    var offsetLL: Double
    var reducer: Double
    var servoOffset: Double
    var state: TurretState
    var targetPosition: Double
}
enum class TurretState{
    TrackingAprilTags,
    ResetEncoder,
    Disabled
}