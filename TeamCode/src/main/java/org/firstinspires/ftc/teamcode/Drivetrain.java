package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Drivetrain {
    private DcMotor motorFL = null;
    private DcMotor motorFR = null;
    private DcMotor motorRL = null;
    private DcMotor motorRR = null;

    private Telemetry telemetry;

    public void init(HardwareMap hardwareMap, Telemetry tele) {
        telemetry = tele;

        motorFL = hardwareMap.get(DcMotor.class, "leftFront");
        motorFR = hardwareMap.get(DcMotor.class, "rightFront");
        motorRL = hardwareMap.get(DcMotor.class, "leftRear");
        motorRR = hardwareMap.get(DcMotor.class, "rightRear");

        motorFL.setDirection(DcMotor.Direction.REVERSE);
        motorFR.setDirection(DcMotor.Direction.FORWARD);
        motorRL.setDirection(DcMotor.Direction.REVERSE);
        motorRR.setDirection(DcMotor.Direction.FORWARD);
    }

    public void drive(double y, double x, double turn) {
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(turn), 1);

        double powerFL = Range.clip((y + x + turn) / denominator, -1.0, 1.0);
        double powerFR = Range.clip((y - x - turn) / denominator, -1.0, 1.0);
        double powerRL = Range.clip((y - x + turn) / denominator, -1.0, 1.0);
        double powerRR = Range.clip((y + x - turn) / denominator, -1.0, 1.0);

        telemetry.addData("F", "L (% .2f), R (% .2f)", powerFL, powerFR);
        telemetry.addData("R", "L (% .2f), R (% .2f)", powerRL, powerRR);

        motorFL.setPower(powerFL);
        motorFR.setPower(powerFR);
        motorRL.setPower(powerRL);
        motorRR.setPower(powerRR);
    }

    public void start() {
//        motorFL.setPower(0.5);
//        motorFR.setPower(0.5);
//        motorRL.setPower(0.5);
//        motorRR.setPower(0.5);
    }

    public void stop() {
        motorFL.setPower(0);
        motorFR.setPower(0);
        motorRL.setPower(0);
        motorRR.setPower(0);
    }
}
