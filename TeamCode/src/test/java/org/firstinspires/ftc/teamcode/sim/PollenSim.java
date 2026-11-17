package org.firstinspires.ftc.teamcode.sim;

import org.codeblooded.ftcodesim.ascope.boundaries.MotionVector;
import org.codeblooded.ftcodesim.hardware.devices.SimHardwareMechanism;
import org.codeblooded.ftcodesim.hardware.drivetrain.SimulatedDrivetrain;
import org.firstinspires.ftc.teamcode.OpMode.SimOpMode;
import org.psilynx.psikit.core.Logger;
import org.psilynx.psikit.core.wpi.math.Translation3d;

import java.util.ArrayList;
import java.util.List;

/**
 * POLLEN game pieces for the sim (2026-27 BIOBUZZ), driven only by the robot code's outputs:
 *  - starting POLLEN: 4 in each FLOWER (taken from the bottom), 4 in each GARDEN corner,
 *    8 per human player area (outside the field, shown only); PRELOAD in the robot (default 0)
 *  - intake: while intk_mtr runs inward, a POLLEN touching the front rollers (arm down) or the
 *    rear rollers goes into the robot, up to CAPACITY
 *  - shoot: while tM (transfer) runs, a stored POLLEN is launched every SHOT_INTERVAL from the
 *    turrets, in the direction each turret is really pointing. Landing in a CELL scores it.
 *  - the robot's frame pushes POLLEN out of the way
 * Published as Translation3d[] "Sim/Pollen" (AdvantageScope "Pollen" game piece source).
 * Pedro inches internally.
 */
public class PollenSim implements SimHardwareMechanism {
    public static final String LOG_KEY = "Sim/Pollen";
    public static int CAPACITY = 4;
    /** POLLEN in the robot at the start. The rules pre-load 4 (G304), but then the robot is full. */
    public static int PRELOAD = 0;
    public static double SHOT_INTERVAL = 0.25;      // s between shots
    public static double LAUNCH_HEIGHT = 16;        // in, turret exit height
    public static double RADIUS = 2.79 / 2;         // in
    private static final double G = 386.1;          // in/s^2
    private static final double CELL_RADIUS = 9;    // in, "inside the CELL opening" (20 x 14 in opening)

    // Intake reach in robot frame (in): ball centre must be inside to be pulled in.
    // Front: arm rollers (outer edge 10.1 in) plus enough to reach the bottom of a FLOWER,
    // whose POLLEN sit ~3.3 in inside its face.
    private static final double FRONT_X0 = 6.0, FRONT_X1 = 14.0, FRONT_HALF_Y = 7.5;
    // Rear: roller shafts (outer edge -10.1 in) and the corner side wheels (y = +-7.15 in,
    // 1.77 in radius), which funnel POLLEN into the rear intake.
    private static final double REAR_X0 = -14.0, REAR_X1 = -7.5, REAR_HALF_Y = 9.5;
    private static final double MAX_PICK_Z = 4;     // in, floor / bottom-of-FLOWER balls only
    // Turret pivots on the robot (in, +x front, +y left), from the CAD
    private static final double BIG_TURRET_X = -0.02, BIG_TURRET_Y = 4.59;
    private static final double SMALL_TURRET_X = -0.02, SMALL_TURRET_Y = -4.85;

    // CELL centres (Pedro x, y, z in), from the field model
    private static final double[][] CELLS = {
            {83.5, 81.1, 56.0},  // blue Scoring
            {83.5, 56.3, 41.6},  // blue Audience
            {58.0, 60.4, 56.0},  // red Audience
            {58.0, 85.2, 41.6},  // red Scoring
    };

    private static final class Ball {
        double x, y, z, vx, vy, vz;
        boolean flying, scored, outside;
        // being pulled into the robot: start point, progress 0..1, which intake
        boolean feeding, feedFront;
        double sx, sy, sz, feedT;
        // FLOWER stack: height it is dropping to (NaN = not dropping)
        double targetZ = Double.NaN;
        Ball(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
    }

    public static double FEED_TIME = 0.35;   // s for a POLLEN to slide from the field into the intake
    public static double STACK_DROP_SPEED = 20; // in/s, FLOWER stack settling after the bottom one is taken

    private final SimulatedDrivetrain drivetrain;
    private final SimHardware.SimServoEx arm, turretBig, turretSmall;
    private final SimHardware.SimMotorEx intake, transfer;
    private final List<Ball> balls = new ArrayList<>();
    private final List<List<Ball>> flowerStacks = new ArrayList<>();
    private int stored = PRELOAD;
    private int scored = 0;
    private double shotTimer = 0;
    private boolean nextBig = true;

