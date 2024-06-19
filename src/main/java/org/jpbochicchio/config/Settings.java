package org.jpbochicchio.config;

import com.beust.jcommander.Parameter;
import de.amr.demos.maze.swingapp.ui.common.ThemeConverter;
import de.amr.swing.Swing;

import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;

public class Settings {
    @Parameter(
            description = "Preview window content width",
            names = {"-width"}
    )
    public int width;
    @Parameter(
            description = "Preview window content height",
            names = {"-height"}
    )
    public int height;
    @Parameter(
            description = "Theme class name (or: 'system', 'cross', 'metal', 'nimbus')",
            names = {"-laf", "-theme"},
            converter = ThemeConverter.class
    )
    public String theme;

    public Settings() {
        Dimension displaySize = Swing.getDisplaySize();
        this.width = displaySize.width;
        this.height = displaySize.height;
        this.theme = NimbusLookAndFeel.class.getName();
    }
}
