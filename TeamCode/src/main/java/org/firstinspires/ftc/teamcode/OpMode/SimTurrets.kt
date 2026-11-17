package org.firstinspires.ftc.teamcode.OpMode

import dev.nextftc.hardware.actuators.NextServo
import org.firstinspires.ftc.teamcode.Subsystems.TurretSubsystem.TurretHardware
import org.firstinspires.ftc.teamcode.Subsystems.TurretSubsystem.TurretState
import org.firstinspires.ftc.teamcode.Subsystems.TurretSubsystem.TurretVars

/**
 * Concrete turrets for the abstract TurretHardware / TurretVars in subsystems/turretBase
 * (the subsystem code itself is unchanged). TurretHardware.update() does the aiming at
 * Robot.deltaVec; these only supply the servo and the tuning values.
 */
class SimTurretVars : TurretVars() {
    override var servoRange = SimOpMode.TURRET_RANGE_DEG // degrees of servo travel
    override var p = 0.0
    override var rotationPred = 0.0
    override var runTurret = false
    override var offset = 0.0
    override var offsetLL = 0.0
    override var reducer = 1.0
    override var servoOffset = 0.0                        // 0 = servo 0.5 faces the robot's front
    override var state = TurretState.Disabled
    override var targetPosition = 0.0
}

object BigTurret : TurretHardware(SimTurretVars()) {
    override val s = NextServo(SimOpMode.TURRET_BIG)
}

object SmallTurret : TurretHardware(SimTurretVars()) {
    override val s = NextServo(SimOpMode.TURRET_SMALL)
}
