package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.LinkedHashMap;

public class Shooter {
    private final DcMotorEx MOTOR_R;
    private final DcMotorEx MOTOR_L;
    private int currentSpeedState;
    private final int MAX_POWER = 1;
    private final int SPEED_STATES_NUM = 3;
    public Shooter (HardwareMap hardwareMap) {
        MOTOR_R = hardwareMap.get(DcMotorEx.class, "shooterRight");
        MOTOR_L = hardwareMap.get(DcMotorEx.class, "shooterLeft");
        MOTOR_R.setDirection(DcMotorEx.Direction.REVERSE);
        MOTOR_L.setDirection(DcMotorEx.Direction.FORWARD);

        currentSpeedState = 0;
    }

    public LinkedHashMap<String, Object> getTelemetry() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("Shooter", String.format("%.2f", MOTOR_L.getVelocity())); // MOTOR_R does not have an encoder
        return result;
    }
    private double calculateMotorPower() {
        return MAX_POWER * ((double) currentSpeedState / SPEED_STATES_NUM);
    }
    private void setPower(double power) {
        MOTOR_R.setPower(power);
        MOTOR_L.setPower(power);
    }
    public void accelerate() {
        if (currentSpeedState < SPEED_STATES_NUM)
            currentSpeedState++;

        double power = calculateMotorPower();
        setPower(power);
    }

    public void decelerate() {
        if (currentSpeedState > 0)
            currentSpeedState--;

        double power = calculateMotorPower();
        setPower(power);
    }
    public void stop() {
        MOTOR_R.setPower(0);
        MOTOR_L.setPower(0);
    }
}