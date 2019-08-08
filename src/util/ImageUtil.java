package util;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Utility methods for BufferedImage objects
 */
public class ImageUtil {

    public static BufferedImage reduceHeight(BufferedImage image, int height) {
        if (image.getHeight() <= height) {
            return image;
        }
        int w = image.getWidth();
        int h = image.getHeight();
        double scale = (height*1.0)/(h*1.0);
        BufferedImage scaledImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(scale, scale);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        scaledImage = scaleOp.filter(image, scaledImage);
        return scaledImage;
    }

    public static BufferedImage readFromFile(File imageFile) {
        BufferedImage originalImage;
        try {
            originalImage = ImageIO.read(imageFile);
            return originalImage;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
