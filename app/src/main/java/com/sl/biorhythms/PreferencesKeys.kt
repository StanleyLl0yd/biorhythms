package com.sl.biorhythms

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey

object PreferencesKeys {
    val BirthDate = longPreferencesKey("birth_date_epoch")
    val ThemeMode = intPreferencesKey("theme_mode")
    val Language = intPreferencesKey("language")
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
