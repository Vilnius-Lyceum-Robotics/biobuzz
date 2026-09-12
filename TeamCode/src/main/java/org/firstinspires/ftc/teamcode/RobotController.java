package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Lift;

import java.util.LinkedHashMap;

public class RobotController {
    private static Drivetrain drivetrain;
    private static Intake intake;
    private static Lift lift;

    private RobotController() {}

    public static void init(HardwareMap hardwareMap) {
        drivetrain = new Drivetrain(hardwareMap);
        intake = new Intake(hardwareMap);
        lift = new Lift(hardwareMap);
        lift.setLiftPosition(0);
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

    public static void toggleLift() {
        lift.toggleLiftPosition();
    }

    public static LinkedHashMap<String, Object> getTelemetry() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.putAll(drivetrain.getTelemetry());
        result.putAll(intake.getTelemetry());
        result.putAll(lift.getTelemetry());
        return result;
    }

    public static void stop() {
        drivetrain.stop();
        intake.stop();
        lift.stop();
    }
}
