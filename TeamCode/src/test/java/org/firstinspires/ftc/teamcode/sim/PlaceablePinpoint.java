package org.firstinspires.ftc.teamcode.sim;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;

import org.codeblooded.ftcodesim.ascope.boundaries.MotionVector;
import org.codeblooded.ftcodesim.hardware.devices.SimPinpoint;
import org.codeblooded.ftcodesim.hardware.drivetrain.SimulatedDrivetrain;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit;

/**
 * Simulated Pinpoint for our robot.
 *
 * 1. Pedro's PinpointLocalizer reads getPosX / getPosY / getHeading / getVelX / getVelY.
 *    FTCodeSim's SimPinpoint only fakes getPosition(), so without these the follower
 *    always thinks it's at (0, 0, 0). Here they all report the simulated robot.
 * 2. setPosition (follower.setPose(startPose)) also PLACES the simulated robot there.
 *    On the real robot that call tells the Pinpoint where the robot was put.
 */
public class PlaceablePinpoint extends SimPinpoint {
    private final SimulatedDrivetrain drivetrain;

    public PlaceablePinpoint(SimulatedDrivetrain drivetrain) {
        super(drivetrain);
        this.drivetrain = drivetrain;
    }

    @Override
    public void setPosition(Pose2D pos) {
        drivetrain.velocity = new MotionVector(0, 0, 0);
        drivetrain.setPosition(new MotionVector(
                pos.getX(DistanceUnit.INCH), pos.getY(DistanceUnit.INCH), pos.getHeading(AngleUnit.RADIANS)));
        super.setPosition(pos);
    }

    // No I2C in the sim: readings come straight from the simulated drivetrain.
    @Override public void update() { }
    @Override public void update(GoBildaPinpointDriver.ReadData data) { }

    @Override
    public double getPosX(DistanceUnit unit) {
        return unit.fromInches(drivetrain.position.x());
    }

    @Override
    public double getPosY(DistanceUnit unit) {
        return unit.fromInches(drivetrain.position.y());
    }

    @Override
    public double getHeading(AngleUnit unit) {
        return unit.fromRadians(drivetrain.position.theta());
    }

    @Override
    public double getHeading(UnnormalizedAngleUnit unit) {
        return unit.fromRadians(drivetrain.position.theta());
    }

    @Override
    public double getVelX(DistanceUnit unit) {
        return unit.fromInches(drivetrain.velocity.x());
    }

    @Override
    public double getVelY(DistanceUnit unit) {
        return unit.fromInches(drivetrain.velocity.y());
    }

    @Override
    public double getHeadingVelocity(UnnormalizedAngleUnit unit) {
        return unit.fromRadians(drivetrain.velocity.theta());
    }
}
