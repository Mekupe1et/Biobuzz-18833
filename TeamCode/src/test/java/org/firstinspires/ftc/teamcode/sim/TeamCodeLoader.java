package org.firstinspires.ftc.teamcode.sim;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/**
 * Loads the robot code (org.firstinspires.ftc.teamcode.*) for the sim, ignoring upper/lower case
 * in folder names.
 *
 * Some Kotlin files declare package "...Subsystems.DriveSubsystem" while living in
 * "subsystems/drive". On Windows the compiler writes those classes into the existing lowercase
 * "subsystems" folder, so inside the app's classes.jar the entry is
 * "subsystems/DriveSubsystem/DriveHardware.class" and a normal (case-sensitive) jar lookup for
 * "Subsystems/DriveSubsystem/DriveHardware.class" fails. On the robot this doesn't matter.
 *
 * FTCodeSim creates OpModes through the thread's context class loader, so OpModeFolder sets
 * this loader there; everything the OpModes use from teamcode then comes through here.
 */
public final class TeamCodeLoader extends ClassLoader {
    private static final String PREFIX = "org/firstinspires/ftc/teamcode/";
    private static final String SIM_PREFIX = PREFIX + "sim/";
    private static TeamCodeLoader instance;

    /** lower-case entry path -> where to read it */
    private final Map<String, Source> index = new HashMap<>();

    private interface Source { byte[] read() throws IOException; }

    public static synchronized TeamCodeLoader get() throws IOException {
        if (instance == null) instance = new TeamCodeLoader(TeamCodeLoader.class.getClassLoader());
        return instance;
    }

    private TeamCodeLoader(ClassLoader parent) throws IOException {
        super(parent);
        for (String entry : System.getProperty("java.class.path").split(File.pathSeparator)) {
            File f = new File(entry);
            if (entry.endsWith(".jar") && f.isFile()) indexJar(f);
            else if (f.isDirectory()) indexDir(f.toPath());
        }
    }

    private void indexJar(File jar) throws IOException {
        try (JarFile jf = new JarFile(jar)) {
            Enumeration<JarEntry> e = jf.entries();
            while (e.hasMoreElements()) {
                String name = e.nextElement().getName();
                if (name.startsWith(PREFIX) && name.endsWith(".class") && !name.startsWith(SIM_PREFIX)) {
                    index.putIfAbsent(name.toLowerCase(Locale.ROOT), () -> {
                        try (JarFile j = new JarFile(jar); InputStream in = j.getInputStream(j.getEntry(name))) {
                            return readAll(in);
                        }
                    });
                }
            }
        }
    }

    private void indexDir(Path root) throws IOException {
        Path pkg = root.resolve(PREFIX);
        if (!Files.isDirectory(pkg)) return;
        try (Stream<Path> files = Files.walk(pkg)) {
            for (Path p : (Iterable<Path>) files::iterator) {
                String rel = root.relativize(p).toString().replace(File.separatorChar, '/');
                if (rel.endsWith(".class") && !rel.startsWith(SIM_PREFIX)) {
                    index.putIfAbsent(rel.toLowerCase(Locale.ROOT), () -> Files.readAllBytes(p));
                }
            }
        }
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (this) {
            String path = name.replace('.', '/') + ".class";
            if (!path.startsWith(PREFIX) || path.startsWith(SIM_PREFIX)) {
                return super.loadClass(name, resolve); // SDK, libraries, sim helpers: normal loading
            }
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                Source src = index.get(path.toLowerCase(Locale.ROOT));
                if (src == null) return super.loadClass(name, resolve);
                try {
                    byte[] bytes = src.read();
                    c = defineClass(name, bytes, 0, bytes.length);
                } catch (IOException e) {
                    throw new ClassNotFoundException(name, e);
                }
            }
            if (resolve) resolveClass(c);
            return c;
        }
    }

    private static byte[] readAll(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int n;
        while ((n = in.read(buf)) > 0) out.write(buf, 0, n);
        return out.toByteArray();
    }
}
