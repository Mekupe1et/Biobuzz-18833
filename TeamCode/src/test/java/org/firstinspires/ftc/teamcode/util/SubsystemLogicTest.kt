package org.firstinspires.ftc.teamcode.util

import dev.nextftc.hardware.actuators.NextServo
import org.firstinspires.ftc.teamcode.subsystems.drive.DriveVars
import org.firstinspires.ftc.teamcode.subsystems.intake.IntakeArmState
import org.firstinspires.ftc.teamcode.subsystems.intake.IntakeCommands
import org.firstinspires.ftc.teamcode.subsystems.intake.IntakeHardware
import org.firstinspires.ftc.teamcode.subsystems.intake.IntakeState
import org.firstinspires.ftc.teamcode.subsystems.intake.IntakeVars
import org.firstinspires.ftc.teamcode.subsystems.transfer.TransferCommands
import org.firstinspires.ftc.teamcode.subsystems.transfer.TransferGateState
import org.firstinspires.ftc.teamcode.subsystems.transfer.TransferHardware
import org.firstinspires.ftc.teamcode.subsystems.transfer.TransferState
import org.firstinspires.ftc.teamcode.subsystems.transfer.TransferVars
import org.firstinspires.ftc.teamcode.subsystems.turretBase.TurretHardware
import org.firstinspires.ftc.teamcode.subsystems.turretBase.TurretState
import org.firstinspires.ftc.teamcode.subsystems.turretBase.TurretVars
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.PI

class SubsystemLogicTest {

    private class TestTurretHardware(vars: TurretVars) : TurretHardware(vars) {
        override val s: NextServo get() = null!!
        fun testNormalize(rad: Double): Double = fastNormalizeRad(rad)
    }

    @Test
    fun testIntakeSubsystemFunctions() {
        // Test function calls on IntakeHardware
        IntakeHardware.setIntakeState(IntakeState.INTAKE)
        assertEquals(IntakeState.INTAKE, IntakeVars.intakeState)
        assertEquals(IntakeVars.intakingPower, IntakeVars.intakePower, 1e-5)

        IntakeHardware.setIntakeArmState(IntakeArmState.DOWN)
        assertEquals(IntakeArmState.DOWN, IntakeVars.intakeArmState)
        assertEquals(IntakeVars.intakeArmPosDown, IntakeVars.intakeArmPos, 1e-5)

        // Test commands
        assertNotNull(IntakeCommands.runIntake)
        assertNotNull(IntakeCommands.openIntakeArm)
    }

    @Test
    fun testTransferSubsystemFunctions() {
        // Test function calls on TransferHardware
        TransferHardware.setTransferState(TransferState.ON)
        assertEquals(TransferState.ON, TransferVars.transferState)
        assertEquals(TransferVars.transferPower, TransferVars.transferPower, 1e-5)

        TransferHardware.setTransferGateModeBig(TransferGateState.OPEN)
        assertEquals(TransferGateState.OPEN, TransferVars.transferGateStateBig)

        TransferHardware.setTransferGateModeSmall(TransferGateState.CLOSE)
        assertEquals(TransferGateState.CLOSE, TransferVars.transferGateStateSmall)

        // Test commands
        assertNotNull(TransferCommands.runTransfer)
        assertNotNull(TransferCommands.openGateBig)
    }

    @Test
    fun testTurretHardwareFunctions() {
        val dummyVars = object : TurretVars() {
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

        val turret = TestTurretHardware(dummyVars)

        // Test angle normalization function
        assertEquals(0.0, turret.testNormalize(0.0), 1e-5)
        assertEquals(PI / 2, turret.testNormalize(PI / 2), 1e-5)
        assertEquals(-PI / 2, turret.testNormalize(-PI / 2), 1e-5)
        assertEquals(0.0, turret.testNormalize(2 * PI), 1e-5)
    }

    @Test
    fun testDriveVarsStartingPose() {
        assertNotNull(DriveVars.startingPose)
        assertNotNull(DriveVars.finishingPose)
    }
}
