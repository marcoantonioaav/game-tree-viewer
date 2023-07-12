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

    public static void saveImage(BufferedImage image, String folderPath, String imageName, String format) {
        File file = new File(folderPath+imageName+"."+format);
        try { 
            ImageIO.write(image, format, file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
