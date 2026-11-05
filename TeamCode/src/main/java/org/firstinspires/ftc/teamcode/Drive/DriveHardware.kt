package org.firstinspires.ftc.teamcode.Drive
import dev.nextftc.hardware.actuators.NextMotor
import dev.nextftc.robot.Mechanism
import dev.nextftc.robot.Telemetry


object DriveHardware : Mechanism{
    val frontright = lazy{NextMotor("fr")}
    val frontleft = lazy{NextMotor("fl")}
    val backright = lazy{NextMotor("br")}
    val backleft = lazy{NextMotor("bl")}
    fun setPowr(powr: Double, powr2: Double, powr3: Double, powr4: Double){
        backleft.value.throttle = powr
        backright.value.throttle = powr2
        frontleft.value.throttle = powr3
        frontright.value.throttle = powr4
    }
    override fun periodic() {
        DriveHardware.setPowr(DriveVars.bl, DriveVars.br, DriveVars.fl, DriveVars.fr)
    }
}