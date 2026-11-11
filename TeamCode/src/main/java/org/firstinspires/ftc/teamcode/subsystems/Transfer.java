package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.LinkedHashMap;

public class Transfer {
    private final int TRANSFER_POWER = 1;
    private final DcMotor MOTOR;

    public Transfer (HardwareMap hardwareMap) {
        MOTOR = hardwareMap.get(DcMotor.class, "transfer");
        MOTOR.setDirection(DcMotor.Direction.FORWARD);
    }

    public int getRunningMode() {
        double motorPower = MOTOR.getPower();
        return (int) ((motorPower >= 0) ? Math.ceil(motorPower) : Math.floor(motorPower));
    }

    public LinkedHashMap<String, Object> getTelemetry() {
        String runningModeLiteral;
        if (getRunningMode() == 1)
            runningModeLiteral = "Running";
        else
            runningModeLiteral = "Stopped";
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("Transfer", runningModeLiteral);
        return result;
    }

    public void start() {
        MOTOR.setPower(TRANSFER_POWER);
    }
    public void stop() {
        MOTOR.setPower(0);
    }
}
