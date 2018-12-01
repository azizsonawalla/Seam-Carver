package com.aziz;

import javafx.util.Pair;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class SeamCarver {

    /* 2D array with ENERGY_MATRIX values for all pixels */
    private ArrayList<ArrayList<Double>> ENERGY_MATRIX;
    /* Image file to seam carve */
    private BufferedImage INPUT_IMAGE;
    /* 2D array with RGB values of input image */
    private ArrayList<ArrayList<Integer>> WORKING_IMAGE;


    public SeamCarver(String imagePath) {
        // load WORKING_IMAGE from file
        File imageFile = new File(imagePath); //TODO: Add correct WORKING_IMAGE path
        try {
            INPUT_IMAGE = ImageIO.read(imageFile);
        } catch (Exception e) {
            System.err.print(e.getMessage());
            return;
        }

        // convert rgb values of input image to array
        WORKING_IMAGE = new ArrayList<ArrayList<Integer>>();
        for (int y = 0; y < INPUT_IMAGE.getHeight(); y++) {
            ArrayList<Integer> row = new ArrayList<>();
            for (int x = 0; x < INPUT_IMAGE.getWidth(); x++) {
                row.add(INPUT_IMAGE.getRGB(x, y));
            }
            WORKING_IMAGE.add(row);
        }
    }

    public void carve() {
        // initialize ENERGY_MATRIX array
        ENERGY_MATRIX = new ArrayList<ArrayList<Double>>();
        for (int y = 0; y < WORKING_IMAGE.size(); y++) {
            ArrayList<Double> row = new ArrayList<>();
            for (int x = 0; x < WORKING_IMAGE.get(0).size(); x++) {
                row.add(energyValueOf(x, y));
            }
            ENERGY_MATRIX.add(row);
        }
        System.out.print(Arrays.deepToString(ENERGY_MATRIX.toArray()));

        // remove least energy rows
        ArrayList<Pair<Integer,Integer>> path = leastEnergyVerticalPath();
        WORKING_IMAGE = removeElements(path, WORKING_IMAGE);

        // create carved image
        int carvedImageHeight = WORKING_IMAGE.size();
        int carvedImageWidth = WORKING_IMAGE.get(0).size();
        BufferedImage carvedImage = new BufferedImage(carvedImageWidth, carvedImageHeight, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < carvedImageHeight; y++) {
            for (int x = 0; x < carvedImageWidth; x++) {
                carvedImage.setRGB(x, y, WORKING_IMAGE.get(y).get(x));
            }
        }

        // write carvedImage back to file
        try {
            ImageIO.write(carvedImage, "png", new File("sample-images/sample1-carved.png"));
        } catch (Exception e) {
            System.err.print(e.getMessage());
        }
    }

    private ArrayList<ArrayList<Integer>> removeElements(ArrayList<Pair<Integer,Integer>> path, ArrayList<ArrayList<Integer>> array) {
        for (int i = 0; i < path.size(); i++) {
            Pair<Integer,Integer> pair = path.get(i);
            int x = pair.getKey();
            int y = pair.getValue();
            array.get(y).remove(x);
        }
        return array;
    }

    private double energyValueOf(int x, int y) {
        /* Using dual gradient energy function */

        // calculate neighbours
        ArrayList<Pair<Integer,Integer>> xNeighbours = getXNeighbours(x, y);
        ArrayList<Pair<Integer,Integer>> yNeighbours = getYNeighbours(x, y);
        Pair<Integer,Integer> xPrev = xNeighbours.get(0);
        Pair<Integer,Integer> xNext = xNeighbours.get(1);
        Pair<Integer,Integer> yPrev = yNeighbours.get(0);
        Pair<Integer,Integer> yNext = yNeighbours.get(1);

        // get RGB values of neighbours
        Color xPrevRGB = new Color(WORKING_IMAGE.get(xPrev.getValue()).get(xPrev.getKey()));
        Color xNextRGB = new Color(WORKING_IMAGE.get(xNext.getValue()).get(xNext.getKey()));
        Color yPrevRGB = new Color(WORKING_IMAGE.get(yPrev.getValue()).get(yPrev.getKey()));
        Color yNextRGB = new Color(WORKING_IMAGE.get(yNext.getValue()).get(yNext.getKey()));

        // calculate RGB gradients in x direction
        double deltaX_Red = Math.abs(xPrevRGB.getRed() - xNextRGB.getRed());
        double deltaX_Green = Math.abs(xPrevRGB.getGreen() - xNextRGB.getGreen());
        double deltaX_Blue = Math.abs(xPrevRGB.getBlue() - xNextRGB.getBlue());

        // calculate RGB gradients in y direction
        double deltaY_Red = Math.abs(yPrevRGB.getRed() - yNextRGB.getRed());
        double deltaY_Green = Math.abs(yPrevRGB.getGreen() - yNextRGB.getGreen());
        double deltaY_Blue = Math.abs(yPrevRGB.getGreen() - yNextRGB.getGreen());

        // calculate x and y gradients
        double deltaX = Math.pow(deltaX_Red, 2) + Math.pow(deltaX_Green, 2) + Math.pow(deltaX_Blue, 2);
        double deltaY = Math.pow(deltaY_Red, 2) + Math.pow(deltaY_Green, 2) + Math.pow(deltaY_Blue, 2);

        return deltaX + deltaY;
    }

    private ArrayList<Pair<Integer,Integer>> getXNeighbours(int x, int y) {
        int xPrevX = x - 1;
        int xNextX = x + 1;

        if (xPrevX < 0) {
            xPrevX = WORKING_IMAGE.get(0).size() -1;
        }

        if (xNextX > WORKING_IMAGE.get(0).size() -1) {
            xPrevX = 0;
        }

        Pair<Integer,Integer> xPrev = new Pair<>(xPrevX, y);
        Pair<Integer,Integer> xNext = new Pair<>(xNextX, y);

        ArrayList<Pair<Integer,Integer>> xNeighbours = new ArrayList<>();
        xNeighbours.add(xPrev);
        xNeighbours.add(xNext);
        return xNeighbours;
    }

    private ArrayList<Pair<Integer,Integer>> getYNeighbours(int x, int y) {
        int yPrevY = y - 1;
        int yNextY = y + 1;

        if (yPrevY < 0) {
            yPrevY = WORKING_IMAGE.size() -1;
        }

        if (yNextY > WORKING_IMAGE.size() -1) {
            yNextY = 0;
        }

        Pair<Integer,Integer> yPrev = new Pair<>(x, yPrevY);
        Pair<Integer,Integer> yNext = new Pair<>(x, yNextY);

        ArrayList<Pair<Integer,Integer>> yNeighbours = new ArrayList<>();
        yNeighbours.add(yPrev);
        yNeighbours.add(yNext);
        return yNeighbours;
    }

    private ArrayList<Pair<Integer, Integer>> leastEnergyVerticalPath() {
        //TODO: Returns least ENERGY_MATRIX path from  given WORKING_IMAGE
        ArrayList<Pair<Integer,Integer>> path = new ArrayList<Pair<Integer,Integer>>();
        for (int i = 0; i < 450; i++) {
            path.add(new Pair<>(0,i));
        }
        return path;
    }

    public static void main(String[] args) {
        SeamCarver carver = new SeamCarver("sample-images/sample1.png");
        carver.carve();
    }
}
