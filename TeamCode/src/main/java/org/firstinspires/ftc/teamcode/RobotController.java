package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.LinkedHashMap;

public class RobotController {
    private static Drivetrain drivetrain;
    private static Shooter shooter;
    private static Intake intake;
    private static Transfer transfer;
    private static Lift lift;
    private static Hood hood;

    private RobotController() {}

    public static void init(HardwareMap hardwareMap) {
        drivetrain = new Drivetrain(hardwareMap);
        intake = new Intake(hardwareMap);
        transfer = new Transfer(hardwareMap);
        lift = new Lift(hardwareMap);
        shooter = new Shooter(hardwareMap);
        hood = new Hood(hardwareMap);
    }

    public static void drive(double y, double x, double turn) {
        drivetrain.drive(y, x, turn);
    }

    public static void toggleIntake() {
        if (intake.isRunning()) {
            intake.stop();
            transfer.stop();
        } else {
            lift.close();
            intake.start();
            transfer.start();
        }
    }
    public static void toggleShoot() {
        if (lift.isOpen) {
            lift.close();
            transfer.stop();
        } else {
            lift.open();
            transfer.start();
        }
    }

    public static void setShooterPower(double power) {
        shooter.setPower(power);
    }

    public static void raiseHood() {
        hood.raise();
    }
    public static void lowerHood() {
        hood.lower();
    }

    public static LinkedHashMap<String, Object> getTelemetry() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.putAll(drivetrain.getTelemetry());
        result.putAll(intake.getTelemetry());
        result.putAll(transfer.getTelemetry());
        result.putAll(lift.getTelemetry());
        result.putAll(shooter.getTelemetry());
        result.putAll(hood.getTelemetry());

        return result;
    }

    public static void stop() {
        drivetrain.stop();
        intake.stop();
        transfer.stop();
        shooter.stop();
    }
}
