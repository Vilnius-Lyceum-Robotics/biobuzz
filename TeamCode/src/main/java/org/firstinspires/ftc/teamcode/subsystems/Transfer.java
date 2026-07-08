package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.LinkedHashMap;

public class Transfer {
    private final int TRANSFER_POWER = 1;
    private final DcMotor MOTOR;
    public Transfer (HardwareMap hardwareMap) {
        MOTOR = hardwareMap.get(DcMotor.class, "transfer");
        MOTOR.setDirection(DcMotor.Direction.REVERSE);
    }

    public boolean isRunning() {
        return MOTOR.getPower() > 0;
    }

    public LinkedHashMap<String, Object> getTelemetry() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("Transfer", isRunning() ? "Running" : "Stopped");
        return result;
    }

    public void start() {
        MOTOR.setPower(TRANSFER_POWER);
    }
    public void stop() {
        MOTOR.setPower(0);
    }
}
