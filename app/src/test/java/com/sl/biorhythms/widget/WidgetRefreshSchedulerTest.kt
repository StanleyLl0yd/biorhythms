package com.sl.biorhythms.widget

import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import org.junit.Assert.assertEquals
import org.junit.Test

class WidgetRefreshSchedulerTest {

    @Test
    fun exactMidnightSchedulesFollowingDay() {
        val zone = ZoneId.of("UTC")
        val now = ZonedDateTime.of(2026, 9, 2, 0, 0, 0, 0, zone)
        val expected = LocalDate.of(2026, 9, 3).atStartOfDay(zone).toInstant().toEpochMilli()

        assertEquals(expected, nextWidgetRefreshEpochMillis(now))
    }

    @Test
    fun leapDayIsPreserved() {
        val zone = ZoneId.of("UTC")
        val now = ZonedDateTime.of(2028, 2, 28, 23, 59, 0, 0, zone)
        val expected = LocalDate.of(2028, 2, 29).atStartOfDay(zone).toInstant().toEpochMilli()

        assertEquals(expected, nextWidgetRefreshEpochMillis(now))
    }

    @Test
    fun springDstUsesNextLocalDayStart() {
        val zone = ZoneId.of("Europe/Amsterdam")
        val now = ZonedDateTime.of(2026, 3, 29, 12, 0, 0, 0, zone)
        val expected = LocalDate.of(2026, 3, 30).atStartOfDay(zone).toInstant().toEpochMilli()

        assertEquals(expected, nextWidgetRefreshEpochMillis(now))
    }

    @Test
    fun autumnDstUsesNextLocalDayStart() {
        val zone = ZoneId.of("Europe/Amsterdam")
        val now = ZonedDateTime.of(2026, 10, 25, 12, 0, 0, 0, zone)
        val expected = LocalDate.of(2026, 10, 26).atStartOfDay(zone).toInstant().toEpochMilli()

        assertEquals(expected, nextWidgetRefreshEpochMillis(now))
    }
}
