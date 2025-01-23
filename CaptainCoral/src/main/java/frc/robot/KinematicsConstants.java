package frc.robot;

public final class KinematicsConstants {
    //====================General====================
    public static final double absoluteZero = 0.0;
    public static final double jogSpeedMultiplier = -0.125;

    //====================Drivetrain====================
    public static final double drivetrainSpeedMultiplier = 0.3;

    //====================Intake====================
    public static final double Intake_kP = 0.05;
    public static final double Intake_kI = 0.0; 
    public static final double Intake_kD = 0.0; 

    public static final double Intake_Stow_Setpoint = 5.0; //Encoder Units
    public static final double Intake_Deploy_Setpoint = 21.5; //Encoder Units
    
    public static final double intakeSpeed = 0.3; 

    //====================End Effector====================
    public static final double EndEffector_kP = 0.05; 
    public static final double EndEffector_kI = 0.0; 
    public static final double EndEffector_kD = 0.0; 
    
    public static final double End_Effector_Wrist_L1_L2_L3_Setpoint = 0.21;  //Encoder Units
    public static final double End_Effector_Wrist_L4_Setpoint = 0.4;  //Encoder Units

    public static final double scoreSpeed = 0.3; 

    //====================Elevator====================
    public static final double Elevator_kP = 0.05;
    public static final double Elevator_kI = 0.0; 
    public static final double Elevator_kD = 0.0; 

    public static final double Elevator_L1_Setpoint = 1.2; //Encoder Units
    public static final double Elevator_L2_Setpoint = 2.6; //Encoder Units
    public static final double Elevator_L3_Setpoint = 4.7; //Encoder Units
    public static final double Elevator_L4_Setpoint = 6.8; //Encoder Units

    //====================Climb====================
    public static final double Climb_Up_Speed = 0.5;
    public static final double Climb_Down_Speed = -0.5;
}