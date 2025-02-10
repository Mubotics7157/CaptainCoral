package frc.robot.commands;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Intake;
import frc.robot.KinematicsConstants;

public class IntakeRunCmd extends Command {
    private final double speed;
    private final Intake intake;

    public IntakeRunCmd(Intake intake, double speed) {
        this.speed = speed;
        this.intake = Intake.getInstance();
        addRequirements(intake);
    }

    @Override
    public void initialize() {
        System.out.println("IntakeRunCmd Started");
    }

    @Override
    public void execute() {
        double motorSpeed = speed;
        intake.setIntakeRollerMotorSpeed(motorSpeed);
        intake.setIndexerMotorSpeed(motorSpeed);
        System.out.println("IntakeRunCmd Ongoing");
    }

    @Override
    public void end(boolean interrupted) {
        intake.setIntakeRollerMotorSpeed(KinematicsConstants.Absolute_Zero);
        intake.setIndexerMotorSpeed(KinematicsConstants.Absolute_Zero);
        System.out.println("IntakeRunCmd Ended");
    }

    public boolean isFinished() {
        return false;
    }
}