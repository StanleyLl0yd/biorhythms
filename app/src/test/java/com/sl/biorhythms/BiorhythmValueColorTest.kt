package com.sl.biorhythms

import org.junit.Assert.assertEquals
import org.junit.Test

class BiorhythmValueColorTest {

    @Test
    fun endpointsAndZeroUseExpectedColors() {
        assertEquals(0xFFFF3B30.toInt(), BiorhythmValueColor.argb(-100.0))
        assertEquals(0xFFFFCC00.toInt(), BiorhythmValueColor.argb(0.0))
        assertEquals(0xFF34C759.toInt(), BiorhythmValueColor.argb(100.0))
    }

    @Test
    fun valuesOutsideRangeAreClamped() {
        assertEquals(BiorhythmValueColor.argb(-100.0), BiorhythmValueColor.argb(-500.0))
        assertEquals(BiorhythmValueColor.argb(100.0), BiorhythmValueColor.argb(500.0))
    }

    @Test
    fun midpointColorsAreDeterministic() {
        assertEquals(0xFFFF8418.toInt(), BiorhythmValueColor.argb(-50.0))
        assertEquals(0xFF9ACA2C.toInt(), BiorhythmValueColor.argb(50.0))
    }
}
