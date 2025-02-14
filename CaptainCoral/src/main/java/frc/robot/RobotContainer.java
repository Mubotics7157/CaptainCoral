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
import frc.robot.commands.IntakeWristCmd;
import frc.robot.commands.IntakeRunCmd;

//End Effector Imports
import frc.robot.subsystems.EndEffector;
import frc.robot.commands.EndEffectorWristCmd;
import frc.robot.commands.EndEffectorRunCmd;
import frc.robot.commands.EndEffectorScoreCmd;
//Elevator Imports
import frc.robot.subsystems.Elevator;
import frc.robot.commands.ElevatorJogCmd;
import frc.robot.commands.ElevatorUpCmd;
import frc.robot.commands.ElevatorDownCmd;
//Climb Imports
import frc.robot.subsystems.Climb;
import frc.robot.commands.ClimbRunCmd;

//Limelight Imports
import frc.robot.commands.LeftAlignCmd;
import frc.robot.commands.RightAlignCmd;
import frc.robot.commands.RightAlignCmd;
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
        autoChooser = AutoBuilder.buildAutoChooser("Tests");
        SmartDashboard.putData("Auto Mode", autoChooser);

        //Alignment Commands
        // NamedCommands.registerCommand("leftAlignCmd", new LeftAlignCmd(drivetrain));
        // NamedCommands.registerCommand("rightAlignCmd", new RightAlignCmd(drivetrain));

        // //Zeroing Commands
        // NamedCommands.registerCommand("zeroEndEffectorWrist", new EndEffectorWristCmd(EndEffector.getInstance(), KinematicsConstants.End_Effector_Wrist_Zero_Setpoint));
        // NamedCommands.registerCommand("zeroElevator", new ElevatorTeleUpCmd(Elevator.getInstance(), KinematicsConstants.Elevator_Zero_Setpoint));
        // NamedCommands.registerCommand("stopEndEffector", new EndEffectorRunCmd(EndEffector.getInstance(), KinematicsConstants.Absolute_Zero));
        // //NamedCommands.registerCommand("stopIntakeRollers", new IntakeRunCmd(Intake.getInstance(), KinematicsConstants.Intake_Ground_Run_Speed));

        // //Action Commands
        // NamedCommands.registerCommand("endEffectorL4", new EndEffectorWristCmd(EndEffector.getInstance(), KinematicsConstants.End_Effector_Wrist_L4_Score_Setpoint));
        // NamedCommands.registerCommand("elevatorL4", new ElevatorPIDCmd(Elevator.getInstance(), KinematicsConstants.Elevator_L4_Setpoint));
        // NamedCommands.registerCommand("runEndEffector", new EndEffectorRunCmd(EndEffector.getInstance(), KinematicsConstants.End_Effector_Ground_Intake_Speed));
        //NamedCommands.registerCommand("groundIntakeRollers", new IntakeRunCmd(Intake.getInstance(), KinematicsConstants.Intake_Ground_Run_Speed));

        configureBindings();
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
        DriverController.leftStick().whileTrue(new LeftAlignCmd(drivetrain));

        // //====================Align Right====================
        DriverController.rightStick().whileTrue(new RightAlignCmd(drivetrain));

        //====================RIO CANBUS BINDINGS====================
        //====================Ground Intake====================
        DriverController.leftTrigger().whileTrue(
                Commands.parallel(    
                new IntakeRunCmd(Intake.getInstance(), KinematicsConstants.Intake_Ground_Run_Speed, DriverController.getHID()),
                new EndEffectorRunCmd(EndEffector.getInstance(), KinematicsConstants.End_Effector_Ground_Intake_Speed)
                )
        );
    
        DriverController.leftTrigger().onFalse(
                Commands.parallel(
                new IntakeRunCmd(Intake.getInstance(), KinematicsConstants.Absolute_Zero, DriverController.getHID()),
                new EndEffectorRunCmd(EndEffector.getInstance(), KinematicsConstants.Absolute_Zero)
                )
        );

         //====================Ground Outtake====================
         DriverController.povUp().whileTrue(
                Commands.parallel(    
                new IntakeRunCmd(Intake.getInstance(), KinematicsConstants.Outake_Ground_Run_Speed, DriverController.getHID()),
                new EndEffectorRunCmd(EndEffector.getInstance(), KinematicsConstants.End_Effector_Ground_Outake_Speed)
                )
        );
    
        DriverController.povUp().onFalse(
                Commands.parallel(
                new IntakeRunCmd(Intake.getInstance(), KinematicsConstants.Absolute_Zero, DriverController.getHID()),
                new EndEffectorRunCmd(EndEffector.getInstance(), KinematicsConstants.Absolute_Zero)
                )
        );

        // //====================End Effector Run====================
        DriverController.rightTrigger().whileTrue(new EndEffectorScoreCmd(EndEffector.getInstance(), KinematicsConstants.End_Effector_Score_Speed));

        //====================Bottom Algae DeScore====================
        DriverController.leftBumper().whileTrue(
            Commands.parallel(
                new EndEffectorWristCmd(EndEffector.getInstance(), KinematicsConstants.End_Effector_Wrist_Algae_Remove_Setpoint),
                new ElevatorDownCmd(Elevator.getInstance(), KinematicsConstants.Elevator_Bottom_Algae_Setpoint)
                )
        );
    
        DriverController.leftBumper().onFalse(
                Commands.parallel(
                new EndEffectorWristCmd(EndEffector.getInstance(), KinematicsConstants.End_Effector_Wrist_Zero_Setpoint),
                new ElevatorDownCmd(Elevator.getInstance(), KinematicsConstants.Elevator_Zero_Setpoint)
                )
        );

        //====================Top Algae DeScore====================
        DriverController.rightBumper().whileTrue(
            Commands.parallel(
                new EndEffectorWristCmd(EndEffector.getInstance(), KinematicsConstants.End_Effector_Wrist_Algae_Remove_Setpoint),
                new ElevatorDownCmd(Elevator.getInstance(), KinematicsConstants.Elevator_Top_Algae_Setpoint)
                )
        );
    
        DriverController.rightBumper().onFalse(
                Commands.parallel(
                new EndEffectorWristCmd(EndEffector.getInstance(), KinematicsConstants.End_Effector_Wrist_Zero_Setpoint),
                new ElevatorDownCmd(Elevator.getInstance(), KinematicsConstants.Elevator_Zero_Setpoint)
                )
        );

        // //====================Level 1 Coral Score====================
        // DriverController.a().whileTrue(
        //         Commands.parallel(
        //         new EndEffectorWristCmd(EndEffector.getInstance(), KinematicsConstants.End_Effector_Wrist_L1_Score_Setpoint),
        //         new ElevatorUpCmd(Elevator.getInstance(), KinematicsConstants.Elevator_L1_Setpoint)
        //         )
        // );

        // DriverController.a().onFalse(
        //         Commands.parallel(
        //         new EndEffectorWristCmd(EndEffector.getInstance(), KinematicsConstants.End_Effector_Wrist_Zero_Setpoint),
        //         new ElevatorDownCmd(Elevator.getInstance(), KinematicsConstants.Elevator_Zero_Setpoint)
        //         )
        // );

        // //====================Level 2 Coral Score====================
        DriverController.b().whileTrue(
                Commands.parallel(
                new EndEffectorWristCmd(EndEffector.getInstance(), KinematicsConstants.End_Effector_Wrist_L2_L3_Score_Setpoint),
                new ElevatorUpCmd(Elevator.getInstance(), KinematicsConstants.Elevator_L2_Setpoint, drivetrain)
                )
        );

        DriverController.b().onFalse(
                Commands.parallel(
                new EndEffectorWristCmd(EndEffector.getInstance(), KinematicsConstants.End_Effector_Wrist_Zero_Setpoint),
                new ElevatorDownCmd(Elevator.getInstance(), KinematicsConstants.Elevator_Zero_Setpoint)
                )
        );

        // //====================Level 3 Coral Score====================
        // DriverController.x().whileTrue(
        //         Commands.parallel(
        //         new EndEffectorWristCmd(EndEffector.getInstance(), KinematicsConstants.End_Effector_Wrist_L2_L3_Score_Setpoint),
        //         new ElevatorUpCmd(Elevator.getInstance(), KinematicsConstants.Elevator_L3_Setpoint)
        //         )
        // );
    
        // DriverController.x().onFalse(
        //         Commands.parallel(
        //         new EndEffectorWristCmd(EndEffector.getInstance(), KinematicsConstants.End_Effector_Wrist_Zero_Setpoint),
        //         new ElevatorDownCmd(Elevator.getInstance(), KinematicsConstants.Elevator_Zero_Setpoint)
        //         )
        // );

        // //====================Level 4 Coral Score====================
        // DriverController.y().whileTrue(
        //     Commands.parallel(
        //         new EndEffectorWristCmd(EndEffector.getInstance(), KinematicsConstants.End_Effector_Wrist_L4_Score_Setpoint),
        //         new ElevatorUpCmd(Elevator.getInstance(), KinematicsConstants.Elevator_L4_Setpoint)
        //         )
        // );
    
        // DriverController.y().onFalse(
        //         Commands.parallel(
        //         new EndEffectorWristCmd(EndEffector.getInstance(), KinematicsConstants.End_Effector_Wrist_Zero_Setpoint),
        //         new ElevatorDownCmd(Elevator.getInstance(), KinematicsConstants.Elevator_Zero_Setpoint)
        //         )
        // );

        // //====================Gullet Intake====================
        // DriverController.button(8).whileTrue(
        //         Commands.parallel(
        //         new EndEffectorWristCmd(EndEffector.getInstance(), KinematicsConstants.End_Effector_Wrist_Coral_Station_Setpoint),
        //         new ElevatorDownCmd(Elevator.getInstance(), KinematicsConstants.Elevator_Coral_Station_Setpoint)
        //         )
        // );

        // DriverController.button(8).onFalse(
        //         Commands.parallel(
        //         new EndEffectorWristCmd(EndEffector.getInstance(), KinematicsConstants.End_Effector_Wrist_Zero_Setpoint),
        //         new ElevatorDownCmd(Elevator.getInstance(), KinematicsConstants.Elevator_Zero_Setpoint)
        //         )
        // );

        //====================Elevator Jog====================
        // DriverController.povUp().whileTrue(new ElevatorJogCmd(Elevator.getInstance(), () -> KinematicsConstants.Jog_Speed_Multiplier * DriverController.getRightY()));
        }

        public Command getAutonomousCommand() {
            return autoChooser.getSelected();
        }
    }