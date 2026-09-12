package org.firstinspires.ftc.teamcode.subsystems;

import static android.os.SystemClock.sleep;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import java.util.LinkedHashMap;

public class Lift {

    private final double SERVO_POSITION_UP = 0;
    private final double SERVO_POSITION_DOWN = 1;
    private final Servo SERVO;

    private int liftPos = 0;

    public Lift (HardwareMap hardwareMap) {
        SERVO = hardwareMap.get(Servo.class, "lift");
        SERVO.setDirection(Servo.Direction.FORWARD);
        SERVO.scaleRange((270-100)/270.0, 1);
    }

    public LinkedHashMap<String, Object> getTelemetry() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("Servo", getLiftPosition());
        return result;
    }

    public double getLiftPosition() {
        return SERVO.getPosition();
    }

    public void toggleLiftPosition() {
        if (liftPos == 0) {
            setLiftPosition(1);
        } else {
            setLiftPosition(0);
        }
    }

    public void setLiftPosition(int position) {
        if (position == 0) {
            liftPos = 0;
            ((ServoImplEx) SERVO).setPwmEnable();
            SERVO.setPosition(SERVO_POSITION_UP);
        } else if (position == 1) {
            liftPos = 1;
            SERVO.setPosition(SERVO_POSITION_DOWN);
            sleep(400);
            ((ServoImplEx) SERVO).setPwmDisable();
        } else {
            throw new RuntimeException(String.format("Invalid lift position: %d", position));
        }
    }

    public void stop() {
        // do nothing
    }
}