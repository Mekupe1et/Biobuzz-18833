package org.firstinspires.ftc.teamcode.sim;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.concurrent.Callable;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/**
 * Makes FTCodeSim list only the OpModes in teamcode/OpMode.
 *
 * FTCodeSim finds OpModes by walking every class folder on "java.class.path" under
 * org/firstinspires/ftc/teamcode. Walking the whole of teamcode breaks on Windows:
 * some Kotlin files declare package "...Subsystems..." while living in "subsystems/",
 * and the case-insensitive file system makes the scanner build the wrong class names.
 *
 * So while FTCodeSim is being created, java.class.path points at a temp folder that
 * holds only the compiled OpMode package (from class folders or jars). Classes still load from the real classpath.
 */
public final class OpModeFolder {
    public static final String PACKAGE_PATH = "org/firstinspires/ftc/teamcode/OpMode";

    private OpModeFolder() {}

    public static <T> T scanOnlyOpModeFolder(Callable<T> createSim) throws Exception {
        String realClassPath = System.getProperty("java.class.path");
        Path scanRoot = Files.createTempDirectory("ftcodesim-opmodes");
        int found = 0;
        for (String entry : realClassPath.split(File.pathSeparator)) {
            if (entry.endsWith(".jar")) {
                found += copyFromJar(new File(entry), scanRoot);
                continue;
            }
            Path pkg = new File(entry, PACKAGE_PATH).toPath();
            if (!Files.isDirectory(pkg)) continue;
            Path target = scanRoot.resolve(PACKAGE_PATH);
            try (Stream<Path> files = Files.walk(pkg)) {
                for (Path f : (Iterable<Path>) files::iterator) {
                    if (!f.toString().endsWith(".class")) continue;
                    Path dest = target.resolve(pkg.relativize(f).toString());
                    Files.createDirectories(dest.getParent());
                    Files.copy(f, dest, StandardCopyOption.REPLACE_EXISTING);
                    found++;
                }
            }
        }
        System.out.println("[Sim] Loading OpModes from teamcode/OpMode (" + found + " class files)");

        // OpModes are created through the context class loader: use the case-tolerant loader
        // (see TeamCodeLoader) so the subsystems' classes can be found.
        TeamCodeLoader loader = TeamCodeLoader.get();
        Thread thread = Thread.currentThread();
        ClassLoader previous = thread.getContextClassLoader();
        System.setProperty("java.class.path", scanRoot.toString());
        thread.setContextClassLoader(loader);
        try {
            return createSim.call();
        } finally {
            thread.setContextClassLoader(previous);
            System.setProperty("java.class.path", realClassPath);
            deleteQuietly(scanRoot);
        }
    }

    /** The app's own classes reach unit tests as classes.jar, so read the OpMode package out of jars too. */
    private static int copyFromJar(File jar, Path scanRoot) throws IOException {
        if (!jar.isFile()) return 0;
        int count = 0;
        try (JarFile jf = new JarFile(jar)) {
            Enumeration<JarEntry> entries = jf.entries();
            while (entries.hasMoreElements()) {
                JarEntry e = entries.nextElement();
                String name = e.getName();
                if (e.isDirectory() || !name.startsWith(PACKAGE_PATH + "/") || !name.endsWith(".class")) continue;
                Path dest = scanRoot.resolve(name);
                Files.createDirectories(dest.getParent());
                try (InputStream in = jf.getInputStream(e)) {
                    Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
                }
                count++;
            }
        }
        return count;
    }

    private static void deleteQuietly(Path root) {
        try (Stream<Path> files = Files.walk(root)) {
            files.sorted((a, b) -> b.getNameCount() - a.getNameCount()).forEach(p -> p.toFile().delete());
        } catch (IOException ignored) {
        }
    }
}
