package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.LinkedHashMap;

public class Drivetrain {
    private final DcMotor MOTOR_FL;
    private final DcMotor MOTOR_FR;
    private final DcMotor MOTOR_RL;
    private final DcMotor MOTOR_RR;

    public Drivetrain (HardwareMap hardwareMap) {
        MOTOR_FL = hardwareMap.get(DcMotor.class, "leftFront");
        MOTOR_FR = hardwareMap.get(DcMotor.class, "rightFront");
        MOTOR_RL = hardwareMap.get(DcMotor.class, "leftRear");
        MOTOR_RR = hardwareMap.get(DcMotor.class, "rightRear");

        MOTOR_FL.setDirection(DcMotor.Direction.REVERSE);
        MOTOR_FR.setDirection(DcMotor.Direction.FORWARD);
        MOTOR_RL.setDirection(DcMotor.Direction.REVERSE);
        MOTOR_RR.setDirection(DcMotor.Direction.FORWARD);
    }
    public LinkedHashMap<String, Object> getTelemetry() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("F", String.format("L (%.2f), R (%.2f)", MOTOR_FL.getPower(), MOTOR_FR.getPower()));
        result.put("R", String.format("L (%.2f), R (%.2f)", MOTOR_RL.getPower(), MOTOR_RR.getPower()));
        return result;
    }
    public void drive(double y, double x, double turn) {
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(turn), 1);

        double powerFL = (y + x + turn) / denominator;
        double powerFR = (y - x - turn) / denominator;
        double powerRL = (y - x + turn) / denominator;
        double powerRR = (y + x - turn) / denominator;

        MOTOR_FL.setPower(powerFL);
        MOTOR_FR.setPower(powerFR);
        MOTOR_RL.setPower(powerRL);
        MOTOR_RR.setPower(powerRR);
    }

    public void stop() {
        MOTOR_FL.setPower(0);
        MOTOR_FR.setPower(0);
        MOTOR_RL.setPower(0);
        MOTOR_RR.setPower(0);
    }
}
