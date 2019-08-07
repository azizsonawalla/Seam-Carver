package model;

import javafx.util.Pair;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class EnergyMap {

    private ArrayList<ArrayList<Pixel>> data;

    public EnergyMap(ArrayList<ArrayList<Pixel>> data) {
        this.data = data;
    }

    public EnergyMap(EnergyMap other) {
        data = new ArrayList<>();
        for (ArrayList<Pixel> row: other.getData()) {
            ArrayList<Pixel> newRow = new ArrayList<>(row);
            data.add(newRow);
        }
    }

    public EnergyMap(BufferedImage image) {
        data = new ArrayList<>();
        for (int y = 0; y < image.getHeight(); y++) {
            ArrayList<Pixel> row = new ArrayList<>();
            for (int x = 0; x < image.getWidth(); x++) {
                Pixel curr = new Pixel(image.getRGB(x, y));
                curr.setPos(new Pair<>(x,y));
                row.add(curr);
            }
            data.add(row);
        }
    }

    public int width() {
        return data.get(0).size();
    }

    public int height() {
        return data.size();
    }

    public ArrayList<ArrayList<Pixel>> getData(){
        return data;
    }

    public Pixel getPixel(int x, int y) {
        return data.get(y).get(x);
    }

    public ArrayList<Pixel> getPixelRow(int y) {
        return data.get(y);
    }

    public void removePixels(ArrayList<Pair<Integer, Integer>> positions) {
        for (Pair<Integer, Integer> pair : positions) {
            int x = pair.getKey();
            int y = pair.getValue();
            getPixelRow(y).remove(x);
        }
    }

    public void setInactivePixels(ArrayList<Pair<Integer, Integer>> positions) {
        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                Pixel currentPixel = getPixel(x, y);
                currentPixel.setActive(true);
            }
        }
        for (Pair<Integer, Integer> pos: positions) {
            getPixel(pos.getKey(), pos.getValue()).setActive(false);
        }
    }

    public void updateAllPixelEnergies() {
        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                int mod_x = wrappedIndex(x, width());
                Pixel currentPixel = getPixel(mod_x, y);
                currentPixel.setEnergy(energyValueOf(mod_x, y));
            }
        }
    }

    public void updateSelectedPixelEnergies(ArrayList<Pair<Integer, Integer>> path) {
        int x_min = path.get(0).getKey();
        int x_max = path.get(0).getKey();
        for (int i = 1; i < path.size(); i++) {
            Pair<Integer,Integer> pos = path.get(i);
            int pos_key = pos.getKey();
            x_min = Math.min(x_min, pos_key);
            x_max = Math.max(x_max, pos_key);
        }
        for (int y = 0; y < height(); y++) {
            for (int x = x_min; x < x_max; x++) {
                int mod_x = wrappedIndex(x, width());
                Pixel currentPixel = getPixel(mod_x, y);
                currentPixel.setEnergy(energyValueOf(mod_x, y));
            }
        }
    }

    public void updateSelectedPixelPositions(ArrayList<Pair<Integer, Integer>> path) {
        for (Pair<Integer, Integer> pos: path) {
            int x_start = wrappedIndex(pos.getKey(), width());
            int y = pos.getValue();
            for (int x = x_start; x < width(); x++) {
                Pixel currentPixel = getPixel(x, y);
                currentPixel.setPos(new Pair<>(x, y));
            }
        }
    }

    private double energyValueOf(int x, int y) {
        Pixel xPrev = getPixel(wrappedIndex(x - 1, width()), y);
        Pixel xNext = getPixel(wrappedIndex(x + 1, width()) ,y);
        Pixel yPrev = getPixel(x,wrappedIndex(y - 1, height()));
        Pixel yNext = getPixel(x,wrappedIndex(y + 1, height()));

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

    public ArrayList<Pixel> leastEnergyVerticalPath() {
        ArrayList<ArrayList<Pair<Integer, Integer>>> backtrackingMatrix = generateBacktrackingMatrix();
        ArrayList<Pixel> lastRow = getPixelRow(height() - 1);

        Pixel minimumCEPixel = lastRow.get(0);
        for (int i = 1; i < lastRow.size(); i++) {
            Pixel currPixel = lastRow.get(i);
            if (currPixel.getCumulativeEnergy() < minimumCEPixel.getCumulativeEnergy()) {
                minimumCEPixel = currPixel;
            }
        }

        Pair<Integer, Integer> currentPos = minimumCEPixel.getPos(); // start point of path
        Pair<Integer, Integer> nextPos = backtrackingMatrix.get(currentPos.getValue()).get(currentPos.getKey());
        ArrayList<Pixel> path = new ArrayList<>();
        while (currentPos != nextPos) {
            path.add(getPixel(currentPos.getKey(),currentPos.getValue()));
            currentPos = nextPos;
            nextPos = backtrackingMatrix.get(currentPos.getValue()).get(currentPos.getKey());
        }
        return path;
    }

    private ArrayList<ArrayList<Pair<Integer, Integer>>> generateBacktrackingMatrix() {
        ArrayList<ArrayList<Pair<Integer, Integer>>> backtrackingMatrix = new ArrayList<>();
        for (int y = 0; y < height(); y++) {
            ArrayList<Pair<Integer, Integer>> backtracking_row = new ArrayList<>();
            for (int x = 0; x < width(); x++) {
                Pixel currentPixel = getPixel(x,y);
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

        topCenter = getPixel(currentPixel.getX(), currentPixel.getY() - 1);
        FE_center = topCenter.getCumulativeEnergy() + currentPixel.getEnergy();
        pathOptions.add(new Pair<>(topCenter,FE_center));
        if (!(currentPixel.getX() == 0)){
            topLeft = getPixel(currentPixel.getX() - 1,currentPixel.getY() - 1);
            left = getPixel(currentPixel.getX() - 1,currentPixel.getY());
            addedEnergyFromLeft = Math.abs(topCenter.getEnergy() - left.getEnergy());
            FE_left = topLeft.getCumulativeEnergy() + currentPixel.getEnergy() + addedEnergyFromLeft;
            pathOptions.add(new Pair<>(topLeft, FE_left));
        }
        if (!(currentPixel.getX() == width()-1)) {
            topRight = getPixel(currentPixel.getX() + 1,currentPixel.getY() - 1);
            right = getPixel(currentPixel.getX() + 1, currentPixel.getY());
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

    private static int wrappedIndex(int num, int size) {
        if (num >= 0 && num < size) {
            return num;
        }
        if (num >= 0) {
            return num % size;
        }
        return size + num;
    }
}
