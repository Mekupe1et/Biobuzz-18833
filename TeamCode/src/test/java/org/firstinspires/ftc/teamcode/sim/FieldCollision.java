package org.firstinspires.ftc.teamcode.sim;

import org.codeblooded.ftcodesim.ascope.boundaries.MotionVector;
import org.codeblooded.ftcodesim.hardware.devices.SimHardwareMechanism;
import org.codeblooded.ftcodesim.hardware.drivetrain.SimulatedDrivetrain;
import org.psilynx.psikit.core.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Keeps the simulated robot from driving through the 2026-27 BIOBUZZ field: perimeter walls,
 * the 4 FLOWERS and the two A-frames of the HIVE structure (foot bar + legs, the parts lower
 * than the robot). The open floor between the A-frames, under the HIVES, stays drivable.
 *
 * The robot is its CAD frame (rectangle). The intakes (arm + rollers) are not solid, so they
 * can reach POLLEN against walls and FLOWERS.
 * Each update, any overlap is pushed out (separating-axis test) and the velocity into the
 * obstacle is removed. Pedro coordinates, inches. Obstacles come from the AdvantageScope field
 * model: Pedro = 70.75 + model_metres * 39.37.
 */
public class FieldCollision implements SimHardwareMechanism {
    // Robot frame around the drivetrain centre (inches, +x = front/intake arm, +y = left), CAD base part.
    public static double FRONT = 7.13, BACK = 9.65, WIDTH_HALF = 8.57;
    public static final double FIELD_MIN = 0.16, FIELD_MAX = 141.34; // inside faces of the walls

    private final SimulatedDrivetrain drivetrain;
    private final List<double[][]> obstacles = new ArrayList<>();

    public FieldCollision(SimulatedDrivetrain drivetrain) {
        this.drivetrain = drivetrain;
        double t = 10; // wall thickness used for collision
        obstacles.add(box(FIELD_MIN - t, FIELD_MIN - t, FIELD_MIN, FIELD_MAX + t)); // red wall   (x = 0)
        obstacles.add(box(FIELD_MAX, FIELD_MIN - t, FIELD_MAX + t, FIELD_MAX + t)); // blue wall  (x = 141)
        obstacles.add(box(FIELD_MIN - t, FIELD_MIN - t, FIELD_MAX + t, FIELD_MIN)); // audience   (y = 0)
        obstacles.add(box(FIELD_MIN - t, FIELD_MAX, FIELD_MAX + t, FIELD_MAX + t)); // back wall  (y = 141)
        // FLOWERS (attached to the walls)
        obstacles.add(box(90.8, -1.0, 97.5, 6.9));
        obstacles.add(box(134.6, 90.8, 142.5, 97.5));
        obstacles.add(box(-1.0, 44.0, 6.9, 50.7));
        obstacles.add(box(44.0, 134.6, 50.7, 142.5));
        // HIVE frame A-frames: foot bar + both legs up to robot height (0.45 m)
        obstacles.add(new double[][]{{95.47, 51.26}, {95.47, 90.24}, {89.88, 81.66}, {89.88, 59.84}}); // blue side
        obstacles.add(new double[][]{{46.03, 51.26}, {51.62, 59.84}, {51.62, 81.66}, {46.03, 90.24}}); // red side
    }

    private static double[][] box(double x0, double y0, double x1, double y1) {
        return new double[][]{{x0, y0}, {x1, y0}, {x1, y1}, {x0, y1}};
    }

    @Override
    public void update(double deltaTime) {
        boolean hit = false;
        for (int pass = 0; pass < 3; pass++) {           // a few passes for corners
            boolean any = false;
            for (double[][] ob : obstacles) {
                double[] mtv = separation(robotCorners(), ob);
                if (mtv == null) continue;
                any = hit = true;
                MotionVector p = drivetrain.position;
                drivetrain.setPosition(new MotionVector(p.x() + mtv[0], p.y() + mtv[1], p.theta()));
                double len = Math.hypot(mtv[0], mtv[1]);
                double nx = mtv[0] / len, ny = mtv[1] / len;   // points out of the obstacle
                MotionVector v = drivetrain.velocity;
                double into = v.x() * nx + v.y() * ny;
                if (into < 0) {
                    drivetrain.velocity = new MotionVector(v.x() - into * nx, v.y() - into * ny, v.theta());
                }
            }
            if (!any) break;
        }
        Logger.recordOutput("Sim/collision", hit);
    }

    private double[][] robotCorners() {
        double front = FRONT;
        MotionVector p = drivetrain.position;
        double c = Math.cos(p.theta()), s = Math.sin(p.theta());
        double[][] local = {{front, WIDTH_HALF}, {-BACK, WIDTH_HALF}, {-BACK, -WIDTH_HALF}, {front, -WIDTH_HALF}};
        double[][] out = new double[4][2];
        for (int i = 0; i < 4; i++) {
            out[i][0] = p.x() + local[i][0] * c - local[i][1] * s;
            out[i][1] = p.y() + local[i][0] * s + local[i][1] * c;
        }
        return out;
    }

    /** Smallest move that pushes polygon a out of polygon b, or null if they don't overlap. */
    private static double[] separation(double[][] a, double[][] b) {
        double best = Double.MAX_VALUE, bx = 0, by = 0;
        for (double[][] poly : new double[][][]{a, b}) {
            for (int i = 0; i < poly.length; i++) {
                double[] p1 = poly[i], p2 = poly[(i + 1) % poly.length];
                double ax = -(p2[1] - p1[1]), ay = p2[0] - p1[0];
                double l = Math.hypot(ax, ay);
                ax /= l; ay /= l;
                double[] ra = project(a, ax, ay), rb = project(b, ax, ay);
                double overlap = Math.min(ra[1], rb[1]) - Math.max(ra[0], rb[0]);
                if (overlap <= 0) return null;
                if (overlap < best) {
                    best = overlap;
                    // push a away from b along this axis
                    double ca = (ra[0] + ra[1]) / 2, cb = (rb[0] + rb[1]) / 2;
                    double sign = ca < cb ? -1 : 1;
                    bx = ax * overlap * sign;
                    by = ay * overlap * sign;
                }
            }
        }
        return new double[]{bx, by};
    }

    private static double[] project(double[][] poly, double ax, double ay) {
        double min = Double.MAX_VALUE, max = -Double.MAX_VALUE;
        for (double[] p : poly) {
            double d = p[0] * ax + p[1] * ay;
            min = Math.min(min, d);
            max = Math.max(max, d);
        }
        return new double[]{min, max};
    }
}
