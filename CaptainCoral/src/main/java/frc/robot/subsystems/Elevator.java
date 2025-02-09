package frc.robot.subsystems;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;

import frc.robot.DeviceConstants;
import frc.robot.KinematicsConstants;

public class Elevator extends SubsystemBase {
    private final TalonFX Elevator_Master_Motor = new TalonFX(DeviceConstants.ELEVATOR_MASTER_MOTOR_DEVICE_ID);
    private final TalonFX Elevator_Slave_Motor = new TalonFX(DeviceConstants.ELEVATOR_SLAVE_MOTOR_DEVICE_ID);

    private double setpoint;

    public static Elevator getInstance() {
        return instance;
    }

    private static Elevator instance = new Elevator();

    public Elevator() {
        //====================Elevator Motion Magic====================
        Elevator_Master_Motor.setPosition(0.0);
        Elevator_Slave_Motor.setPosition(0.0);

        var elevatorMotorConfigs = new TalonFXConfiguration();

        elevatorMotorConfigs.MotorOutput.NeutralMode = NeutralModeValue.Brake;

        var generalSlotConfigs = elevatorMotorConfigs.Slot0;
        generalSlotConfigs.kS = 0.0;
        generalSlotConfigs.kV = 0.12;
        generalSlotConfigs.kA = 0.05;
        generalSlotConfigs.kP = 12.5; //6.0, 17.5
        generalSlotConfigs.kI = 0.0;
        generalSlotConfigs.kD = 0.0; 

        // public static final double Elevator_L1_Setpoint = 3.0;
        // public static final double Elevator_L2_Setpoint = 8.15;
        // public static final double Elevator_L3_Setpoint = 14.8;
        // public static final double Elevator_L4_Setpoint = 26.5;

        var motionMagicConfigs = elevatorMotorConfigs.MotionMagic;
        motionMagicConfigs.MotionMagicCruiseVelocity = 20; //16, 32
        motionMagicConfigs.MotionMagicAcceleration = 40; //32, 64
        motionMagicConfigs.MotionMagicJerk = 80; //64, 128

        Elevator_Master_Motor.getConfigurator().apply(elevatorMotorConfigs);
        Elevator_Slave_Motor.getConfigurator().apply(elevatorMotorConfigs);

        //====================Elevator Master Current Limit====================
        var elevatorMasterConfigurator = Elevator_Master_Motor.getConfigurator();
        var elevatorMasterLimitConfigs = new CurrentLimitsConfigs();

        elevatorMasterLimitConfigs.StatorCurrentLimit = 120;
        elevatorMasterLimitConfigs.StatorCurrentLimitEnable = true;
        elevatorMasterConfigurator.apply(elevatorMasterLimitConfigs);

        //====================Elevator Slave Current Limit====================
        var elevatorSlaveConfigurator = Elevator_Slave_Motor.getConfigurator();
        var elevatorSlaveLimitConfigs = new CurrentLimitsConfigs();

        elevatorSlaveLimitConfigs.StatorCurrentLimit = 120;
        elevatorSlaveLimitConfigs.StatorCurrentLimitEnable = true;
        elevatorSlaveConfigurator.apply(elevatorSlaveLimitConfigs);
    }

    public void setElevatorSetpoint(double setpoint) {
        this.setpoint = setpoint;
    }

    public void goToElevatorSetpoint() {
        final MotionMagicVoltage m_request = new MotionMagicVoltage(KinematicsConstants.absoluteZero);
        Elevator_Master_Motor.setControl(m_request.withPosition(this.setpoint));
        Elevator_Slave_Motor.setControl(m_request.withPosition(this.setpoint));
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Elevator Master Encoder", getElevatorMasterEncoder());
        SmartDashboard.putNumber("Elevator Slave Encoder", getElevatorSlaveEncoder());
    }

    //====================Elevator Methods====================
    public double getElevatorMasterEncoder() {
        return Elevator_Master_Motor.getPosition().getValueAsDouble();
    }

    public double getElevatorSlaveEncoder() {
        return Elevator_Slave_Motor.getPosition().getValueAsDouble();
    }

    public void setElevatorMotorSpeed(double speed) {
        Elevator_Master_Motor.set(speed);
        Elevator_Slave_Motor.set(speed);
    }
}