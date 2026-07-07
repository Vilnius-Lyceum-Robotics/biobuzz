package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Shooter {
    private DcMotor motorIntake = null;
    private DcMotor motorTransfer = null;
    private DcMotor motorShooterL = null;
    private DcMotor motorShooterR = null;
    private Servo servoLift = null;

    private Telemetry telemetry;
    public void init(HardwareMap hardwareMap, Telemetry tele) {
        telemetry = tele;

        motorIntake = hardwareMap.get(DcMotor.class, "intake");
        motorTransfer = hardwareMap.get(DcMotor.class, "transfer");
        motorShooterL = hardwareMap.get(DcMotor.class, "shooterLeft");
        motorShooterR = hardwareMap.get(DcMotor.class, "shooterRight");
        servoLift = hardwareMap.get(Servo.class, "lift");

        motorIntake.setDirection(DcMotor.Direction.FORWARD);
        motorTransfer.setDirection(DcMotor.Direction.REVERSE);
        motorShooterL.setDirection(DcMotor.Direction.FORWARD);
        motorShooterR.setDirection(DcMotor.Direction.REVERSE);
    }

    public void intakePower(double power) {
        telemetry.addData("Intake", "% .2f", power);
        motorIntake.setPower(power);
    }

    public void transferPower(double power) {
        telemetry.addData("Transfer", "% .2f", power);
        motorTransfer.setPower(power);
    }

    public void shooterPower(double power) {
        telemetry.addData("Shooter", "% .2f", power);
        motorShooterL.setPower(power);
        motorShooterR.setPower(power);
    }

    public void stop() {
        motorIntake.setPower(0);
        motorTransfer.setPower(0);
        motorShooterL.setPower(0);
        motorShooterR.setPower(0);
    }
}
