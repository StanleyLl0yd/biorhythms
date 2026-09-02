package com.sl.biorhythms.notification

import com.sl.biorhythms.BiorhythmCycleForecast
import com.sl.biorhythms.BiorhythmDayForecast
import com.sl.biorhythms.BiorhythmEvent
import com.sl.biorhythms.BiorhythmTrend
import com.sl.biorhythms.SynchronizedExtreme
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class NotificationPlannerTest {
    @Test
    fun disabledNotificationsNeverNotify() {
        val preferences = NotificationPreferences(enabled = false)

        assertFalse(NotificationPlanner.shouldNotify(preferences, ordinaryDay()))
    }

    @Test
    fun dailySummaryNotifiesOnOrdinaryDay() {
        val preferences = NotificationPreferences(
            enabled = true,
            dailySummary = true,
            importantEvents = false,
        )

        assertTrue(NotificationPlanner.shouldNotify(preferences, ordinaryDay()))
    }

    @Test
    fun importantOnlyModeStaysQuietOnOrdinaryDay() {
        val preferences = NotificationPreferences(
            enabled = true,
            dailySummary = false,
            importantEvents = true,
        )

        assertFalse(NotificationPlanner.shouldNotify(preferences, ordinaryDay()))
    }

    @Test
    fun importantOnlyModeNotifiesForCriticalCycle() {
        val preferences = NotificationPreferences(
            enabled = true,
            dailySummary = false,
            importantEvents = true,
        )
        val forecast = ordinaryDay().copy(
            cycles = ordinaryDay().cycles.mapIndexed { index, cycle ->
                if (index == 0) cycle.copy(event = BiorhythmEvent.CRITICAL) else cycle
            },
        )

        assertTrue(NotificationPlanner.shouldNotify(preferences, forecast))
    }

    @Test
    fun synchronizedExtremeCountsAsImportantEvent() {
        val preferences = NotificationPreferences(
            enabled = true,
            dailySummary = false,
            importantEvents = true,
        )
        val forecast = ordinaryDay().copy(synchronizedExtreme = SynchronizedExtreme.HIGH)

        assertTrue(NotificationPlanner.shouldNotify(preferences, forecast))
    }

    @Test
    fun dailySummaryRequiresAtLeastOneSelectedCycle() {
        val preferences = NotificationPreferences(
            enabled = true,
            dailySummary = true,
            importantEvents = false,
            physical = false,
            emotional = false,
            intellectual = false,
        )

        assertFalse(preferences.shouldSchedule)
        assertFalse(NotificationPlanner.shouldNotify(preferences, ordinaryDay()))
    }

    private fun ordinaryDay(): BiorhythmDayForecast = BiorhythmDayForecast(
        date = LocalDate.of(2026, 9, 2),
        cycles = List(3) {
            BiorhythmCycleForecast(
                percent = 25.0,
                trend = BiorhythmTrend.RISING,
                event = null,
            )
        },
        synchronizedExtreme = null,
    )
}
