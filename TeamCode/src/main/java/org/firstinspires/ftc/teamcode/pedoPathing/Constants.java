package org.firstinspires.ftc.teamcode.pedoPathing;

import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {
    public static FollowerConstants followerConstants = new FollowerConstants()
            .mass(6.875)
            .forwardZeroPowerAcceleration(-27.115125737098365)
            .lateralZeroPowerAcceleration(-52.56350791702381)
            .translationalPIDFCoefficients(new PIDFCoefficients(0.03, 0, 0.001, 0.03))
//            .translationalPIDFCoefficients(new PIDFCoefficients(0.04, 0, 0.01, 0.02))
            .headingPIDFCoefficients(new PIDFCoefficients(0.7, 0, 0.03, 0.08))
            .drivePIDFCoefficients(new FilteredPIDFCoefficients(0.6, 0, 0.0001, 0.6, 0.025))
            .centripetalScaling(0.0002);
//            .headingPIDFCoefficients(new PIDFCoefficients(0.03, 0.03, 0.01, 0.2));

    public static MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(1)
            .rightFrontMotorName("rightFront")
            .rightRearMotorName("rightRear")
            .leftRearMotorName("leftRear")
            .leftFrontMotorName("leftFront")
            .leftFrontMotorDirection(DcMotor.Direction.FORWARD)
            .leftRearMotorDirection(DcMotor.Direction.FORWARD)
            .rightFrontMotorDirection(DcMotor.Direction.REVERSE)
            .rightRearMotorDirection(DcMotor.Direction.REVERSE)
            .xVelocity(90.89628252645177)
            .yVelocity(46.479119248277556);

    public static PinpointConstants localizerConstants = new PinpointConstants()
            .forwardPodY(123)
            .strafePodX(0)
            .distanceUnit(DistanceUnit.MM)
            .hardwareMapName("pinpoint")
            .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
            .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.REVERSED)
            .strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD);

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1.5, 1);

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .pinpointLocalizer(localizerConstants)
                .pathConstraints(pathConstraints)
                .mecanumDrivetrain(driveConstants)
                .build();
    }
}
