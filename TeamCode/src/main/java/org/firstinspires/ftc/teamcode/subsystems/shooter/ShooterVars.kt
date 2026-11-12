package org.firstinspires.ftc.teamcode.subsystems.shooter

import com.ThermalEquilibrium.homeostasis.Controllers.Feedback.BasicPID
import com.ThermalEquilibrium.homeostasis.Parameters.PIDCoefficients
import com.bylazar.configurables.annotations.Configurable
import org.firstinspires.ftc.teamcode.util.InterpLUT


interface ShooterVars {
    var shootPowLUT: InterpLUT
    var hoodLUT: InterpLUT
    var kv: Double
    var ks: Double
    var targetVelocity: Double
    var veloCoefficients: PIDCoefficients
    var veloControl: BasicPID
    val minLUTVal: Double
        get() {
            return shootPowLUT.minX
        }
    val maxLUTVal: Double
        get() {
            return shootPowLUT.maxX
        }
    fun createShooterLut(): InterpLUT
    fun createHoodLUT(): InterpLUT

    fun createLUT(map: Map<Double, Double>): InterpLUT{
        var lut = InterpLUT()
        map.forEach{
            lut.add(it.key, it.value)
        }
        lut.createLUT()
        return lut
    }
}