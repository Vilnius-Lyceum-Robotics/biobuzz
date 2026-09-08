package org.firstinspires.ftc.teamcode.opmodes;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.math.Vector;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.teamcode.RobotController;
import org.firstinspires.ftc.teamcode.pedoPathing.Constants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This is the Autotune OpMode. It automatically runs, back to back and with no driver input
 * required (besides pressing INIT and PLAY, and optionally B to abort), the same tuning
 * sequence that used to require manually selecting each entry from the Tuning menu:
 *
 *   1. Determines forwardEncoderDirection / strafeEncoderDirection (LocalizationTest logic).
 *   2. Runs the ForwardVelocityTuner logic and records xVelocity.
 *   3. Runs the LateralVelocityTuner logic and records yVelocity.
 *   4. Runs the ForwardZeroPowerAccelerationTuner logic and records forwardZeroPowerAcceleration.
 *   5. Runs the LateralZeroPowerAccelerationTuner logic and records lateralZeroPowerAcceleration.
 *
 * Before step 1, it (re)writes Constants.java from a clean baseline template matching the
 * project's existing Constants.java. After every step, it regenerates Constants.java with the
 * newly discovered value(s) baked in, so the file always reflects the latest known-good state.
 *
 * This class is intentionally self-contained and does NOT depend on Tuning.java (no shared
 * static follower/telemetry/drawing helpers), since Tuning.java is meant to be removed once
 * Autotune replaces it.
 *
 * IMPORTANT ASSUMPTIONS - please verify these against your project before running on a robot:
 *   - RobotController.drive(double forward, double strafe, double turn) is a static method.
 *     If RobotController.drive is actually an instance method, instantiate it (e.g.
 *     `new RobotController(hardwareMap)`) in init() and call it on that instance instead.
 *   - GoBildaPinpointDriver.EncoderDirection has values FORWARD and REVERSED.
 *   - Constants.java lives under the OnBotJava source root (AppUtil.FIRST_FOLDER + "/src/...").
 *     If you're building with Android Studio instead of OnBotJava, runtime code generally can't
 *     write back into the compiled source tree - point CONSTANTS_FILE at wherever your build
 *     process can pick the file up (e.g. an external storage path you pull with adb), or just
 *     read the discovered values off telemetry/logcat and hand-copy them.
 *   - The physical thresholds below (ENCODER_TEST_DISPLACEMENT_IN, timeouts, tuner distances,
 *     target velocities) match the defaults used in the original Tuning.java tuners. Adjust to
 *     fit your field space / robot as needed.
 */
@TeleOp(name = "Autotune", group = "Pedro Pathing")
public class Autotune extends OpMode {

    // ---- Tunable run parameters (match the original Tuning.java tuner defaults) ----
    private static final double ENCODER_TEST_DISPLACEMENT_IN = 6.0;   // how far to move before reading direction
    private static final double ENCODER_TEST_TIMEOUT_S = 3.0;         // safety timeout per encoder-direction test
    private static final double VELOCITY_TUNE_DISTANCE = 48;          // inches, matches ForwardVelocityTuner/LateralVelocityTuner
    private static final int RECORD_NUMBER = 10;                      // matches ForwardVelocityTuner/LateralVelocityTuner
    private static final double ZPA_TARGET_VELOCITY = 30;             // in/s, matches Forward/LateralZeroPowerAccelerationTuner

    // ---- Where Constants.java lives (OnBotJava source root assumption - see class javadoc) ----
    private static final File CONSTANTS_FILE = new File(
            AppUtil.FIRST_FOLDER,
            "src/org/firstinspires/ftc/teamcode/pedoPathing/Constants.java"
    );

    private enum State {
        WRITE_INITIAL_CONSTANTS,
        FORWARD_ENCODER_TEST,
        STRAFE_ENCODER_TEST,
        REBUILD_FOLLOWER_AFTER_ENCODER_TEST,
        FORWARD_VELOCITY_INIT,
        FORWARD_VELOCITY_RUN,
        LATERAL_VELOCITY_INIT,
        LATERAL_VELOCITY_RUN,
        FORWARD_ZPA_INIT,
        FORWARD_ZPA_ACCELERATE,
        FORWARD_ZPA_DECELERATE,
        LATERAL_ZPA_INIT,
        LATERAL_ZPA_ACCELERATE,
        LATERAL_ZPA_DECELERATE,
        DONE
    }

