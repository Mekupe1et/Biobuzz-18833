package org.firstinspires.ftc.teamcode.Drive
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap

class DriveHardware {
    lateinit var frontright: DcMotor
    lateinit var frontleft: DcMotor
    lateinit var backright: DcMotor
    lateinit var backleft: DcMotor
    fun driving(hardwareMap: HardwareMap){
        frontleft = hardwareMap.get(DcMotor::class.java, "fl")
        frontright = hardwareMap.get(DcMotor::class.java, "fr")
        backleft = hardwareMap.get(DcMotor::class.java,"bl")
        backright = hardwareMap.get(DcMotor::class.java,"br")

        frontleft.direction = DcMotorSimple.Direction.REVERSE
        backleft.direction = DcMotorSimple.Direction.REVERSE
    }

}