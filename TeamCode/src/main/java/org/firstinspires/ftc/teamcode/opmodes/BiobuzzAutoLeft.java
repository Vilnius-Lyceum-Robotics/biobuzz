package org.firstinspires.ftc.teamcode.opmodes;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.RobotController;
import org.firstinspires.ftc.teamcode.pedoPathing.Constants;

@Autonomous(name = "BiobuzzAutoLeft", preselectTeleOp = "BiobuzzTeleOp")
public class BiobuzzAutoLeft extends OpMode {
    private Follower follower;

    private final Pose startPose = new Pose(0, 0, Math.toRadians(0));
    private final Pose targetPose = new Pose(9, -75, Math.toRadians(0));

    private final ElapsedTime timer = new ElapsedTime();

    private enum AutoState {
        DRIVE_TO_POS,
        SWITCH_ON_ENGINES,
        SWITCH_ON_INTAKE,
        SHOOT,
        DONE
    }

    private AutoState state = AutoState.DRIVE_TO_POS;

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

        RobotController.init(hardwareMap);
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
        telemetry.addData("State", state.name());
        telemetry.update();

        switch (state) {
            case DRIVE_TO_POS:
                if (!follower.isBusy()) {
                    for (int i = 0; i < 20; i++) RobotController.accelerateShooter();
                    state = AutoState.SWITCH_ON_ENGINES;
                    timer.reset();
                }
                break;
            case SWITCH_ON_ENGINES:
                if (timer.seconds() >= 2) {
                    RobotController.changeTransInRunningMode(1);
                    state = AutoState.SWITCH_ON_INTAKE;
                    timer.reset();
                }
                break;
            case SWITCH_ON_INTAKE:
                if (timer.seconds() >= 1) {
                    RobotController.openShooter();
                    state = AutoState.SHOOT;
                    timer.reset();
                }
                break;
            case SHOOT:
                if (timer.seconds() >= 5) {
                    RobotController.closeShooter();
                    RobotController.changeTransInRunningMode(1);
                    for (int i = 0; i < 20; i++) RobotController.decelerateShooter();
                    state = AutoState.DONE;
                    timer.reset();
                }
        }
    }
}
