package android.util;

/**
 * Stand-in for Android's Log in the JVM sim (src/test only, never on the robot).
 * NextFTC and the FTC SDK log through android.util.Log, which throws "not mocked" in unit tests.
 * Debug/verbose messages are dropped; warnings and errors are printed.
 */
public final class Log {
    public static final int VERBOSE = 2, DEBUG = 3, INFO = 4, WARN = 5, ERROR = 6, ASSERT = 7;

    private Log() {}

    public static int v(String tag, String msg) { return 0; }
    public static int v(String tag, String msg, Throwable tr) { return 0; }
    public static int d(String tag, String msg) { return 0; }
    public static int d(String tag, String msg, Throwable tr) { return 0; }
    public static int i(String tag, String msg) { return 0; }
    public static int i(String tag, String msg, Throwable tr) { return 0; }
    public static int w(String tag, String msg) { return print("W", tag, msg, null); }
    public static int w(String tag, String msg, Throwable tr) { return print("W", tag, msg, tr); }
    public static int w(String tag, Throwable tr) { return print("W", tag, "", tr); }
    public static int e(String tag, String msg) { return print("E", tag, msg, null); }
    public static int e(String tag, String msg, Throwable tr) { return print("E", tag, msg, tr); }
    public static int wtf(String tag, String msg) { return print("A", tag, msg, null); }
    public static int wtf(String tag, Throwable tr) { return print("A", tag, "", tr); }
    public static int wtf(String tag, String msg, Throwable tr) { return print("A", tag, msg, tr); }
    public static int println(int priority, String tag, String msg) { return priority >= WARN ? print("P", tag, msg, null) : 0; }
    public static boolean isLoggable(String tag, int level) { return level >= WARN; }

    public static String getStackTraceString(Throwable tr) {
        if (tr == null) return "";
        java.io.StringWriter sw = new java.io.StringWriter();
        tr.printStackTrace(new java.io.PrintWriter(sw));
        return sw.toString();
    }

    private static int print(String level, String tag, String msg, Throwable tr) {
        System.out.println("[" + level + "/" + tag + "] " + msg);
        if (tr != null) tr.printStackTrace(System.out);
        return 0;
    }
}
