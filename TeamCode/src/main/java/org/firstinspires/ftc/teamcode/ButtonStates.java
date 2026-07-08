package org.firstinspires.ftc.teamcode;

import java.util.EnumMap;
import java.util.Map;

public class ButtonStates {
    public enum Button {
        LB1, RB1, DPAD_U1, DPAD_D1, CROSS1
    }

    private static final Map<Button, Boolean> lastStates = new EnumMap<>(Button.class);

    static {
        for (Button button : Button.values()) {
            lastStates.put(button, false);
        }
    }

    public static boolean justPressed(boolean currentButtonState, Button button) {
        boolean lastState = lastStates.get(button);
        boolean result = currentButtonState && !lastState;
        lastStates.put(button, currentButtonState);
        return result;
    }
}