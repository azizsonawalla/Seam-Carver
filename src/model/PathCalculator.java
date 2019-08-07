package model;

import javafx.util.Pair;

import java.util.ArrayList;

public class PathCalculator {

    private EnergyMap energyMap;

    public PathCalculator(EnergyMap energyMap) {
        this.energyMap = energyMap;
    }

    public  ArrayList<ArrayList<Pair<Integer, Integer>>> getPaths() {
        ArrayList<ArrayList<Pair<Integer, Integer>>> paths = new ArrayList<>();
        energyMap.updateAllPixelEnergies();
        ArrayList<Pixel> pixelPath;
        int originalWidth = energyMap.width();

        for (int col = 0; col < originalWidth-1; col++) {
            pixelPath = energyMap.leastEnergyVerticalPath();
            ArrayList<Pair<Integer, Integer>> currPosPath = new ArrayList<>();
            for (Pixel pixel: pixelPath) currPosPath.add(pixel.getPos());
            ArrayList<Pair<Integer, Integer>> originalPosPath = new ArrayList<>();
            for (Pixel pixel: pixelPath) originalPosPath.add(pixel.getOriginalPos());

            energyMap.removePixels(currPosPath);
            paths.add(originalPosPath);
            energyMap.updateSelectedPixelPositions(currPosPath);
            energyMap.updateSelectedPixelEnergies(currPosPath);
        }
        return paths;
    }
}
