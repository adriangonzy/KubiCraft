package org.jmc.gui;

import org.jmc.util.Resources;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

/**
 * @author paul
 */
public class CustomFont {
    public static final Font minecraft = initMinecraftFont();

    private static Font initMinecraftFont(){
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font font;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, Resources.load("/conf/ui/Minecraftia-Regular.ttf"));
            font = font.deriveFont(Font.PLAIN, 15);
        } catch (FontFormatException | IOException e) {
            font = new Font("Arial", Font.PLAIN, 15);
        }
        ge.registerFont(font);
        return font;
    }

}
