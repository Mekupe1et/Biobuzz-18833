package org.firstinspires.ftc.teamcode.OpMode;

import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Blue alliance, starting in the corner of the blue wall and the audience wall,
 * facing into the field, back and side of the robot frame touching the walls; clear of the blue
 * LOADING ZONE and the FLOWERS (G304).
 */
@TeleOp(name = "Sim Blue Audience Corner", group = "Sim")
public class SimBlueAudienceCorner extends SimOpMode {
    @Override
    protected Pose startPose() {
        return new Pose(131.7, 8.7, Math.toRadians(180));
    }
}
