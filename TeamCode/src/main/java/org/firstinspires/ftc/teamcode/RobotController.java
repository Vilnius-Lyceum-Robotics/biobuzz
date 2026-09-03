package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Intake;

import java.util.LinkedHashMap;

public class RobotController {
    private static Drivetrain drivetrain;
    private static Intake intake;

    private RobotController() {}

    public static void init(HardwareMap hardwareMap) {
        drivetrain = new Drivetrain(hardwareMap);
        intake = new Intake(hardwareMap);
    }

    public static void drive(double y, double x, double turn) {
        drivetrain.drive(y, x, turn);
    }

    public static void changeIntakeRunningMode(int runningMode) {
        if (intake.getRunningMode() != runningMode)
            intake.setRunningMode(runningMode);
        else
            intake.stop();
    }

    public static LinkedHashMap<String, Object> getTelemetry() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.putAll(drivetrain.getTelemetry());
        result.putAll(intake.getTelemetry());
        return result;
    }

    public static void stop() {
        drivetrain.stop();
        intake.stop();
    }
}
