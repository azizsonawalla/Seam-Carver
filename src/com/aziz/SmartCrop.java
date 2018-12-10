package com.aziz;

import javafx.util.Pair;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

public class SmartCrop {

    /* 2D array with shortest paths */
    private ArrayList<ArrayList<Pair<Integer, Integer>>> BACKTRACKING_MATRIX;
    /* 2D array with RGB values of input image */
    private ArrayList<ArrayList<Pixel>> PIXEL_ARRAY;
    private int PIXEL_ARRAY_HEIGHT;
    private int PIXEL_ARRAY_WIDTH;


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

    private void horizontalCrop(int colsToRemove) {

        /* check for out-of-bounds crop */
        if (PIXEL_ARRAY_WIDTH - colsToRemove < 1){
            System.out.println("Error: Cannot crop further than width of image.");
            return;
        }

        for (int col = 0; col < colsToRemove; col++) {
            printProgressBarToConsole(col, colsToRemove);
            updatePixelPositions();
            updatePixelEnergies();
            calculateCumulativeEnergies();
            ArrayList<Pair<Integer, Integer>> path = leastEnergyVerticalPath();
            removeElements(path);
            if (col % 10 == 0 || col == colsToRemove-1) {
                saveCroppedImage("sample-images/frames3/frame" + col + ".png");
            }
        }
    }

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

    private Pair<Pixel, Double> leastEnergyPixelFromAbove(Pixel currentPixel) {
        Pixel topLeft, topRight, topCenter, left, right;
        double addedEnergyFromLeft, addedEnergyFromRight, FE_left, FE_right, FE_center;
        ArrayList<Pair<Pixel, Double>> pathOptions = new ArrayList<>();

        topCenter = PIXEL_ARRAY.get(currentPixel.getY() - 1).get(currentPixel.getX());
        FE_center = topCenter.getCumulativeEnergy() + currentPixel.getEnergy();
        pathOptions.add(new Pair(topCenter,FE_center));
        if (!(currentPixel.getX() == 0)){
            topLeft = PIXEL_ARRAY.get(currentPixel.getY() - 1).get(currentPixel.getX() - 1);
            left = PIXEL_ARRAY.get(currentPixel.getY()).get(currentPixel.getX() - 1);
            addedEnergyFromLeft = Math.abs(topCenter.getEnergy() - left.getEnergy());
            FE_left = topLeft.getCumulativeEnergy() + currentPixel.getEnergy() + addedEnergyFromLeft;
            pathOptions.add(new Pair(topLeft, FE_left));
        }
        if (!(currentPixel.getX() == PIXEL_ARRAY_WIDTH-1)) {
            topRight = PIXEL_ARRAY.get(currentPixel.getY() - 1).get(currentPixel.getX() + 1);
            right = PIXEL_ARRAY.get(currentPixel.getY()).get(currentPixel.getX() + 1);
            addedEnergyFromRight = Math.abs(topCenter.getEnergy() - right.getEnergy());
            FE_right = topRight.getCumulativeEnergy() + currentPixel.getEnergy() + addedEnergyFromRight;
            pathOptions.add(new Pair(topRight, FE_right));
        }

        Pair<Pixel, Double> idealPath = null;
        for (Pair<Pixel, Double> path: pathOptions) {
            if (idealPath == null || idealPath.getValue() >= path.getValue()) {
                idealPath = path;
            }
        }
        return idealPath;
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

    private void updatePixelEnergies() {
        for (int y = 0; y < PIXEL_ARRAY_HEIGHT; y++) {
            for (int x = 0; x < PIXEL_ARRAY_WIDTH; x++) {
                Pixel currentPixel = PIXEL_ARRAY.get(y).get(x);
                currentPixel.setEnergy(energyValueOf(x, y));
            }
        }
    }

    private void updatePixelPositions() {
        for (int y = 0; y < PIXEL_ARRAY_HEIGHT; y++) {
            for (int x = 0; x < PIXEL_ARRAY_WIDTH; x++) {
                Pixel currentPixel = PIXEL_ARRAY.get(y).get(x);
                currentPixel.setPos(new Pair<>(x,y));
            }
        }
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

    private void printProgressBarToConsole(int current, int total) {
        String message;
        if (current == total-1) {
            message = "Done!\r";
        } else {
            Double percentageDone = ((double) current / (double) total) * 100.0;
            DecimalFormat value = new DecimalFormat("###.#");
            String percentageDoneString = value.format(percentageDone);
            message = "Processing your image (" + percentageDoneString + "% completed)\r";
        }
        System.out.print(message);
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
//        SmartCrop smartCrop = new SmartCrop("sample-images/sample8-input.jpg");
//        smartCrop.horizontalCrop(110);
//        smartCrop.saveCroppedImage("sample-images/frames/sample8-output.jpg");

//        SmartCrop smartCrop = new SmartCrop("sample-images/sample7-input.jpg");
//        smartCrop.horizontalCrop(840);
//        smartCrop.saveCroppedImage("sample-images/frames2/sample7-output.jpg");

        SmartCrop smartCrop = new SmartCrop("sample-images/sample9-input.jpg");
        smartCrop.horizontalCrop(500);
        smartCrop.saveCroppedImage("sample-images/frames3/sample9-output.jpg");
    }
}
