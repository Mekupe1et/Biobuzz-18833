package org.firstinspires.ftc.teamcode.OpMode

import com.pedropathing.follower.ManualDrive
import com.pedropathing.math.Pose
import com.pedropathing.math.Vector
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import dev.nextftc.hardware.actuators.NextServo
import org.firstinspires.ftc.teamcode.Subsystems.DriveSubsystem.DriveHardware
import org.firstinspires.ftc.teamcode.subsystems.Robot
import org.firstinspires.ftc.teamcode.subsystems.intake.IntakeArmState
import org.firstinspires.ftc.teamcode.subsystems.intake.IntakeHardware
import org.firstinspires.ftc.teamcode.subsystems.intake.IntakeState
import org.firstinspires.ftc.teamcode.subsystems.transfer.TransferGateState
import org.firstinspires.ftc.teamcode.subsystems.transfer.TransferHardware
import org.firstinspires.ftc.teamcode.subsystems.transfer.TransferState
import org.firstinspires.ftc.teamcode.subsystems.transfer.TransferVars
import kotlin.math.hypot

/**
 * Blue-alliance TeleOp built on the robot's subsystems (DriveHardware, IntakeHardware,
 * TransferHardware, TurretHardware). Used by SimBlueAudienceCorner / SimBlueBackCorner,
 * which only pick the start pose. Not an OpMode by itself.
 *
 *   on Start              intake arm goes down
 *   left stick            drive (field-centric, from the blue driver's side)
 *   right stick x         turn
 *   LT (hold)             intake
 *   RT (hold)             shoot: transfer on + both gates open
 *   A / B                 intake arm down / up
 *   dpad up / down        small hood
 *   turrets               always aim at the closest blue CELL
 *
 * Pedro field (inches): x = 0 red wall -> 141 blue wall, y = 0 audience -> 141 back wall.
 */
abstract class SimOpMode : OpMode() {
    companion object {
        // Hardware names. Turret/hood servos aren't in the subsystems yet.
        const val TURRET_BIG = "turret_big"
        const val TURRET_SMALL = "turret_small"
        const val HOOD_SMALL = "hood_small"
        const val INTAKE_ARM = "intk_arm"   // IntakeHardware
        const val INTAKE_MOTOR = "intk_mtr" // IntakeHardware
        const val TRANSFER_MOTOR = "tM"     // TransferHardware
        const val GATE_SMALL = "SS"         // TransferHardware
        const val GATE_BIG = "SB"           // TransferHardware

        /** Total turret servo travel (degrees); servo 0.5 = turret facing the robot's front. */
        const val TURRET_RANGE_DEG = 270.0

        /** Blue HIVE CELLS (centre of each CELL), from the 2026-27 field model. */
        @JvmField val BLUE_SCORING_CELL = Pose(83.5, 81.1)   // back side of the blue HIVE
        @JvmField val BLUE_AUDIENCE_CELL = Pose(83.5, 56.3)  // audience side of the blue HIVE

        private const val HOOD_RATE = 0.8 // servo units per second
        private const val TRIGGER = 0.2
    }

    private val follower get() = DriveHardware.follower.value
    private val hood = NextServo(HOOD_SMALL)
    private var hoodPos = 0.0
    private var lastNanos = 0L

    /** Where this OpMode starts on the field. */
    protected abstract fun startPose(): Pose

    override fun init() {
        DriveHardware.setPoseEstimate(startPose())

        IntakeHardware.setIntakeState(IntakeState.OFF)
        IntakeHardware.setIntakeArmState(IntakeArmState.UP)
        TransferHardware.setTransferState(TransferState.OFF)
        TransferHardware.setTransferGateModeBig(TransferGateState.CLOSE)
        TransferHardware.setTransferGateModeSmall(TransferGateState.CLOSE)
        applyIntakeAndTransfer(shooting = false)
        hood.position = hoodPos

        telemetry.addData("Start", "x %.1f  y %.1f", startPose().x(), startPose().y())
        telemetry.addLine("Keyboard: WASD drive, J/L turn, U = LT intake, O = RT shoot, ; = A, [ = B, arrows = dpad")
    }

