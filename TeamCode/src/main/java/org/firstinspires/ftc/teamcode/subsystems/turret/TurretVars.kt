package org.firstinspires.ftc.teamcode.subsystems.turret

import com.pedropathing.math.Vector


interface TurretVars {
    //TODO: add servo range and pos for both turrets
    var servoRange: Double
    var rotationPred: Double
    var runTurret: Boolean
    var turretPos: Vector
    var servoOffset: Double
    var targetPosition: Double
}