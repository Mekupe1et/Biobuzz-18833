import org.codeblooded.ftcodesim.input.DefaultKeybinds;
import org.codeblooded.ftcodesim.simulator.FTCodeSim;
import org.codeblooded.ftcodesim.simulator.SimConfig;
import org.firstinspires.ftc.teamcode.sim.AscopeSetup;
import org.firstinspires.ftc.teamcode.sim.OpModeFolder;
import org.firstinspires.ftc.teamcode.sim.SimRobot;
import org.junit.Test;

/**
 * Runs our robot in FTCodeSim and shows it in AdvantageScope with our own CAD model.
 *
 * How to run: close AdvantageScope, then double-click run-sim.bat (or the green arrow here).
 * The fake Driver Station opens; AdvantageScope opens on the "3D FTCodeSim" tab.
 * Pick an OpMode from teamcode/OpMode (e.g. "Sim Blue Audience Corner"), Init, Start.
 * Close the Driver Station window to stop.
 */
public class SimulateRobot {
    @Test
    public void simulate() throws Exception {
        AscopeSetup.apply(SimRobot.FIELD);
        SimRobot robot = new SimRobot();

        SimConfig simConfig = new SimConfig();
        simConfig.gamepad1Keybinds = new DefaultKeybinds();
        simConfig.gamepad2Keybinds = new DefaultKeybinds();
        simConfig.simHardwareMap = robot.hardwareMap;
        simConfig.loopTimeMs = 10;
        simConfig.field = SimRobot.FIELD;
        simConfig.autoConfigureAscope = true;

        // Only OpModes in teamcode/OpMode show up in the sim's Driver Station.
        FTCodeSim sim = OpModeFolder.scanOnlyOpModeFolder(() -> new FTCodeSim(simConfig));
        sim.run();
    }
}