    private Follower follower;
    private TelemetryManager telemetryM;
    private final ElapsedTime stateTimer = new ElapsedTime();

    private State state = State.WRITE_INITIAL_CONSTANTS;

    // ---- Discovered results, baked into Constants.java as they're found ----
    private String forwardEncoderDirection = "FORWARD";
    private String strafeEncoderDirection = "FORWARD";
    private Double xVelocity = null;
    private Double yVelocity = null;
    private Double forwardZeroPowerAcceleration = null;
    private Double lateralZeroPowerAcceleration = null;

    // ---- Working variables, reused across phases ----
    private double phaseStartX;
    private double phaseStartY;
    private final ArrayList<Double> velocitySamples = new ArrayList<>();
    private final ArrayList<Double> accelerationSamples = new ArrayList<>();
    private double zpaPreviousVelocity;
    private long zpaPreviousTimeNano;

    @Override
    public void init() {
        RobotController.init(hardwareMap);
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(72, 72));
        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();
        state = State.WRITE_INITIAL_CONSTANTS;
    }

    @Override
    public void init_loop() {
        telemetryM.debug("Autotune will automatically run, in order:");
        telemetryM.debug("1) Forward/strafe encoder direction test");
        telemetryM.debug("2) Forward Velocity Tuner");
        telemetryM.debug("3) Lateral Velocity Tuner");
        telemetryM.debug("4) Forward Zero Power Acceleration Tuner");
        telemetryM.debug("5) Lateral Zero Power Acceleration Tuner");
        telemetryM.debug("Make sure you have several feet of clear space in every direction.");
        telemetryM.debug("Press B on gamepad 1 at any time to abort.");
        telemetryM.update(telemetry);
    }

    @Override
    public void start() {
        stateTimer.reset();
    }

    @Override
    public void loop() {
        if (gamepad1.bWasPressed()) {
            RobotController.drive(0, 0, 0);
            stopRobot();
            telemetryM.debug("Autotune aborted by driver.");
            telemetryM.update(telemetry);
            requestOpModeStop();
            return;
        }

        follower.update();

        switch (state) {
            case WRITE_INITIAL_CONSTANTS:
                writeConstantsFile();
                telemetryM.debug("Wrote baseline Constants.java. Starting forward encoder direction test.");
                phaseStartX = follower.getPose().getX();
                RobotController.drive(1, 0, 0);
                stateTimer.reset();
                state = State.FORWARD_ENCODER_TEST;
                break;

            case FORWARD_ENCODER_TEST: {
                double dx = follower.getPose().getX() - phaseStartX;
                telemetryM.debug("Forward encoder test dx: " + dx);
                if (Math.abs(dx) > ENCODER_TEST_DISPLACEMENT_IN || stateTimer.seconds() > ENCODER_TEST_TIMEOUT_S) {
                    RobotController.drive(0, 0, 0);
                    forwardEncoderDirection = dx > 0 ? "FORWARD" : "REVERSED";
                    telemetryM.debug("Forward encoder direction: " + forwardEncoderDirection);
                    writeConstantsFile();

                    phaseStartY = follower.getPose().getY();
                    RobotController.drive(0, 1, 0);
                    stateTimer.reset();
                    state = State.STRAFE_ENCODER_TEST;
                }
                break;
            }

            case STRAFE_ENCODER_TEST: {
                double dy = follower.getPose().getY() - phaseStartY;
                telemetryM.debug("Strafe encoder test dy: " + dy);
                if (Math.abs(dy) > ENCODER_TEST_DISPLACEMENT_IN || stateTimer.seconds() > ENCODER_TEST_TIMEOUT_S) {
                    RobotController.drive(0, 0, 0);
                    strafeEncoderDirection = dy > 0 ? "FORWARD" : "REVERSED";
                    telemetryM.debug("Strafe encoder direction: " + strafeEncoderDirection);
                    writeConstantsFile();
                    state = State.REBUILD_FOLLOWER_AFTER_ENCODER_TEST;
                }
                break;
            }

            case REBUILD_FOLLOWER_AFTER_ENCODER_TEST:
                // Apply the discovered encoder directions to the in-memory constants and rebuild
                // the follower so the rest of the tuning run uses correct localization.
                Constants.localizerConstants
                        .forwardEncoderDirection(directionFor(forwardEncoderDirection))
                        .strafeEncoderDirection(directionFor(strafeEncoderDirection));
                follower = Constants.createFollower(hardwareMap);
                follower.setStartingPose(new Pose(72, 72));
                telemetryM.debug("Follower rebuilt with corrected encoder directions.");
                state = State.FORWARD_VELOCITY_INIT;
                break;

            case FORWARD_VELOCITY_INIT:
                resetSamples(velocitySamples);
                follower.setStartingPose(new Pose(72, 72));
                follower.startTeleopDrive(true);
                follower.update();
                telemetryM.debug("Running Forward Velocity Tuner...");
                state = State.FORWARD_VELOCITY_RUN;
                break;

            case FORWARD_VELOCITY_RUN:
                if (Math.abs(follower.getPose().getX()) > (VELOCITY_TUNE_DISTANCE + 72)) {
                    stopRobot();
                    xVelocity = average(velocitySamples);
                    follower.setXVelocity(xVelocity);
                    telemetryM.debug("Forward Velocity (xVelocity): " + xVelocity);
                    writeConstantsFile();
                    state = State.LATERAL_VELOCITY_INIT;
                } else {
                    follower.setTeleOpDrive(1, 0, 0, true);
                    double currentVelocity = Math.abs(follower.poseTracker.getLocalizer().getVelocity().getX());
                    velocitySamples.add(currentVelocity);
                    velocitySamples.remove(0);
                }
                break;

            case LATERAL_VELOCITY_INIT:
                resetSamples(velocitySamples);
                follower.setStartingPose(new Pose(72, 72));
                follower.startTeleopDrive(true);
                follower.update();
                telemetryM.debug("Running Lateral Velocity Tuner...");
                state = State.LATERAL_VELOCITY_RUN;
                break;

            case LATERAL_VELOCITY_RUN:
                if (Math.abs(follower.getPose().getY()) > (VELOCITY_TUNE_DISTANCE + 72)) {
                    stopRobot();
                    yVelocity = average(velocitySamples);
                    follower.setYVelocity(yVelocity);
                    telemetryM.debug("Lateral Velocity (yVelocity): " + yVelocity);
                    writeConstantsFile();
                    state = State.FORWARD_ZPA_INIT;
                } else {
                    follower.setTeleOpDrive(0, 1, 0, true);
                    double currentVelocity = Math.abs(follower.getVelocity().dot(new Vector(1, Math.PI / 2)));
                    velocitySamples.add(currentVelocity);
                    velocitySamples.remove(0);
                }
                break;

            case FORWARD_ZPA_INIT:
                accelerationSamples.clear();
                follower.setStartingPose(new Pose(72, 72));
                follower.startTeleopDrive(false);
                follower.update();
                follower.setTeleOpDrive(1, 0, 0, true);
                telemetryM.debug("Running Forward Zero Power Acceleration Tuner...");
                state = State.FORWARD_ZPA_ACCELERATE;
                break;

            case FORWARD_ZPA_ACCELERATE: {
                Vector heading = new Vector(1.0, follower.getPose().getHeading());
                if (follower.getVelocity().dot(heading) > ZPA_TARGET_VELOCITY) {
                    zpaPreviousVelocity = follower.getVelocity().dot(heading);
                    zpaPreviousTimeNano = System.nanoTime();
                    follower.setTeleOpDrive(0, 0, 0, true);
                    state = State.FORWARD_ZPA_DECELERATE;
                }
                break;
            }

            case FORWARD_ZPA_DECELERATE: {
                Vector heading = new Vector(1.0, follower.getPose().getHeading());
                double currentVelocity = follower.getVelocity().dot(heading);
                accelerationSamples.add((currentVelocity - zpaPreviousVelocity)
                        / ((System.nanoTime() - zpaPreviousTimeNano) / Math.pow(10.0, 9)));
                zpaPreviousVelocity = currentVelocity;
                zpaPreviousTimeNano = System.nanoTime();
                if (currentVelocity < follower.getConstraints().getVelocityConstraint()) {
                    forwardZeroPowerAcceleration = average(accelerationSamples);
                    follower.getConstants().setForwardZeroPowerAcceleration(forwardZeroPowerAcceleration);
                    telemetryM.debug("Forward Zero Power Acceleration: " + forwardZeroPowerAcceleration);
                    writeConstantsFile();
                    state = State.LATERAL_ZPA_INIT;
                }
                break;
            }

            case LATERAL_ZPA_INIT:
                accelerationSamples.clear();
                follower.setStartingPose(new Pose(72, 72));
                follower.startTeleopDrive(false);
                follower.update();
                follower.setTeleOpDrive(0, 1, 0, true);
                telemetryM.debug("Running Lateral Zero Power Acceleration Tuner...");
                state = State.LATERAL_ZPA_ACCELERATE;
                break;

            case LATERAL_ZPA_ACCELERATE: {
                Vector heading = new Vector(1.0, follower.getPose().getHeading() - Math.PI / 2);
                if (Math.abs(follower.getVelocity().dot(heading)) > ZPA_TARGET_VELOCITY) {
                    zpaPreviousVelocity = Math.abs(follower.getVelocity().dot(heading));
                    zpaPreviousTimeNano = System.nanoTime();
                    follower.setTeleOpDrive(0, 0, 0, true);
                    state = State.LATERAL_ZPA_DECELERATE;
                }
                break;
            }

            case LATERAL_ZPA_DECELERATE: {
                Vector heading = new Vector(1.0, follower.getPose().getHeading() - Math.PI / 2);
                double currentVelocity = Math.abs(follower.getVelocity().dot(heading));
                accelerationSamples.add((currentVelocity - zpaPreviousVelocity)
                        / ((System.nanoTime() - zpaPreviousTimeNano) / Math.pow(10.0, 9)));
                zpaPreviousVelocity = currentVelocity;
                zpaPreviousTimeNano = System.nanoTime();
                if (currentVelocity < follower.getConstraints().getVelocityConstraint()) {
                    lateralZeroPowerAcceleration = average(accelerationSamples);
                    follower.getConstants().setLateralZeroPowerAcceleration(lateralZeroPowerAcceleration);
                    telemetryM.debug("Lateral Zero Power Acceleration: " + lateralZeroPowerAcceleration);
                    writeConstantsFile();
                    state = State.DONE;
                }
                break;
            }

            case DONE:
                stopRobot();
                telemetryM.debug("Autotune complete! Constants.java has been updated with:");
                telemetryM.debug("forwardEncoderDirection: " + forwardEncoderDirection);
                telemetryM.debug("strafeEncoderDirection: " + strafeEncoderDirection);
                telemetryM.debug("xVelocity: " + xVelocity);
                telemetryM.debug("yVelocity: " + yVelocity);
                telemetryM.debug("forwardZeroPowerAcceleration: " + forwardZeroPowerAcceleration);
                telemetryM.debug("lateralZeroPowerAcceleration: " + lateralZeroPowerAcceleration);
                break;
        }

        telemetryM.update(telemetry);
    }

    @Override
    public void stop() {
        RobotController.drive(0, 0, 0);
        if (follower != null) {
            stopRobot();
        }
    }

    // ---- Helpers ----

    /** This creates a full stop of the robot by setting the drive motors to run at 0 power. */
    private void stopRobot() {
        follower.startTeleopDrive(true);
        follower.setTeleOpDrive(0, 0, 0, true);
    }

    private void resetSamples(ArrayList<Double> samples) {
        samples.clear();
        for (int i = 0; i < RECORD_NUMBER; i++) {
            samples.add(0.0);
        }
    }

    private double average(ArrayList<Double> values) {
        if (values.isEmpty()) return 0;
        double sum = 0;
        for (double v : values) sum += v;
        return sum / values.size();
    }

    private GoBildaPinpointDriver.EncoderDirection directionFor(String name) {
        return "FORWARD".equals(name)
                ? GoBildaPinpointDriver.EncoderDirection.FORWARD
                : GoBildaPinpointDriver.EncoderDirection.REVERSED;
    }

    /**
     * Regenerates Constants.java from scratch using whatever values have been discovered so far,
     * and overwrites the file on disk. Values not yet discovered are simply omitted (matching the
     * commented-out placeholders in the original template).
     */
    private void writeConstantsFile() {
        try {
            File parent = CONSTANTS_FILE.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            try (FileWriter writer = new FileWriter(CONSTANTS_FILE, false)) {
                writer.write(generateConstantsSource());
            }
        } catch (IOException e) {
            telemetryM.debug("Failed to write Constants.java: " + e.getMessage());
        }
    }

    private String generateConstantsSource() {
        StringBuilder sb = new StringBuilder();
        sb.append("package org.firstinspires.ftc.teamcode.pedoPathing;\n\n");
        sb.append("import com.pedropathing.follower.Follower;\n");
        sb.append("import com.pedropathing.follower.FollowerConstants;\n");
        sb.append("import com.pedropathing.ftc.FollowerBuilder;\n");
        sb.append("import com.pedropathing.ftc.drivetrains.MecanumConstants;\n");
        sb.append("import com.pedropathing.ftc.localization.constants.PinpointConstants;\n");
        sb.append("import com.pedropathing.paths.PathConstraints;\n");
        sb.append("import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;\n");
        sb.append("import com.qualcomm.robotcore.hardware.DcMotor;\n");
        sb.append("import com.qualcomm.robotcore.hardware.DcMotorSimple;\n");
        sb.append("import com.qualcomm.robotcore.hardware.HardwareMap;\n\n");
        sb.append("import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;\n\n");
        sb.append("public class Constants {\n");

        sb.append("    public static FollowerConstants followerConstants = new FollowerConstants()\n");
        sb.append("            .mass(6.875)");
        if (forwardZeroPowerAcceleration != null) {
            sb.append("\n            .forwardZeroPowerAcceleration(").append(forwardZeroPowerAcceleration).append(")");
        }
        if (lateralZeroPowerAcceleration != null) {
            sb.append("\n            .lateralZeroPowerAcceleration(").append(lateralZeroPowerAcceleration).append(")");
        }
        sb.append(";\n\n");

        sb.append("    public static MecanumConstants driveConstants = new MecanumConstants()\n");
        sb.append("            .maxPower(1)\n");
        sb.append("            .rightFrontMotorName(\"rightFront\")\n");
        sb.append("            .rightRearMotorName(\"rightRear\")\n");
        sb.append("            .leftRearMotorName(\"leftRear\")\n");
        sb.append("            .leftFrontMotorName(\"leftFront\")\n");
        sb.append("            .leftFrontMotorDirection(DcMotor.Direction.REVERSE)\n");
        sb.append("            .leftRearMotorDirection(DcMotor.Direction.REVERSE)\n");
        sb.append("            .rightFrontMotorDirection(DcMotor.Direction.FORWARD)\n");
        sb.append("            .rightRearMotorDirection(DcMotor.Direction.FORWARD)");
        if (xVelocity != null) {
            sb.append("\n            .xVelocity(").append(xVelocity).append(")");
        }
        if (yVelocity != null) {
            sb.append("\n            .yVelocity(").append(yVelocity).append(")");
        }
        sb.append(";\n\n");

        sb.append("    public static PinpointConstants localizerConstants = new PinpointConstants()\n");
        sb.append("            .forwardPodY(-123)\n");
        sb.append("            .strafePodX(0)\n");
        sb.append("            .distanceUnit(DistanceUnit.MM)\n");
        sb.append("            .hardwareMapName(\"pinpoint\")\n");
        sb.append("            .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)\n");
        sb.append("            .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.")
                .append(forwardEncoderDirection).append(")\n");
        sb.append("            .strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.")
                .append(strafeEncoderDirection).append(");\n\n");

        sb.append("    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1);\n\n");

        sb.append("    public static Follower createFollower(HardwareMap hardwareMap) {\n");
        sb.append("        return new FollowerBuilder(followerConstants, hardwareMap)\n");
        sb.append("                .pinpointLocalizer(localizerConstants)\n");
        sb.append("                .pathConstraints(pathConstraints)\n");
        sb.append("                .mecanumDrivetrain(driveConstants)\n");
        sb.append("                .build();\n");
        sb.append("    }\n");
        sb.append("}\n");

        return sb.toString();
    }
}