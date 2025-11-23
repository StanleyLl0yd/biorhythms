package com.sl.biorhythms.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.sl.biorhythms.MainActivity
import com.sl.biorhythms.R
import com.sl.biorhythms.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin

class BiorhythmsWidgetProvider : AppWidgetProvider() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            scope.launch {
                try {
                    updateAppWidget(context, appWidgetManager, appWidgetId)
                } catch (e: Exception) {
                    android.util.Log.e("BiorhythmsWidget", "Error updating widget", e)
                    // Показываем базовый виджет с ошибкой
                    showErrorWidget(context, appWidgetManager, appWidgetId, e.message)
                }
            }
        }
    }

    override fun onEnabled(context: Context) {
        // Вызывается при добавлении первого виджета
    }

    override fun onDisabled(context: Context) {
        // Вызывается при удалении последнего виджета
        job.cancel()
    }

    private suspend fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val prefs = WidgetPreferences(context)
        val dataStore = context.dataStore

        // Получаем настройки из DataStore
        val preferences = dataStore.data.first()
        val birthDateEpoch = preferences[WidgetPreferences.BirthDateKey]
        val themeModeInt = preferences[WidgetPreferences.ThemeModeKey] ?: 0
        val languageInt = preferences[WidgetPreferences.LanguageKey] ?: 0

        // Получаем прозрачность виджета
        val alpha = prefs.getAlpha(appWidgetId)

        val views = RemoteViews(context.packageName, R.layout.widget_biorhythms)

        // Настройка клика на контейнер виджета - открывает приложение
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            val containerId = context.resources.getIdentifier("widget_container", "id", context.packageName)
            if (containerId != 0) {
                views.setOnClickPendingIntent(containerId, pendingIntent)
            }
        } catch (e: Exception) {
            android.util.Log.e("BiorhythmsWidget", "Error setting container click", e)
        }

        // Настройка клика на кнопку настроек
        val configIntent = Intent(context, WidgetConfigActivity::class.java).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        val configPendingIntent = PendingIntent.getActivity(
            context,
            appWidgetId,
            configIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            val settingsId = context.resources.getIdentifier("widget_settings", "id", context.packageName)
            if (settingsId != 0) {
                views.setOnClickPendingIntent(settingsId, configPendingIntent)
            }
        } catch (e: Exception) {
            android.util.Log.e("BiorhythmsWidget", "Error setting settings click", e)
        }

        if (birthDateEpoch != null) {
            val birthDate = LocalDate.ofEpochDay(birthDateEpoch)
            val today = LocalDate.now()

            // Определяем тему
            val isDarkTheme = when (themeModeInt) {
                0 -> context.isSystemDarkTheme()
                1 -> false
                2 -> true
                else -> context.isSystemDarkTheme()
            }

            // Определяем язык
            val locale = when (languageInt) {
                0 -> Locale.getDefault()
                1 -> Locale.forLanguageTag("ru")
                2 -> Locale.forLanguageTag("en")
                else -> Locale.getDefault()
            }

            // Генерируем контент виджета
            val bitmap = try {
                generateWidgetBitmap(context, birthDate, today, isDarkTheme, alpha)
            } catch (e: Exception) {
                android.util.Log.e("BiorhythmsWidget", "Error generating bitmap", e)
                null
            }

            if (bitmap != null) {
                try {
                    val contentId = context.resources.getIdentifier("widget_content", "id", context.packageName)
                    if (contentId != 0) {
                        views.setImageViewBitmap(contentId, bitmap)
                    }
                } catch (e: Exception) {
                    android.util.Log.e("BiorhythmsWidget", "Error setting bitmap", e)
                }
            }

            // Устанавливаем текст с локализацией
            val resources = context.createConfigurationContext(
                context.resources.configuration.apply { setLocale(locale) }
            ).resources

            try {
                val titleId = context.resources.getIdentifier("widget_title", "id", context.packageName)
                if (titleId != 0) {
                    views.setTextViewText(titleId, resources.getString(R.string.widget_title))
                }
            } catch (e: Exception) {
                android.util.Log.e("BiorhythmsWidget", "Error setting title", e)
            }

            // Устанавливаем прозрачность фона
            try {
                val alphaValue = (alpha * 255 / 100).coerceIn(0, 255)
                val backgroundColor = if (isDarkTheme) {
                    0xFF131A3A.toInt()
                } else {
                    0xFFFFFFFF.toInt()
                }
                val colorWithAlpha = (backgroundColor and 0x00FFFFFF) or (alphaValue shl 24)
                val containerId = context.resources.getIdentifier("widget_container", "id", context.packageName)
                if (containerId != 0) {
                    views.setInt(containerId, "setBackgroundColor", colorWithAlpha)
                }
            } catch (e: Exception) {
                android.util.Log.e("BiorhythmsWidget", "Error setting background", e)
            }

        } else {
            // Если дата рождения не установлена
            val resources = context.createConfigurationContext(
                context.resources.configuration.apply {
                    setLocale(when (languageInt) {
                        0 -> Locale.getDefault()
                        1 -> Locale.forLanguageTag("ru")
                        2 -> Locale.forLanguageTag("en")
                        else -> Locale.getDefault()
                    })
                }
            ).resources

            try {
                val titleId = context.resources.getIdentifier("widget_title", "id", context.packageName)
                if (titleId != 0) {
                    views.setTextViewText(titleId, resources.getString(R.string.widget_no_birth_date))
                }
            } catch (e: Exception) {
                android.util.Log.e("BiorhythmsWidget", "Error setting no-birth-date title", e)
            }

            try {
                val contentId = context.resources.getIdentifier("widget_content", "id", context.packageName)
                if (contentId != 0) {
                    views.setImageViewBitmap(contentId, null)
                }
            } catch (e: Exception) {
                android.util.Log.e("BiorhythmsWidget", "Error clearing bitmap", e)
            }
        }

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun generateWidgetBitmap(
        context: Context,
        birthDate: LocalDate,
        today: LocalDate,
        isDarkTheme: Boolean,
        alpha: Int
    ): Bitmap {
        val width = 400
        val height = 200
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val daysFromBirth = ChronoUnit.DAYS.between(birthDate, today)

        // Расчет биоритмов
        val physical = biorhythmValue(daysFromBirth, 23.0)
        val emotional = biorhythmValue(daysFromBirth, 28.0)
        val intellectual = biorhythmValue(daysFromBirth, 33.0)

        val physicalPercent = toPercent(physical)
        val emotionalPercent = toPercent(emotional)
        val intellectualPercent = toPercent(intellectual)

        // Цвета
        val textColor = try {
            if (isDarkTheme) {
                ContextCompat.getColor(context, R.color.widget_text_dark)
            } else {
                ContextCompat.getColor(context, R.color.widget_text_light)
            }
        } catch (e: Exception) {
            if (isDarkTheme) 0xFFE8EDFF.toInt() else 0xFF0B1026.toInt()
        }

        val physicalColor = try {
            ContextCompat.getColor(context, R.color.widget_physical)
        } catch (e: Exception) {
            0xFFF05D64.toInt()
        }

        val emotionalColor = try {
            ContextCompat.getColor(context, R.color.widget_emotional)
        } catch (e: Exception) {
            0xFF1FAE9E.toInt()
        }

        val intellectualColor = try {
            ContextCompat.getColor(context, R.color.widget_intellectual)
        } catch (e: Exception) {
            0xFF3C5CE5.toInt()
        }

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = 28f
        paint.color = textColor

        // Рисуем три строки биоритмов
        val startY = 40f
        val lineHeight = 50f

        // Physical
        drawBiorhythmLine(
            canvas,
            try { context.getString(R.string.legend_physical) } catch (e: Exception) { "Physical" },
            physicalPercent,
            physicalColor,
            textColor,
            20f,
            startY,
            width - 40f
        )

        // Emotional
        drawBiorhythmLine(
            canvas,
            try { context.getString(R.string.legend_emotional) } catch (e: Exception) { "Emotional" },
            emotionalPercent,
            emotionalColor,
            textColor,
            20f,
            startY + lineHeight,
            width - 40f
        )

        // Intellectual
        drawBiorhythmLine(
            canvas,
            try { context.getString(R.string.legend_intellectual) } catch (e: Exception) { "Intellectual" },
            intellectualPercent,
            intellectualColor,
            textColor,
            20f,
            startY + lineHeight * 2,
            width - 40f
        )

        return bitmap
    }

    private fun drawBiorhythmLine(
        canvas: Canvas,
        label: String,
        percent: Double,
        barColor: Int,
        textColor: Int,
        x: Float,
        y: Float,
        maxWidth: Float
    ) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Рисуем label
        paint.color = textColor
        paint.textSize = 24f
        canvas.drawText(label, x, y, paint)

        // Рисуем процент справа
        val percentText = String.format(Locale.getDefault(), "%+d%%", percent.roundToInt())
        val percentColor = colorForPercent(percent)
        paint.color = percentColor
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText(percentText, x + maxWidth, y, paint)

        // Рисуем прогресс-бар
        val barY = y + 8f
        val barHeight = 12f
        val barWidth = maxWidth * 0.6f

        // Фон бара
        paint.color = textColor
        paint.alpha = 50
        paint.style = Paint.Style.FILL
        val backgroundRect = RectF(x, barY, x + barWidth, barY + barHeight)
        canvas.drawRoundRect(backgroundRect, 6f, 6f, paint)

        // Заполненная часть
        val fillWidth = barWidth * ((percent + 100) / 200).toFloat()
        paint.color = barColor
        paint.alpha = 255
        val fillRect = RectF(x, barY, x + fillWidth, barY + barHeight)
        canvas.drawRoundRect(fillRect, 6f, 6f, paint)
    }

    private fun biorhythmValue(daysFromBirth: Long, period: Double): Double =
        sin(2.0 * PI * daysFromBirth / period)

    private fun toPercent(value: Double): Double =
        (value * 100.0).coerceIn(-100.0, 100.0)

    private fun colorForPercent(percent: Double): Int {
        return when {
            percent <= -50 -> 0xFFFF3B30.toInt()
            percent <= 0 -> 0xFFFFCC00.toInt()
            percent <= 50 -> 0xFF8BC34A.toInt()
            else -> 0xFF34C759.toInt()
        }
    }
}

private fun Context.isSystemDarkTheme(): Boolean {
    val nightMode = resources.configuration.uiMode and
            android.content.res.Configuration.UI_MODE_NIGHT_MASK
    return nightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES
}

private fun BiorhythmsWidgetProvider.showErrorWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    errorMessage: String?
) {
    val views = RemoteViews(context.packageName, R.layout.widget_biorhythms)

    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    try {
        val containerId = context.resources.getIdentifier("widget_container", "id", context.packageName)
        if (containerId != 0) {
            views.setOnClickPendingIntent(containerId, pendingIntent)
        }
    } catch (e: Exception) {
        android.util.Log.e("BiorhythmsWidget", "Error in error widget", e)
    }

    try {
        val titleId = context.resources.getIdentifier("widget_title", "id", context.packageName)
        if (titleId != 0) {
            views.setTextViewText(titleId, "Widget Error: ${errorMessage ?: "Unknown"}")
        }
    } catch (e: Exception) {
        android.util.Log.e("BiorhythmsWidget", "Error setting error message", e)
    }

    appWidgetManager.updateAppWidget(appWidgetId, views)
}