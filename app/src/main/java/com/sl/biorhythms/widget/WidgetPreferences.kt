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

    fun remapAlpha(oldWidgetIds: IntArray, newWidgetIds: IntArray) {
        val mappings = buildList {
            repeat(minOf(oldWidgetIds.size, newWidgetIds.size)) { index ->
                val oldId = oldWidgetIds[index]
                val newId = newWidgetIds[index]
                if (oldId != newId && prefs.contains(KEY_ALPHA + oldId)) {
                    add(AlphaMapping(oldId, newId, getAlpha(oldId)))
                }
            }
        }
        if (mappings.isEmpty()) return

        prefs.edit {
            mappings.forEach { remove(KEY_ALPHA + it.oldId) }
            mappings.forEach { putInt(KEY_ALPHA + it.newId, it.alpha) }
        }
    }

    private data class AlphaMapping(
        val oldId: Int,
        val newId: Int,
        val alpha: Int,
    )
}
