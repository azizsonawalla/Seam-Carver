package com.aziz;

import javafx.util.Pair;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class SeamCarver {

    /* 2D array with ENERGY_MATRIX values for all pixels */
    private ArrayList<ArrayList<Float>> ENERGY_MATRIX;
    /* Image file to seam carve */
    private ArrayList<ArrayList<Integer>> INPUT_IMAGE;
    private int INPUT_IMAGE_HEIGHT;
    private int INPUT_IMAGE_WIDTH;
    /* carving values */
    private int ROWS_TO_CARVE;
    private int COLS_TO_CARVE;


    public SeamCarver(String imagePath, int rows, int cols) {
        // load INPUT_IMAGE from file
        BufferedImage inputImage;
        File imageFile = new File(imagePath); //TODO: Add correct INPUT_IMAGE path
        try {
            inputImage = ImageIO.read(imageFile);
        } catch (Exception e) {
            System.err.print(e.getMessage());
            return;
        }

        // initialize dimensions
        INPUT_IMAGE_HEIGHT = inputImage.getHeight();
        INPUT_IMAGE_WIDTH = inputImage.getWidth();

        // convert rgb values of input image to array
        INPUT_IMAGE = new ArrayList<ArrayList<Integer>>();
        for (int y = 0; y < INPUT_IMAGE_HEIGHT; y++) {
            ArrayList<Integer> row = new ArrayList<>();
            for (int x = 0; x < INPUT_IMAGE_WIDTH; x++) {
                row.add(inputImage.getRGB(x, y));
            }
            INPUT_IMAGE.add(row);
        }

        // initialize carving values
        ROWS_TO_CARVE = rows;
        COLS_TO_CARVE = cols;
    }

    public void carve() {
        // initialize ENERGY_MATRIX array
        ENERGY_MATRIX = new ArrayList<ArrayList<Float>>();
        for (int y = 0; y < INPUT_IMAGE_HEIGHT; y++) {
            ArrayList<Float> row = new ArrayList<>();
            for (int x = 0; x < INPUT_IMAGE_WIDTH; x++) {
                row.add(energyValueOf(x, y));
            }
            ENERGY_MATRIX.add(row);
        }

        // remove least energy rows
        for (int i = 0; i < ROWS_TO_CARVE; i++) {
            ArrayList<Pair> path = leastEnergyVerticalPath(ENERGY_MATRIX);
        }

        // remove least energy cols


        // create carved image
        BufferedImage carvedImage = new BufferedImage(INPUT_IMAGE_WIDTH-1, INPUT_IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < INPUT_IMAGE_HEIGHT; y++) {
            for (int x = 0; x < INPUT_IMAGE_WIDTH; x++) {
                //TODO
            }
        }

        // write carvedImage back to file
        try {
            ImageIO.write(carvedImage, "png", new File("sample-images/sample1-carved.png"));
        } catch (Exception e) {
            System.err.print(e.getMessage());
            return;
        }
    }

    private void setValueInEnergy(int x, int y, float value) {
        ENERGY_MATRIX.get(y).set(x, value);
    }

    private float energyValueOf(int x, int y) {
        //TODO: Returns ENERGY_MATRIX value of pixel at given coordinates
        return 0;
    }

    private ArrayList<Pair> leastEnergyVerticalPath(ArrayList<ArrayList<Float>> energyMatrix) {
        //TODO: Returns least ENERGY_MATRIX path from  given INPUT_IMAGE
        return new ArrayList<Pair>();
    }

    public static void main(String[] args) {
        SeamCarver carver = new SeamCarver("sample-images/sample1.png", 0, 1);
        carver.carve();
    }
}
