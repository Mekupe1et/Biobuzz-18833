package org.firstinspires.ftc.teamcode.sim;

import com.qualcomm.ftccommon.FtcEventLoop;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerImpl;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorControllerEx;
import com.qualcomm.robotcore.hardware.DcMotorImpl;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoControllerEx;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import org.codeblooded.ftcodesim.hardware.devices.SimHardwareDevice;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import dev.nextftc.hardware.RobotController;

/**
 * Lets the real subsystems (NextFTC NextMotor / NextServo) run in the sim without changing them.
 *
 * - NextMotor/NextServo need the SDK's concrete DcMotorImplEx / ServoImplEx, so SimMotorEx and
 *   SimServoEx extend those classes with a fake controller that just remembers the values.
 * - NextFTC finds the hardware map through RobotController, which normally gets it from the
 *   robot app's event loop. installRobotController() gives it a stand-in that returns the sim map.
 */
public final class SimHardware {
    private SimHardware() {}

    /** Point NextFTC's RobotController at the sim hardware map. */
    public static void installRobotController(HardwareMap hardwareMap) throws Exception {
        OpModeManagerImpl manager = allocate(OpModeManagerImpl.class);
        setField(OpModeManagerImpl.class, manager, "hardwareMap", hardwareMap);
        FtcEventLoop loop = allocate(FtcEventLoop.class);
        setField(FtcEventLoop.class, loop, "opModeManager", manager);
        RobotController.INSTANCE.setEventLoop$hardware_release(loop);
    }

    /** Creates an object WITHOUT running its constructor (the SDK ones need the robot app). */
    @SuppressWarnings("unchecked")
    private static <T> T allocate(Class<T> type) throws Exception {
        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
        Field uf = unsafeClass.getDeclaredField("theUnsafe");
        uf.setAccessible(true);
        Object unsafe = uf.get(null);
        return (T) unsafeClass.getMethod("allocateInstance", Class.class).invoke(unsafe, type);
    }

    private static void setField(Class<?> owner, Object target, String name, Object value) throws Exception {
        Field f = owner.getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    /** A controller interface implementation that stores motor powers / servo positions per port. */
    @SuppressWarnings("unchecked")
    private static <T> T fakeController(Class<T> type, Map<String, Double> values) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, (proxy, m, args) -> {
            String n = m.getName();
            switch (n) {
                case "setMotorPower": case "setServoPosition":
                    values.put(n.substring(3), ((Number) args[1]).doubleValue());
                    return null;
                case "getMotorPower": case "getServoPosition":
                    return values.getOrDefault(n.substring(3), 0.0);
                case "toString": return type.getSimpleName() + "(sim)";
                case "hashCode": return System.identityHashCode(proxy);
                case "equals": return proxy == args[0];
            }
            Class<?> r = m.getReturnType();
            if (r == boolean.class) return false;
            if (r == int.class) return 0;
            if (r == double.class) return 0.0;
            if (r == float.class) return 0f;
            if (r == long.class) return 0L;
            if (r == String.class) return "sim";
            if (r == DcMotor.RunMode.class) return DcMotor.RunMode.RUN_WITHOUT_ENCODER;
            if (r == DcMotor.ZeroPowerBehavior.class) return DcMotor.ZeroPowerBehavior.FLOAT;
            if (r == PwmControl.PwmRange.class) return PwmControl.PwmRange.defaultRange;
            if (r == HardwareDevice.Manufacturer.class) return HardwareDevice.Manufacturer.Other;
            return null;
        });
    }

    /**
     * Motor usable by NextMotor. getCommandedPower() is whatever the code last commanded.
     * Built with create(): DcMotorImpl's constructor logs through Android and needs the SDK's
     * motor-type registry, so the object is allocated and its fields are set directly.
     */
    public static class SimMotorEx extends DcMotorImplEx {
        private Map<String, Double> values;
        private String name;

        private SimMotorEx() {
            super(null, 0); // never runs: instances come from create()
        }

        public static SimMotorEx create(String name) throws Exception {
            SimMotorEx m = allocate(SimMotorEx.class);
            m.values = new HashMap<>();
            m.name = name;
            setField(DcMotorImpl.class, m, "controller", fakeController(DcMotorControllerEx.class, m.values));
            setField(DcMotorImpl.class, m, "portNumber", 0);
            setField(DcMotorImpl.class, m, "direction", DcMotorSimple.Direction.FORWARD);
            // setPower() asks the motor type which way it spins; a blank type set to CW (no inversion).
            Class<?> typeClass = Class.forName(
                    "com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType");
            Object type = allocate(typeClass);
            setField(typeClass, type, "orientation", org.firstinspires.ftc.robotcore.external.navigation.Rotation.CW);
            setField(DcMotorImpl.class, m, "motorType", type);
            return m;
        }

        /** Power actually sent to the "hub" (after direction), -1..1. */
        public double getCommandedPower() {
            return values.getOrDefault("MotorPower", 0.0);
        }

        @Override public String getDeviceName() { return name; }
        @Override public String getConnectionInfo() { return "sim"; }
    }

    /** Servo usable by NextServo. Moves toward the commanded position at a limited speed. */
    public static class SimServoEx extends ServoImplEx implements SimHardwareDevice {
        private final Map<String, Double> values;
        private final String name;
        private final double speed;
        private double actual;

        private SimServoEx(String name, double start, double fullTravelSeconds, Map<String, Double> values) {
            super(fakeController(ServoControllerEx.class, values), 0, Servo.Direction.FORWARD, null); // type only goes to the fake controller
            this.values = values;
            this.name = name;
            this.speed = 1.0 / fullTravelSeconds;
            values.put("ServoPosition", start);
            this.actual = start;
        }

        public SimServoEx(String name, double startPosition, double fullTravelSeconds) {
            this(name, startPosition, fullTravelSeconds, new HashMap<>());
        }

        /** Where the servo horn really is right now (0..1). */
        public double getActualPosition() {
            return actual;
        }

        @Override
        public void update(double deltaTime) {
            double target = values.getOrDefault("ServoPosition", actual);
            double step = speed * deltaTime;
            double error = target - actual;
            actual = Math.abs(error) <= step ? target : actual + Math.signum(error) * step;
        }

        @Override public String getDeviceName() { return name; }
        @Override public String getConnectionInfo() { return "sim"; }
    }
}
