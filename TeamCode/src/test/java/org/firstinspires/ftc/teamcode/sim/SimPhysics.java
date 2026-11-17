package org.firstinspires.ftc.teamcode.sim;

import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;

/**
 * Drivetrain physics for the simulated robot (top speed, coasting, braking).
 * Constants.foresightConfig is not tuned yet (its brake coefficients are 0, which
 * divides by zero inside the sim's physics and makes the robot teleport).
 * Once Constants is tuned on the real robot, SimulateRobot can use it instead.
 */
public final class SimPhysics {
    private SimPhysics() {}

    /** Values from FTCodeSim's SimulateMecanumBase (a real, tuned mecanum robot). */
    public static ForesightConfig config() {
        return new ForesightConfig(c -> {
            Controller primaryForward = Controller.proportional(0.226493318780903);
            Controller secondaryForward = Controller.proportional(0.08368322927660432);
            Controller primaryLateral = Controller.proportional(0.5015381114306278);
            Controller secondaryLateral = Controller.proportional(0.18530493082846347);
            c.forwardTranslational.set(Controller.piecewise(secondaryForward).put(2.5, primaryForward));
            c.strafeTranslational.set(Controller.piecewise(secondaryLateral).put(2.5, primaryLateral));
            c.coast.set(Controller.proportionalFeedforward(0.012957603483588636));
            c.brake.set(Controller.proportionalFeedforward(0.01101396296105034));
            c.headingFeedback.set(Controller.proportional(2.447421655157805));
            c.headingBrakeCoefficients.set(Vector2D.cartesian(0.04730634263294, 0.007540928288975995));
            c.linearBrakeCoefficients.set(Matrix.diag(0.06447, 0.06447));
            c.quadraticBrakeCoefficients.set(Matrix.diag(0.0024, 0.0024));
            c.maxAchievableForwardVelocity.set(82.80318372444954);
            c.maxAchievableStrafeVelocity.set(66.35440133280454);
            c.naturalForwardDeceleration.set(42.84137414187512);
            c.naturalStrafeDeceleration.set(62.12942961650345);
        });
    }
}
