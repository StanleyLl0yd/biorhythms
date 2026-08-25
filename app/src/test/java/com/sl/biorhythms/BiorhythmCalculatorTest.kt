package com.sl.biorhythms

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import kotlin.math.abs

class BiorhythmCalculatorTest {

    @Test
    fun allCyclesAreZeroOnBirthDate() {
        val birthDate = LocalDate.of(1990, 1, 1)

        assertEquals(0.0, BiorhythmCalculator.value(birthDate, birthDate, BiorhythmCalculator.PHYSICAL_PERIOD), 1e-12)
        assertEquals(0.0, BiorhythmCalculator.value(birthDate, birthDate, BiorhythmCalculator.EMOTIONAL_PERIOD), 1e-12)
        assertEquals(0.0, BiorhythmCalculator.value(birthDate, birthDate, BiorhythmCalculator.INTELLECTUAL_PERIOD), 1e-12)
    }

    @Test
    fun eachCycleReturnsToZeroAfterItsFullPeriod() {
        val birthDate = LocalDate.of(1990, 1, 1)

        assertTrue(abs(BiorhythmCalculator.value(birthDate, birthDate.plusDays(23), BiorhythmCalculator.PHYSICAL_PERIOD)) < 1e-12)
        assertTrue(abs(BiorhythmCalculator.value(birthDate, birthDate.plusDays(28), BiorhythmCalculator.EMOTIONAL_PERIOD)) < 1e-12)
        assertTrue(abs(BiorhythmCalculator.value(birthDate, birthDate.plusDays(33), BiorhythmCalculator.INTELLECTUAL_PERIOD)) < 1e-12)
    }

    @Test
    fun percentConvertsAndClampsValues() {
        assertEquals(100.0, BiorhythmCalculator.percent(1.0), 0.0)
        assertEquals(-100.0, BiorhythmCalculator.percent(-1.0), 0.0)
        assertEquals(50.0, BiorhythmCalculator.percent(0.5), 0.0)
        assertEquals(100.0, BiorhythmCalculator.percent(5.0), 0.0)
        assertEquals(-100.0, BiorhythmCalculator.percent(-5.0), 0.0)
    }

    @Test
    fun dayCountHandlesLeapYearCorrectly() {
        val leapDay = LocalDate.of(2000, 2, 29)
        val nextYear = LocalDate.of(2001, 2, 28)

        assertEquals(365L, BiorhythmCalculator.daysFromBirth(leapDay, nextYear))
    }

    @Test
    fun valueForDateMatchesValueForExplicitDayCount() {
        val birthDate = LocalDate.of(1977, 12, 1)
        val date = LocalDate.of(2026, 8, 25)
        val days = BiorhythmCalculator.daysFromBirth(birthDate, date)

        assertEquals(
            BiorhythmCalculator.value(days, BiorhythmCalculator.PHYSICAL_PERIOD),
            BiorhythmCalculator.value(birthDate, date, BiorhythmCalculator.PHYSICAL_PERIOD),
            1e-12,
        )
    }
}
