package org.firstinspires.ftc.teamcode.sim;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.codeblooded.ftcodesim.ascope.SeasonField;
import org.codeblooded.ftcodesim.ascope.SourceType;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

/**
 * Makes FTCodeSim show our own robot model instead of the generic "Drive Base":
 *  1. copies src/test/resources/robot-model/Robot_MyFTCRobot into AdvantageScope's userAssets
 *  2. points FTCodeSim's robot source at the "My FTC Robot" model
 *  3. writes the "3D FTCodeSim" tab so it shows the robot + its component poses
 *
 * Must run BEFORE new FTCodeSim(...). AdvantageScope only reads its layout when it
 * starts, so AdvantageScope should be closed when the simulation is launched.
 */
public final class AscopeSetup {
    public static final String MODEL_FOLDER = "Robot_MyFTCRobot";
    public static final String MODEL_NAME = "My FTC Robot";      // "name" in config.json
    public static final String TAB_TITLE = "3D FTCodeSim";        // tab FTCodeSim looks for
    public static final String ROBOT_POSE_KEY = "RealOutputs/Mecanum/position ftc coords (m)";
    public static final String COMPONENTS_KEY = "RealOutputs/" + RobotComponents.LOG_KEY;
    public static final String POLLEN_KEY = "RealOutputs/" + PollenSim.LOG_KEY;

    private AscopeSetup() {}

    public static void apply(SeasonField field) throws Exception {
        // FTCodeSim adds its robot source with these options; the object is mutable,
        // so renaming the model here makes FTCodeSim use our model everywhere.
        SourceType.ROBOT_MECANUM_BASE.options.put("model", MODEL_NAME);

        File ascopeDir = advantageScopeFolder();
        copyModel(new File(ascopeDir, "userAssets").toPath());
        writeLayout(ascopeDir, field);

        if (isAdvantageScopeRunning()) {
            System.out.println("[Sim] WARNING: AdvantageScope is already open, so it won't load the new layout. "
                    + "Close it and run the simulation again to see " + MODEL_NAME + ".");
        }
    }

    private static void copyModel(Path userAssets) throws Exception {
        URL url = AscopeSetup.class.getClassLoader().getResource("robot-model/" + MODEL_FOLDER);
        if (url == null) throw new IllegalStateException("robot-model/" + MODEL_FOLDER + " missing from test resources");
        Path source = Paths.get(url.toURI());
        Path target = userAssets.resolve(MODEL_FOLDER);
        Files.createDirectories(target);
        try (Stream<Path> files = Files.list(source)) {
            for (Path f : (Iterable<Path>) files::iterator) {
                Path dest = target.resolve(f.getFileName().toString());
                // Replace when changed, so a re-exported model shows up without manual copying.
                if (!Files.exists(dest) || Files.size(dest) != Files.size(f)
                        || Files.getLastModifiedTime(dest).compareTo(Files.getLastModifiedTime(f)) < 0) {
                    Files.copy(f, dest, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("[Sim] Copied " + f.getFileName() + " to " + target);
                }
            }
        }
    }

    private static void writeLayout(File ascopeDir, SeasonField field) throws IOException {
        File[] states = ascopeDir.listFiles((d, n) -> n.startsWith("state-") && n.endsWith(".json"));
        if (states == null || states.length == 0) {
            throw new IllegalStateException("No AdvantageScope state file. Open and close AdvantageScope once first.");
        }
        File stateFile = states[0]; // same file FTCodeSim edits
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = (ObjectNode) mapper.readTree(stateFile);
        ObjectNode tabs = (ObjectNode) root.path("hubs").path(0).path("state").path("tabs");
        ArrayNode tabArray = (ArrayNode) tabs.get("tabs");

        ObjectNode tab = null;
        for (JsonNode t : tabArray) {
            if (TAB_TITLE.equals(t.path("title").asText())
                    && field.ascopeName.equals(t.path("controller").path("game").asText())) {
                tab = (ObjectNode) t;
            }
        }
        if (tab == null) {
            tab = mapper.createObjectNode();
            tab.put("type", 3);
            tab.put("title", TAB_TITLE);
            tab.put("controllerUUID", "abcdefghijklmnopqrstuv1234567890");
            tab.set("controller", mapper.createObjectNode().put("game", field.ascopeName));
            tab.put("controlsHeight", 100);
            tabArray.insert(1, tab);
        }
        tabs.put("selected", 1);

        ObjectNode controller = (ObjectNode) tab.get("controller");
        ArrayNode sources = mapper.createArrayNode();
        // Replace every robot / component / game piece source with ours.
        for (JsonNode s : controller.path("sources")) {
            String type = s.path("type").asText();
            if (!type.equals("robot") && !type.equals("component") && !type.equals("gamePiece")) sources.add(s);
        }
        ObjectNode robot = sources.addObject();
        robot.put("type", "robot").put("logKey", ROBOT_POSE_KEY).put("logType", "Pose2d").put("visible", true);
        robot.set("options", mapper.createObjectNode().put("model", MODEL_NAME));
        // A component source attaches to the robot source right before it.
        ObjectNode comps = sources.addObject();
        comps.put("type", "component").put("logKey", COMPONENTS_KEY).put("logType", "Pose3d[]").put("visible", true);
        comps.set("options", mapper.createObjectNode());
        // POLLEN from PollenSim (also hides the field model's built-in POLLEN)
        ObjectNode pollen = sources.addObject();
        pollen.put("type", "gamePiece").put("logKey", POLLEN_KEY).put("logType", "Translation3d[]").put("visible", true);
        pollen.set("options", mapper.createObjectNode().put("variant", "Pollen"));
        controller.set("sources", sources);

        mapper.writerWithDefaultPrettyPrinter().writeValue(stateFile, root);
        System.out.println("[Sim] AdvantageScope layout set to " + MODEL_NAME + " in " + stateFile.getName());
    }

    private static File advantageScopeFolder() {
        String os = System.getProperty("os.name").toLowerCase();
        String home = System.getProperty("user.home");
        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            return appData != null ? new File(appData, "AdvantageScope")
                    : new File(home, "AppData/Roaming/AdvantageScope");
        } else if (os.contains("mac")) {
            return new File(home, "Library/Application Support/AdvantageScope");
        }
        String xdg = System.getenv("XDG_CONFIG_HOME");
        return xdg != null ? new File(xdg, "AdvantageScope") : new File(home, ".config/AdvantageScope");
    }

    private static boolean isAdvantageScopeRunning() {
        boolean win = System.getProperty("os.name").toLowerCase().contains("win");
        try {
            Process p = (win ? new ProcessBuilder("tasklist") : new ProcessBuilder("pgrep", "-f", "AdvantageScope")).start();
            try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = r.readLine()) != null) {
                    if (line.toLowerCase().contains("advantagescope")) return true;
                }
            }
        } catch (IOException ignored) {
        }
        return false;
    }
}
