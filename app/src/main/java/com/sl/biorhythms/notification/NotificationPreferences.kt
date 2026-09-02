package com.sl.biorhythms.notification

import androidx.datastore.preferences.core.Preferences
import com.sl.biorhythms.PreferencesKeys

data class NotificationPreferences(
    val enabled: Boolean = false,
    val hour: Int = DEFAULT_HOUR,
    val minute: Int = DEFAULT_MINUTE,
    val dailySummary: Boolean = true,
    val importantEvents: Boolean = true,
    val physical: Boolean = true,
    val emotional: Boolean = true,
    val intellectual: Boolean = true,
) {
    val hasSelectedCycles: Boolean
        get() = physical || emotional || intellectual

    val shouldSchedule: Boolean
        get() = enabled && ((dailySummary && hasSelectedCycles) || importantEvents)

    companion object {
        const val DEFAULT_HOUR = 9
        const val DEFAULT_MINUTE = 0

        fun fromPreferences(preferences: Preferences): NotificationPreferences =
            NotificationPreferences(
                enabled = preferences[PreferencesKeys.NotificationEnabled]
                    ?: preferences[PreferencesKeys.LegacyNotificationEnabled]
                    ?: false,
                hour = (preferences[PreferencesKeys.NotificationHour] ?: DEFAULT_HOUR).coerceIn(0, 23),
                minute = (preferences[PreferencesKeys.NotificationMinute] ?: DEFAULT_MINUTE).coerceIn(0, 59),
                dailySummary = preferences[PreferencesKeys.NotificationDailySummary] ?: true,
                importantEvents = preferences[PreferencesKeys.NotificationImportantEvents] ?: true,
                physical = preferences[PreferencesKeys.NotificationPhysical] ?: true,
                emotional = preferences[PreferencesKeys.NotificationEmotional] ?: true,
                intellectual = preferences[PreferencesKeys.NotificationIntellectual] ?: true,
            )
    }
}
