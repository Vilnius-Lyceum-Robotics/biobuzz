package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Shooter;
import org.firstinspires.ftc.teamcode.subsystems.Transfer;

import java.util.LinkedHashMap;

public class RobotController {
    private static Drivetrain drivetrain;
    private static Intake intake;
    private static Transfer transfer;

    private static Shooter shooter;

    private RobotController() {}

    public static void init(HardwareMap hardwareMap) {
        drivetrain = new Drivetrain(hardwareMap);
        intake = new Intake(hardwareMap);
        transfer = new Transfer(hardwareMap);
        shooter = new Shooter(hardwareMap);
    }

    public static void drive(double y, double x, double turn) {
        drivetrain.drive(y, x, turn);
    }

    public static void changeTransInRunningMode(int runningMode) {
        if (intake.getRunningMode() != runningMode) {
            intake.setRunningMode(runningMode);
            if (runningMode == 1)
                transfer.start();
            else
                transfer.stop();
        }
        else {
            intake.stop();
            transfer.stop();
        }
    }

    public static void accelerateShooter(){
        shooter.accelerate();
    }

    public static void decelerateShooter(){
        shooter.decelerate();
    }

    public static LinkedHashMap<String, Object> getTelemetry() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.putAll(drivetrain.getTelemetry());
        result.putAll(intake.getTelemetry());
        result.putAll(transfer.getTelemetry());
        result.putAll(shooter.getTelemetry());
        return result;
    }

    public static void stop() {
        drivetrain.stop();
        intake.stop();
        transfer.stop();
        shooter.stop();
    }
}
