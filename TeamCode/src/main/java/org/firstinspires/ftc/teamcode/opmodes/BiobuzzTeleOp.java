/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.RobotController;
import org.firstinspires.ftc.teamcode.DriverHubCLIMenu;

import java.util.Map;


/*
 * This file contains a minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When a selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list
 */

@TeleOp(name="BiobuzzTeleOp", group="Linear OpMode")
//@Disabled
public class BiobuzzTeleOp extends LinearOpMode {

    // Declare OpMode members.
    private final ElapsedTime runtime = new ElapsedTime();

    public void populateTelemetry(String[] lines) {
        for (String line : lines) {
            telemetry.addLine(line);
        }
    }
    public void populateTelemetry(Map<String, Object> data) {
        for (String key : data.keySet()) {
            telemetry.addData(key, data.get(key));
        }
    }

    @Override
    public void runOpMode() {
        // INIT
        DriverHubCLIMenu[] initMenus = {};

        for (DriverHubCLIMenu initMenu : initMenus) {
            while (!isStopRequested()) {
                populateTelemetry(initMenu.getTelemetry());
                telemetry.update();

                if (gamepad1.dpadUpWasPressed()){
                    initMenu.selectPrevious();
                } else if (gamepad1.dpadDownWasPressed()){
                    initMenu.selectNext();
                } else if (gamepad1.crossWasPressed()){
                    initMenu.confirm();
                    break;
                }
            }
        }
        RobotController.init(hardwareMap);
        telemetry.addLine("Initialization complete. Press START");
        telemetry.update();
        runtime.reset();
        waitForStart();

        // START
        while (opModeIsActive()) {
            RobotController.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
            if (gamepad1.rightBumperWasPressed()) {
                RobotController.changeIntakeRunningMode(1);
            } else if (gamepad1.leftBumperWasPressed()) {
                RobotController.changeIntakeRunningMode(-1);
            }

            if (gamepad1.dpadUpWasPressed()) {
                RobotController.toggleLift();
            }

            populateTelemetry(RobotController.getTelemetry());
            telemetry.addData("Run Time", runtime.toString());
            telemetry.update();
        }

        // STOP
        RobotController.stop();
        telemetry.addData("Status", "Stopped");
        telemetry.update();
    }
}
