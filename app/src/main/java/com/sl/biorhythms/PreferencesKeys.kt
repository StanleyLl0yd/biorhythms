package com.sl.biorhythms

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey

object PreferencesKeys {
    val BirthDate = longPreferencesKey("birth_date_epoch")
    val ThemeMode = intPreferencesKey("theme_mode")
    val Language = intPreferencesKey("language")

    val NotificationEnabled = booleanPreferencesKey("notification_enabled")
    val LegacyNotificationEnabled = booleanPreferencesKey("notif_enabled")
    val NotificationHour = intPreferencesKey("notification_hour")
    val NotificationMinute = intPreferencesKey("notification_minute")
    val NotificationDailySummary = booleanPreferencesKey("notification_daily_summary")
    val NotificationImportantEvents = booleanPreferencesKey("notification_important_events")
    val NotificationPhysical = booleanPreferencesKey("notification_physical")
    val NotificationEmotional = booleanPreferencesKey("notification_emotional")
    val NotificationIntellectual = booleanPreferencesKey("notification_intellectual")
    val NotificationLastEpochDay = longPreferencesKey("notification_last_epoch_day")
}

enum class AppThemeMode(val storedValue: Int) {
    SYSTEM(0),
    LIGHT(1),
    DARK(2);

    companion object {
        fun fromStored(value: Int?): AppThemeMode =
            entries.firstOrNull { it.storedValue == value } ?: SYSTEM
    }
}

enum class AppLanguage(val storedValue: Int) {
    SYSTEM(0),
    RU(1),
    EN(2);

    companion object {
        fun fromStored(value: Int?): AppLanguage =
            entries.firstOrNull { it.storedValue == value } ?: SYSTEM
    }
}
