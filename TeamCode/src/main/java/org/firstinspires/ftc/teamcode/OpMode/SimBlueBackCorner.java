package org.firstinspires.ftc.teamcode.OpMode;

import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Blue alliance, starting in the corner of the blue wall and the back wall,
 * facing into the field, back and side of the robot frame touching the walls; clear of the FLOWERS (G304).
 */
@TeleOp(name = "Sim Blue Back Corner", group = "Sim")
public class SimBlueBackCorner extends SimOpMode {
    @Override
    protected Pose startPose() {
        return new Pose(131.7, 132.8, Math.toRadians(180));
    }
}
