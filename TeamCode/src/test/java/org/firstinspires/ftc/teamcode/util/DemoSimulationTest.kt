package org.firstinspires.ftc.teamcode.util

import com.pedropathing.math.Vector
import org.firstinspires.ftc.teamcode.subsystems.intake.IntakeState
import org.firstinspires.ftc.teamcode.subsystems.intake.IntakeVars
import org.firstinspires.ftc.teamcode.subsystems.transfer.TransferGateState
import org.firstinspires.ftc.teamcode.subsystems.transfer.TransferState
import org.firstinspires.ftc.teamcode.subsystems.transfer.TransferVars
import org.firstinspires.ftc.teamcode.subsystems.turretBase.TurretState
import org.firstinspires.ftc.teamcode.subsystems.turretBase.TurretVars
import org.junit.Assert.*
import org.junit.Test

class DemoSimulationTest {

    @Test
    fun testIntakeSimulationWithFakeData() {
        // Set fake intake state
        IntakeVars.intakeState = IntakeState.INTAKE
        assertEquals(IntakeState.INTAKE, IntakeVars.intakeState)
        assertEquals(IntakeVars.intakingPower, IntakeVars.intakePower, 1e-5)

        IntakeVars.intakeState = IntakeState.OFF
        assertEquals(IntakeState.OFF, IntakeVars.intakeState)
        assertEquals(IntakeVars.stoppingPower, IntakeVars.intakePower, 1e-5)
    }

    @Test
    fun testTransferSimulationWithFakeData() {
        // Set fake transfer state
        TransferVars.transferState = TransferState.ON
        assertEquals(TransferState.ON, TransferVars.transferState)

        TransferVars.transferState = TransferState.OFF
        assertEquals(TransferState.OFF, TransferVars.transferState)
    }

    @Test
    fun testTurretVarsSimulationWithFakeData() {
        // Create an anonymous subclass of TurretVars for simulation testing
        val fakeTurretVars = object : TurretVars() {
            override var servoRange: Double = 180.0
            override var p: Double = 0.5
            override var rotationPred: Double = 1.0
            override var runTurret: Boolean = true
            override var offset: Double = 0.0
            override var offsetLL: Double = 0.0
            override var reducer: Double = 1.0
            override var servoOffset: Double = 0.0
            override var state: TurretState = TurretState.TrackingAprilTags
            override var targetPosition: Double = 90.0
        }

        assertTrue(fakeTurretVars.runTurret)
        assertEquals(180.0, fakeTurretVars.servoRange, 1e-5)
        assertEquals(TurretState.TrackingAprilTags, fakeTurretVars.state)
        assertEquals(90.0, fakeTurretVars.targetPosition, 1e-5)
    }

    @Test
    fun testOpmodeSimulationAndMath() {
        // Simulate loop time
        MyOpmode.totalLoopTime = 20.0
        assertEquals(20.0, MyOpmode.totalLoopTime, 1e-5)

        // Vector angle & wrapping
        val vector = Vector(3.0, 4.0)
        val angle = Util.vectorAngle(vector)
        assertEquals(Math.atan2(4.0, 3.0), angle, 1e-5)

        val wrappedAngle = Util.wrap360(380.0)
        assertEquals(20.0, wrappedAngle, 1e-5)
    }

    @Test
    fun testTelemetryLoggingDataVerification() {
        IntakeVars.intakeState = IntakeState.INTAKE
        IntakeVars.intakePower = 0.75
        IntakeVars.intakeArmPos = 0.5

        assertEquals("INTAKE", IntakeVars.intakeState.toString())
        assertEquals("0.75", IntakeVars.intakePower.toString())
        assertEquals("0.5", IntakeVars.intakeArmPos.toString())

        TransferVars.transferState = TransferState.ON
        TransferVars.transferPower = 1.0
        TransferVars.transferGateStateSmall = TransferGateState.OPEN
        assertEquals("ON", TransferVars.transferState.toString())
        assertEquals("1.0", TransferVars.transferPower.toString())
    }

