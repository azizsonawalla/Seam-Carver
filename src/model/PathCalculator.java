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
        ArrayList<Pair<Integer, Integer>> path;
        int originalWidth = energyMap.width();
        for (int col = 0; col < originalWidth-1; col++) {
            path = energyMap.leastEnergyVerticalPath();
            energyMap.removeElements(path);
            paths.add(path);
            energyMap.updateSelectedPixelPositions(path);
            energyMap.updateSelectedPixelEnergies(path);
        }
        return paths;
    }
}
