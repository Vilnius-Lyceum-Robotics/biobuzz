package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.LinkedHashMap;

public class Lift {
    private final double LIFT_OPEN_POS = 0.2;
    private final double LIFT_CLOSED_POS = 0.35; // ???
    private final Servo SERVO;

    public boolean isOpen = false;

    public Lift (HardwareMap hardwareMap) {
        SERVO = hardwareMap.get(Servo.class, "lift");
    }

    public LinkedHashMap<String, Object> getTelemetry() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("Lift", isOpen ? "Open" : "Closed");
        return result;
    }
    public void open() {
        SERVO.setPosition(LIFT_OPEN_POS);
        isOpen = true;
    }

    public void close() {
        SERVO.setPosition(LIFT_CLOSED_POS);
        isOpen = false;
    }
}