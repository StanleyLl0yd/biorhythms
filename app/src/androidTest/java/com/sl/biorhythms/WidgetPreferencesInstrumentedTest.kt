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
}
