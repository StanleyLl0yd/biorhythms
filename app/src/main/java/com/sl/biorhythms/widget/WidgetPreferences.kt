package com.sl.biorhythms.widget

import android.content.Context
import androidx.core.content.edit

class WidgetPreferences(context: Context) {
    companion object {
        private const val PREF_NAME = "widget_prefs"
        private const val KEY_ALPHA = "alpha_"
        const val DEFAULT_ALPHA = 100
    }

    private val prefs = context.applicationContext.getSharedPreferences(
        PREF_NAME,
        Context.MODE_PRIVATE,
    )

    fun getAlpha(appWidgetId: Int): Int =
        prefs.getInt(KEY_ALPHA + appWidgetId, DEFAULT_ALPHA).coerceIn(0, 100)

    fun setAlpha(appWidgetId: Int, alpha: Int) {
        prefs.edit {
            putInt(KEY_ALPHA + appWidgetId, alpha.coerceIn(0, 100))
        }
    }

    fun deleteAlpha(appWidgetId: Int) {
        prefs.edit {
            remove(KEY_ALPHA + appWidgetId)
        }
    }
}
