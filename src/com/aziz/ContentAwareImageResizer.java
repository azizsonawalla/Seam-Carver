package com.aziz;

import javafx.util.Pair;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class ContentAwareImageResizer {

    /* 2D array with shortest paths */
    private ArrayList<ArrayList<Pair<Integer, Integer>>> BACKTRACKING_MATRIX;
    /* 2D array with RGB values of input image */
    private ArrayList<ArrayList<Pixel>> PIXEL_ARRAY;
    private int PIXEL_ARRAY_HEIGHT;
    private int PIXEL_ARRAY_WIDTH;


    public ContentAwareImageResizer(String imagePathIn) {
        // load image from file
        BufferedImage inputImage;
        File imageFile = new File(imagePathIn);
        try {
            inputImage = ImageIO.read(imageFile);
        } catch (Exception e) {
            System.err.print(e.getMessage());
            return;
        }
        // convert input image to array of Pixels
        PIXEL_ARRAY = imageToArray(inputImage);
        // initialize PIXEL_ARRAY dimensions
        PIXEL_ARRAY_HEIGHT = PIXEL_ARRAY.size();
        PIXEL_ARRAY_WIDTH = PIXEL_ARRAY.get(0).size();
    }

    private void resize(int cols, String imagePathOut) {
        if (PIXEL_ARRAY_WIDTH - cols < 1){
            System.out.println("Too many columns to be removed");
            return;
        }
        for (int col = 0; col < cols; col++) {

            if (col%10==0) {System.out.println("Removing column " + col);}

            // initialize Pixel positions
            for (int y = 0; y < PIXEL_ARRAY_HEIGHT; y++) {
                for (int x = 0; x < PIXEL_ARRAY_WIDTH; x++) {
                    Pixel currentPixel = PIXEL_ARRAY.get(y).get(x);
                    currentPixel.setPos(new Pair<>(x,y));
                }
            }

            // initialize Pixel energies
            for (int y = 0; y < PIXEL_ARRAY_HEIGHT; y++) {
                for (int x = 0; x < PIXEL_ARRAY_WIDTH; x++) {
                    Pixel currentPixel = PIXEL_ARRAY.get(y).get(x);
                    currentPixel.setEnergy(energyValueOf(x, y));
                }
            }

            // initialize Pixel cumulative energies and backtracking matrix
            BACKTRACKING_MATRIX = new ArrayList<>();
            for (int y = 0; y < PIXEL_ARRAY_HEIGHT; y++) {
                ArrayList<Pair<Integer, Integer>> backtracking_row = new ArrayList<Pair<Integer, Integer>>();
                for (int x = 0; x < PIXEL_ARRAY_WIDTH; x++) {
                    Pixel currentPixel = PIXEL_ARRAY.get(y).get(x);
                    if (y == 0) {
                        currentPixel.setCumulativeEnergy(currentPixel.getEnergy());
                        backtracking_row.add(currentPixel.getPos());
                    } else {
                        Pair<Pixel, Double> forwardEnergyPixel = minimumPixelAbove(currentPixel);
                        Pixel pixelAbove = forwardEnergyPixel.getKey();
                        currentPixel.setCumulativeEnergy(forwardEnergyPixel.getValue());
                        backtracking_row.add(pixelAbove.getPos());
                    }
                }
                BACKTRACKING_MATRIX.add(backtracking_row);
            }

            // remove least energy rows
            ArrayList<Pair<Integer, Integer>> path = leastEnergyVerticalPath();
            removeElements(path);
        }

        // create carved image
        int carvedImageHeight = PIXEL_ARRAY_HEIGHT;
        int carvedImageWidth = PIXEL_ARRAY_WIDTH;
        BufferedImage carvedImage = new BufferedImage(carvedImageWidth, carvedImageHeight, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < carvedImageHeight; y++) {
            for (int x = 0; x < carvedImageWidth; x++) {
                carvedImage.setRGB(x, y, PIXEL_ARRAY.get(y).get(x).getRGB());
            }
        }

        // write carvedImage back to file
        try {
            ImageIO.write(carvedImage, "png", new File(imagePathOut));
        } catch (Exception e) {
            System.err.print(e.getMessage());
        }
    }

    private Pair<Pixel, Double> minimumPixelAbove(Pixel currentPixel) {
        Pixel upLeft, upRight, upCenter, left, right;
        double left_new_energy, right_new_energy;
        upCenter = PIXEL_ARRAY.get(currentPixel.getY() - 1).get(currentPixel.getX());
        if (currentPixel.getX() == 0){
            upLeft = upCenter;
            left_new_energy = 0;
        } else {
            upLeft = PIXEL_ARRAY.get(currentPixel.getY() - 1).get(mod(currentPixel.getX() - 1, PIXEL_ARRAY_WIDTH));
            left = PIXEL_ARRAY.get(currentPixel.getY()).get(mod(currentPixel.getX() - 1, PIXEL_ARRAY_WIDTH));
            left_new_energy = Math.abs(upCenter.getEnergy() - left.getEnergy());
        }
        if (currentPixel.getX() == PIXEL_ARRAY_WIDTH-1) {
            upRight = upCenter;
            right_new_energy = 0;
        } else {
            upRight = PIXEL_ARRAY.get(currentPixel.getY() - 1).get(mod(currentPixel.getX() + 1, PIXEL_ARRAY_WIDTH));
            right = PIXEL_ARRAY.get(currentPixel.getY()).get(mod(currentPixel.getX() + 1, PIXEL_ARRAY_WIDTH));
            right_new_energy = Math.abs(upCenter.getEnergy() - right.getEnergy());
        }

        double FE_left = upLeft.getCumulativeEnergy() + currentPixel.getEnergy() + left_new_energy;
        double FE_center = upCenter.getCumulativeEnergy() + currentPixel.getEnergy();
        double FE_right = upRight.getCumulativeEnergy() + currentPixel.getEnergy() + right_new_energy;

        double minimumCFE = Math.min(Math.min(FE_left, FE_center), FE_right);
        if (minimumCFE == FE_left) {
            return new Pair<>(upLeft, FE_left);
        }
        if (minimumCFE == FE_center) {
            return new Pair<>(upCenter, FE_center);
        }
        return new Pair<>(upRight, FE_right);
    }

    private void removeElements(ArrayList<Pair<Integer, Integer>> path) {
        for (int i = 0; i < path.size(); i++) {
            Pair<Integer, Integer> pair = path.get(i);
            int x = pair.getKey();
            int y = pair.getValue();
            PIXEL_ARRAY.get(y).remove(x);
        }
        PIXEL_ARRAY_WIDTH--;
    }

    private double energyValueOf(int x, int y) {
        /* Using dual gradient energy function */

        // calculate neighbours
        Pixel xPrev = PIXEL_ARRAY.get(y).get(mod(x - 1, PIXEL_ARRAY_WIDTH));
        Pixel xNext = PIXEL_ARRAY.get(y).get(mod(x + 1, PIXEL_ARRAY_WIDTH));
        Pixel yPrev = PIXEL_ARRAY.get(mod(y - 1, PIXEL_ARRAY_HEIGHT)).get(x);
        Pixel yNext = PIXEL_ARRAY.get(mod(y + 1, PIXEL_ARRAY_HEIGHT)).get(x);

        // calculate RGB gradients in x direction
        double deltaX_Red = Math.abs(xPrev.getRed() - xNext.getRed());
        double deltaX_Green = Math.abs(xPrev.getGreen() - xNext.getGreen());
        double deltaX_Blue = Math.abs(xPrev.getBlue() - xNext.getBlue());

        // calculate RGB gradients in y direction
        double deltaY_Red = Math.abs(yPrev.getRed() - yNext.getRed());
        double deltaY_Green = Math.abs(yPrev.getGreen() - yNext.getGreen());
        double deltaY_Blue = Math.abs(yPrev.getBlue() - yNext.getBlue());

        // calculate x and y gradients
        double deltaX = Math.pow(deltaX_Red, 2) + Math.pow(deltaX_Green, 2) + Math.pow(deltaX_Blue, 2);
        double deltaY = Math.pow(deltaY_Red, 2) + Math.pow(deltaY_Green, 2) + Math.pow(deltaY_Blue, 2);

        return deltaX + deltaY;
    }

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
        ContentAwareImageResizer carver = new ContentAwareImageResizer("sample-images/sample5-input.jpg");
        carver.resize(800, "sample-images/sample5-output.jpg");
    }
}
