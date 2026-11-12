package org.firstinspires.ftc.teamcode.subsystems.shooter.bigShooter

import com.ThermalEquilibrium.homeostasis.Controllers.Feedback.BasicPID
import com.ThermalEquilibrium.homeostasis.Parameters.PIDCoefficients
import com.bylazar.configurables.annotations.Configurable
import org.firstinspires.ftc.teamcode.subsystems.shooter.ShooterVars
import org.firstinspires.ftc.teamcode.util.InterpLUT

@Configurable
object BigShooterVars : ShooterVars {
    @JvmField var shootPowLUTField = createShooterLut()
    override var shootPowLUT: InterpLUT
        get() = shootPowLUTField
        set(value) { shootPowLUTField = value }

    @JvmField var hoodLUTField = createHoodLUT()
    override var hoodLUT: InterpLUT
        get() = hoodLUTField
        set(value) { hoodLUTField = value }

    @JvmField var kvField = 0.00039
    override var kv: Double
        get() = kvField
        set(value) { kvField = value }

    @JvmField var ksField = 0.045
    override var ks: Double
        get() = ksField
        set(value) { ksField = value }

    @JvmField var targetVelocityField = 0.0
    override var targetVelocity: Double
        get() = targetVelocityField
        set(value) { targetVelocityField = value }

    @JvmField var veloCoefficientsField = PIDCoefficients(0.009, 0.0, 0.00)
    override var veloCoefficients: PIDCoefficients
        get() = veloCoefficientsField
        set(value) { veloCoefficientsField = value }

    @JvmField var veloControlField = BasicPID(veloCoefficientsField)
    override var veloControl: BasicPID
        get() = veloControlField
        set(value) { veloControlField = value }

    override fun createShooterLut(): InterpLUT {
        return createLUT(mapOf(61.3 to 960.0, 71.0 to 1015.0, 94.0 to 1120.0, 117.2 to 1240.0, 135.0 to 1380.0, 162.8 to 1470.0))
    }

    override fun createHoodLUT(): InterpLUT {
        return createLUT(mapOf(61.3 to 0.0, 71.0 to 0.1, 94.0 to 0.25, 117.2 to 0.25, 135.0 to 0.35, 162.8 to 0.35))
    }
}