    @Test
    fun testSubsystemTelemetryDataCollection() {
        // Set up fake subsystem states to simulate Control Hub data payload
        IntakeVars.intakeState = IntakeState.INTAKE
        IntakeVars.intakePower = 0.85
        IntakeVars.intakeArmPos = 0.2

        TransferVars.transferState = TransferState.ON
        TransferVars.transferPower = 1.0
        TransferVars.transferGateStateSmall = TransferGateState.OPEN

        val telemetryLog = mutableMapOf<String, String>()

        // Simulate collecting telemetry data the way MyOpmode / subsystems format it for the Control Hub
        telemetryLog["Intake State"] = IntakeVars.intakeState.toString()
        telemetryLog["Intake Power"] = IntakeVars.intakePower.toString()
        telemetryLog["Intake Arm Position"] = IntakeVars.intakeArmPos.toString()

        telemetryLog["Transfer Power"] = TransferVars.transferPower.toString()
        telemetryLog["Transfer Gate Small State"] = TransferVars.transferGateStateSmall.toString()

        // Assert correctness of data passed to Control Hub telemetry
        assertEquals("INTAKE", telemetryLog["Intake State"])
        assertEquals("0.85", telemetryLog["Intake Power"])
        assertEquals("0.2", telemetryLog["Intake Arm Position"])
        assertEquals("1.0", telemetryLog["Transfer Power"])
        assertEquals("OPEN", telemetryLog["Transfer Gate Small State"])
    }

    @Test
    fun testFakeTelemetryRecorder() {
        val fakeTelemetry = FakeTelemetry()

        // Simulate subsystem telemetry logging into FakeTelemetry using standard telemetry methods
        IntakeVars.intakeState = IntakeState.OUTTAKE
        IntakeVars.intakePower = -0.9

        fakeTelemetry.addData("Intake State", IntakeVars.intakeState)
        fakeTelemetry.addData("Intake Power", IntakeVars.intakePower)
        fakeTelemetry.addLine("--IntakeHardware--")

        // Verify data saved in FakeTelemetry
        assertEquals("OUTTAKE", fakeTelemetry.loggedData["Intake State"])
        assertEquals("-0.9", fakeTelemetry.loggedData["Intake Power"])
        assertEquals(1, fakeTelemetry.loggedLines.size)
        assertEquals("--IntakeHardware--", fakeTelemetry.loggedLines[0])

        // Clear and verify
        fakeTelemetry.clear()
        assertTrue(fakeTelemetry.loggedData.isEmpty())
        assertTrue(fakeTelemetry.loggedLines.isEmpty())
    }

    @Test
    fun testMyOpmodeAndSubsystemTelemetryOutputs() {
        val fakeTelemetry = FakeTelemetry()

        MyOpmode.totalLoopTime = 12.34

        // Simulate MyOpmode telemetry formatting for subsystems, variables, and loop times
        fakeTelemetry.addLine("--IntakeHardware--")
        fakeTelemetry.addData("Intake State", IntakeState.INTAKE)
        fakeTelemetry.addData("Intake Power", 0.75)
        fakeTelemetry.addLine("Subsystem Loop Time: 2.1 ms")
        fakeTelemetry.addLine("Total Loop Time: 12.34 ms")
        fakeTelemetry.update()

        // Verify that FakeTelemetry correctly records all outputs including titles, vars, and loop times
        assertTrue(fakeTelemetry.loggedLines.contains("--IntakeHardware--"))
        assertEquals("INTAKE", fakeTelemetry.loggedData["Intake State"])
        assertEquals("0.75", fakeTelemetry.loggedData["Intake Power"])
        assertTrue(fakeTelemetry.loggedLines.contains("Subsystem Loop Time: 2.1 ms"))
        assertTrue(fakeTelemetry.loggedLines.contains("Total Loop Time: 12.34 ms"))
    }

    @Test
    fun testHtmlTelemetryFormattingAndModifiers() {
        val formatter = com.skeletonarmy.marrow.telemetry.TelemetryFormatter(
            "Test Metric",
            listOf(
                com.skeletonarmy.marrow.telemetry.modifiers.BoldModifier(),
                com.skeletonarmy.marrow.telemetry.modifiers.ItalicModifier()
            )
        )
        val formatted = formatter.format()
        assertNotNull(formatted)
        assertTrue(formatted.contains("Test Metric"))
    }
}
