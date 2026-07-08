package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.LinkedHashMap;

public class Shooter {
    private final DcMotorEx MOTOR_R;
    private final DcMotorEx MOTOR_L;
    private final int SHOOTER_POWER = 1;
    public Shooter (HardwareMap hardwareMap) {
        MOTOR_R = hardwareMap.get(DcMotorEx.class, "shooterRight");
        MOTOR_L = hardwareMap.get(DcMotorEx.class, "shooterLeft");
        MOTOR_R.setDirection(DcMotorEx.Direction.REVERSE);
        MOTOR_L.setDirection(DcMotorEx.Direction.FORWARD);
    }

    public LinkedHashMap<String, Object> getTelemetry() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("Shooter", String.format("L (%.2f), R (%.2f)", MOTOR_L.getVelocity(), MOTOR_R.getVelocity()));
        return result;
    }

    public void start() {
        MOTOR_R.setPower(SHOOTER_POWER);
        MOTOR_L.setPower(SHOOTER_POWER);
    }
    public void stop() {
        MOTOR_R.setPower(0);
        MOTOR_L.setPower(0);
    }
}
