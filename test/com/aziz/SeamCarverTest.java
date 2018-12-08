package com.aziz;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    }

    @Test
    void energyValueOf() {
    }

    @Test
    void mod() {
        assertEquals(test.mod(0,100), 0);
        assertEquals(test.mod(1,100), 1);
        assertEquals(test.mod(-1,100), 98);
        assertEquals(test.mod(99,100), 99);
        assertEquals(test.mod(100,100), 0);
        assertEquals(test.mod(101,100), 1);
    }

    @Test
    void leastEnergyVerticalPath() {
    }
}