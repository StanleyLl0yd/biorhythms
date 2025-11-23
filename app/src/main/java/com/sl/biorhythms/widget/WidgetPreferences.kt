package com.sl.biorhythms.widget

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey

class WidgetPreferences(private val context: Context) {

    companion object {
        val BirthDateKey = longPreferencesKey("birth_date_epoch")
        val ThemeModeKey = intPreferencesKey("theme_mode")
        val LanguageKey = intPreferencesKey("language")

        private const val PREF_NAME = "widget_prefs"
        private const val KEY_ALPHA = "alpha_"
    }

    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun getAlpha(appWidgetId: Int): Int {
        return prefs.getInt(KEY_ALPHA + appWidgetId, 100) // По умолчанию 100%
    }

    fun setAlpha(appWidgetId: Int, alpha: Int) {
        prefs.edit().putInt(KEY_ALPHA + appWidgetId, alpha.coerceIn(0, 100)).apply()
    }

    fun deleteAlpha(appWidgetId: Int) {
        prefs.edit().remove(KEY_ALPHA + appWidgetId).apply()
    }
}