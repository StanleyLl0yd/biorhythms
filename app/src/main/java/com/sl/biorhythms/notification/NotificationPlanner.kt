package com.sl.biorhythms.notification

import com.sl.biorhythms.BiorhythmDayForecast

object NotificationPlanner {
    fun hasImportantEvent(forecast: BiorhythmDayForecast): Boolean =
        forecast.synchronizedExtreme != null || forecast.cycles.any { it.event != null }

    fun shouldNotify(
        preferences: NotificationPreferences,
        forecast: BiorhythmDayForecast,
    ): Boolean = preferences.shouldNotify(hasImportantEvent(forecast))
}
