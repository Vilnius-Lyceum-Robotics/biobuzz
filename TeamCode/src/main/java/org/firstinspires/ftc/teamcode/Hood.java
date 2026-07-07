package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.LinkedHashMap;

public class Hood {
    private final double HOOD_LOWERED_POS = 0.17;
    private final double HOOD_RAISED_POS = 0.62;
    private final Servo SERVO;

    public boolean isRaised = false;

    public Hood(HardwareMap hardwareMap) {
        SERVO = hardwareMap.get(Servo.class, "hood");
    }

    public LinkedHashMap<String, Object> getTelemetry() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("Hood", isRaised ? "Raised" : "Lowered");
        return result;
    }

    public void raise() {
        SERVO.setPosition(HOOD_RAISED_POS);
        isRaised = true;
    }

    public void lower() {
        SERVO.setPosition(HOOD_LOWERED_POS);
        isRaised = false;
    }
}