package com.sl.biorhythms.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import com.sl.biorhythms.AppLanguage
import com.sl.biorhythms.AppThemeMode
import com.sl.biorhythms.BiorhythmCycleType
import com.sl.biorhythms.BiorhythmDayForecast
import com.sl.biorhythms.BiorhythmForecast
import com.sl.biorhythms.MainActivity
import com.sl.biorhythms.PreferencesKeys
import com.sl.biorhythms.R
import com.sl.biorhythms.SynchronizedExtreme
import com.sl.biorhythms.dataStore
import com.sl.biorhythms.resolveLocale
import com.sl.biorhythms.storedBirthDate
import com.sl.biorhythms.symbol
import java.io.IOException
import java.time.LocalDate
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class BiorhythmsWidgetProvider : AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action in refreshActions) {
            val manager = AppWidgetManager.getInstance(context)
            val appWidgetIds = activeWidgetIds(context, manager)
            if (appWidgetIds.isNotEmpty()) {
                updateAsync(context, manager, appWidgetIds)
            }
            WidgetRefreshScheduler.schedule(context)
            return
        }
        super.onReceive(context, intent)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        updateAsync(context, appWidgetManager, appWidgetIds)
        WidgetRefreshScheduler.schedule(context)
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        WidgetRefreshScheduler.schedule(context)
    }

    override fun onDisabled(context: Context) {
        WidgetRefreshScheduler.cancel(context)
        super.onDisabled(context)
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

    override fun onRestored(context: Context, oldWidgetIds: IntArray, newWidgetIds: IntArray) {
        WidgetPreferences(context).remapAlpha(oldWidgetIds, newWidgetIds)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val manager = AppWidgetManager.getInstance(context)
            newWidgetIds.forEach { appWidgetId ->
                val options = manager.getAppWidgetOptions(appWidgetId)
                options.putBoolean(AppWidgetManager.OPTION_APPWIDGET_RESTORE_COMPLETED, true)
                manager.updateAppWidgetOptions(appWidgetId, options)
            }
        }
        super.onRestored(context, oldWidgetIds, newWidgetIds)
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
        val themeMode = AppThemeMode.fromStored(storedPreferences[PreferencesKeys.ThemeMode])
        val language = AppLanguage.fromStored(storedPreferences[PreferencesKeys.Language])
        val locale = resolveLocale(language, context.resources.configuration.locales[0])
        val resources = localizedResources(context, locale)
        val widgetViews = createViews(context, appWidgetId, themeMode, resources)
        val views = widgetViews.remoteViews
        val today = LocalDate.now()
        val birthDate = storedBirthDate(storedPreferences[PreferencesKeys.BirthDate], today)

        if (birthDate == null) {
            showStatus(views, resources.getString(R.string.widget_no_birth_date))
            appWidgetManager.updateAppWidget(appWidgetId, views)
            return
        }

        showValues(
            context = context,
            views = views,
            resources = resources,
            locale = locale,
            forecast = BiorhythmForecast.day(birthDate, today),
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
        applyWidgetLocalizedText(views, resources)
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
        val alphaValue = alpha * 255 / 100
        val colorWithAlpha = (backgroundColor and 0x00FFFFFF) or (alphaValue shl 24)
        views.setInt(R.id.widget_container, "setBackgroundColor", colorWithAlpha)
        views.setTextColor(R.id.widget_title, textColor)
        views.setTextColor(R.id.widget_status, textColor)
        views.setTextColor(R.id.widget_alert, textColor)
        views.setInt(R.id.widget_settings, "setColorFilter", textColor)
    }

    private fun showStatus(views: RemoteViews, status: String) {
        views.setViewVisibility(R.id.widget_values, View.GONE)
        views.setViewVisibility(R.id.widget_alert, View.GONE)
        views.setViewVisibility(R.id.widget_status, View.VISIBLE)
        views.setTextViewText(R.id.widget_status, status)
    }

    private fun showValues(
        context: Context,
        views: RemoteViews,
        resources: Resources,
        locale: Locale,
        forecast: BiorhythmDayForecast,
        textColor: Int,
    ) {
        views.setViewVisibility(R.id.widget_status, View.GONE)
        views.setViewVisibility(R.id.widget_values, View.VISIBLE)
        views.removeAllViews(R.id.widget_values)

        BiorhythmCycleType.entries.zip(forecast.cycles).forEach { (type, cycle) ->
            val label = resources.getString(type.labelResId)
            val valueText = String.format(
                locale,
                "%+d%% %s",
                cycle.percent.roundToInt(),
                cycle.trend.symbol(),
            )
            val row = RemoteViews(context.packageName, R.layout.widget_biorhythm_row).apply {
                setTextViewText(R.id.widget_cycle_label, label)
                setTextViewText(R.id.widget_cycle_value, valueText)
                setTextColor(R.id.widget_cycle_label, textColor)
                setTextColor(R.id.widget_cycle_value, textColor)
                setContentDescription(R.id.widget_cycle_row, "$label $valueText")
            }
            views.addView(R.id.widget_values, row)
        }

        val extreme = forecast.synchronizedExtreme
        if (extreme == null) {
            views.setViewVisibility(R.id.widget_alert, View.GONE)
        } else {
            val threshold = BiorhythmForecast.SYNCHRONIZED_EXTREME_THRESHOLD.roundToInt()
            val alertResId = if (extreme == SynchronizedExtreme.LOW) {
                R.string.sync_low_compact
            } else {
                R.string.sync_high_compact
            }
            views.setTextViewText(R.id.widget_alert, resources.getString(alertResId, threshold))
            views.setViewVisibility(R.id.widget_alert, View.VISIBLE)
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

    private fun activeWidgetIds(context: Context, manager: AppWidgetManager): IntArray =
        manager.getAppWidgetIds(ComponentName(context, BiorhythmsWidgetProvider::class.java))

    private data class WidgetViews(
        val remoteViews: RemoteViews,
        val textColor: Int,
    )

    private companion object {
        const val TAG = "BiorhythmsWidget"
        val refreshActions = setOf(
            WidgetRefreshScheduler.ACTION_DAILY_REFRESH,
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_LOCALE_CHANGED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
        )
    }
}

internal fun applyWidgetLocalizedText(views: RemoteViews, resources: Resources) {
    views.setTextViewText(R.id.widget_title, resources.getString(R.string.widget_title))
    views.setContentDescription(R.id.widget_settings, resources.getString(R.string.settings_title))
}