    public PollenSim(SimulatedDrivetrain drivetrain, SimHardware.SimServoEx arm,
                     SimHardware.SimServoEx turretBig, SimHardware.SimServoEx turretSmall,
                     SimHardware.SimMotorEx intake, SimHardware.SimMotorEx transfer) {
        this.drivetrain = drivetrain;
        this.arm = arm;
        this.turretBig = turretBig;
        this.turretSmall = turretSmall;
        this.intake = intake;
        this.transfer = transfer;
        reset();
    }

    @Override
    public void reset() {
        balls.clear();
        flowerStacks.clear();
        stored = PRELOAD;
        scored = 0;
        for (double[] f : new double[][]{{2.7, 47.4}, {47.4, 138.8}, {94.1, 2.7}, {138.8, 94.1}}) {
            List<Ball> stack = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                Ball b = new Ball(f[0], f[1], 1.4 + i * 2.9);
                stack.add(b);
                balls.add(b);
            }
            flowerStacks.add(stack);
        }
        for (int i = 0; i < 4; i++) {
            balls.add(new Ball(1.5 + i * 2.87, 1.5, RADIUS));        // red GARDEN
            balls.add(new Ball(131.4 + i * 2.87, 140.0, RADIUS));    // blue GARDEN
        }
        for (int i = 0; i < 8; i++) {                                 // human player areas
            Ball red = new Ball(-2.3, 97.2 + i * 2.8, 0.8); red.outside = true; balls.add(red);
            Ball blue = new Ball(143.8, 24.7 + i * 2.8, 0.8); blue.outside = true; balls.add(blue);
        }
    }

    @Override
    public void update(double dt) {
        if (dt <= 0 || dt > 0.1) dt = Math.min(Math.max(dt, 0), 0.1);
        MotionVector p = drivetrain.position;
        double c = Math.cos(p.theta()), s = Math.sin(p.theta());

        // ---- intake ----
        boolean intaking = intake.getCommandedPower() > 0.1;
        boolean armDown = arm.getActualPosition() < 0.3; // IntakeVars: 0 = down
        if (intaking && stored < CAPACITY) {
            List<Ball> taken = new ArrayList<>();
            for (Ball b : balls) {
                if (b.flying || b.scored || b.outside || b.feeding || !Double.isNaN(b.targetZ)
                        || b.z > MAX_PICK_Z || stored >= CAPACITY) continue; // settled balls only
                double dx = b.x - p.x(), dy = b.y - p.y();
                double lx = dx * c + dy * s, ly = -dx * s + dy * c;   // robot frame
                boolean front = armDown && lx >= FRONT_X0 && lx <= FRONT_X1 && Math.abs(ly) <= FRONT_HALF_Y;
                boolean rear = lx >= REAR_X0 && lx <= REAR_X1 && Math.abs(ly) <= REAR_HALF_Y;
                if (front || rear) {
                    b.feeding = true; b.feedFront = front; b.feedT = 0;
                    b.sx = b.x; b.sy = b.y; b.sz = b.z;
                    stored++;
                    dropFlowerStack(b);
                }
            }
        }

        // ---- POLLEN sliding into the intake (out of the FLOWER's bottom opening / off the floor) ----
        List<Ball> inRobot = new ArrayList<>();
        for (Ball b : balls) {
            if (!b.feeding) continue;
            b.feedT += dt / FEED_TIME;
            double lx = b.feedFront ? 8.5 : -8.5;          // intake roller, robot frame
            double tx = p.x() + lx * c, ty = p.y() + lx * s, tz = 3.0;
            double e = b.feedT >= 1 ? 1 : b.feedT * b.feedT * (3 - 2 * b.feedT); // ease in/out
            b.x = b.sx + (tx - b.sx) * e;
            b.y = b.sy + (ty - b.sy) * e;
            b.z = b.sz + (tz - b.sz) * e;
            if (b.feedT >= 1) inRobot.add(b);
        }
        balls.removeAll(inRobot);

        // ---- FLOWER stacks settle ----
        for (Ball b : balls) {
            if (Double.isNaN(b.targetZ)) continue;
            b.z = Math.max(b.targetZ, b.z - STACK_DROP_SPEED * dt);
            if (b.z <= b.targetZ) b.targetZ = Double.NaN;
        }

        // ---- shoot ----
        shotTimer -= dt;
        boolean shooting = transfer.getCommandedPower() > 0.5;
        if (shooting && stored - feedingCount() > 0 && shotTimer <= 0) {
            launch(p, nextBig);
            nextBig = !nextBig;
            stored--;
            shotTimer = SHOT_INTERVAL;
        }

        // ---- flight ----
        for (Ball b : balls) {
            if (!b.flying) continue;
            b.vz -= G * dt;
            b.x += b.vx * dt; b.y += b.vy * dt; b.z += b.vz * dt;
            for (double[] cell : CELLS) {
                if (b.vz < 0 && Math.abs(b.z - cell[2]) < 6
                        && Math.hypot(b.x - cell[0], b.y - cell[1]) < CELL_RADIUS) {
                    b.flying = false; b.scored = true; scored++;
                    b.x = cell[0] + (Math.random() - 0.5) * 6;
                    b.y = cell[1] + (Math.random() - 0.5) * 6;
                    b.z = cell[2] - 4;
                    break;
                }
            }
            if (b.flying) {
                b.x = clamp(b.x, RADIUS, FieldCollision.FIELD_MAX - RADIUS);
                b.y = clamp(b.y, RADIUS, FieldCollision.FIELD_MAX - RADIUS);
                if (b.z <= RADIUS) { b.z = RADIUS; b.flying = false; b.vx = b.vy = b.vz = 0; }
            }
        }

        // ---- robot frame pushes floor balls ----
        for (Ball b : balls) {
            if (b.flying || b.scored || b.outside || b.feeding || b.z > MAX_PICK_Z) continue;
            double dx = b.x - p.x(), dy = b.y - p.y();
            double lx = dx * c + dy * s, ly = -dx * s + dy * c;
            double fx = FieldCollision.FRONT + RADIUS, bx = -FieldCollision.BACK - RADIUS, hy = FieldCollision.WIDTH_HALF + RADIUS;
            if (lx > bx && lx < fx && Math.abs(ly) < hy) {
                double toFront = fx - lx, toBack = lx - bx, toSide = hy - Math.abs(ly);
                if (toSide <= toFront && toSide <= toBack) ly = Math.signum(ly == 0 ? 1 : ly) * hy;
                else if (toFront < toBack) lx = fx;
                else lx = bx;
                b.x = clamp(p.x() + lx * c - ly * s, RADIUS, FieldCollision.FIELD_MAX - RADIUS);
                b.y = clamp(p.y() + lx * s + ly * c, RADIUS, FieldCollision.FIELD_MAX - RADIUS);
            }
        }

        publish(p);
    }

