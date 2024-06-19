package org.jpbochicchio.utilities;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageCaptureUtility {
    public static void captureImage(Container container, String imagePath) {
        BufferedImage imageBuffer = new BufferedImage(container.getWidth(), container.getHeight(), BufferedImage.TYPE_INT_ARGB);
        container.paint(imageBuffer.getGraphics());

        try {
            ImageIO.write(imageBuffer, "PNG", new File(imagePath));
            System.out.println("Successfully generated image");
            System.out.println(imagePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
