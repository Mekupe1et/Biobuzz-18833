package org.firstinspires.ftc.teamcode.sim;

import org.codeblooded.ftcodesim.hardware.devices.SimHardwareMechanism;
import org.codeblooded.ftcodesim.hardware.devices.SimMotor;
import org.firstinspires.ftc.teamcode.OpMode.SimOpMode;
import org.psilynx.psikit.core.Logger;
import org.psilynx.psikit.core.wpi.math.Pose3d;
import org.psilynx.psikit.core.wpi.math.Quaternion;
import org.psilynx.psikit.core.wpi.math.Rotation3d;
import org.psilynx.psikit.core.wpi.math.Transform3d;
import org.psilynx.psikit.core.wpi.math.Translation3d;

/**
 * Animates the moving parts of the "My FTC Robot" model: publishes a Pose3d[] under
 * "Robot/Components" in the order of config.json:
 *   [0] intake arm          servo intk_arm
 *   [1] big turret          servo turret_big
 *   [2] small turret        servo turret_small
 *   [3] small hood          servo hood_small (rides on the small turret)
 *   [4..7] drive wheels     FL, FR, BL, BR, spun by the simulated wheel speeds
 *   [8] rollers on the arm pivot shaft   } spun by the intake motor (intk_mtr),
 *   [9] rollers on the front shaft       } ride on the arm
 *   [10] rear intake rollers, 333mm shaft  }
 *   [11] rear intake rollers, 343mm shaft  } also spun by intk_mtr
 *   [12] rear left side wheels (vertical)  }
 *   [13] rear right side wheels (vertical) }
 * All angles 0 = exactly as in the CAD.
 */
public class RobotComponents implements SimHardwareMechanism {
    public static final String LOG_KEY = "Robot/Components";

    // ---- Servo position -> angle (tune to match the real robot) ----
    /** IntakeVars.intakeArmPosDown / intakeArmPosUp. */
    public static double ARM_POS_DOWN = 0.0, ARM_POS_UP = 1.0;
    /** CAD has the arm deployed (DOWN = 0). Positive = swings down, negative = up. */
    public static double ARM_ANGLE_DOWN = 0.0, ARM_ANGLE_UP = Math.toRadians(-80);
    /** Servo 0.5 = turret facing the robot's front (TurretHardware.setAngle with servoOffset 0). */
    public static double TURRET_RANGE = Math.toRadians(SimOpMode.TURRET_RANGE_DEG);
    public static double HOOD_ANGLE_AT_0 = 0.0, HOOD_ANGLE_AT_1 = Math.toRadians(35);
    /** Intake roller speed at full power, rad/s (looks right; not measured). */
    public static double ROLLER_MAX_SPEED = 40;

    // ---- Pivots, robot frame (metres): README.txt + wheel/roller export ----
    private static final Translation3d ARM_PIVOT = new Translation3d(0.1307, 0.0, 0.1560);
    private static final Translation3d BIG_PIVOT = new Translation3d(-0.0006, 0.1166, 0.3290);
    private static final Translation3d SMALL_PIVOT = new Translation3d(-0.0006, -0.1231, 0.3290);
    private static final Translation3d HOOD_FROM_SMALL = new Translation3d(0.0283, -0.0533, 0.0580);
    private static final double[] HOOD_AXIS = normalize(0.8647, 0.5023, 0.0);
    private static final Translation3d[] WHEEL_PIVOTS = {
            new Translation3d(0.1174, 0.1930, 0.0517),   // FL
            new Translation3d(0.1174, -0.1920, 0.0514),  // FR
            new Translation3d(-0.1181, 0.1919, 0.0515),  // BL
            new Translation3d(-0.1186, -0.1930, 0.0518), // BR
    };
    private static final Translation3d ROLLER_A = new Translation3d(0.1305, 0.0, 0.1559);
    private static final Translation3d ROLLER_B = new Translation3d(0.1987, 0.0, 0.1328);
    // Rear (static) intake: two roller shafts + small side wheels on vertical axles.
    private static final Translation3d ROLLER_C = new Translation3d(-0.2010, 0.0, 0.1190);
    private static final Translation3d ROLLER_D = new Translation3d(-0.1170, 0.0, 0.1650);
    private static final Translation3d SIDE_L = new Translation3d(-0.2184, 0.1817, 0.0375);
    private static final Translation3d SIDE_R = new Translation3d(-0.2184, -0.1817, 0.0375);

