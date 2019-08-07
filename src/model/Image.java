package model;

import javafx.util.Pair;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

/**
 * Represents an image to be seam carved
 *
 * @author Aziz Sonawalla
 */
public class Image {

    // 2D array with Pixel information of the original image
    private EnergyMap energyMap;

    // Vertical pixel paths from least energy to highest energy
    private ArrayList<ArrayList<Pair<Integer, Integer>>> pixelPathsOrderedByEnergy;

    public Image(File imageFile) {
        BufferedImage originalImage;
        try {
            originalImage = ImageIO.read(imageFile);
        } catch (Exception e) {
            System.err.print(e.getMessage());
            return;
        }
        energyMap = new EnergyMap(originalImage);
        PathCalculator calculator = new PathCalculator(new EnergyMap(energyMap));
        pixelPathsOrderedByEnergy = calculator.getPaths();
    }

    public int width() {
        return energyMap.width();
    }

    public int height() {
        return energyMap.height();
    }

    public BufferedImage getCropped(Double relativePixels) throws Exception {
        // TODO
        if (relativePixels > 0) {
            throw new Exception("Upscaling not supported yet");
        } else {
            int colsToRemove = (int)Math.abs(relativePixels);
            return this.getDownScaled(colsToRemove);
        }
    }

    public void saveCroppedImage(Double relativePixels, String imagePathOut) throws Exception {
        BufferedImage image = getCropped(relativePixels);
        ImageIO.write(image, "png", new File(imagePathOut)); // TODO: Change to other library
    }

    private BufferedImage getDownScaled(int colsToRemove) throws Exception {
        /* check for out-of-bounds crop */
        if (pixelPathsOrderedByEnergy.size() < colsToRemove){
            throw new Exception("Unsupported operation: Upscaling not implemented");
        }
        ArrayList<Pair<Integer, Integer>> pixelsToRemove = new ArrayList<>();
        for (int col = 0; col < colsToRemove; col++) {
            pixelsToRemove.addAll(pixelPathsOrderedByEnergy.get(col));
        }
        energyMap.setInactivePixels(pixelsToRemove);
        return getArrayAsImage(energyMap.width()-colsToRemove, energyMap.height());
    }

    private BufferedImage getArrayAsImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int x = 0;
        int y = 0;
        for (ArrayList<Pixel> row: energyMap.getData()) {
            for (Pixel pixel: row) {
                if (pixel.isActive()) {
                    image.setRGB(x, y, pixel.getRGB());
                    x++;
                }
            }
            x=0;
            y++;
        }
        return image;
    }

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        Image image = new Image(new File("samples/sample1.jpg"));
        long endTime1 = System.nanoTime();
        System.out.println("Initialization took " + (endTime1-startTime) + " nanoseconds");
        try {
            image.saveCroppedImage(-300.0,"samples/sample1-out3.png");
            image.saveCroppedImage(-400.0,"samples/sample1-out4.png");
            image.saveCroppedImage(-200.0,"samples/sample1-out5.png");
            image.saveCroppedImage(-150.0,"samples/sample1-out6.png");
            image.saveCroppedImage(-500.0,"samples/sample1-out7.png");
        } catch (Exception e) {
            System.out.println("Couldn't save image: " + e.getMessage());
        }
        long endTime2 = System.nanoTime();
        System.out.println("Cropping took " + (endTime2-endTime1) + " nanoseconds");
    }
}
