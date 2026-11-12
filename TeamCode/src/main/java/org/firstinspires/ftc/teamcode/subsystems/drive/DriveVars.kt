package org.firstinspires.ftc.teamcode.subsystems.drive

import com.bylazar.configurables.annotations.Configurable
import com.pedropathing.drivetrain.DrivePowers
import com.pedropathing.follower.ManualDrive
import com.pedropathing.math.Pose


@Configurable
object DriveVars {


    @JvmField var startingPose = Pose(72.0, 72.0, 0.0)
    @JvmField var finishingPose = Pose(0.0, 0.0, 0.0)


}