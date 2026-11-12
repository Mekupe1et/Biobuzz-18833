package org.firstinspires.ftc.teamcode.subsystems.global

import com.pedropathing.math.Vector
import com.qualcomm.hardware.lynx.LynxModule
import dev.nextftc.robot.NextRobot
import org.firstinspires.ftc.robotcore.external.navigation.VoltageUnit
import org.firstinspires.ftc.teamcode.subsystems.global.RobotVars.nominalVoltage
import org.firstinspires.ftc.teamcode.util.MyOpModeManager

object Robot : NextRobot {
    var deltaVec = Vector(0.0, 0.0)

    private val controlHub by lazy { MyOpModeManager.hm.getAll(LynxModule::class.java)[0] }
    private var cachedVoltage = 12.0
    private var lastVoltageUpdateTime = 0L

    val voltage: Double
        get() {
            val currentNs = System.nanoTime()
            if (currentNs - lastVoltageUpdateTime >= 200_000_000L) { // 200ms
                cachedVoltage = controlHub.getInputVoltage(VoltageUnit.VOLTS)
                lastVoltageUpdateTime = currentNs
            }
            return cachedVoltage
        }

    fun normalizePower(power: Double): Double = power * nominalVoltage / voltage
}