    private final SimHardware.SimServoEx arm, big, small, hood;
    private final SimMotor[] wheels;          // FL, FR, BL, BR
    private final SimHardware.SimMotorEx intake;

    private final double[] wheelAngle = new double[4];
    private double rollerAngle;

    public RobotComponents(SimHardware.SimServoEx arm, SimHardware.SimServoEx big, SimHardware.SimServoEx small,
                           SimHardware.SimServoEx hood, SimMotor[] wheels, SimHardware.SimMotorEx intake) {
        this.arm = arm;
        this.big = big;
        this.small = small;
        this.hood = hood;
        this.wheels = wheels;
        this.intake = intake;
    }

    @Override
    public void update(double deltaTime) {
        double armT = (arm.getActualPosition() - ARM_POS_DOWN) / (ARM_POS_UP - ARM_POS_DOWN);
        double armAngle = ARM_ANGLE_DOWN + armT * (ARM_ANGLE_UP - ARM_ANGLE_DOWN);
        double bigYaw = (big.getActualPosition() - 0.5) * TURRET_RANGE;
        double smallYaw = (small.getActualPosition() - 0.5) * TURRET_RANGE;
        double hoodAngle = HOOD_ANGLE_AT_0 + hood.getActualPosition() * (HOOD_ANGLE_AT_1 - HOOD_ANGLE_AT_0);

        // Wheels: FTCodeSim's wheel speed (rad/s). Positive = rolling the robot forward = +pitch.
        for (int i = 0; i < 4; i++) {
            wheelAngle[i] = wrap(wheelAngle[i] + wheels[i].velocity * deltaTime);
        }
        // Rollers: positive intake power pulls game pieces in (bottom of roller moves backward = +pitch).
        rollerAngle = wrap(rollerAngle + intake.getCommandedPower() * ROLLER_MAX_SPEED * deltaTime);

        Pose3d armPose = new Pose3d(ARM_PIVOT, new Rotation3d(0, armAngle, 0));
        Pose3d bigPose = new Pose3d(BIG_PIVOT, new Rotation3d(0, 0, bigYaw));
        Pose3d smallPose = new Pose3d(SMALL_PIVOT, new Rotation3d(0, 0, smallYaw));
        Pose3d hoodPose = smallPose.transformBy(new Transform3d(HOOD_FROM_SMALL, axisAngle(HOOD_AXIS, hoodAngle)));

        Pose3d[] poses = new Pose3d[14];
        poses[0] = armPose;
        poses[1] = bigPose;
        poses[2] = smallPose;
        poses[3] = hoodPose;
        for (int i = 0; i < 4; i++) {
            poses[4 + i] = new Pose3d(WHEEL_PIVOTS[i], new Rotation3d(0, wheelAngle[i], 0));
        }
        poses[8] = armPose.transformBy(new Transform3d(minus(ROLLER_A, ARM_PIVOT), new Rotation3d(0, rollerAngle, 0)));
        poses[9] = armPose.transformBy(new Transform3d(minus(ROLLER_B, ARM_PIVOT), new Rotation3d(0, rollerAngle, 0)));
        // Rear intake pulls from the back (-X): rollers turn the other way; side wheels push inward.
        poses[10] = new Pose3d(ROLLER_C, new Rotation3d(0, -rollerAngle, 0));
        poses[11] = new Pose3d(ROLLER_D, new Rotation3d(0, -rollerAngle, 0));
        poses[12] = new Pose3d(SIDE_L, new Rotation3d(0, 0, rollerAngle));
        poses[13] = new Pose3d(SIDE_R, new Rotation3d(0, 0, -rollerAngle));
        Logger.recordOutput(LOG_KEY, poses);
    }

    private static Translation3d minus(Translation3d a, Translation3d b) {
        return new Translation3d(a.getX() - b.getX(), a.getY() - b.getY(), a.getZ() - b.getZ());
    }

    private static double wrap(double a) {
        return Math.atan2(Math.sin(a), Math.cos(a));
    }

    private static Rotation3d axisAngle(double[] axis, double angle) {
        double s = Math.sin(angle / 2);
        return new Rotation3d(new Quaternion(Math.cos(angle / 2), axis[0] * s, axis[1] * s, axis[2] * s));
    }

    private static double[] normalize(double x, double y, double z) {
        double n = Math.sqrt(x * x + y * y + z * z);
        return new double[]{x / n, y / n, z / n};
    }
}
