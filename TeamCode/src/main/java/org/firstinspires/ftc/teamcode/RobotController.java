package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;

import java.util.LinkedHashMap;

public class RobotController {
    private static Drivetrain drivetrain;

    private RobotController() {}

    public static void init(HardwareMap hardwareMap) {
        drivetrain = new Drivetrain(hardwareMap);
    }

    public static void drive(double y, double x, double turn) {
        drivetrain.drive(y, x, turn);
    }

    public static LinkedHashMap<String, Object> getTelemetry() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.putAll(drivetrain.getTelemetry());
        return result;
    }

    public static void stop() {
        drivetrain.stop();
    }
}
