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

import frc.robot.DeviceConstants;
import frc.robot.KinematicsConstants;

public class EndEffector extends SubsystemBase {
    private final TalonFX End_Effector_Wrist_Master_Motor = new TalonFX(DeviceConstants.END_EFFECTOR_WRIST_MOTOR_DEVICE_ID);
    private final TalonFX End_Effector_Master_Motor_Roller = new TalonFX(DeviceConstants.END_EFFECTOR_MASTER_ROLLER_MOTOR_DEVICE_ID);
    private final TalonFX End_Effector_Slave_Motor_Roller = new TalonFX(DeviceConstants.END_EFFECTOR_SLAVE_ROLLER_MOTOR_DEVICE_ID);

    private final DutyCycleEncoder End_Effector_Wrist_Through_Bore_Encoder = new DutyCycleEncoder(new DigitalInput(DeviceConstants.END_EFFECTOR_WRIST_THROUGH_BORE_ENCODER));
    private final DigitalInput End_Effector_Sensor = new DigitalInput(DeviceConstants.END_EFFECTOR_SENSOR_PORT);

    private double setpoint;

    public static EndEffector getInstance() {
        return instance;
    }

    private static EndEffector instance = new EndEffector();

    public EndEffector() {
        System.out.println("====================EndEffector Subsystem Initialized====================");

        //====================End Effector Wrist====================
        var endEffectorWristMotorConfigs = new TalonFXConfiguration();

        End_Effector_Wrist_Master_Motor.setPosition(getEndEffectorWristThroughBoreEncoder() * KinematicsConstants.End_Effector_Absolute_To_Integrated); //Change to KinematicsConstants.Absolute_Zero if not working

        //Brake Mode
        endEffectorWristMotorConfigs.MotorOutput.NeutralMode = NeutralModeValue.Brake;

        //General Configurations
        var generalSlotConfigs = endEffectorWristMotorConfigs.Slot0;
        generalSlotConfigs.kS = KinematicsConstants.End_Effector_Wrist_kS;
        generalSlotConfigs.kV = KinematicsConstants.End_Effector_Wrist_kV;
        generalSlotConfigs.kA = KinematicsConstants.End_Effector_Wrist_kA;
        generalSlotConfigs.kP = KinematicsConstants.End_Effector_Wrist_kP;
        generalSlotConfigs.kI = KinematicsConstants.End_Effector_Wrist_kI;
        generalSlotConfigs.kD = KinematicsConstants.End_Effector_Wrist_kD;

        //Motion Magic
        var motionMagicConfigs = endEffectorWristMotorConfigs.MotionMagic;
        motionMagicConfigs.MotionMagicCruiseVelocity = KinematicsConstants.End_Effector_Wrist_Velocity;
        motionMagicConfigs.MotionMagicAcceleration = KinematicsConstants.End_Effector_Wrist_Acceleration;
        motionMagicConfigs.MotionMagicJerk = KinematicsConstants.End_Effector_Wrist_Jerk;

        //Current Limits
        var endEffectorWristLimitConfigs = endEffectorWristMotorConfigs.CurrentLimits;
        endEffectorWristLimitConfigs.StatorCurrentLimit = KinematicsConstants.End_Effector_Wrist_Current_Limit;
        endEffectorWristLimitConfigs.StatorCurrentLimitEnable = true;

        //Applies Configs
        End_Effector_Wrist_Master_Motor.getConfigurator().apply(endEffectorWristMotorConfigs);

        //====================End Effector Rollers====================
        var endEffectorRollersMotorConfigs = new TalonFXConfiguration();

        //Current Limits
        var endEffectorRollerLimitConfigs = endEffectorRollersMotorConfigs.CurrentLimits;
        endEffectorRollerLimitConfigs.StatorCurrentLimit = KinematicsConstants.End_Effector_Roller_Current_Limit;
        endEffectorRollerLimitConfigs.StatorCurrentLimitEnable = true; 
        
        endEffectorRollersMotorConfigs.MotorOutput.NeutralMode = NeutralModeValue.Brake;

        //Applies Configs
        End_Effector_Master_Motor_Roller.getConfigurator().apply(endEffectorRollerLimitConfigs);
        End_Effector_Slave_Motor_Roller.getConfigurator().apply(endEffectorRollerLimitConfigs);
    }

    public void setEndEffectorSetpoint(double setpoint) {
        this.setpoint = setpoint;
    }

    public void goToEndEffectorSetpoint() {
        final MotionMagicVoltage m_request = new MotionMagicVoltage(KinematicsConstants.Absolute_Zero);
        End_Effector_Wrist_Master_Motor.setControl(m_request.withPosition(-1 * this.setpoint));
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("End Effector Wrist Master Encoder", getEndEffectorWristMasterEncoder());
        SmartDashboard.putNumber("End Effector Through Bore Encoder", getEndEffectorWristThroughBoreEncoder());
        SmartDashboard.putBoolean("End Effector Sensor Reading", getEndEffectorSensorReading());
    }
    
    //====================End Effector Wrist Methods====================
    public double getEndEffectorWristMasterEncoder() {
        return End_Effector_Wrist_Master_Motor.getPosition().getValueAsDouble();
    }

    public double getEndEffectorWristThroughBoreEncoder() {
        return End_Effector_Wrist_Through_Bore_Encoder.get();
    }

    //====================End Effector Roller Methods====================
    public void setEndEffectorRollerMotorSpeed(double speed) {
        End_Effector_Master_Motor_Roller.set(-1 * speed);
        End_Effector_Slave_Motor_Roller.set(speed);
    }

    public boolean getEndEffectorSensorReading() {
        return !End_Effector_Sensor.get();
    }
}