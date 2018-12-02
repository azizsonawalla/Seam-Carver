package com.aziz;

import javafx.util.Pair;

public class Pixel {

    private int RGB;
    private int X;
    private int Y;
    private double ENERGY;
    private double CUMULATIVE_ENERGY;

    public Pixel(int rgb, int x, int y, double energy, double cumulative_energy) {
        this.RGB = rgb;
        this.X = x;
        this.Y = y;
        this.ENERGY = energy;
        this.CUMULATIVE_ENERGY = cumulative_energy;
    }

    public Pixel(int rgb, int x, int y) {
        this.RGB = rgb;
        this.X = x;
        this.Y = y;
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

    public int getAlpha() {
        return (getRGB() >> 24) & 0xff;
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

    public void setRGB(int rgb) {
        this.RGB = rgb;
    }

    public void setX(int x) {
        this.X = x;
    }

    public void setY(int y) {
        this.Y = y;
    }

    public void setPos(Pair<Integer,Integer> pos) {
        this.X = pos.getKey();
        this.Y = pos.getValue();
    }

    public void setEnergy(double energy) {
        this.ENERGY = energy;
    }

    public void setCumulativeEnergy(double cumulativeEnergy) {
        this.CUMULATIVE_ENERGY = cumulativeEnergy;
    }
}
