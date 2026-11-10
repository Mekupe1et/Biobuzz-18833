package org.firstinspires.ftc.teamcode.subsystems.drive

import com.pedropathing.ivy.commands.Commands.instant

import org.firstinspires.ftc.teamcode.subsystems.drive.DriveHardware.follower
import org.firstinspires.ftc.teamcode.subsystems.drive.DriveHardware.getPoseEstimate
import org.firstinspires.ftc.teamcode.subsystems.drive.DriveHardware.isHolding


object DriveCommands {
    val resetIMU = instant{
        follower.value.setHeading(0.0)
        if (isHolding) follower.value.hold(getPoseEstimate())

    }
//    val resetPos = InstantCommand{
//        if (RobotVars.allianceColor == AllianceColor.RED) {
//            follower.pose = resetPosRed
//        } else {
//            follower.pose = resetPosBlue
//        }
//        if (isHolding) follower.holdPoint(follower.pose)
//
//    }
}