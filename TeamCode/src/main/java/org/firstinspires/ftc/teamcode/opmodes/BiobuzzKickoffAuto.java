package org.firstinspires.ftc.teamcode.opmodes; // make sure this aligns with class location

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import  com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.pedoPathing.Constants;

@Autonomous(name = "BiobuzzKickoffAuto", group = "Linear OpMode")
public class BiobuzzKickoffAuto extends OpMode {

//    private Follower follower;
//    private Timer pathTimer, actionTimer, opmodeTimer;
//
//    private int pathState;
//    private final Pose startPose = new Pose(0, 0, Math.toRadians(180));
//    private final Pose endPose = new Pose(72, 72, Math.toRadians(180));
//
//    private PathChain goChain;
//
//    public void buildPaths() {
//        /* This is our scorePreload path. We are using a BezierLine, which is a straight line. */
//
//        /* This is our grabPickup1 PathChain. We are using a single path with a BezierLine, which is a straight line. */
//        goChain = follower.pathBuilder()
//                .addPath(new BezierLine(startPose, endPose))
//                .setLinearHeadingInterpolation(startPose.getHeading(), endPose.getHeading())
//                .build();
//    }
//
//    public void autonomousPathUpdate() {
//        switch (pathState) {
//            case 0:
//
//            /* You could check for
//            - Follower State: "if(!follower.isBusy()) {}"
//            - Time: "if(pathTimer.getElapsedTimeSeconds() > 1) {}"
//            - Robot Position: "if(follower.getPose().getX() > 36) {}"
//            */
//
//                /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the scorePose's position */
//                if (!follower.isBusy()) {
//                    /* Score Preload */
//
//                    /* Since this is a pathChain, we can have Pedro hold the end point while we are grabbing the sample */
//                    follower.followPath(goChain, true);
//                }
//                break;
//        }
//    }
//
//    /**
//     * These change the states of the paths and actions. It will also reset the timers of the individual switches
//     **/
//    public void setPathState(int pState) {
//        pathState = pState;
//        pathTimer.resetTimer();
//    }
//
//    /**
//     * This is the main loop of the OpMode, it will run repeatedly after clicking "Play".
//     **/
//    @Override
//    public void loop() {
//
//        // These loop the movements of the robot, these must be called continuously in order to work
//        follower.update();
//        autonomousPathUpdate();
//
//        // Feedback to Driver Hub for debugging
//        telemetry.addData("path state", pathState);
//        telemetry.addData("x", follower.getPose().getX());
//        telemetry.addData("y", follower.getPose().getY());
//        telemetry.addData("heading", follower.getPose().getHeading());
//        telemetry.update();
//    }
//
//    /**
//     * This method is called once at the init of the OpMode.
//     **/
//    @Override
//    public void init() {
//        pathTimer = new Timer();
//        opmodeTimer = new Timer();
//        opmodeTimer.resetTimer();
//
//
//        follower = Constants.createFollower(hardwareMap);
//        buildPaths();
//        follower.setStartingPose(startPose);
//
//    }
//
//    /**
//     * This method is called continuously after Init while waiting for "play".
//     **/
//    @Override
//    public void init_loop() {
//    }
//
//    /**
//     * This method is called once at the start of the OpMode.
//     * It runs all the setup actions, including building paths and starting the path system
//     **/
//    @Override
//    public void start() {
//        opmodeTimer.resetTimer();
//        setPathState(0);
//    }
//
//    /**
//     * We do not use this because everything should automatically disable
//     **/
//    @Override
//    public void stop() {
//    }
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