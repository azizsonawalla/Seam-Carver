package model;

import javafx.util.Pair;
import util.ImageUtil;

import javax.imageio.ImageIO;
import java.awt.*;
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
    private EnergyMatrix energyMatrix;

    // Vertical pixel paths from least energy to highest energy
    private ArrayList<ArrayList<Pair<Integer, Integer>>> pixelPathsOrderedByEnergy;

    public Image(BufferedImage originalImage) {
        energyMatrix = new EnergyMatrix(originalImage);
        PathCalculator calculator = new PathCalculator(new EnergyMatrix(energyMatrix));
        pixelPathsOrderedByEnergy = calculator.getPaths();
    }

    public int width() {
        return energyMatrix.width();
    }

    public int height() {
        return energyMatrix.height();
    }

    public void saveCroppedImage(Double relativePixels, String imagePathOut) throws Exception {
        BufferedImage image = getCropped(relativePixels);
        ImageIO.write(image, "png", new File(imagePathOut)); // TODO: Change to other library
    }

    public void saveRedImage(Double relativePixels, String imagePathOut) throws Exception {
        BufferedImage image = getRed(relativePixels);
        ImageIO.write(image, "png", new File(imagePathOut)); // TODO: Change to other library
    }

    public BufferedImage getRed(Double relativePixels) throws Exception {
        if (relativePixels > 0) {
            throw new Exception("Upscaling not supported yet");
        } else {
            int colsToRemove = (int)Math.abs(relativePixels);
            this.getDownScaled(colsToRemove);
            return this.getArrayAsRedImage(energyMatrix.width(), energyMatrix.height());
        }
    }

    public BufferedImage getCropped(Double relativePixels) throws Exception {
        if (relativePixels > 0) {
            throw new Exception("Upscaling not supported yet");
        } else {
            int colsToRemove = (int)Math.abs(relativePixels);
            return this.getDownScaled(colsToRemove);
        }
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
        energyMatrix.setInactivePixels(pixelsToRemove);
        return getArrayAsImage(energyMatrix.width()-colsToRemove, energyMatrix.height());
    }

    private BufferedImage getArrayAsImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int x = 0;
        int y = 0;
        for (ArrayList<Pixel> row: energyMatrix.getData()) {
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

    private BufferedImage getArrayAsRedImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int x = 0;
        int y = 0;
        for (ArrayList<Pixel> row: energyMatrix.getData()) {
            for (Pixel pixel: row) {
                if (pixel.isActive()) {
                    image.setRGB(x, y, pixel.getRGB());
                } else {
                    image.setRGB(x, y, new Color(255, 0,0).getRGB());
                }
                x++;
            }
            x=0;
            y++;
        }
        return image;
    }

    public static void main(String[] args) {
        double COLS_TO_REMOVE = 800.0;
        for(double i=0; i<=COLS_TO_REMOVE; i++) {
            System.out.println("Working on "+i);
            String filename_in = "C:\\Users\\i871675\\Downloads\\paris\\paris_"+i+".jpg";
            String filename_out_red = "C:\\Users\\i871675\\Downloads\\paris\\paris_"+(i+1)+"_red.jpg";
            String filename_out = "C:\\Users\\i871675\\Downloads\\paris\\paris_"+(i+1)+".jpg";
            Image image = new Image(ImageUtil.readFromFile(new File(filename_in)));
            try {
                image.saveRedImage(-1.0,filename_out_red);
            } catch (Exception e) {
                System.out.println("Couldn't save image: " + e.getMessage());
            }
            try {
                image.saveCroppedImage(-1.0,filename_out);
            } catch (Exception e) {
                System.out.println("Couldn't save image: " + e.getMessage());
            }
        }
//        Image image = new Image(ImageUtil.readFromFile(new File("C:\\Users\\i871675\\Downloads\\paris.jpg")));
//        long endTime1 = System.nanoTime();
//        System.out.println("Initialization took " + (endTime1-startTime) + " nanoseconds");
//        try {
//            image.saveCroppedImage(-800.0,"C:\\Users\\i871675\\Downloads\\paris-out.jpg");
//        } catch (Exception e) {
//            System.out.println("Couldn't save image: " + e.getMessage());
//        }
//        long endTime2 = System.nanoTime();
//        System.out.println("Cropping took " + (endTime2-endTime1) + " nanoseconds");
    }
}
