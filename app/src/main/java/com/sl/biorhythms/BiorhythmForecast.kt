package com.sl.biorhythms

import java.time.LocalDate
import kotlin.math.abs

enum class BiorhythmTrend {
    RISING,
    FALLING,
    STEADY,
}

enum class BiorhythmEvent {
    CRITICAL,
    PEAK,
    LOW,
}

enum class SynchronizedExtreme {
    HIGH,
    LOW,
}

data class BiorhythmCycleForecast(
    val percent: Double,
    val trend: BiorhythmTrend,
    val event: BiorhythmEvent?,
)

data class BiorhythmDayForecast(
    val date: LocalDate,
    val cycles: List<BiorhythmCycleForecast>,
    val synchronizedExtreme: SynchronizedExtreme?,
)

object BiorhythmForecast {
    const val SYNCHRONIZED_EXTREME_THRESHOLD = 80.0
    const val DEFAULT_FORECAST_DAYS = 7

    private val classicPeriods = listOf(
        BiorhythmCalculator.PHYSICAL_PERIOD,
        BiorhythmCalculator.EMOTIONAL_PERIOD,
        BiorhythmCalculator.INTELLECTUAL_PERIOD,
    )

    fun day(birthDate: LocalDate, date: LocalDate): BiorhythmDayForecast {
        val cycles = classicPeriods.map { period -> cycle(birthDate, date, period) }
        return BiorhythmDayForecast(
            date = date,
            cycles = cycles,
            synchronizedExtreme = synchronizedExtreme(cycles.map(BiorhythmCycleForecast::percent)),
        )
    }

    fun days(
        birthDate: LocalDate,
        startDate: LocalDate,
        count: Int = DEFAULT_FORECAST_DAYS,
    ): List<BiorhythmDayForecast> {
        require(count > 0) { "count must be positive" }
        return List(count) { offset -> day(birthDate, startDate.plusDays(offset.toLong())) }
    }

    internal fun cycle(
        birthDate: LocalDate,
        date: LocalDate,
        period: Double,
    ): BiorhythmCycleForecast {
        val previous = BiorhythmCalculator.percent(birthDate, date.minusDays(1), period)
        val current = BiorhythmCalculator.percent(birthDate, date, period)
        val next = BiorhythmCalculator.percent(birthDate, date.plusDays(1), period)
        return BiorhythmCycleForecast(
            percent = current,
            trend = when {
                next > current -> BiorhythmTrend.RISING
                next < current -> BiorhythmTrend.FALLING
                else -> BiorhythmTrend.STEADY
            },
            event = when {
                abs(current) < abs(previous) && abs(current) <= abs(next) -> BiorhythmEvent.CRITICAL
                current > previous && current >= next -> BiorhythmEvent.PEAK
                current < previous && current <= next -> BiorhythmEvent.LOW
                else -> null
            },
        )
    }

    internal fun synchronizedExtreme(
        values: List<Double>,
        threshold: Double = SYNCHRONIZED_EXTREME_THRESHOLD,
    ): SynchronizedExtreme? {
        require(threshold in 0.0..100.0) { "threshold must be within 0..100" }
        if (values.size != classicPeriods.size) return null
        return when {
            values.all { it >= threshold } -> SynchronizedExtreme.HIGH
            values.all { it <= -threshold } -> SynchronizedExtreme.LOW
            else -> null
        }
    }
}

internal fun BiorhythmTrend.symbol(): String = when (this) {
    BiorhythmTrend.RISING -> "↑"
    BiorhythmTrend.FALLING -> "↓"
    BiorhythmTrend.STEADY -> "→"
}
