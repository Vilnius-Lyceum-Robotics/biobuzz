package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.LinkedHashMap;

public class Shooter {
    private final DcMotorEx MOTOR_R;
    private final DcMotorEx MOTOR_L;

    private final Servo TRIGGER_R;
    private final Servo TRIGGER_L;

    private int currentSpeedState;
    private boolean triggerState;
    private final double MAX_L_POWER = 0.81; // big balls
    private final double MAX_R_POWER = 0.8; // small balls
    private final int SPEED_STATES_NUM = 5;
    private final double TRIGGER_L_RANGE = 0.33;
    private final double TRIGGER_R_RANGE = 0.36;

    public Shooter (HardwareMap hardwareMap) {
        MOTOR_R = hardwareMap.get(DcMotorEx.class, "shooterRight");
        MOTOR_L = hardwareMap.get(DcMotorEx.class, "shooterLeft");
        MOTOR_R.setDirection(DcMotorEx.Direction.FORWARD);
        MOTOR_L.setDirection(DcMotorEx.Direction.FORWARD);

        TRIGGER_R = hardwareMap.get(Servo.class, "servoRight");
        TRIGGER_L = hardwareMap.get(Servo.class, "servoLeft");
        TRIGGER_R.setDirection(Servo.Direction.FORWARD);
        TRIGGER_L.setDirection(Servo.Direction.REVERSE);
        TRIGGER_R.scaleRange(0.5, 0.8);
        TRIGGER_L.scaleRange(0.2, 0.5);

        closeTriggers();

        currentSpeedState = 0;
    }

    public LinkedHashMap<String, Object> getTelemetry() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("Shooter", String.format("L (%.2f), R (%.2f)", MOTOR_L.getPower(), MOTOR_R.getPower()));
        result.put("Trigger", (triggerState ? "Closed" : "Open"));
        return result;
    }
    private double calculateLeftMotorPower() {
        return MAX_L_POWER * (double) currentSpeedState / SPEED_STATES_NUM;
    }
    private double calculateRightMotorPower() {
        return MAX_R_POWER * (double) currentSpeedState / SPEED_STATES_NUM;
    }
    private void setLeftPower(double power) {
        MOTOR_L.setPower(power);
    }

    private void setRightPower(double power) {
        MOTOR_R.setPower(power);
    }
    public void accelerate() {
        if (currentSpeedState < SPEED_STATES_NUM)
            currentSpeedState++;

        setLeftPower(calculateLeftMotorPower());
        setRightPower(calculateRightMotorPower());
    }

    public void decelerate() {
        if (currentSpeedState > 0)
            currentSpeedState--;

        setLeftPower(calculateLeftMotorPower());
        setRightPower(calculateRightMotorPower());
    }

    public void openTriggers() {
        TRIGGER_R.setPosition(0);
        TRIGGER_L.setPosition(0);
        triggerState = false;
    }

    public void closeTriggers() {
        TRIGGER_R.setPosition(TRIGGER_R_RANGE);
        TRIGGER_L.setPosition(TRIGGER_L_RANGE);
        triggerState = true;
    }

    public void stop() {
        openTriggers();
        currentSpeedState = 0;
        MOTOR_R.setPower(0);
        MOTOR_L.setPower(0);
    }
}