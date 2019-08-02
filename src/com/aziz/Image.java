package com.aziz;

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

    /* 2D array with Pixel information of the original image */
    private ArrayList<ArrayList<Pixel>> imageAsPixels;
    private int ogImageHeight;
    private int ogImageWidth;

    public Image(String path) {
        File imageFile = new File(path);
        BufferedImage originalImage;
        try {
            originalImage = ImageIO.read(imageFile);
        } catch (Exception e) {
            System.err.print(e.getMessage());
            return;
        }
        initializePixelArray(originalImage);
    }

    private void initializePixelArray(BufferedImage originalImage) {
        imageAsPixels = new ArrayList<>();
        for (int y = 0; y < originalImage.getHeight(); y++) {
            ArrayList<Pixel> row = new ArrayList<>();
            for (int x = 0; x < originalImage.getWidth(); x++) {
                row.add(new Pixel(originalImage.getRGB(x, y)));
            }
            imageAsPixels.add(row);
        }
        ogImageHeight = imageAsPixels.size();
        ogImageWidth = imageAsPixels.get(0).size();
    }
}
