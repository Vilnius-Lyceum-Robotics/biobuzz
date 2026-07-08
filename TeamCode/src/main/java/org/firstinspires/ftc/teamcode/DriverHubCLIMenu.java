package org.firstinspires.ftc.teamcode;

public class DriverHubCLIMenu {
    public String caption;
    public String[] choices;
    public int selectedIndex;
    public String confirmed;
    private DriverHubCLIMenu (Builder builder) {
        this.caption = builder.caption;
        this.choices = builder.choices;
        selectedIndex = 0;
    }

    public static class Builder {
        private String caption = "";
        private String[] choices;

        public Builder caption(String caption) {
            this.caption = caption;
            return this;
        }
        public Builder choices(String... choices) {
            this.choices = choices;
            return this;
        }
        public DriverHubCLIMenu build() {
            return new DriverHubCLIMenu(this);
        }
    }

    public String[] getTelemetry() {
        String[] result = new String[choices.length + 1];
        result[0] = caption;
        for (int i = 0; i < choices.length; i++) {
            result[i + 1] = (i == selectedIndex)
                    ? String.format("--> %s", choices[i])
                    : choices[i];
        }

        return result;
    }

    public void selectNext() {
        if (selectedIndex == choices.length - 1) {
            selectedIndex = 0;
        } else {
            selectedIndex++;
        }
    }

    public void selectPrevious() {
        if (selectedIndex == 0) {
            selectedIndex = choices.length - 1;
        } else {
            selectedIndex--;
        }
    }

    public void confirm() {
        confirmed = choices[selectedIndex];
    }
}
