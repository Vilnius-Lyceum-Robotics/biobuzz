package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.LinkedHashMap;

public class Intake {
    private final int INTAKE_POWER_IN = 1;
    private final int INTAKE_POWER_OUT = -1;
    private final DcMotor MOTOR;

    public Intake (HardwareMap hardwareMap) {
        MOTOR = hardwareMap.get(DcMotor.class, "intake");
        MOTOR.setDirection(DcMotor.Direction.FORWARD);
    }

    public int getRunningMode() {
        double motorPower = MOTOR.getPower();
        return (int) ((motorPower >= 0) ? Math.ceil(motorPower) : Math.floor(motorPower));
    }

    public LinkedHashMap<String, Object> getTelemetry() {
        String runningModeLiteral;
        switch (getRunningMode()){
            case -1:
                runningModeLiteral = "Out";
                break;
            case 0:
                runningModeLiteral = "Stopped";
                break;
            case 1:
                runningModeLiteral = "In";
                break;
            default:
                runningModeLiteral = "Invalid running mode";
        }
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("Intake", runningModeLiteral);
        return result;
    }

    public void setRunningMode(int runningMode) {
        if (runningMode == 1)
            MOTOR.setPower(INTAKE_POWER_IN);
        else if (runningMode == -1) {
            MOTOR.setPower(INTAKE_POWER_OUT);
        } else {
            throw new RuntimeException(String.format("Invalid intake running mode: %d", runningMode));
        }
    }
    public void stop() {
        MOTOR.setPower(0);
    }
}