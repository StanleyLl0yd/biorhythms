package com.sl.biorhythms.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import com.sl.biorhythms.AppLanguage
import com.sl.biorhythms.AppThemeMode
import com.sl.biorhythms.BiorhythmCalculator
import com.sl.biorhythms.BiorhythmValueColor
import com.sl.biorhythms.MainActivity
import com.sl.biorhythms.PreferencesKeys
import com.sl.biorhythms.R
import com.sl.biorhythms.dataStore
import com.sl.biorhythms.resolveLocale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale
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
        newOptions: Bundle,
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
                        android.util.Log.e("BiorhythmsWidget", "Error updating widget", error)
                        showErrorWidget(context, appWidgetManager, appWidgetId)
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
        val alpha = WidgetPreferences(context).getAlpha(appWidgetId)
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

        val views = RemoteViews(context.packageName, R.layout.widget_biorhythms)
        configureClicks(context, views, appWidgetId)
        applyAppearance(views, textColor, backgroundColor, alpha)

        if (birthDateEpoch == null) {
            views.setTextViewText(R.id.widget_title, resources.getString(R.string.widget_no_birth_date))
            views.setImageViewBitmap(R.id.widget_content, null)
            appWidgetManager.updateAppWidget(appWidgetId, views)
            return
        }

        val birthDate = LocalDate.ofEpochDay(birthDateEpoch)
        val today = LocalDate.now()
        val options = appWidgetManager.getAppWidgetOptions(appWidgetId)
        val density = context.resources.displayMetrics.density
        val widthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 250).coerceAtLeast(180)
        val heightDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 120).coerceAtLeast(100)
        val bitmapWidth = (widthDp * density).roundToInt().coerceIn(300, 1200)
        val bitmapHeight = (heightDp * density * 0.62f).roundToInt().coerceIn(120, 800)

        val bitmap = generateWidgetBitmap(
            context = context,
            birthDate = birthDate,
            today = today,
            textColor = textColor,
            locale = locale,
            labels = listOf(
                resources.getString(R.string.legend_physical),
                resources.getString(R.string.legend_emotional),
                resources.getString(R.string.legend_intellectual),
            ),
            width = bitmapWidth,
            height = bitmapHeight,
        )

        views.setTextViewText(R.id.widget_title, resources.getString(R.string.widget_title))
        views.setImageViewBitmap(R.id.widget_content, bitmap)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun configureClicks(
        context: Context,
        views: RemoteViews,
        appWidgetId: Int,
    ) {
        val openAppIntent = Intent(context, MainActivity::class.java)
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
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
        views.setInt(R.id.widget_settings, "setColorFilter", textColor)
    }

    private fun generateWidgetBitmap(
        context: Context,
        birthDate: LocalDate,
        today: LocalDate,
        textColor: Int,
        locale: Locale,
        labels: List<String>,
        width: Int,
        height: Int,
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val density = context.resources.displayMetrics.density
        val daysFromBirth = BiorhythmCalculator.daysFromBirth(birthDate, today)

        val values = listOf(
            BiorhythmCalculator.percent(
                BiorhythmCalculator.value(daysFromBirth, BiorhythmCalculator.PHYSICAL_PERIOD),
            ),
            BiorhythmCalculator.percent(
                BiorhythmCalculator.value(daysFromBirth, BiorhythmCalculator.EMOTIONAL_PERIOD),
            ),
            BiorhythmCalculator.percent(
                BiorhythmCalculator.value(daysFromBirth, BiorhythmCalculator.INTELLECTUAL_PERIOD),
            ),
        )
        val colors = listOf(
            ContextCompat.getColor(context, R.color.widget_physical),
            ContextCompat.getColor(context, R.color.widget_emotional),
            ContextCompat.getColor(context, R.color.widget_intellectual),
        )

        val horizontalPadding = 12f * density
        val lineHeight = height / 3f
        labels.forEachIndexed { index, label ->
            drawBiorhythmLine(
                canvas = canvas,
                label = label,
                percent = values[index],
                barColor = colors[index],
                textColor = textColor,
                locale = locale,
                x = horizontalPadding,
                baselineY = lineHeight * index + lineHeight * 0.52f,
                maxWidth = width - horizontalPadding * 2f,
                density = density,
            )
        }

        return bitmap
    }

    private fun drawBiorhythmLine(
        canvas: Canvas,
        label: String,
        percent: Double,
        barColor: Int,
        textColor: Int,
        locale: Locale,
        x: Float,
        baselineY: Float,
        maxWidth: Float,
        density: Float,
    ) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textColor
            textSize = 13f * density
        }
        canvas.drawText(label, x, baselineY, paint)

        val percentText = String.format(locale, "%+d%%", percent.roundToInt())
        paint.color = BiorhythmValueColor.argb(percent)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText(percentText, x + maxWidth, baselineY, paint)

        val barY = baselineY + 5f * density
        val barHeight = 5f * density
        val barWidth = maxWidth * 0.60f
        paint.style = Paint.Style.FILL
        paint.color = textColor
        paint.alpha = 50
        paint.textAlign = Paint.Align.LEFT
        canvas.drawRoundRect(
            RectF(x, barY, x + barWidth, barY + barHeight),
            barHeight / 2f,
            barHeight / 2f,
            paint,
        )

        val fillWidth = barWidth * ((percent + 100.0) / 200.0).toFloat()
        paint.color = barColor
        paint.alpha = 255
        canvas.drawRoundRect(
            RectF(x, barY, x + fillWidth, barY + barHeight),
            barHeight / 2f,
            barHeight / 2f,
            paint,
        )
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

    private fun showErrorWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_biorhythms)
        configureClicks(context, views, appWidgetId)
        views.setTextViewText(R.id.widget_title, context.getString(R.string.widget_error))
        views.setImageViewBitmap(R.id.widget_content, null)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
