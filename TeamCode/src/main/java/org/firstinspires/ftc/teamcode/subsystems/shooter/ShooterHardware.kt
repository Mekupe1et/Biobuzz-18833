package org.firstinspires.ftc.teamcode.subsystems.shooter

import androidx.core.math.MathUtils.clamp
import dev.nextftc.hardware.actuators.NextMotor
import dev.nextftc.hardware.actuators.NextServo
import org.firstinspires.ftc.teamcode.subsystems.global.Robot.deltaVec
import org.firstinspires.ftc.teamcode.subsystems.global.Robot.normalizePower
import org.firstinspires.ftc.teamcode.subsystems.turret.TurretHardware
import org.firstinspires.ftc.teamcode.util.HtmlTelemetry
import org.firstinspires.ftc.teamcode.util.Subsystem
import kotlin.math.*

abstract class ShooterHardware(val turret: TurretHardware, val vars: ShooterVars) : Subsystem() {
    abstract val shooterMotor: NextMotor
    abstract val hoodServo: NextServo

    private var lastPower = 0.0
    private var lastHoodPos = -1.0
    private val IO_THRESHOLD = 0.001 // Smallest change worth sending to hardware

    var currentVelocity = 0.0
    private var hoodTarget = 0.0

    override fun start() {
        lastHoodPos = -1.0
        lastPower = -2.0
        setPower(0.0, force = true)
    }

    // --- Optimized Getters (Read once per loop) ---

    fun getVelocity(): Double = currentVelocity

    // --- Optimized Setters (Write-Skipping) ---

    fun setPower(power: Double, force: Boolean = false) {
        val clampedPower = normalizePower(power)
        if (force || abs(clampedPower - lastPower) > IO_THRESHOLD) {
            shooterMotor.throttle = clampedPower
            lastPower = clampedPower
        }
    }

    fun setHoodPosition(position: Double) {
        val clampedPos = clamp(position, 0.0, 0.52)
        if (abs(clampedPos - lastHoodPos) > IO_THRESHOLD) {
            hoodServo.position = clampedPos
            lastHoodPos = clampedPos
        }
    }


    /**
     * Logic calculation - No Hardware IO here
     */
    fun update() {
        // Cache smartDist locally to ensure consistency across calculations
        val distance = clamp(deltaVec.magnitude(), vars.minLUTVal, vars.maxLUTVal)

        vars.targetVelocity = vars.shootPowLUT.get(distance)
        hoodTarget = vars.hoodLUT.get(distance)

        // Motor Control Logic
        if (vars.targetVelocity < 1.0) {
            setPower(0.0)
        } else {
            val feedForward = (vars.kv * vars.targetVelocity) + vars.ks
            val feedback = vars.veloControl.calculate(vars.targetVelocity, currentVelocity)
            setPower(feedback + feedForward)
        }

        setHoodPosition(hoodTarget)
        turret.update()
    }

    override fun periodic() {
        // Perform all hardware READS at the start of the loop
        currentVelocity = shooterMotor.encoderVelocity.magnitude
    }

    override fun telemetry() {
        update()
        HtmlTelemetry.addData("Shooter velocity", currentVelocity)
        HtmlTelemetry.addData("Shooter target", vars.targetVelocity)
    }
}