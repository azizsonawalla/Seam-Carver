package model;

import javafx.util.Pair;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Represents an image to be seam carved
 *
 * @author Aziz Sonawalla
 */
public class Image {

    // 2D array with Pixel information of the original image
    private EnergyMap ogImage;

    // Vertical pixel paths from least energy to highest energy
    private ArrayList<ArrayList<Pair<Integer, Integer>>> colsToRemove;

    public Image(String path) {
        File imageFile = new File(path);
        BufferedImage originalImage;
        try {
            originalImage = ImageIO.read(imageFile);
        } catch (Exception e) {
            System.err.print(e.getMessage());
            return;
        }
        ogImage = new EnergyMap(originalImage);
        PathCalculator calculator = new PathCalculator(new EnergyMap(ogImage));
        colsToRemove = calculator.getPaths();
    }

    public BufferedImage getCropped(Double relativePixels) throws Exception {
        // TODO
        if (relativePixels > 0) {
            throw new Exception("Upscaling not supported yet");
        } else {
            int pixelsToRemove = (int)Math.abs(relativePixels);
            this.horizontalCrop(pixelsToRemove);
            return getArrayAsImage();
        }
    }

    public void saveCroppedImage(String imagePathOut) throws IOException {
        BufferedImage image = getArrayAsImage();
        ImageIO.write(image, "png", new File(imagePathOut)); // TODO: Change to other library
    }

    private void horizontalCrop(int remove) {
        /* check for out-of-bounds crop */
        if (colsToRemove.size() < remove){
            System.out.println("Error: Cannot crop further than width of image.");
            return;
        }
        for (int col = 0; col < remove; col++) {
            ogImage.removeElements(colsToRemove.get(col));
        }
    }

    private BufferedImage getArrayAsImage() {
        BufferedImage image = new BufferedImage(ogImage.width(), ogImage.height(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < ogImage.height(); y++) {
            for (int x = 0; x < ogImage.width(); x++) {
                image.setRGB(x, y, ogImage.getPixel(x,y).getRGB());
            }
        }
        return image;
    }

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        Image image = new Image("samples/sample1.jpg");
        long endTime1 = System.nanoTime();
        System.out.println("Initialization took " + (endTime1-startTime) + " nanoseconds");
        image.horizontalCrop(300);
        try {
            image.saveCroppedImage("samples/sample1-out3.png");
        } catch (Exception e) {
            System.out.println("Couldn't save image: " + e.getMessage());
        }
        long endTime2 = System.nanoTime();
        System.out.println("Cropping took " + (endTime2-endTime1) + " nanoseconds");
    }
}
