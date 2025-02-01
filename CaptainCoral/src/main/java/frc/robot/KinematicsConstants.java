package frc.robot;

public final class KinematicsConstants {
    //====================General====================
    public static final double absoluteZero = 0.0;
    public static final double jogSpeedMultiplier = -0.125;

    //====================Drivetrain====================
    public static final double drivetrainSpeedMultiplier = 0.2;

    //====================Intake====================
    public static final double Intake_Stow_Setpoint = 0.0; //Encoder Units
    public static final double Intake_Deploy_Setpoint = 21.5; //Encoder Units
    
    public static final double intakeSpeed = 0.75; 

    //====================End Effector====================
    public static final double End_Effector_Wrist_Rest_Setpoint = 0.02;  //Encoder Units
    public static final double End_Effector_Wrist_Score_Setpoint = 5.8;  //Encoder Units
    public static final double End_Effector_Wrist_L4_Score_Setpoint = 7.5;  //Encoder Units

    public static final double End_Effector_Wrist_Algae_Remove_Setpoint = 19.0;  //Encoder Units
    public static final double End_Effector_Wrist_Gullet_Setpoint = 1.2;  //Encoder Units

    public static final double scoreSpeed = -0.7; 
    public static final double deScoreSpeed = 0.7; 

    //====================Elevator====================
    public static final double Elevator_Rest_Setpoint = 0.0;
    public static final double Elevator_L1_Setpoint = 2.5;
    public static final double Elevator_L2_Setpoint = 8.0;
    public static final double Elevator_L3_Setpoint = 14.0;
    public static final double Elevator_L4_Setpoint = 26.5;

    public static final double Elevator_Bottom_Algae_Setpoint = 15.0;
    public static final double Elevator_Gullet_Setpoint = 6.0;
    public static final double Elevator_Coral_Ground_Intake_Setpoint = 0.0;

    //====================Climb====================
    public static final double climbUpSpeed = 0.75;
    public static final double climbDownSpeed = -0.75;
}