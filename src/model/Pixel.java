package model;

import javafx.util.Pair;

/**
 * A class to represent a pixel in an image to be cropped using Smart Crop
 */
public class Pixel {

    private int RGB;
    private int X = -1;
    private int Y = -1;
    private double ENERGY;
    private double CUMULATIVE_ENERGY;

    private int originalX;
    private int originalY;

    private boolean active = true;

    public Pixel(int rgb, int x, int y, double energy, double cumulative_energy) {
        RGB = rgb;
        X = originalX = x;
        Y = originalY = y;
        ENERGY = energy;
        CUMULATIVE_ENERGY = cumulative_energy;
    }

    public Pixel(int rgb, int x, int y) {
        this.RGB = rgb;
        X = originalX = x;
        Y = originalY = y;
    }

    public Pixel(int rgb) {
        this.RGB = rgb;
    }

    public int getRGB(){
        return this.RGB;
    }

    public int getRed(){
        return (getRGB() >> 16) & 0xFF;
    }

    public int getGreen(){
        return (getRGB() >> 8) & 0xFF;
    }

    public int getBlue(){
        return (getRGB()) & 0xFF;
    }

    public int getX() {
        return this.X;
    }

    public int getY() {
        return this.Y;
    }

    public Pair<Integer,Integer> getPos() {
        return new Pair<>(this.X, this.Y);
    }

    public double getEnergy() {
        return this.ENERGY;
    }

    public double getCumulativeEnergy() {
        return this.CUMULATIVE_ENERGY;
    }

    public void setPos(Pair<Integer,Integer> pos) {
        if (X == -1 || Y == -1) {
            X = originalX = pos.getKey();
            Y = originalY = pos.getValue();
        } else {
            X = pos.getKey();
            Y = pos.getValue();
        }
    }

    public void setEnergy(double energy) {
        this.ENERGY = energy;
    }

    public void setCumulativeEnergy(double cumulativeEnergy) {
        this.CUMULATIVE_ENERGY = cumulativeEnergy;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
