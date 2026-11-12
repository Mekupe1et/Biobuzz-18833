package org.firstinspires.ftc.teamcode.subsystems.drive

import com.bylazar.configurables.annotations.Configurable
import com.pedropathing.drivetrain.DrivePowers
import com.pedropathing.follower.ManualDrive
import com.pedropathing.math.Pose
import dev.nextftc.hardware.RobotController
import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta
import org.firstinspires.ftc.teamcode.subsystems.drive.DriveVars.startingPose
import org.firstinspires.ftc.teamcode.pedro.Constants.create
import org.firstinspires.ftc.teamcode.util.MyOpModeManager
import org.firstinspires.ftc.teamcode.util.MyOpModeManager.gp1
import org.firstinspires.ftc.teamcode.util.Subsystem
import org.firstinspires.ftc.teamcode.util.Util
import kotlin.math.abs

@Configurable
object DriveHardware : Subsystem() {
    @JvmField val follower = lazy{create(RobotController.hardwareMap)}

    var isHolding = false
        private set

    fun getPoseEstimate(): Pose = methodWarper { follower.value.pose() }
    fun setPoseEstimate(pose: Pose) = methodWarper { follower.value.setPose(pose) }

    override fun start() {
        if (Util.roughlyEquals(getPoseEstimate(), Pose(0.0,0.0,0.0), 1.0)){
            setPoseEstimate(startingPose)
        }
    }

    override fun periodic() {
        if (MyOpModeManager.flavor == OpModeMeta.Flavor.TELEOP){
            val stickThreshold = 0.03
            val velocityThreshold = 0.01
            val angularThreshold = 0.03

            // Cache stick values to avoid repeated calls
            val lx = gp1.leftStickX.value
            val ly = gp1.leftStickY.value
            val rx = gp1.rightStickX.value
            var powers: DrivePowers = ManualDrive.fieldCentric(
                -gp1.leftStickY.value,
                gp1.leftStickX.value,
                gp1.rightStickX.value,
                follower.value.pose().heading()
            )
            val sticksAtRest = abs(lx) < stickThreshold &&
                    abs(ly) < stickThreshold &&
                    abs(rx) < stickThreshold

            if (sticksAtRest) {
                val vel = follower.value.velocity().toVector()
                val robotStopped = vel.magnitude() < velocityThreshold &&
                        abs(follower.value.twist().omega) < angularThreshold

                if (robotStopped && !isHolding) {
                    follower.value.hold(getPoseEstimate())
                    isHolding = true
                }
            } else {
                if (isHolding || follower.value.isBusy) {
                    follower.value.manual(powers);
                    isHolding = false
                }
            }
        }
    }
}
