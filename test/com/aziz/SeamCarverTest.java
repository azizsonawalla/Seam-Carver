package com.aziz;

import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

import static jdk.nashorn.internal.objects.Global.print;
import static org.junit.jupiter.api.Assertions.*;

class SeamCarverTest {

    SeamCarver test;

    @BeforeEach
    void setUp() {
        test = new SeamCarver("path");
    }

    @Test
    void minimumPixelAbove() {
    }

    @Test
    void removeElements() {

        Color color00 = new Color(5, 10, 15);
        Color color10 = new Color(10,15,10);
        Color color20 = new Color(20,20,15);
        Color color30 = new Color(50,1,2);

        Color color01 = new Color(50,1,2);
        Color color11 = new Color(5,10,15);
        Color color21 = new Color(10,5,15);
        Color color31 = new Color(3,10,15);

        Color color02 = new Color(3,10,15);
        Color color12 = new Color(5,30,15);
        Color color22 = new Color(5,10,5);
        Color color32 = new Color(5, 10, 15);

        Color color03 = new Color(50,1,2);
        Color color13 = new Color(5,10,15);
        Color color23 = new Color(10,5,15);
        Color color33 = new Color(3,10,15);

        ArrayList<ArrayList<Pixel>> PIXEL_ARRAY = new ArrayList<>();
        ArrayList<Pixel> row1 = new ArrayList<>();
        row1.add(new Pixel(color00.getRGB(), 0, 0));
        row1.add(new Pixel(color10.getRGB(), 1, 0));
        row1.add(new Pixel(color20.getRGB(), 2, 0));
        row1.add(new Pixel(color30.getRGB(), 3, 0));
        ArrayList<Pixel> row2 = new ArrayList<>();
        row2.add(new Pixel(color01.getRGB(), 0, 1));
        row2.add(new Pixel(color11.getRGB(), 1, 1));
        row2.add(new Pixel(color21.getRGB(), 2, 1));
        row2.add(new Pixel(color31.getRGB(), 3, 1));
        ArrayList<Pixel> row3 = new ArrayList<>();
        row3.add(new Pixel(color02.getRGB(), 0, 2));
        row3.add(new Pixel(color12.getRGB(), 1, 2));
        row3.add(new Pixel(color22.getRGB(), 2, 2));
        row3.add(new Pixel(color32.getRGB(), 3, 2));
        ArrayList<Pixel> row4 = new ArrayList<>();
        row4.add(new Pixel(color03.getRGB(), 0, 3));
        row4.add(new Pixel(color13.getRGB(), 1, 3));
        row4.add(new Pixel(color23.getRGB(), 2, 3));
        row4.add(new Pixel(color33.getRGB(), 3, 3));
        PIXEL_ARRAY.add(row1);
        PIXEL_ARRAY.add(row2);
        PIXEL_ARRAY.add(row3);
        PIXEL_ARRAY.add(row4);
        test.setPixelArray(PIXEL_ARRAY);
        //System.out.print(test.getPixelArray());
        ArrayList<Pair<Integer,Integer>> path = new ArrayList<>();
        path.add(new Pair<>(1,0));
        path.add(new Pair<>(0,1));
        path.add(new Pair<>(1,2));
        path.add(new Pair<>(2,3));
        //System.out.print(path);
        test.removeElements(path);

        ArrayList<ArrayList<Pixel>> PIXEL_ARRAY_2 = new ArrayList<>();
        ArrayList<Pixel> row1_2 = row1;
        row1_2.remove(1);
        ArrayList<Pixel> row2_2 = row2;
        row2_2.remove(0);
        ArrayList<Pixel> row3_2 = row3;
        row3_2.remove(1);
        ArrayList<Pixel> row4_2 = row4;
        row4_2.remove(2);
        PIXEL_ARRAY_2.add(row1_2);
        PIXEL_ARRAY_2.add(row2_2);
        PIXEL_ARRAY_2.add(row3_2);
        PIXEL_ARRAY_2.add(row4_2);

        assertEquals(PIXEL_ARRAY_2, test.getPixelArray());

    }

    @Test
    void energyValueOf() {
        /*
        (5,10,15)   (10,15,10)  (20,20,15)
        (50,1,2)    (5,10,15)   (10,5,15)
        (3,10,15)   (5,30,15)   (5,10,5)
         */

        Color color00 = new Color(5, 10, 15);
        Color color10 = new Color(10,15,10);
        Color color20 = new Color(20,20,15);

        Color color01 = new Color(50,1,2);
        Color color11 = new Color(5,10,15);
        Color color21 = new Color(10,5,15);

        Color color02 = new Color(3,10,15);
        Color color12 = new Color(5,30,15);
        Color color22 = new Color(5,10,5);

        ArrayList<ArrayList<Pixel>> PIXEL_ARRAY = new ArrayList<>();
        ArrayList<Pixel> row1 = new ArrayList<>();
        row1.add(new Pixel(color00.getRGB(), 0, 0));
        row1.add(new Pixel(color10.getRGB(), 1, 0));
        row1.add(new Pixel(color20.getRGB(), 2, 0));
        ArrayList<Pixel> row2 = new ArrayList<>();
        row2.add(new Pixel(color01.getRGB(), 0, 1));
        row2.add(new Pixel(color11.getRGB(), 1, 1));
        row2.add(new Pixel(color21.getRGB(), 2, 1));
        ArrayList<Pixel> row3 = new ArrayList<>();
        row3.add(new Pixel(color02.getRGB(), 0, 2));
        row3.add(new Pixel(color12.getRGB(), 1, 2));
        row3.add(new Pixel(color22.getRGB(), 2, 2));
        PIXEL_ARRAY.add(row1);
        PIXEL_ARRAY.add(row2);
        PIXEL_ARRAY.add(row3);
        test.setPixelArray(PIXEL_ARRAY);

        assertEquals(3, PIXEL_ARRAY.size());
        assertEquals(3, PIXEL_ARRAY.get(0).size());

        assertEquals(((10*10)+(5*5)+(5*5)) +((47*47)+(9*9)+(13*13)), test.energyValueOf(0,0));
        //assertEquals(0, test.energyValueOf(1,0));
        //assertEquals(0, test.energyValueOf(2,0));

        //assertEquals(0, test.energyValueOf(0,1));
        assertEquals(((40*40) + (4*4) + (13*13)) + ((5*5) + (15*15) + (5*5)), test.energyValueOf(1,1));
        //assertEquals(0, test.energyValueOf(2,1));

        //assertEquals(0, test.energyValueOf(0,2));
        //assertEquals(0, test.energyValueOf(1,2));
        //assertEquals(0, test.energyValueOf(2,2));

    }

    @Test
    void mod() {
        assertEquals(test.mod(0,100), 0);
        assertEquals(test.mod(1,100), 1);
        assertEquals(test.mod(-1,100), 99);
        assertEquals(test.mod(99,100), 99);
        assertEquals(test.mod(100,100), 0);
        assertEquals(test.mod(101,100), 1);
    }

    @Test
    void leastEnergyVerticalPath() {
    }
}