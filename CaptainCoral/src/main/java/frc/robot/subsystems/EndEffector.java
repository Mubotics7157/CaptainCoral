package frc.robot.subsystems;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import frc.robot.Devices;
import frc.robot.Constants;

public class EndEffector extends SubsystemBase {
    private final TalonFX End_Effector_Wrist_Motor = new TalonFX(Devices.END_EFFECTOR_WRIST_MOTOR);
    private final TalonFX End_Effector_Top_Roller_Motor = new TalonFX(Devices.END_EFFECTOR_ROLLER_MOTOR);

    private final DutyCycleEncoder End_Effector_Wrist_Through_Bore_Encoder = new DutyCycleEncoder(new DigitalInput(Devices.END_EFFECTOR_WRIST_THROUGH_BORE_PORT));
    private final DigitalInput End_Effector_Front_Photoelectric = new DigitalInput(Devices.END_EFFECTOR_PHOTOELECTRIC_FRONT_PORT);
    private final DigitalInput End_Effector_Back_Photoelectric = new DigitalInput(Devices.END_EFFECTOR_PHOTOELECTRIC_BACK_PORT);

    private double setpoint;

    public static EndEffector getInstance() {
        return instance;
    }

    private static EndEffector instance = new EndEffector();

    public EndEffector() {
        System.out.println("====================EndEffector Subsystem Online====================");

        //====================End Effector Wrist====================
        var endEffectorWristMotorConfigs = new TalonFXConfiguration();

        End_Effector_Wrist_Motor.setPosition(getEndEffectorWristThroughBore() * Constants.End_Effector_Absolute_To_Integrated); //Change to KinematicsConstants.Absolute_Zero if not working

        //Brake Mode
        endEffectorWristMotorConfigs.MotorOutput.NeutralMode = NeutralModeValue.Brake;

        //General Configurations
        var generalSlotConfigs = endEffectorWristMotorConfigs.Slot0;
        generalSlotConfigs.kS = Constants.End_Effector_Wrist_kS;
        generalSlotConfigs.kV = Constants.End_Effector_Wrist_kV;
        generalSlotConfigs.kA = Constants.End_Effector_Wrist_kA;
        generalSlotConfigs.kP = Constants.End_Effector_Wrist_kP;
        generalSlotConfigs.kI = Constants.End_Effector_Wrist_kI;
        generalSlotConfigs.kD = Constants.End_Effector_Wrist_kD;

        //Motion Magic
        var motionMagicConfigs = endEffectorWristMotorConfigs.MotionMagic;
        // motionMagicConfigs.MotionMagicCruiseVelocity = Constants.End_Effector_Wrist_Velocity;
        // motionMagicConfigs.MotionMagicAcceleration = Constants.End_Effector_Wrist_Acceleration;

        //Current Limits
        var endEffectorWristLimitConfigs = endEffectorWristMotorConfigs.CurrentLimits;
        endEffectorWristLimitConfigs.StatorCurrentLimit = Constants.End_Effector_Wrist_Current_Limit;
        endEffectorWristLimitConfigs.StatorCurrentLimitEnable = true;

        //Applies Configs
        End_Effector_Wrist_Motor.getConfigurator().apply(endEffectorWristMotorConfigs);

        //====================End Effector Rollers====================
        var endEffectorRollerMotorsConfigs = new TalonFXConfiguration();

        //Current Limits
        var endEffectorRollerLimitConfigs = endEffectorRollerMotorsConfigs.CurrentLimits;
        endEffectorRollerLimitConfigs.StatorCurrentLimit = Constants.End_Effector_Roller_Current_Limit;
        endEffectorRollerLimitConfigs.StatorCurrentLimitEnable = true; 

        //Applies Configs
        End_Effector_Top_Roller_Motor.getConfigurator().apply(endEffectorRollerLimitConfigs);
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("End Effector Wrist Encoder", getEndEffectorWristEncoder());
        SmartDashboard.putNumber("End Effector Through Bore Encoder", getEndEffectorWristThroughBore());
        SmartDashboard.putBoolean("End Effector Front Photoelectric Reading", getEndEffectorFrontPhotoElectricReading());
        SmartDashboard.putBoolean("End Effector Back Photoelectric Reading", getEndEffectorBackPhotoElectricReading());
    }
    
    //====================End Effector Wrist Methods====================
    public double getEndEffectorWristEncoder() {
        return End_Effector_Wrist_Motor.getPosition().getValueAsDouble();
    }

    public double getEndEffectorWristThroughBore() {
        return End_Effector_Wrist_Through_Bore_Encoder.get();
    }

    public void setEndEffectorWristSetpoint(double setpoint) {
        this.setpoint = setpoint;
    }

    public void goToEndEffectorWristSetpoint() {
        final MotionMagicVoltage m_request = new MotionMagicVoltage(Constants.Absolute_Zero);
        End_Effector_Wrist_Motor.setControl(m_request.withPosition(-1 * this.setpoint));
    }

    public void setEndEffectorWristSpeed(double speed) {
        End_Effector_Wrist_Motor.set(-1 * speed);
    }

    public void zeroEndEffectorWrist() {
        End_Effector_Wrist_Motor.setPosition(Constants.Absolute_Zero);
    }

    //====================End Effector Roller Methods====================
    public void setEndEffectorRollerMotorSpeed(double speed) {
        End_Effector_Top_Roller_Motor.set(-1 * speed);
    }

    public boolean getEndEffectorFrontPhotoElectricReading() {
        return !End_Effector_Front_Photoelectric.get();
    }

    public boolean getEndEffectorBackPhotoElectricReading() {
        return !End_Effector_Back_Photoelectric.get();
    }
}