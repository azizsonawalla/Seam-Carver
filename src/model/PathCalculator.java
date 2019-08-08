package model;

import javafx.util.Pair;

import java.util.ArrayList;

public class PathCalculator {

    private EnergyMatrix energyMatrix;

    public PathCalculator(EnergyMatrix energyMatrix) {
        this.energyMatrix = energyMatrix;
    }

    public  ArrayList<ArrayList<Pair<Integer, Integer>>> getPaths() {
        ArrayList<ArrayList<Pair<Integer, Integer>>> paths = new ArrayList<>();
        energyMatrix.updateAllPixelEnergies();
        ArrayList<Pixel> pixelPath;
        int originalWidth = energyMatrix.width();

        for (int col = 0; col < originalWidth-1; col++) {
            pixelPath = energyMatrix.leastEnergyVerticalPath();
            ArrayList<Pair<Integer, Integer>> currPosPath = new ArrayList<>();
            for (Pixel pixel: pixelPath) currPosPath.add(pixel.getPos());
            ArrayList<Pair<Integer, Integer>> originalPosPath = new ArrayList<>();
            for (Pixel pixel: pixelPath) originalPosPath.add(pixel.getOriginalPos());

            energyMatrix.removePixels(currPosPath);
            paths.add(originalPosPath);
            energyMatrix.updateSelectedPixelPositions(currPosPath);
            energyMatrix.updateSelectedPixelEnergies(currPosPath);
        }
        return paths;
    }
}
