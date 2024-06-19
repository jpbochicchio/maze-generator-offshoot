package org.jpbochicchio.config;

import com.beust.jcommander.Parameter;

import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class ExtendedSettings extends Settings {
    private Settings baseSettings;

    @Parameter(
            description = "How many mazes should be generated when calling for a mass generation via the UI",
            names = {"-massCount", "-count", "-mazesPerRun"}
    )
    public int massGenerationMazeCount;

    public ExtendedSettings() {
        this.massGenerationMazeCount = 10;
        this.width = 336;
        this.height = 384;
        this.theme = NimbusLookAndFeel.class.getName();
    }

    public Settings getBaseSettings() {
        if (this.baseSettings != null) {
            return this.baseSettings;
        }

        this.baseSettings = new Settings();
        this.baseSettings.width = this.width;
        this.baseSettings.height = this.height;
        this.baseSettings.theme = this.theme;

        return this.baseSettings;
    }
}
