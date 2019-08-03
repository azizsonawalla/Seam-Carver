package com.aziz;

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
    private ArrayList<ArrayList<Pixel>> ogImage;
    private int ogImageHeight;
    private int ogImageWidth;

    // 2D array with Pixel information of the working image
    private ArrayList<ArrayList<Pixel>> tempImage;
    private int tempImageHeight;
    private int tempImageWidth;

    // Vertical pixel paths from least energy to highest energy
    private ArrayList<ArrayList<Pair<Integer, Integer>>> colsToRemove = new ArrayList<>();

    public Image(String path) {
        File imageFile = new File(path);
        BufferedImage originalImage;
        try {
            originalImage = ImageIO.read(imageFile);
        } catch (Exception e) {
            System.err.print(e.getMessage());
            return;
        }
        initializeImagePixelData(originalImage);
        calculateAllPixelPaths();
    }

    private void initializeImagePixelData(BufferedImage originalImage) {
        ogImage = new ArrayList<>();
        for (int y = 0; y < originalImage.getHeight(); y++) {
            ArrayList<Pixel> row = new ArrayList<>();
            for (int x = 0; x < originalImage.getWidth(); x++) {
                Pixel curr = new Pixel(originalImage.getRGB(x, y));
                curr.setPos(new Pair<>(x,y));
                row.add(curr);
            }
            ogImage.add(row);
        }
        ogImageHeight = ogImage.size();
        ogImageWidth = ogImage.get(0).size();
    }

    private void calculateAllPixelPaths() {
        tempImage = deepCopy(ogImage);
        tempImageHeight = tempImage.size();
        tempImageWidth = tempImage.get(0).size();
        updateAllPixelEnergies();
        ArrayList<Pair<Integer, Integer>> path;
        for (int col = 0; col < ogImageWidth-1; col++) {
            path = leastEnergyVerticalPath();
            removeElements(path);
            colsToRemove.add(path);
            updateSelectedPixelPositions(path);
            updateSelectedPixelEnergies(path);
        }
        System.out.println(colsToRemove.size());
    }

    private void updateAllPixelEnergies() {
        for (int y = 0; y < tempImageHeight; y++) {
            for (int x = 0; x < tempImageWidth; x++) {
                int mod_x = wrappedIndex(x, tempImageWidth);
                Pixel currentPixel = tempImage.get(y).get(mod_x);
                currentPixel.setEnergy(energyValueOf(mod_x, y));
            }
        }
    }

    private void updateSelectedPixelEnergies(ArrayList<Pair<Integer,Integer>> path) {
        int x_min = path.get(0).getKey();
        int x_max = path.get(0).getKey();
        for (int i = 1; i < path.size(); i++) {
            Pair<Integer,Integer> pos = path.get(i);
            int pos_key = pos.getKey();
            x_min = Math.min(x_min, pos_key);
            x_max = Math.max(x_max, pos_key);
        }
        for (int y = 0; y < tempImageHeight; y++) {
            for (int x = x_min; x < x_max; x++) {
                final int mod_x = wrappedIndex(x, tempImageWidth);
                Pixel currentPixel = tempImage.get(y).get(mod_x);
                currentPixel.setEnergy(energyValueOf(mod_x, y));
            }
        }
    }

    private void updateSelectedPixelPositions(ArrayList<Pair<Integer,Integer>> path) {
        for (Pair<Integer, Integer> pos: path) {
            int x_start = wrappedIndex(pos.getKey(), tempImageWidth);
            int y = pos.getValue();
            for (int x = x_start; x < tempImageWidth; x++) {
                Pixel currentPixel = tempImage.get(y).get(x);
                currentPixel.setPos(new Pair<>(x, y));
            }
        }
    }

    private double energyValueOf(int x, int y) {
        Pixel xPrev = tempImage.get(y).get(wrappedIndex(x - 1, tempImageWidth));
        Pixel xNext = tempImage.get(y).get(wrappedIndex(x + 1, tempImageWidth));
        Pixel yPrev = tempImage.get(wrappedIndex(y - 1, tempImageHeight)).get(x);
        Pixel yNext = tempImage.get(wrappedIndex(y + 1, tempImageHeight)).get(x);

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

    private ArrayList<Pair<Integer, Integer>> leastEnergyVerticalPath() {
        ArrayList<ArrayList<Pair<Integer, Integer>>> backtrackingMatrix = generateBacktrackingMatrix();
        ArrayList<Pixel> lastRow = tempImage.get(tempImageHeight - 1);
        Pixel minimumCEPixel = lastRow.get(0);

        for (int i = 1; i < lastRow.size(); i++) {
            Pixel currPixel = lastRow.get(i);
            if (currPixel.getCumulativeEnergy() < minimumCEPixel.getCumulativeEnergy()) {
                minimumCEPixel = currPixel;
            }
        }

        Pair<Integer, Integer> currentPos = minimumCEPixel.getPos(); // start point of path
        Pair<Integer, Integer> nextPos = backtrackingMatrix.get(currentPos.getValue()).get(currentPos.getKey());
        ArrayList<Pair<Integer, Integer>> path = new ArrayList<>();

        while (currentPos != nextPos) {
            path.add(currentPos);
            currentPos = nextPos;
            nextPos = backtrackingMatrix.get(currentPos.getValue()).get(currentPos.getKey());
        }

        return path;
    }

    private void removeElements(ArrayList<Pair<Integer, Integer>> path) {
        for (Pair<Integer, Integer> pair : path) {
            int x = pair.getKey();
            int y = pair.getValue();
            tempImage.get(y).remove(x);
        }
        tempImageWidth--;
    }

    private void removeElementsOg(ArrayList<Pair<Integer, Integer>> path) {
        for (Pair<Integer, Integer> pair : path) {
            int x = pair.getKey();
            int y = pair.getValue();
            ogImage.get(y).remove(x);
        }
        ogImageWidth--;
    }

    private ArrayList<ArrayList<Pair<Integer, Integer>>> generateBacktrackingMatrix() {
        ArrayList<ArrayList<Pair<Integer, Integer>>> backtrackingMatrix = new ArrayList<>();
        for (int y = 0; y < tempImageHeight; y++) {
            ArrayList<Pair<Integer, Integer>> backtracking_row = new ArrayList<>();
            for (int x = 0; x < tempImageWidth; x++) {
                Pixel currentPixel = tempImage.get(y).get(x);
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
            backtrackingMatrix.add(backtracking_row);
        }
        return backtrackingMatrix;
    }

    private Pair<Pixel, Double> leastEnergyPixelFromAbove(Pixel currentPixel) {
        Pixel topLeft, topRight, topCenter, left, right;
        double addedEnergyFromLeft, addedEnergyFromRight, FE_left, FE_right, FE_center;
        ArrayList<Pair<Pixel, Double>> pathOptions = new ArrayList<>();

        topCenter = tempImage.get(currentPixel.getY() - 1).get(currentPixel.getX());
        FE_center = topCenter.getCumulativeEnergy() + currentPixel.getEnergy();
        pathOptions.add(new Pair<>(topCenter,FE_center));
        if (!(currentPixel.getX() == 0)){
            topLeft = tempImage.get(currentPixel.getY() - 1).get(currentPixel.getX() - 1);
            left = tempImage.get(currentPixel.getY()).get(currentPixel.getX() - 1);
            addedEnergyFromLeft = Math.abs(topCenter.getEnergy() - left.getEnergy());
            FE_left = topLeft.getCumulativeEnergy() + currentPixel.getEnergy() + addedEnergyFromLeft;
            pathOptions.add(new Pair<>(topLeft, FE_left));
        }
        if (!(currentPixel.getX() == tempImageWidth-1)) {
            topRight = tempImage.get(currentPixel.getY() - 1).get(currentPixel.getX() + 1);
            right = tempImage.get(currentPixel.getY()).get(currentPixel.getX() + 1);
            addedEnergyFromRight = Math.abs(topCenter.getEnergy() - right.getEnergy());
            FE_right = topRight.getCumulativeEnergy() + currentPixel.getEnergy() + addedEnergyFromRight;
            pathOptions.add(new Pair<>(topRight, FE_right));
        }

        Pair<Pixel, Double> idealPath = null;
        for (Pair<Pixel, Double> path: pathOptions) {
            if (idealPath == null || idealPath.getValue() >= path.getValue()) {
                idealPath = path;
            }
        }
        return idealPath;
    }

    private void horizontalCrop(int remove) {
        /* check for out-of-bounds crop */
        if (colsToRemove.size() < remove){
            System.out.println("Error: Cannot crop further than width of image.");
            return;
        }
        for (int col = 0; col < remove; col++) {
            removeElementsOg(colsToRemove.get(col));
        }
    }

    private void saveCroppedImage(String imagePathOut) {
        int imageHeight = ogImageHeight;
        int imageWidth = ogImageWidth;
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                image.setRGB(x, y, ogImage.get(y).get(x).getRGB());
            }
        }
        try {
            ImageIO.write(image, "png", new File(imagePathOut));
        } catch (Exception e) {
            System.err.print(e.getMessage());
        }
    }

    private static int wrappedIndex(int num, int size) {
        if (num >= 0 && num < size) {
            return num;
        }
        if (num >= 0) {
            return num % size;
        }
        return size + num;
    }

    private static ArrayList<ArrayList<Pixel>> deepCopy(ArrayList<ArrayList<Pixel>> old) {
        ArrayList<ArrayList<Pixel>> copy = new ArrayList<>();
        for (ArrayList<Pixel> row: old) {
            ArrayList<Pixel> newRow = new ArrayList<>();
            for (Pixel pixel: row) {
                newRow.add(pixel);
            }
            copy.add(newRow);
        }
        return copy;
    }

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        Image image = new Image("samples/sample1.jpg");
        long endTime1 = System.nanoTime();
        System.out.println("Initialization took " + (endTime1-startTime) + " nanoseconds");
        image.horizontalCrop(300);
        image.saveCroppedImage("samples/sample1-out3.png");
        long endTime2 = System.nanoTime();
        System.out.println("Cropping took " + (endTime2-endTime1) + " nanoseconds");
    }
}
