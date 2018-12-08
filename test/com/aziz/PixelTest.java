package com.aziz;

import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class PixelTest {

    Pixel samplePixel1 = null;
    Pixel samplePixel2 = null;
    Pixel samplePixel3 = null;
    Color color;

    @BeforeEach
    void setUp() {
        color = new Color(121,205,4, 1);
        samplePixel1 = new Pixel(color.getRGB(),1,1,50.0,129.2);
        samplePixel2 = new Pixel(color.getRGB(), 2, 3);
        samplePixel3 = new Pixel(color.getRGB());
    }

    @Test
    void constructors() {
        assertNotEquals(samplePixel1, null);
        assertNotEquals(samplePixel2, null);
        assertNotEquals(samplePixel3, null);
    }

    @Test
    void getRGB() {
        assertEquals(samplePixel1.getRGB(), color.getRGB());
        assertEquals(samplePixel2.getRGB(), color.getRGB());
        assertEquals(samplePixel3.getRGB(), color.getRGB());
    }

    @Test
    void getRed() {
        assertEquals(samplePixel1.getRed(), 121);
        assertEquals(samplePixel2.getRed(), 121);
        assertEquals(samplePixel3.getRed(), 121);
    }

    @Test
    void getGreen() {
        assertEquals(samplePixel1.getGreen(), 205);
        assertEquals(samplePixel2.getGreen(), 205);
        assertEquals(samplePixel3.getGreen(), 205);
    }

    @Test
    void getBlue() {
        assertEquals(samplePixel1.getBlue(), 4);
        assertEquals(samplePixel2.getBlue(), 4);
        assertEquals(samplePixel3.getBlue(), 4);
    }

    @Test
    void getAlpha() {
        assertEquals(samplePixel1.getAlpha(), 1);
        assertEquals(samplePixel2.getAlpha(), 1);
        assertEquals(samplePixel3.getAlpha(), 1);
    }

    @Test
    void getX() {
        assertEquals(samplePixel1.getX(), 1);
        assertEquals(samplePixel2.getX(), 2);
    }

    @Test
    void getY() {
        assertEquals(samplePixel1.getY(), 1);
        assertEquals(samplePixel2.getY(), 3);
    }

    @Test
    void getPos() {
        assertEquals(samplePixel1.getPos(), new Pair<>(samplePixel1.getX(), samplePixel1.getY()));
        assertEquals(samplePixel2.getPos(), new Pair<>(samplePixel2.getX(), samplePixel2.getY()));
    }

    @Test
    void getEnergy() {
        assertEquals(samplePixel1.getEnergy(), 50.0);
    }

    @Test
    void getCumulativeEnergy() {
        assertEquals(samplePixel1.getCumulativeEnergy(), 129.2);
    }

    @Test
    void setRGB() {
        Color newColor = new Color(1,2,3,4);
        samplePixel1.setRGB(newColor.getRGB());
        samplePixel2.setRGB(newColor.getRGB());
        samplePixel3.setRGB(newColor.getRGB());

        assertEquals(samplePixel1.getRGB(), newColor.getRGB());
        assertEquals(samplePixel2.getRGB(), newColor.getRGB());
        assertEquals(samplePixel3.getRGB(), newColor.getRGB());
    }

    @Test
    void setX() {
        samplePixel1.setX(57);
        samplePixel2.setX(57);
        samplePixel3.setX(57);

        assertEquals(samplePixel1.getX(), 57);
        assertEquals(samplePixel2.getX(), 57);
        assertEquals(samplePixel3.getX(), 57);
    }

    @Test
    void setY() {
        samplePixel1.setY(44);
        samplePixel2.setY(44);
        samplePixel3.setY(44);

        assertEquals(samplePixel1.getY(), 44);
        assertEquals(samplePixel2.getY(), 44);
        assertEquals(samplePixel3.getY(), 44);
    }

    @Test
    void setPos() {
        Pair<Integer, Integer> pos = new Pair(3,5);

        samplePixel1.setPos(pos);
        samplePixel2.setPos(pos);
        samplePixel3.setPos(pos);

        assertEquals(samplePixel1.getPos(), pos);
        assertEquals(samplePixel2.getPos(), pos);
        assertEquals(samplePixel3.getPos(), pos);
    }

    @Test
    void setEnergy() {
        samplePixel1.setEnergy(56.78);
        samplePixel2.setEnergy(56.78);
        samplePixel3.setEnergy(56.78);

        assertEquals(samplePixel1.getEnergy(), 56.78);
        assertEquals(samplePixel2.getEnergy(), 56.78);
        assertEquals(samplePixel3.getEnergy(), 56.78);
    }

    @Test
    void setCumulativeEnergy() {
        samplePixel1.setCumulativeEnergy(56.78);
        samplePixel2.setCumulativeEnergy(56.78);
        samplePixel3.setCumulativeEnergy(56.78);

        assertEquals(samplePixel1.getCumulativeEnergy(), 56.78);
        assertEquals(samplePixel2.getCumulativeEnergy(), 56.78);
        assertEquals(samplePixel3.getCumulativeEnergy(), 56.78);
    }
}