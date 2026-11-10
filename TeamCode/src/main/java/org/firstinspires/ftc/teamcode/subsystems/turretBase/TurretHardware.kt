package org.firstinspires.ftc.teamcode.Subsystems.TurretSubsystem

import androidx.core.math.MathUtils.clamp
import com.pedropathing.math.Vector
import dev.nextftc.hardware.actuators.NextMotor
import dev.nextftc.hardware.actuators.NextServo
import dev.nextftc.units.radians
import org.firstinspires.ftc.teamcode.Subsystems.DriveSubsystem.DriveHardware
import org.firstinspires.ftc.teamcode.Subsystems.DriveSubsystem.DriveHardware.follower
import org.firstinspires.ftc.teamcode.subsystems.Robot
import org.firstinspires.ftc.teamcode.subsystems.Robot.deltaVec
import org.firstinspires.ftc.teamcode.util.Subsystem
import org.firstinspires.ftc.teamcode.util.Util.vectorAngle
import org.firstinspires.ftc.teamcode.util.Util.wrap360
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sin
import kotlin.math.cos

abstract class TurretHardware(val vars: TurretVars) : Subsystem() {
    // Lazy initialize to avoid null refs during object creation
    protected abstract val s: NextServo


    // --- CACHED VALUES (The "Snapshot") ---
    private var cachedServoPos = 0.0
    private var lastSetPos = -1.0 // Track what we LAST sent to the hub

    /**
     * Read all hardware ONCE at the start of the loop.
     * This eliminates redundant I2C/USB overhead.
     */
    override fun periodic() {
        cachedServoPos = s.position
    }

    fun setPosition(pos: Double) {
        // Optimization: Only write to hardware if the change is significant (> 0.1%)
        // This keeps the Control Hub's command buffer clear.
        if (Math.abs(lastSetPos - pos) > 0.0001) {
            s.position = pos
            lastSetPos = pos
        }
    }

    fun setAngle(angle: Double) {
        // Multiplication is slightly faster than division; pre-calc 1/servoRange if possible
        val pos = (angle + vars.servoOffset + (vars.servoRange * 0.5)) / vars.servoRange
        setPosition(pos)
    }


    fun getPositionServo(): Double = cachedServoPos
    fun getTargetAngle(): Double = cachedServoPos * 360.0

    fun getGlobalHeadingTarget(): Double {
        val baseHeading = -Math.toDegrees(DriveHardware.getPoseEstimate().heading())
        return wrap360(baseHeading + getTargetAngle())
    }

    /**
     * Normalizes an angle to [-PI, PI] using atan2 for O(1) performance.
     * Much faster than while loops for large angles.
     */
    protected fun fastNormalizeRad(radians: Double): Double {
        return atan2(sin(radians), cos(radians))
    }
    fun update(targetOffset: Double) {
        if (!vars.runTurret) return
        // 1. Snapshot robot state to local variables
        val robotHeadingRad = follower.value.pose().heading().radians + (follower.value.twist().omega.radians * Robot.looptime / 1000.0) + follower.value.twist().omega.radians * vars.rotationPred
        val globalTargetAngleRad = vectorAngle(deltaVec) - targetOffset

        var localAngleRad = globalTargetAngleRad - robotHeadingRad.magnitude + vars.offsetLL

        // 3. Normalize to shortest path
        localAngleRad = fastNormalizeRad(localAngleRad)

        // 4. Convert and Clamp
        val localAngleDeg = Math.toDegrees(localAngleRad)
        val finalAngle = clamp(localAngleDeg, -vars.servoRange * 0.5, vars.servoRange * 0.5)

        setAngle(finalAngle)
    }

    override fun start() {
        vars.runTurret = true
        lastSetPos = -1.0
        vars.state = TurretState.TrackingAprilTags
        vars.targetPosition = 180.0
    }

}