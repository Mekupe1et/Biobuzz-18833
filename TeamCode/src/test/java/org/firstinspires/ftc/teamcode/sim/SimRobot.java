package org.firstinspires.ftc.teamcode.sim;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.codeblooded.ftcodesim.ascope.SeasonField;
import org.codeblooded.ftcodesim.ascope.SourceType;
import org.codeblooded.ftcodesim.ascope.boundaries.RobotGeometry;
import org.codeblooded.ftcodesim.hardware.SimHardwareMap;
import org.codeblooded.ftcodesim.hardware.devices.SimMotor;
import org.codeblooded.ftcodesim.hardware.drivetrain.SimMecanumConfig;
import org.codeblooded.ftcodesim.hardware.drivetrain.SimulatedMecanum;
import org.firstinspires.ftc.teamcode.OpMode.SimOpMode;
import org.firstinspires.ftc.teamcode.subsystems.intake.IntakeVars;

/** The simulated robot: drivetrain, Pinpoint, every mechanism device and the model animation. */
public final class SimRobot {
    public static final SeasonField FIELD = SeasonField.BIOBUZZ;

    public final SimHardwareMap hardwareMap = new SimHardwareMap();
    public final SimulatedMecanum drivetrain;
    public final PollenSim pollen;

    public SimRobot() throws Exception {
        SimMecanumConfig config = new SimMecanumConfig();
        // Names from pedro/Constants.java
        config.frontLeftMotorName = "left_front";
        config.frontRightMotorName = "right_front";
        config.backLeftMotorName = "left_back";
        config.backRightMotorName = "right_back";

        // Measured from the CAD (robot asm): 104 mm mecanums.
        config.wheelbase = 9.3;     // in, front to back
        config.trackWidth = 15.2;   // in, left to right
        config.wheelRadius = 2.047; // in (104 mm / 2)

        // Physics: FTCodeSim example values until we measure our own.
        config.staticVelocityRegion = 4;
        config.staticFriction = 55;
        config.maxAcceleration = 200;
        config.turnNaturalDeceleration = 2.5;
        config.maxTurnAccel = 10;
        config.maxAngularVelocity = 5;
        config.field = FIELD;
        config.robotGeometry = new RobotGeometry(17, 17, 0, 0); // FTCodeSim's wall check; FieldCollision uses the real footprint
        config.robotModel = SourceType.ROBOT_MECANUM_BASE; // renamed to "My FTC Robot" by AscopeSetup
        // Drivetrain PHYSICS. Constants.foresightConfig isn't tuned yet (brake coefficients are 0,
        // which divides by zero in the sim), so FTCodeSim's measured example robot is used.
        config.foresightConfig = SimPhysics.config();

        drivetrain = new SimulatedMecanum(config);
        SimHardwareMap hw = hardwareMap;
        hw.register(drivetrain);
        hw.register("pinpoint", new PlaceablePinpoint(drivetrain)); // setPose also places the robot

        // Mechanism devices used by the subsystems (NextMotor / NextServo need these types).
        SimHardware.SimServoEx arm = hw.register(SimOpMode.INTAKE_ARM,
                new SimHardware.SimServoEx(SimOpMode.INTAKE_ARM, IntakeVars.intakeArmPosUp, 0.4));
        SimHardware.SimServoEx big = hw.register(SimOpMode.TURRET_BIG, new SimHardware.SimServoEx(SimOpMode.TURRET_BIG, 0.5, 0.8));
        SimHardware.SimServoEx small = hw.register(SimOpMode.TURRET_SMALL, new SimHardware.SimServoEx(SimOpMode.TURRET_SMALL, 0.5, 0.8));
        SimHardware.SimServoEx hood = hw.register(SimOpMode.HOOD_SMALL, new SimHardware.SimServoEx(SimOpMode.HOOD_SMALL, 0.0, 0.5));
        hw.register(SimOpMode.GATE_SMALL, new SimHardware.SimServoEx(SimOpMode.GATE_SMALL, 0, 0.3));
        hw.register(SimOpMode.GATE_BIG, new SimHardware.SimServoEx(SimOpMode.GATE_BIG, 0, 0.3));
        SimHardware.SimMotorEx intake = hw.register(SimOpMode.INTAKE_MOTOR, SimHardware.SimMotorEx.create(SimOpMode.INTAKE_MOTOR));
        hw.register(SimOpMode.TRANSFER_MOTOR, SimHardware.SimMotorEx.create(SimOpMode.TRANSFER_MOTOR));

        SimMotor[] wheels = {
                (SimMotor) hw.get(DcMotorEx.class, config.frontLeftMotorName),
                (SimMotor) hw.get(DcMotorEx.class, config.frontRightMotorName),
                (SimMotor) hw.get(DcMotorEx.class, config.backLeftMotorName),
                (SimMotor) hw.get(DcMotorEx.class, config.backRightMotorName),
        };
        hw.register(new FieldCollision(drivetrain)); // walls, flowers, hive A-frames
        SimHardware.SimMotorEx transfer = (SimHardware.SimMotorEx) hw.get(SimOpMode.TRANSFER_MOTOR);
        pollen = new PollenSim(drivetrain, arm, big, small, intake, transfer);
        hw.register(pollen); // game pieces
        hw.register(new RobotComponents(arm, big, small, hood, wheels, intake));

        // NextFTC (the subsystems) reads the hardware map through RobotController.
        SimHardware.installRobotController(hw);
    }
}
