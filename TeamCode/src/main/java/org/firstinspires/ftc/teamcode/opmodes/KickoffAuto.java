package org.firstinspires.ftc.teamcode.opmodes;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.pedoPathing.Constants;

@Autonomous(name = "Kickoff Auto", preselectTeleOp = "BiobuzzTeleOp")
public class KickoffAuto extends OpMode {
    private Follower follower;

    private final Pose startPose = new Pose(0, 0, Math.toRadians(0));
    private final Pose targetPose = new Pose(51, -53, Math.toRadians(0));

    private PathChain path;

    @Override
    public void init() {
        // Create Pedro follower
        follower = Constants.createFollower(hardwareMap);

        // Tell Pedro where the robot physically starts
        follower.setStartingPose(startPose);

        // Create a straight-line path
        path = follower.pathBuilder()
                .addPath(new BezierLine(startPose, targetPose))
                .setConstantHeadingInterpolation(
                        startPose.getHeading()
                )
                .build();
    }

    @Override
    public void start() {
        // Start following the path
        follower.followPath(path);
    }

    @Override
    public void loop() {
        // This MUST be called continuously
        follower.update();

        telemetry.addData("X", follower.getPose().getX());
        telemetry.addData("Y", follower.getPose().getY());
        telemetry.addData(
                "Heading",
                Math.toDegrees(follower.getPose().getHeading())
        );
        telemetry.addData("Busy", follower.isBusy());
        telemetry.update();
    }
}