    override fun start() {
        // Arm down right away (on Start, not Init: the robot must stay inside 18 in. and
        // motionless after Init, rule G304).
        IntakeHardware.setIntakeArmState(IntakeArmState.DOWN)
        BigTurret.start()
        SmallTurret.start()
        lastNanos = System.nanoTime()
    }

    override fun loop() {
        val now = System.nanoTime()
        val dt = (now - lastNanos) * 1e-9
        lastNanos = now
        Robot.looptime = (dt * 1000).toInt()

        // ---- Drive (DriveHardware's follower) ----
        follower.update()
        val pose = follower.pose()
        // Blue driver stands at the blue wall (x = 141): stick forward = toward x = 0, so the
        // field frame is turned 180 deg; stick right / turn right are negative in Pedro.
        val powers = ManualDrive.fieldCentric(
            (-gamepad1.left_stick_y).toDouble(),
            (-gamepad1.left_stick_x).toDouble(),
            (-gamepad1.right_stick_x).toDouble(),
            pose.heading() + Math.PI
        )
        follower.manual(powers)

        // ---- Intake (IntakeHardware) ----
        if (gamepad1.a) IntakeHardware.setIntakeArmState(IntakeArmState.DOWN)
        if (gamepad1.b) IntakeHardware.setIntakeArmState(IntakeArmState.UP)
        val intaking = gamepad1.left_trigger > TRIGGER
        IntakeHardware.setIntakeState(if (intaking) IntakeState.INTAKE else IntakeState.OFF)

        // ---- Shoot (TransferHardware) ----
        val shooting = gamepad1.right_trigger > TRIGGER
        TransferHardware.setTransferState(if (shooting) TransferState.ON else TransferState.OFF)
        val gate = if (shooting) TransferGateState.OPEN else TransferGateState.CLOSE
        TransferHardware.setTransferGateModeBig(gate)
        TransferHardware.setTransferGateModeSmall(gate)
        applyIntakeAndTransfer(shooting)

        // ---- Turrets (TurretHardware aims at Robot.deltaVec) ----
        val target = closestBlueCell(pose)
        Robot.deltaVec = Vector(target.x() - pose.x(), target.y() - pose.y())
        BigTurret.periodic(); BigTurret.update(0.0)
        SmallTurret.periodic(); SmallTurret.update(0.0)

        // ---- Hood ----
        if (gamepad1.dpad_up) hoodPos += HOOD_RATE * dt
        if (gamepad1.dpad_down) hoodPos -= HOOD_RATE * dt
        hoodPos = hoodPos.coerceIn(0.0, 1.0)
        hood.position = hoodPos

        telemetry.addData("Pose", "x %.1f  y %.1f  h %.0f deg", pose.x(), pose.y(), Math.toDegrees(pose.heading()))
        telemetry.addData("Target", if (target === BLUE_SCORING_CELL) "Blue Scoring cell" else "Blue Audience cell")
        telemetry.addData("Intake", if (intaking) "IN" else "off")
        telemetry.addData("Shooting", shooting)
        telemetry.addLine("Keyboard: WASD drive, J/L turn, U = LT intake, O = RT shoot, ; = A, [ = B, arrows = dpad")
    }

    /** IntakeHardware.periodic() writes intake power + arm position; transfer has no periodic. */
    private fun applyIntakeAndTransfer(shooting: Boolean) {
        IntakeHardware.periodic()
        // TransferVars.transferState's setter sets transferPower to 0 on OFF and never restores it,
        // so the ON power is used directly here (see message to the team).
        TransferHardware.setPower(if (shooting) 1.0 else TransferVars.transferStopPower)
        TransferHardware.TransferGateServoBig.position = TransferVars.transferGateModeBig
        TransferHardware.TransferGateServoSmall.position = TransferVars.transferGateModeSmall
        // NextMotor only sends its power in update() (BlazeOpMode does this on the robot).
        IntakeHardware.intakeMotor.update()
        TransferHardware.TransferMotor.update()
    }

    private fun closestBlueCell(robot: Pose): Pose =
        if (distance(robot, BLUE_SCORING_CELL) <= distance(robot, BLUE_AUDIENCE_CELL)) BLUE_SCORING_CELL
        else BLUE_AUDIENCE_CELL

    private fun distance(a: Pose, b: Pose) = hypot(a.x() - b.x(), a.y() - b.y())
}
