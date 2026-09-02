package com.sl.biorhythms.notification

import com.sl.biorhythms.BiorhythmDayForecast

object NotificationPlanner {
    fun hasImportantEvent(forecast: BiorhythmDayForecast): Boolean =
        forecast.synchronizedExtreme != null || forecast.cycles.any { it.event != null }

    fun shouldNotify(
        preferences: NotificationPreferences,
        forecast: BiorhythmDayForecast,
    ): Boolean {
        if (!preferences.enabled) return false
        val hasDailySummary = preferences.dailySummary && preferences.hasSelectedCycles
        val hasImportantEvent = preferences.importantEvents && hasImportantEvent(forecast)
        return hasDailySummary || hasImportantEvent
    }
}
