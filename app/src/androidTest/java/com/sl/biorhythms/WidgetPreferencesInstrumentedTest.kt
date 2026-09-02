package com.sl.biorhythms

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sl.biorhythms.widget.WidgetPreferences
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WidgetPreferencesInstrumentedTest {

    @Test
    fun alphaIsPersistedClampedAndDeleted() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val prefs = WidgetPreferences(context)
        val widgetId = Int.MAX_VALUE - 42

        try {
            prefs.deleteAlpha(widgetId)
            assertEquals(WidgetPreferences.DEFAULT_ALPHA, prefs.getAlpha(widgetId))

            prefs.setAlpha(widgetId, 37)
            assertEquals(37, prefs.getAlpha(widgetId))

            prefs.setAlpha(widgetId, 150)
            assertEquals(100, prefs.getAlpha(widgetId))

            prefs.setAlpha(widgetId, -10)
            assertEquals(0, prefs.getAlpha(widgetId))
        } finally {
            prefs.deleteAlpha(widgetId)
        }

        assertEquals(WidgetPreferences.DEFAULT_ALPHA, prefs.getAlpha(widgetId))
    }

    @Test
    fun restoredWidgetIdsPreserveAlphaAcrossOverlappingMappings() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val prefs = WidgetPreferences(context)
        val oldFirst = Int.MAX_VALUE - 101
        val oldSecond = Int.MAX_VALUE - 102
        val newFirst = oldSecond
        val newSecond = Int.MAX_VALUE - 103
        val ids = intArrayOf(oldFirst, oldSecond, newSecond)

        try {
            ids.forEach(prefs::deleteAlpha)
            prefs.setAlpha(oldFirst, 25)
            prefs.setAlpha(oldSecond, 75)

            prefs.remapAlpha(
                oldWidgetIds = intArrayOf(oldFirst, oldSecond),
                newWidgetIds = intArrayOf(newFirst, newSecond),
            )

            assertEquals(WidgetPreferences.DEFAULT_ALPHA, prefs.getAlpha(oldFirst))
            assertEquals(25, prefs.getAlpha(newFirst))
            assertEquals(75, prefs.getAlpha(newSecond))
        } finally {
            ids.forEach(prefs::deleteAlpha)
        }
    }
}
