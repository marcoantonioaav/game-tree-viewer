package viewer;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public abstract class Utils {
    public static BufferedImage newImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    public static BufferedImage newWhiteImage(int width, int height) {
        BufferedImage image = newImage(width, height);
        Graphics2D g2 = image.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);
        g2.dispose();
        return image;
    }

    public static void saveImage(BufferedImage image, String absolutePath, String extension) {
        File file = new File(absolutePath+extension);
        try { 
            ImageIO.write(image, extension.substring(1), file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