    private void launch(MotionVector p, boolean big) {
        double px = big ? BIG_TURRET_X : SMALL_TURRET_X;
        double py = big ? BIG_TURRET_Y : SMALL_TURRET_Y;
        double servo = (big ? turretBig : turretSmall).getActualPosition();
        double yaw = p.theta() + Math.toRadians((servo - 0.5) * SimOpMode.TURRET_RANGE_DEG);
        double c = Math.cos(p.theta()), s = Math.sin(p.theta());
        Ball b = new Ball(p.x() + px * c - py * s, p.y() + px * s + py * c, LAUNCH_HEIGHT);
        // Distance and height of the nearest blue CELL set the speed; the direction is the turret's.
        double[] cell = nearestBlueCell(b.x, b.y);
        double dist = Math.hypot(cell[0] - b.x, cell[1] - b.y);
        double t = 0.45 + dist / 220.0;                    // flight time (s)
        double vh = dist / t;
        b.vx = vh * Math.cos(yaw);
        b.vy = vh * Math.sin(yaw);
        b.vz = (cell[2] - b.z + 0.5 * G * t * t) / t;
        b.flying = true;
        balls.add(b);
    }

    private static double[] nearestBlueCell(double x, double y) {
        return Math.hypot(CELLS[0][0] - x, CELLS[0][1] - y) <= Math.hypot(CELLS[1][0] - x, CELLS[1][1] - y)
                ? CELLS[0] : CELLS[1];
    }

    private void dropFlowerStack(Ball taken) {
        for (List<Ball> stack : flowerStacks) {
            if (stack.remove(taken)) {
                for (Ball b : stack) b.targetZ = (Double.isNaN(b.targetZ) ? b.z : b.targetZ) - 2.9;
                return;
            }
        }
    }

    private void publish(MotionVector p) {
        List<Translation3d> out = new ArrayList<>();
        for (Ball b : balls) out.add(ftc(b.x, b.y, b.z));
        // stored POLLEN ride inside the robot
        for (int i = 0; i < stored - feedingCount(); i++) {
            double c = Math.cos(p.theta()), s = Math.sin(p.theta());
            double lx = -3 + i * 2.9, ly = 0;
            out.add(ftc(p.x() + lx * c - ly * s, p.y() + lx * s + ly * c, 6));
        }
        Logger.recordOutput(LOG_KEY, out.toArray(new Translation3d[0]));
        Logger.recordOutput("Sim/PollenStored", stored);
        Logger.recordOutput("Sim/PollenScored", scored);
    }

    /** Pedro inches -> FTCodeSim's logged field frame (metres), same as the robot pose. */
    private static Translation3d ftc(double x, double y, double z) {
        return new Translation3d(-(y - 70.75) / 39.37, (x - 70.75) / 39.37, z / 39.37);
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    private int feedingCount() {
        int n = 0;
        for (Ball b : balls) if (b.feeding) n++;
        return n;
    }

    public int getStored() { return stored; }
    public int getScored() { return scored; }
}
