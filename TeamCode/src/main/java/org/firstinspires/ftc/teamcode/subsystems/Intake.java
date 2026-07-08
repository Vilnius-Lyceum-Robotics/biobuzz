package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.LinkedHashMap;

public class Intake {
    private final int INTAKE_POWER = 1;
    private final DcMotor MOTOR;

    public Intake (HardwareMap hardwareMap) {
        MOTOR = hardwareMap.get(DcMotor.class, "intake");
        MOTOR.setDirection(DcMotor.Direction.FORWARD);
    }

    public boolean isRunning() {
        return MOTOR.getPower() > 0;
    }

    public LinkedHashMap<String, Object> getTelemetry() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("Intake", isRunning() ? "Running" : "Stopped");
        return result;
    }

    public void start() {
        MOTOR.setPower(INTAKE_POWER);
    }
    public void stop() {
        MOTOR.setPower(0);
    }
}
