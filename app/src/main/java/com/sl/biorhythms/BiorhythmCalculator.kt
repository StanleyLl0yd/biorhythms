package com.sl.biorhythms

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.PI
import kotlin.math.sin

object BiorhythmCalculator {
    const val PHYSICAL_PERIOD = 23.0
    const val EMOTIONAL_PERIOD = 28.0
    const val INTELLECTUAL_PERIOD = 33.0

    fun daysFromBirth(birthDate: LocalDate, date: LocalDate): Long =
        ChronoUnit.DAYS.between(birthDate, date)

    fun value(daysFromBirth: Long, period: Double): Double =
        sin(2.0 * PI * daysFromBirth / period)

    fun value(birthDate: LocalDate, date: LocalDate, period: Double): Double =
        value(daysFromBirth(birthDate, date), period)

    fun percent(value: Double): Double =
        (value * 100.0).coerceIn(-100.0, 100.0)

    fun percent(birthDate: LocalDate, date: LocalDate, period: Double): Double =
        percent(value(birthDate, date, period))
}
