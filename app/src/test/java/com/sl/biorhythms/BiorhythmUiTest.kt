package com.sl.biorhythms

import org.junit.Assert.assertEquals
import org.junit.Test

class BiorhythmUiTest {

    @Test
    fun chartOffsetMapsEdgesAndCenter() {
        assertEquals(-15, chartOffsetForPosition(0f, 300f, 15, 15))
        assertEquals(0, chartOffsetForPosition(150f, 300f, 15, 15))
        assertEquals(15, chartOffsetForPosition(300f, 300f, 15, 15))
    }

    @Test
    fun chartOffsetClampsOutsideChartBounds() {
        assertEquals(-15, chartOffsetForPosition(-100f, 300f, 15, 15))
        assertEquals(15, chartOffsetForPosition(500f, 300f, 15, 15))
    }

    @Test
    fun chartOffsetHandlesMissingWidth() {
        assertEquals(0, chartOffsetForPosition(100f, 0f, 15, 15))
    }

    @Test
    fun chartGridUsesFiveDayIntervalsAndKeepsEdges() {
        assertEquals(
            listOf(-15, -10, -5, 0, 5, 10, 15),
            chartGridOffsets(15, 15),
        )
        assertEquals(
            listOf(-12, -10, -5, 0, 5, 9),
            chartGridOffsets(12, 9),
        )
    }
}
