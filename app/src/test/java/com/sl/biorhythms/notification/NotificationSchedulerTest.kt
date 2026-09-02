package com.sl.biorhythms.notification

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime

class NotificationSchedulerTest {
    @Test
    fun nextTriggerUsesTodayWhenTimeIsStillAhead() {
        val zone = ZoneId.of("Europe/Amsterdam")
        val now = ZonedDateTime.of(2026, 9, 2, 8, 15, 0, 0, zone)

        val trigger = NotificationScheduler.nextTrigger(now, 9, 0)

        assertEquals(2026, trigger.year)
        assertEquals(9, trigger.monthValue)
        assertEquals(2, trigger.dayOfMonth)
        assertEquals(9, trigger.hour)
        assertEquals(0, trigger.minute)
    }

    @Test
    fun nextTriggerUsesTomorrowWhenTimeHasPassed() {
        val zone = ZoneId.of("Europe/Amsterdam")
        val now = ZonedDateTime.of(2026, 9, 2, 9, 0, 0, 0, zone)

        val trigger = NotificationScheduler.nextTrigger(now, 9, 0)

        assertEquals(3, trigger.dayOfMonth)
        assertEquals(9, trigger.hour)
        assertEquals(0, trigger.minute)
    }

    @Test
    fun nextTriggerResolvesSpringDstGapInLocalTime() {
        val zone = ZoneId.of("Europe/Amsterdam")
        val now = ZonedDateTime.of(2026, 3, 28, 23, 0, 0, 0, zone)

        val trigger = NotificationScheduler.nextTrigger(now, 2, 30)

        assertEquals(29, trigger.dayOfMonth)
        assertEquals(3, trigger.hour)
        assertEquals(30, trigger.minute)
    }
}
