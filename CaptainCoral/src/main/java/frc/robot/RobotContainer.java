package frc.robot;

//Drivetrain Imports
import static edu.wpi.first.units.Units.*;
import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.Kinematics;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;
import frc.robot.subsystems.Drivetrain;

//Intake Imports
import frc.robot.subsystems.Intake;
import frc.robot.commands.IntakeWrist;
import frc.robot.commands.IntakeRunCmd;

//End Effector Imports
import frc.robot.subsystems.EndEffector;
import frc.robot.commands.EndEffectorWrist;
import frc.robot.commands.STATEIntakeGround;
import frc.robot.commands.EndEffectorRun;
import frc.robot.commands.EndEffectorScore;
//Elevator Imports
import frc.robot.subsystems.Elevator;
import frc.robot.commands.ElevatorJog;
import frc.robot.commands.ElevatorPlace;
import frc.robot.commands.ElevatorDefault;
//Climb Imports
import frc.robot.subsystems.Climb;
import frc.robot.commands.ClimbRun;

//Limelight Imports
import frc.robot.commands.DrivetrainLeftAlign;
import frc.robot.commands.DrivetrainRightAlign;
import frc.robot.commands.DrivetrainRotAlign;
import frc.robot.commands.DrivetrainRightAlign;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class RobotContainer {
    //====================GENERAL SETUP====================
    private final SendableChooser<Command> autoChooser;
    private final CommandXboxController DriverController = new CommandXboxController(DeviceConstants.DRIVER_CONTROLLER_DEVICE_ID);

    //====================SWERVE SETUP====================
    private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
            .withDeadband(KinematicsConstants.DrivetrainMaxSpeed * 0.1).withRotationalDeadband(KinematicsConstants.DrivetrainMaxAngularRate * 0.1) // Add a 10% deadband
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage); // Use open-loop control for drive motors
    private final SwerveRequest.RobotCentric forwardStraight = new SwerveRequest.RobotCentric()
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage);

    public final Drivetrain drivetrain = TunerConstants.createDrivetrain();    

    public RobotContainer() {
        //====================Autonomous Commands====================
        //Alignment Commands
        NamedCommands.registerCommand("leftAlignCmd", new DrivetrainLeftAlign(drivetrain));
        NamedCommands.registerCommand("rightAlignCmd", new DrivetrainRightAlign(drivetrain));

        //Zeroing Commands
        NamedCommands.registerCommand("zeroEndEffectorWrist", new EndEffectorWrist(EndEffector.getInstance(), KinematicsConstants.End_Effector_Wrist_Zero_Setpoint));
        NamedCommands.registerCommand("zeroElevator", new ElevatorDefault(Elevator.getInstance(), KinematicsConstants.Elevator_Zero_Setpoint));
        NamedCommands.registerCommand("endEffectorStop", new EndEffectorScore(EndEffector.getInstance(), KinematicsConstants.Absolute_Zero));
        NamedCommands.registerCommand("stopGroundIntake", new STATEIntakeGround(EndEffector.getInstance(), 0.0, 0.0, Intake.getInstance(), 5.0, 0.0));

        //Action Commands
        NamedCommands.registerCommand("endEffectorL4", new EndEffectorWrist(EndEffector.getInstance(), KinematicsConstants.End_Effector_Wrist_L4_Score_Setpoint));
        NamedCommands.registerCommand("elevatorL4", new ElevatorDefault(Elevator.getInstance(), KinematicsConstants.Elevator_L4_Setpoint));
        NamedCommands.registerCommand("endEffectorScore", new EndEffectorScore(EndEffector.getInstance(), KinematicsConstants.End_Effector_Score_Speed));
        NamedCommands.registerCommand("groundIntake", new STATEIntakeGround(EndEffector.getInstance(), 0.375, 0.573, Intake.getInstance(), 17.0, -0.8));

        configureBindings();

        autoChooser = AutoBuilder.buildAutoChooser("StraightPreload");
        SmartDashboard.putData("Auto Mode", autoChooser);
    }

    private void configureBindings() {
        //====================SWERVE CANBUS BINDINGS====================
            drivetrain.setDefaultCommand(drivetrain.applyRequest(() -> drive
            .withVelocityX(-1 * MathUtil.applyDeadband(DriverController.getLeftY(), 0.05) * KinematicsConstants.Drivetrain_Speed_Multiplier * KinematicsConstants.DrivetrainMaxSpeed)
            .withVelocityY(-1 * MathUtil.applyDeadband(DriverController.getLeftX(), 0.05) * KinematicsConstants.Drivetrain_Speed_Multiplier * KinematicsConstants.DrivetrainMaxSpeed)
            .withRotationalRate(-1 * MathUtil.applyDeadband(DriverController.getRightX(), 0.05) * KinematicsConstants.Drivetrain_Turn_Multiplier * KinematicsConstants.DrivetrainMaxAngularRate)
            )
        );

        DriverController.back().and(DriverController.y()).whileTrue(drivetrain.sysIdDynamic(Direction.kForward));
        DriverController.back().and(DriverController.x()).whileTrue(drivetrain.sysIdDynamic(Direction.kReverse));
        DriverController.start().and(DriverController.y()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kForward));
        DriverController.start().and(DriverController.x()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kReverse));

        //====================Swerve Heading Reset====================
        DriverController.povDown().onTrue(drivetrain.runOnce(() -> drivetrain.seedFieldCentric()));

        //====================Align Left====================
        DriverController.leftBumper().whileTrue(new DrivetrainLeftAlign(drivetrain));

        // //====================Align Right====================
        DriverController.rightBumper().whileTrue(new DrivetrainRightAlign(drivetrain));

        // ROT ALIGN
        DriverController.povLeft().whileTrue(new DrivetrainRotAlign(drivetrain));

        //====================RIO CANBUS BINDINGS====================
        // DriverController.povLeft().whileTrue(new IntakeWristCmd(Intake.getInstance(), 16.0));
        // DriverController.povLeft().onFalse(new IntakeWristCmd(Intake.getInstance(), 5.0));

        //====================Ground Intake====================
        DriverController.leftTrigger().whileTrue(new STATEIntakeGround(EndEffector.getInstance(), 0.375, 0.573, Intake.getInstance(), 17.0, -0.8));
        DriverController.leftTrigger().whileTrue(new ElevatorDefault(Elevator.getInstance(), 0.5));

        DriverController.leftTrigger().onFalse(new STATEIntakeGround(EndEffector.getInstance(), 0.0, 0.0, Intake.getInstance(), 5.0, 0.0));
        DriverController.leftTrigger().onFalse(new ElevatorDefault(Elevator.getInstance(), 0.0));

        //====================Ground Outtake====================
        DriverController.povUp().whileTrue(
                Commands.parallel(    
                new IntakeRunCmd(Intake.getInstance(), KinematicsConstants.Outake_Ground_Run_Speed),
                new EndEffectorRun(EndEffector.getInstance(), KinematicsConstants.End_Effector_Ground_Outake_Speed, DriverController.getHID())
                )
        );
    
        DriverController.povUp().onFalse(
                Commands.parallel(
                new IntakeRunCmd(Intake.getInstance(), KinematicsConstants.Absolute_Zero),
                new EndEffectorRun(EndEffector.getInstance(), KinematicsConstants.Absolute_Zero, DriverController.getHID())
                )
        );

        // //====================End Effector Run====================
        DriverController.rightTrigger().whileTrue(new EndEffectorScore(EndEffector.getInstance(), KinematicsConstants.End_Effector_Score_Speed));
        DriverController.rightTrigger().onFalse(new EndEffectorScore(EndEffector.getInstance(), KinematicsConstants.Absolute_Zero));

        //====================Bottom Algae DeScore====================
        // DriverController.rightBumper().whileTrue(
        //     Commands.parallel(
        //         new EndEffectorWristCmd(EndEffector.getInstance(), KinematicsConstants.End_Effector_Wrist_Algae_Remove_Setpoint),
        //         new ElevatorPlaceCmd(Elevator.getInstance(), KinematicsConstants.Elevator_Bottom_Algae_Setpoint, drivetrain, DriverController.getHID(), KinematicsConstants.Drivetrain_Elevator_Speed_Multiplier, KinematicsConstants.Drivetrain_Elevator_Turn_Multiplier)
        //         )
        // );

        // DriverController.rightBumper().onFalse(
        //         Commands.parallel(
        //         new EndEffectorWristCmd(EndEffector.getInstance(), KinematicsConstants.End_Effector_Wrist_Zero_Setpoint),
        //         new ElevatorPlaceCmd(Elevator.getInstance(), KinematicsConstants.Elevator_Zero_Setpoint, drivetrain, DriverController.getHID(), KinematicsConstants.Drivetrain_Elevator_Speed_Multiplier, KinematicsConstants.Drivetrain_Elevator_Turn_Multiplier)
        //         )
        // );

        //====================Top Algae DeScore====================
        // DriverController.rightBumper().whileTrue(
        //     Commands.parallel(
        //         new EndEffectorWristCmd(EndEffector.getInstance(), KinematicsConstants.End_Effector_Wrist_Algae_Remove_Setpoint),
        //         new ElevatorPlaceCmd(Elevator.getInstance(), KinematicsConstants.Elevator_Top_Algae_Setpoint, drivetrain, DriverController.getHID(), KinematicsConstants.Drivetrain_Elevator_Speed_Multiplier, KinematicsConstants.Drivetrain_Elevator_Turn_Multiplier)
        //         )
        // );

        // //CHANGE WRIST SETPOINT
        // DriverController.rightBumper().onFalse(
        //         Commands.parallel(
        //         new EndEffectorWristCmd(EndEffector.getInstance(), KinematicsConstants.End_Effector_Wrist_Zero_Setpoint),
        //         new ElevatorPlaceCmd(Elevator.getInstance(), KinematicsConstants.Elevator_Zero_Setpoint, drivetrain, DriverController.getHID(), KinematicsConstants.Drivetrain_Elevator_Speed_Multiplier, KinematicsConstants.Drivetrain_Elevator_Turn_Multiplier)
        //         )
        // );

        // //====================Level 1 Coral Score====================
        DriverController.a().whileTrue(new EndEffectorScore(EndEffector.getInstance(), 0.2));
        DriverController.a().onFalse(new EndEffectorScore(EndEffector.getInstance(), KinematicsConstants.Absolute_Zero));

        // // //====================Level 2 Coral Score====================
        DriverController.b().whileTrue(
                Commands.parallel(
                new EndEffectorWrist(EndEffector.getInstance(), KinematicsConstants.End_Effector_Wrist_L2_L3_Score_Setpoint),
                new ElevatorPlace(Elevator.getInstance(), KinematicsConstants.Elevator_L2_Setpoint, drivetrain, DriverController.getHID(), KinematicsConstants.Drivetrain_Elevator_Speed_Multiplier, KinematicsConstants.Drivetrain_Elevator_Turn_Multiplier)
                )
        );

        DriverController.b().onFalse(
                Commands.parallel(
                new EndEffectorWrist(EndEffector.getInstance(), KinematicsConstants.End_Effector_Wrist_Zero_Setpoint),
                new ElevatorDefault(Elevator.getInstance(), KinematicsConstants.Elevator_Zero_Setpoint)
                )
        );

        //====================Level 3 Coral Score====================
        DriverController.x().whileTrue(
                Commands.parallel(
                new EndEffectorWrist(EndEffector.getInstance(), KinematicsConstants.End_Effector_Wrist_L2_L3_Score_Setpoint),
                new ElevatorPlace(Elevator.getInstance(), KinematicsConstants.Elevator_L3_Setpoint, drivetrain, DriverController.getHID(), KinematicsConstants.Drivetrain_Elevator_Speed_Multiplier, KinematicsConstants.Drivetrain_Elevator_Turn_Multiplier)
                )
        );
    
        DriverController.x().onFalse(
                Commands.parallel(
                new EndEffectorWrist(EndEffector.getInstance(), KinematicsConstants.End_Effector_Wrist_Zero_Setpoint),
                new ElevatorDefault(Elevator.getInstance(), KinematicsConstants.Elevator_Zero_Setpoint)
                )
        );

        //====================Level 4 Coral Score====================
        DriverController.y().whileTrue(
            Commands.parallel(
                new EndEffectorWrist(EndEffector.getInstance(), KinematicsConstants.End_Effector_Wrist_L4_Score_Setpoint),
                new ElevatorPlace(Elevator.getInstance(), KinematicsConstants.Elevator_L4_Setpoint, drivetrain, DriverController.getHID(), KinematicsConstants.Drivetrain_Elevator_Speed_Multiplier, KinematicsConstants.Drivetrain_Elevator_Turn_Multiplier)
                )
        );
    
        DriverController.y().onFalse(
                Commands.parallel(
                new EndEffectorWrist(EndEffector.getInstance(), KinematicsConstants.End_Effector_Wrist_Zero_Setpoint),
                new ElevatorDefault(Elevator.getInstance(), KinematicsConstants.Elevator_Zero_Setpoint)
                )
        );

        //====================Gullet Intake====================
        DriverController.button(8).whileTrue(
                Commands.parallel(
                new EndEffectorWrist(EndEffector.getInstance(), KinematicsConstants.End_Effector_Wrist_Coral_Station_Setpoint),
                new ElevatorPlace(Elevator.getInstance(), KinematicsConstants.Elevator_Coral_Station_Setpoint, drivetrain, DriverController.getHID(), KinematicsConstants.Drivetrain_Elevator_Speed_Multiplier, KinematicsConstants.Drivetrain_Elevator_Turn_Multiplier)
                )
        );

        DriverController.button(8).onFalse(
                Commands.parallel(
                new EndEffectorWrist(EndEffector.getInstance(), KinematicsConstants.End_Effector_Wrist_Zero_Setpoint),
                new ElevatorDefault(Elevator.getInstance(), KinematicsConstants.Elevator_Zero_Setpoint)
                )
        );

        //====================Elevator Jog=====================
        DriverController.povRight().whileTrue(new ElevatorJog(Elevator.getInstance(), () -> KinematicsConstants.Jog_Speed_Multiplier * DriverController.getRightY()));
        }

        public Command getAutonomousCommand() {
            return autoChooser.getSelected();
        }
    }