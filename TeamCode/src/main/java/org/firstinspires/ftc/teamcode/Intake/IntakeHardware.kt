package org.firstinspires.ftc.teamcode.Intake

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareMap

class IntakeHardware {
    lateinit var intake: DcMotor
    fun intakeDc(hardwareMap: HardwareMap) {
        intake = hardwareMap.get(DcMotor::class.java, "intake")
    }
}