MY FTC ROBOT - AdvantageScope custom asset
==========================================
Made from "robot asm.SLDASM" (21 Oct 2026). Game pieces removed.

INSTALL
  AdvantageScope > Help > Show Assets Folder, put this whole "Robot_MyFTCRobot"
  folder there (unzipped), restart AdvantageScope, then pick "My FTC Robot" in the
  3D Field tab (FTC field).

ROBOT FRAME (what your robot pose means)
  Origin = on the floor, under the centre of the 4 mecanum wheels.
  +X = front = the end with the DROP-DOWN intake arm.  +Z = up.
  If you want the other end to be the front, change the top-level
  "rotations" in config.json to:  [{"axis":"x","degrees":90},{"axis":"z","degrees":180}]
  (and flip the sign of X/Y in the component positions below).

FILES / COMPONENTS (index = order you publish the Pose3d[] array)
  model.glb    chassis, drivetrain, static intake (spinning-only end), sorter, electronics
  model_0.glb  [0] drop-down intake arm  - pivots on the powered 363mm shaft
  model_1.glb  [1] BIG shooter turret (incl. Limelight) - spins on its bearing stack
  model_2.glb  [2] SMALL shooter turret - spins on its bearing stack
  model_3.glb  [3] SMALL shooter hood (adjust arms + back plate) - pivots on the flywheel shaft
  (The big shooter has no adjustable hood in the CAD, so its hood is part of the big turret.)

COMPONENT POSES TO PUBLISH (robot-relative, metres / radians)
  All angles = 0 puts every part exactly where it is in the CAD.

  [0] intake arm : Pose3d( 0.1307,  0.0000, 0.1560,  Rotation3d(0, armAngle, 0) )
        rotation is about the robot Y axis. Positive armAngle = arm swings DOWN,
        negative = swings UP (relative to the CAD position).
  [1] big turret : Pose3d(-0.0006,  0.1166, 0.3290,  Rotation3d(0, 0, bigYaw) )
  [2] small turret: Pose3d(-0.0006, -0.1231, 0.3290, Rotation3d(0, 0, smallYaw) )
  [3] small hood : must follow the small turret, so build it FROM the turret pose:
        hood = smallTurret.transformBy(new Transform3d(
                   new Translation3d(0.0283, -0.0533, 0.0580),
                   new Rotation3d(VecBuilder.fill(0.8647, 0.5023, 0.0), hoodAngle)));
        (if the hood moves the wrong way, use -hoodAngle)

  Java (WPILib geometry, works with AdvantageKit / PsiKit style logging):

    Pose3d arm   = new Pose3d(0.1307, 0, 0.156, new Rotation3d(0, armAngle, 0));
    Pose3d big   = new Pose3d(-0.0006, 0.1166, 0.329, new Rotation3d(0, 0, bigYaw));
    Pose3d small = new Pose3d(-0.0006, -0.1231, 0.329, new Rotation3d(0, 0, smallYaw));
    Pose3d hood  = small.transformBy(new Transform3d(
        new Translation3d(0.0283, -0.0533, 0.058),
        new Rotation3d(VecBuilder.fill(0.8647, 0.5023, 0.0), hoodAngle)));
    Logger.recordOutput("Robot/Components", new Pose3d[] {arm, big, small, hood});

  In AdvantageScope's 3D Field: drag your robot pose onto the field, then drag
  "Robot/Components" onto the robot as "Component Poses".

  TIP: first publish all four with angles = 0 - every part should sit exactly
  where it is in the CAD. Then turn the real angles on.
