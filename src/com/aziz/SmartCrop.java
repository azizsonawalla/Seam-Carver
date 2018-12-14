package com.aziz;

import javafx.util.Pair;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class SmartCrop {

    /* 2D array with shortest paths */
    private ArrayList<ArrayList<Pair<Integer, Integer>>> BACKTRACKING_MATRIX;
    /* 2D array with RGB values of input image */
    private ArrayList<ArrayList<Pixel>> PIXEL_ARRAY;
    private int PIXEL_ARRAY_HEIGHT;
    private int PIXEL_ARRAY_WIDTH;


    /**
     * Constructor loads image from file and initializes PIXEL_ARRAY, PIXEL_ARRAY_HEIGHT
     * and PIXEL_ARRAY_WIDTH
     * @param imagePathIn path to input image
     */
    private SmartCrop(String imagePathIn) {
        BufferedImage inputImage;
        File imageFile = new File(imagePathIn);
        try {
            inputImage = ImageIO.read(imageFile);
        } catch (Exception e) {
            System.err.print(e.getMessage());
            return;
        }
        PIXEL_ARRAY = imageToArray(inputImage);
        PIXEL_ARRAY_HEIGHT = PIXEL_ARRAY.size();
        PIXEL_ARRAY_WIDTH = PIXEL_ARRAY.get(0).size();
    }

    /**
     * Detects vertical paths in image with least value (energy) and
     * removes them from the image
     * @param colsToRemove the number of vertical paths to remove
     */
    private void horizontalCrop(int colsToRemove) {
        /* check for out-of-bounds crop */
        if (PIXEL_ARRAY_WIDTH - colsToRemove < 1){
            System.out.println("Error: Cannot crop further than width of image.");
            return;
        }

        ArrayList<Pair<Integer, Integer>> path = new ArrayList<>();
        for (int col = 0; col < colsToRemove; col++) {
            printProgressBarToConsole("Cropping horizontally", col, colsToRemove);
            updateAllPixelPositions();
            if (col==0) {
                updateAllPixelEnergies();
            } else {
                updateSelectedPixelEnergies(path);
            }
            calculateCumulativeEnergies();
            path = leastEnergyVerticalPath();
            removeElements(path);
        }
    }

    /**
     * Saves PIXEL_ARRAY as an image
     * @param imagePathOut path where output image is to be stored
     */
    private void saveCroppedImage(String imagePathOut) {
        int imageHeight = PIXEL_ARRAY_HEIGHT;
        int imageWidth = PIXEL_ARRAY_WIDTH;
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                image.setRGB(x, y, PIXEL_ARRAY.get(y).get(x).getRGB());
            }
        }
        try {
            ImageIO.write(image, "png", new File(imagePathOut));
        } catch (Exception e) {
            System.err.print(e.getMessage());
        }
    }

    /**
     * For given pixel, returns one of the three pixels from the row above
     * (top left, top center, and top right) that, if removed in combination
     *  with the given pixel, would introduce the least amount of energy
     *  into the image
     * @param currentPixel pixel being considered for removal
     * @return Pair<Pixel, Double> where the key is the pixel from the top row
     * and the value is the energy introduced
     */
    private Pair<Pixel, Double> leastEnergyPixelFromAbove(Pixel currentPixel) {
        Pixel topLeft, topRight, topCenter, left, right;
        double addedEnergyFromLeft, addedEnergyFromRight, FE_left, FE_right, FE_center;
        ArrayList<Pair<Pixel, Double>> pathOptions = new ArrayList<>();

        topCenter = PIXEL_ARRAY.get(currentPixel.getY() - 1).get(currentPixel.getX());
        FE_center = topCenter.getCumulativeEnergy() + currentPixel.getEnergy();
        pathOptions.add(new Pair<Pixel, Double>(topCenter,FE_center));
        if (!(currentPixel.getX() == 0)){
            topLeft = PIXEL_ARRAY.get(currentPixel.getY() - 1).get(currentPixel.getX() - 1);
            left = PIXEL_ARRAY.get(currentPixel.getY()).get(currentPixel.getX() - 1);
            addedEnergyFromLeft = Math.abs(topCenter.getEnergy() - left.getEnergy());
            FE_left = topLeft.getCumulativeEnergy() + currentPixel.getEnergy() + addedEnergyFromLeft;
            pathOptions.add(new Pair<Pixel, Double>(topLeft, FE_left));
        }
        if (!(currentPixel.getX() == PIXEL_ARRAY_WIDTH-1)) {
            topRight = PIXEL_ARRAY.get(currentPixel.getY() - 1).get(currentPixel.getX() + 1);
            right = PIXEL_ARRAY.get(currentPixel.getY()).get(currentPixel.getX() + 1);
            addedEnergyFromRight = Math.abs(topCenter.getEnergy() - right.getEnergy());
            FE_right = topRight.getCumulativeEnergy() + currentPixel.getEnergy() + addedEnergyFromRight;
            pathOptions.add(new Pair<Pixel, Double>(topRight, FE_right));
        }

        Pair<Pixel, Double> idealPath = null;
        for (Pair<Pixel, Double> path: pathOptions) {
            if (idealPath == null || idealPath.getValue() >= path.getValue()) {
                idealPath = path;
            }
        }
        return idealPath;
    }

    /**
     * Removes given path from PIXEL_ARRAY
     * @param path to remove
     */
    private void removeElements(ArrayList<Pair<Integer, Integer>> path) {
        for (int i = 0; i < path.size(); i++) {
            Pair<Integer, Integer> pair = path.get(i);
            int x = pair.getKey();
            int y = pair.getValue();
            PIXEL_ARRAY.get(y).remove(x);
        }
        PIXEL_ARRAY_WIDTH--;
    }

    /***
     * Returns the energy of the pixel at given coordinates. Energy is calculated
     * using the dual gradient formula.
     * @param x coordinate of pixel
     * @param y coordinate of pixel
     * @return energy of pixel
     */
    private double energyValueOf(int x, int y) {
        Pixel xPrev = PIXEL_ARRAY.get(y).get(mod(x - 1, PIXEL_ARRAY_WIDTH));
        Pixel xNext = PIXEL_ARRAY.get(y).get(mod(x + 1, PIXEL_ARRAY_WIDTH));
        Pixel yPrev = PIXEL_ARRAY.get(mod(y - 1, PIXEL_ARRAY_HEIGHT)).get(x);
        Pixel yNext = PIXEL_ARRAY.get(mod(y + 1, PIXEL_ARRAY_HEIGHT)).get(x);

        double deltaX_Red = Math.abs(xPrev.getRed() - xNext.getRed());
        double deltaX_Green = Math.abs(xPrev.getGreen() - xNext.getGreen());
        double deltaX_Blue = Math.abs(xPrev.getBlue() - xNext.getBlue());

        double deltaY_Red = Math.abs(yPrev.getRed() - yNext.getRed());
        double deltaY_Green = Math.abs(yPrev.getGreen() - yNext.getGreen());
        double deltaY_Blue = Math.abs(yPrev.getBlue() - yNext.getBlue());

        double deltaX = Math.pow(deltaX_Red, 2) + Math.pow(deltaX_Green, 2) + Math.pow(deltaX_Blue, 2);
        double deltaY = Math.pow(deltaY_Red, 2) + Math.pow(deltaY_Green, 2) + Math.pow(deltaY_Blue, 2);

        return deltaX + deltaY;
    }

    /**
     * Returns the vertical path with the least cumulative energy
     * from PIXEL_ARRAY. Assumes that energies have already been
     * calculated and BACKTRACKING_MATRIX has already been initialized.
     * @return path of least energy
     */
    private ArrayList<Pair<Integer, Integer>> leastEnergyVerticalPath() {
        ArrayList<Pixel> lastRow = PIXEL_ARRAY.get(PIXEL_ARRAY_HEIGHT - 1);
        Pixel minimumCEPixel = lastRow.get(0);

        for (int i = 1; i < lastRow.size(); i++) {
            Pixel currPixel = lastRow.get(i);
            if (currPixel.getCumulativeEnergy() < minimumCEPixel.getCumulativeEnergy()) {
                minimumCEPixel = currPixel;
            }
        }

        Pair<Integer, Integer> currentPos = minimumCEPixel.getPos(); // start point of path
        Pair<Integer, Integer> nextPos = BACKTRACKING_MATRIX.get(currentPos.getValue()).get(currentPos.getKey());
        ArrayList<Pair<Integer, Integer>> path = new ArrayList<Pair<Integer, Integer>>();

        while (currentPos != nextPos) {
            path.add(currentPos);
            currentPos = nextPos;
            nextPos = BACKTRACKING_MATRIX.get(currentPos.getValue()).get(currentPos.getKey());
        }

        return path;
    }

    /**
     * Initializes elements of PIXEL_ARRAY with cumulative energies
     * and fills the BACKTRACKING_MATRIX
     */
    private void calculateCumulativeEnergies() {
        BACKTRACKING_MATRIX = new ArrayList<>();
        for (int y = 0; y < PIXEL_ARRAY_HEIGHT; y++) {
            ArrayList<Pair<Integer, Integer>> backtracking_row = new ArrayList<Pair<Integer, Integer>>();
            for (int x = 0; x < PIXEL_ARRAY_WIDTH; x++) {
                Pixel currentPixel = PIXEL_ARRAY.get(y).get(x);
                if (y == 0) {
                    currentPixel.setCumulativeEnergy(currentPixel.getEnergy());
                    backtracking_row.add(currentPixel.getPos());
                } else {
                    Pair<Pixel, Double> forwardEnergyPixel = leastEnergyPixelFromAbove(currentPixel);
                    Pixel pixelAbove = forwardEnergyPixel.getKey();
                    currentPixel.setCumulativeEnergy(forwardEnergyPixel.getValue());
                    backtracking_row.add(pixelAbove.getPos());
                }
            }
            BACKTRACKING_MATRIX.add(backtracking_row);
        }
    }

    /**
     * Updates energies of pixels in PIXEL_ARRAY
     */
    private void updateAllPixelEnergies() {
        for (int y = 0; y < PIXEL_ARRAY_HEIGHT; y++) {
            for (int x = 0; x < PIXEL_ARRAY_WIDTH; x++) {
                Pixel currentPixel = PIXEL_ARRAY.get(y).get(x);
                currentPixel.setEnergy(energyValueOf(x, y));
            }
        }
    }

    /**
     * Updates energies of pixels affected by removal of path
     * @param path path removed
     */
    private void updateSelectedPixelEnergies(ArrayList<Pair<Integer,Integer>> path) {
        int x_min = 0;
        int x_max = 0;
        for (int i = 0; i < path.size(); i++) {
            Pair<Integer,Integer> pos = path.get(i);
            int pos_key = pos.getKey();
            int pos_val = pos.getValue();
            if (i==0){
                x_min = pos_key;
                x_max = pos_key;
            } else {
                x_min = Math.min(x_min, pos_key);
                x_max = Math.max(x_max, pos_key);
            }
        }
        x_max +=2;
        x_min -=2;

        for (int y = 0; y < PIXEL_ARRAY_HEIGHT; y++) {
            for (int x = x_min; x <= x_max; x++) {
                int mod_x = mod(x, PIXEL_ARRAY_WIDTH);
                Pixel currentPixel = PIXEL_ARRAY.get(y).get(mod_x);
                currentPixel.setEnergy(energyValueOf(mod_x, y));
            }
        }
    }

    /**
     * Updates positions of all pixels in PIXEL_ARRAY
     */
    private void updateAllPixelPositions() {
        for (int y = 0; y < PIXEL_ARRAY_HEIGHT; y++) {
            for (int x = 0; x < PIXEL_ARRAY_WIDTH; x++) {
               Pixel currentPixel = PIXEL_ARRAY.get(y).get(x);
                currentPixel.setPos(new Pair<>(x,y));
            }
        }
    }

    /**
     * Converts an image to an array of Pixels
     * @param inputImage image to convert
     * @return converted array
     */
    private ArrayList<ArrayList<Pixel>> imageToArray(BufferedImage inputImage) {
        ArrayList<ArrayList<Pixel>> imageArray = new ArrayList<ArrayList<Pixel>>();
        for (int y = 0; y < inputImage.getHeight(); y++) {
            ArrayList<Pixel> row = new ArrayList<>();
            for (int x = 0; x < inputImage.getWidth(); x++) {
                row.add(new Pixel(inputImage.getRGB(x, y)));
            }
            imageArray.add(row);
        }
        return imageArray;
    }

    /**
     * Prints progress of process to console
     * @param current operations completed
     * @param total total operations to do
     */
    private void printProgressBarToConsole(String operation, int current, int total) {
        String message;
        if (current == total-1) {
            message = "Finished " + operation + "\n";
        } else {
            Double percentageDone = ((double) current / (double) total) * 100.0;
            DecimalFormat value = new DecimalFormat("###.#");
            String percentageDoneString = value.format(percentageDone);
            message = operation + " (" + percentageDoneString + "% completed)\r";
        }
        System.out.print(message);
    }

    /**
     * Returns array-friendly modulo of a number
     */
    private int mod(int num, int modulo) {
        if (num >= 0 && num < modulo) {
            return num;
        }
        if (num >= 0) {
            return num % modulo;
        }
        return modulo + num;
    }

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        SmartCrop smartCrop = new SmartCrop("sample-images-tests/sample4-input.jpg");
        smartCrop.horizontalCrop(395);
        smartCrop.saveCroppedImage("sample-images-tests/sample4-output.png");
        long endTime = System.nanoTime();
        System.out.println("Operation took " + (endTime-startTime) + " nanoseconds");
    }
}
