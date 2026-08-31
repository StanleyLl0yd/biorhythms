package com.sl.biorhythms.widget

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
    fun localizedWidgetTextOverridesSystemResourceLocale() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val configuration = Configuration(context.resources.configuration).apply {
            setLocale(Locale.forLanguageTag("ru"))
        }
        val resources = context.createConfigurationContext(configuration).resources
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_biorhythms)

        applyWidgetLocalizedText(remoteViews, resources)

        val root = remoteViews.apply(context, FrameLayout(context))
        val title = root.findViewById<TextView>(R.id.widget_title)
        val settings = root.findViewById<View>(R.id.widget_settings)

        assertEquals("Биоритмы сегодня", title.text.toString())
        assertEquals("Настройки", settings.contentDescription.toString())
    }
}
