package com.sl.biorhythms.widget

import android.content.Context
import android.content.res.Configuration
import android.view.View
import android.widget.FrameLayout
import android.widget.RemoteViews
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sl.biorhythms.R
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WidgetLocalizationInstrumentedTest {

    @Test
    fun widgetLocalizedTextUsesProvidedResources() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        assertWidgetText(
            context = context,
            languageTag = "en",
            expectedTitle = "Biorhythms Today",
            expectedSettings = "Settings",
        )
        assertWidgetText(
            context = context,
            languageTag = "ru",
            expectedTitle = "Биоритмы сегодня",
            expectedSettings = "Настройки",
        )
    }

    private fun assertWidgetText(
        context: Context,
        languageTag: String,
        expectedTitle: String,
        expectedSettings: String,
    ) {
        val configuration = Configuration(context.resources.configuration).apply {
            setLocale(Locale.forLanguageTag(languageTag))
        }
        val resources = context.createConfigurationContext(configuration).resources
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_biorhythms)

        applyWidgetLocalizedText(remoteViews, resources)

        val root = remoteViews.apply(context, FrameLayout(context))
        val title = root.findViewById<TextView>(R.id.widget_title)
        val settings = root.findViewById<View>(R.id.widget_settings)

        assertEquals(expectedTitle, title.text.toString())
        assertEquals(expectedSettings, settings.contentDescription.toString())
    }
}
