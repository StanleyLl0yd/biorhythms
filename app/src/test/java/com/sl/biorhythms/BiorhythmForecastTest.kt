package com.sl.biorhythms

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BiorhythmForecastTest {
    private val birthDate = LocalDate.of(1990, 1, 1)

    @Test
    fun forecastContainsSevenConsecutiveDaysByDefault() {
        val startDate = LocalDate.of(2026, 9, 2)
        val forecast = BiorhythmForecast.days(birthDate, startDate)

        assertEquals(7, forecast.size)
        assertEquals(startDate, forecast.first().date)
        assertEquals(startDate.plusDays(6), forecast.last().date)
    }

    @Test
    fun birthDateIsCriticalAndCyclesAreRising() {
        val forecast = BiorhythmForecast.day(birthDate, birthDate)

        forecast.cycles.forEach { cycle ->
            assertEquals(BiorhythmEvent.CRITICAL, cycle.event)
            assertEquals(BiorhythmTrend.RISING, cycle.trend)
            assertEquals(0.0, cycle.percent, 0.000001)
        }
    }

    @Test
    fun physicalCycleDetectsPeakAndLow() {
        val peak = BiorhythmForecast.cycle(
            birthDate,
            birthDate.plusDays(6),
            BiorhythmCalculator.PHYSICAL_PERIOD,
        )
        val low = BiorhythmForecast.cycle(
            birthDate,
            birthDate.plusDays(17),
            BiorhythmCalculator.PHYSICAL_PERIOD,
        )

        assertEquals(BiorhythmEvent.PEAK, peak.event)
        assertTrue(peak.percent > 99.0)
        assertEquals(BiorhythmEvent.LOW, low.event)
        assertTrue(low.percent < -99.0)
    }

    @Test
    fun emotionalHalfCycleIsCritical() {
        val critical = BiorhythmForecast.cycle(
            birthDate,
            birthDate.plusDays(14),
            BiorhythmCalculator.EMOTIONAL_PERIOD,
        )

        assertEquals(BiorhythmEvent.CRITICAL, critical.event)
        assertEquals(0.0, critical.percent, 0.000001)
    }

    @Test
    fun synchronizedExtremeUsesAllThreeCyclesAndInclusiveThreshold() {
        assertEquals(
            SynchronizedExtreme.HIGH,
            BiorhythmForecast.synchronizedExtreme(listOf(80.0, 91.0, 100.0)),
        )
        assertEquals(
            SynchronizedExtreme.LOW,
            BiorhythmForecast.synchronizedExtreme(listOf(-80.0, -95.0, -83.0)),
        )
        assertNull(BiorhythmForecast.synchronizedExtreme(listOf(80.0, 79.9, 95.0)))
        assertNull(BiorhythmForecast.synchronizedExtreme(listOf(90.0, 90.0)))
    }
}
