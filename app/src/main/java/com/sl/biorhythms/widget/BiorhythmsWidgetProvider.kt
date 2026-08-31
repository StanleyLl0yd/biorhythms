package com.sl.biorhythms.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.view.View
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import com.sl.biorhythms.AppLanguage
import com.sl.biorhythms.AppThemeMode
import com.sl.biorhythms.BiorhythmCalculator
import com.sl.biorhythms.MainActivity
import com.sl.biorhythms.PreferencesKeys
import com.sl.biorhythms.R
import com.sl.biorhythms.dataStore
import com.sl.biorhythms.resolveLocale
import java.io.IOException
import java.time.LocalDate
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class BiorhythmsWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        updateAsync(context, appWidgetManager, appWidgetIds)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: android.os.Bundle,
    ) {
        updateAsync(context, appWidgetManager, intArrayOf(appWidgetId))
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        val prefs = WidgetPreferences(context)
        appWidgetIds.forEach(prefs::deleteAlpha)
        super.onDeleted(context, appWidgetIds)
    }

    private fun updateAsync(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                appWidgetIds.forEach { appWidgetId ->
                    try {
                        updateAppWidget(context.applicationContext, appWidgetManager, appWidgetId)
                    } catch (error: Exception) {
                        android.util.Log.e(TAG, "Error updating widget", error)
                        showErrorWidget(context.applicationContext, appWidgetManager, appWidgetId)
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
    ) {
        val storedPreferences = context.dataStore.data.first()
        val birthDateEpoch = storedPreferences[PreferencesKeys.BirthDate]
        val themeMode = AppThemeMode.fromStored(storedPreferences[PreferencesKeys.ThemeMode])
        val language = AppLanguage.fromStored(storedPreferences[PreferencesKeys.Language])
        val locale = resolveLocale(language, context.resources.configuration.locales[0])
        val resources = localizedResources(context, locale)
        val widgetViews = createViews(context, appWidgetId, themeMode, resources)
        val views = widgetViews.remoteViews

        if (birthDateEpoch == null) {
            showStatus(views, resources.getString(R.string.widget_no_birth_date))
            appWidgetManager.updateAppWidget(appWidgetId, views)
            return
        }

        val birthDate = runCatching { LocalDate.ofEpochDay(birthDateEpoch) }.getOrNull()
        val today = LocalDate.now()
        if (birthDate == null || birthDate.isAfter(today)) {
            showStatus(views, resources.getString(R.string.widget_no_birth_date))
            appWidgetManager.updateAppWidget(appWidgetId, views)
            return
        }

        val values = listOf(
            BiorhythmCalculator.percent(
                BiorhythmCalculator.value(birthDate, today, BiorhythmCalculator.PHYSICAL_PERIOD),
            ),
            BiorhythmCalculator.percent(
                BiorhythmCalculator.value(birthDate, today, BiorhythmCalculator.EMOTIONAL_PERIOD),
            ),
            BiorhythmCalculator.percent(
                BiorhythmCalculator.value(birthDate, today, BiorhythmCalculator.INTELLECTUAL_PERIOD),
            ),
        )

        showValues(
            context = context,
            views = views,
            resources = resources,
            locale = locale,
            values = values,
            textColor = widgetViews.textColor,
        )
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun createViews(
        context: Context,
        appWidgetId: Int,
        themeMode: AppThemeMode,
        resources: Resources,
    ): WidgetViews {
        val isDarkTheme = when (themeMode) {
            AppThemeMode.SYSTEM -> context.isSystemDarkTheme()
            AppThemeMode.LIGHT -> false
            AppThemeMode.DARK -> true
        }
        val textColor = ContextCompat.getColor(
            context,
            if (isDarkTheme) R.color.widget_text_dark else R.color.widget_text_light,
        )
        val backgroundColor = ContextCompat.getColor(
            context,
            if (isDarkTheme) R.color.widget_background_dark else R.color.widget_background_light,
        )
        val alpha = WidgetPreferences(context).getAlpha(appWidgetId)
        val views = RemoteViews(context.packageName, R.layout.widget_biorhythms)
        configureClicks(context, views, appWidgetId)
        applyAppearance(views, textColor, backgroundColor, alpha)
        applyLocalizedText(views, resources)
        return WidgetViews(views, textColor)
    }

    private fun configureClicks(
        context: Context,
        views: RemoteViews,
        appWidgetId: Int,
    ) {
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        views.setOnClickPendingIntent(R.id.widget_container, openAppPendingIntent)

        val configIntent = Intent(context, WidgetConfigActivity::class.java).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        val configPendingIntent = PendingIntent.getActivity(
            context,
            appWidgetId,
            configIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        views.setOnClickPendingIntent(R.id.widget_settings, configPendingIntent)
    }

    private fun applyAppearance(
        views: RemoteViews,
        textColor: Int,
        backgroundColor: Int,
        alpha: Int,
    ) {
        val alphaValue = (alpha.coerceIn(0, 100) * 255 / 100).coerceIn(0, 255)
        val colorWithAlpha = (backgroundColor and 0x00FFFFFF) or (alphaValue shl 24)
        views.setInt(R.id.widget_container, "setBackgroundColor", colorWithAlpha)
        views.setTextColor(R.id.widget_title, textColor)
        views.setTextColor(R.id.widget_status, textColor)
        views.setInt(R.id.widget_settings, "setColorFilter", textColor)
    }

    private fun applyLocalizedText(views: RemoteViews, resources: Resources) {
        views.setTextViewText(R.id.widget_title, resources.getString(R.string.widget_title))
        views.setContentDescription(R.id.widget_settings, resources.getString(R.string.settings_title))
    }

    private fun showStatus(views: RemoteViews, status: String) {
        views.setViewVisibility(R.id.widget_values, View.GONE)
        views.setViewVisibility(R.id.widget_status, View.VISIBLE)
        views.setTextViewText(R.id.widget_status, status)
    }

    private fun showValues(
        context: Context,
        views: RemoteViews,
        resources: Resources,
        locale: Locale,
        values: List<Double>,
        textColor: Int,
    ) {
        views.setViewVisibility(R.id.widget_status, View.GONE)
        views.setViewVisibility(R.id.widget_values, View.VISIBLE)
        views.removeAllViews(R.id.widget_values)

        val labels = listOf(
            resources.getString(R.string.legend_physical),
            resources.getString(R.string.legend_emotional),
            resources.getString(R.string.legend_intellectual),
        )

        labels.zip(values).forEach { (label, value) ->
            val valueText = String.format(locale, "%+d%%", value.roundToInt())
            val row = RemoteViews(context.packageName, R.layout.widget_biorhythm_row).apply {
                setTextViewText(R.id.widget_cycle_label, label)
                setTextViewText(R.id.widget_cycle_value, valueText)
                setTextColor(R.id.widget_cycle_label, textColor)
                setTextColor(R.id.widget_cycle_value, textColor)
                setContentDescription(R.id.widget_cycle_row, "$label $valueText")
            }
            views.addView(R.id.widget_values, row)
        }
    }

    private fun localizedResources(context: Context, locale: Locale): Resources {
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config).resources
    }

    private fun Context.isSystemDarkTheme(): Boolean {
        val nightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightMode == Configuration.UI_MODE_NIGHT_YES
    }

    private suspend fun showErrorWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
    ) {
        val storedPreferences = try {
            context.dataStore.data.first()
        } catch (_: IOException) {
            null
        }
        val themeMode = AppThemeMode.fromStored(storedPreferences?.get(PreferencesKeys.ThemeMode))
        val language = AppLanguage.fromStored(storedPreferences?.get(PreferencesKeys.Language))
        val locale = resolveLocale(language, context.resources.configuration.locales[0])
        val resources = localizedResources(context, locale)
        val views = createViews(context, appWidgetId, themeMode, resources).remoteViews
        showStatus(views, resources.getString(R.string.widget_error))
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private data class WidgetViews(
        val remoteViews: RemoteViews,
        val textColor: Int,
    )

    private companion object {
        const val TAG = "BiorhythmsWidget"
    }
}
