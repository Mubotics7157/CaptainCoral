package frc.robot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class KinematicsConstants { //FINAL KEYWORD???
    //====================General====================
    public static final double Absolute_Zero = 0.0;
    public static final double Jog_Speed_Multiplier = -0.125;
    public static final double PID_Setpoint_Tolerance = 0.1;

    //====================Drivetrain====================
    public static final double Drivetrain_Speed_Multiplier = 0.1;
    public static final double Drivetrain_Turn_Multiplier = 0.5;

    public static final double Left_Pole_Setpoint = 19.0; //Square Up: 18.5 | Triangle: 10.0
    public static final double Right_Pole_Setpoint = -19.0; //Square Up: -21.0 | Triangle: 20.0
    public static final double Square_Up_Setpoint = 0.0;

    public static final double Drivetrain_FB_kP = 0.015; //0.015
    public static final double Drivetrain_LR_kP = 0.0075; //0.0075

    //====================Intake====================
    public static final double Intake_Stow_Setpoint = 0.0;
    public static final double Intake_Deploy_Setpoint = 8.5;
    
    public static final double Intake_Run_Speed = 0.75;

    //====================End Effector====================
    public static final double End_Effector_Wrist_Zero_Setpoint = 0.1;
    public static final double End_Effector_Wrist_Algae_Remove_Setpoint = 19.0;
    public static final double End_Effector_Wrist_Gullet_Setpoint = 0.02;

    public static final double End_Effector_Wrist_L1_Score_Setpoint = SmartDashboard.getNumber("End_Effector_Wrist_L1_Score_Setpoint", 3.0);
    public static final double End_Effector_Wrist_L2_L3_Score_Setpoint = 4.25;
    public static final double End_Effector_Wrist_L4_Score_Setpoint = 5.5;

    public static final double End_Effector_Wrist_Processor_Score_Setpoint = 19.0;
    public static final double End_Effector_Wrist_Barge_Score_Setpoint = 14.0;
    
    public static final double End_Effector_Wrist_Current_Limit = 80.0;
    public static final double End_Effector_Roller_Current_Limit = 80.0;

    public static final double End_Effector_Score_Speed = -0.4;
    public static final double End_Effector_Tap_Speed = -0.15;

    //====================Elevator====================
    public static final double Elevator_Zero_Setpoint = 0.0;
    public static final double Elevator_Bottom_Algae_Setpoint = 15.0;
    public static final double Elevator_Top_Algae_Setpoint = 20.5;
    public static final double Elevator_Gullet_Setpoint = 6.5;
    public static final double Elevator_Coral_Ground_Intake_Setpoint = 0.0;

    public static final double Elevator_L1_Setpoint = 3.0;
    public static final double Elevator_L2_Setpoint = 8.15;
    public static final double Elevator_L3_Setpoint = 14.8;
    public static final double Elevator_L4_Setpoint = 26.5;

    public static final double Elevator_Current_Limit = 80.0;

    public static final double Elevator_Processor_Score_Setpoint = 0.0;
    public static final double Elevator_Barge_Score_Setpoint = 26.5;

    //====================Climb====================
    public static final double Climb_Current_Limit = 120.0;

    public static final double Climb_Up_Speed = 0.75;
    public static final double Climb_Down_Speed = -0.75;
}