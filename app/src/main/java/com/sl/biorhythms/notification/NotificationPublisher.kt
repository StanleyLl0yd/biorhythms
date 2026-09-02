package com.sl.biorhythms.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.sl.biorhythms.AppLanguage
import com.sl.biorhythms.BiorhythmEvent
import com.sl.biorhythms.BiorhythmForecast
import com.sl.biorhythms.MainActivity
import com.sl.biorhythms.R
import com.sl.biorhythms.SynchronizedExtreme
import com.sl.biorhythms.localizedResources
import com.sl.biorhythms.resolveLocale
import com.sl.biorhythms.symbol
import java.time.LocalDate
import kotlin.math.roundToInt

object NotificationPublisher {
    private const val CHANNEL_ID = "biorhythms_daily"
    private const val NOTIFICATION_ID = 1600
    private const val CONTENT_INTENT_REQUEST_CODE = 1601

    fun show(
        context: Context,
        birthDate: LocalDate,
        language: AppLanguage,
        preferences: NotificationPreferences,
        date: LocalDate = LocalDate.now(),
    ): Boolean {
        val manager = context.getSystemService(NotificationManager::class.java) ?: return false
        if (!canPostNotifications(context, manager)) return false

        val forecast = BiorhythmForecast.day(birthDate, date)
        if (!NotificationPlanner.shouldNotify(preferences, forecast)) return false

        val resources = localizedResources(context, language)
        val locale = resolveLocale(language, resources.configuration.locales[0])
        ensureChannel(manager, resources.getString(R.string.notification_channel_name), resources.getString(R.string.notification_channel_description))

        val labels = listOf(
            resources.getString(R.string.legend_physical),
            resources.getString(R.string.legend_emotional),
            resources.getString(R.string.legend_intellectual),
        )
        val selected = listOf(preferences.physical, preferences.emotional, preferences.intellectual)

        val eventLines = if (preferences.importantEvents) {
            buildList {
                forecast.synchronizedExtreme?.let { extreme ->
                    val threshold = BiorhythmForecast.SYNCHRONIZED_EXTREME_THRESHOLD.roundToInt()
                    add(
                        resources.getString(
                            when (extreme) {
                                SynchronizedExtreme.HIGH -> R.string.sync_high_title
                                SynchronizedExtreme.LOW -> R.string.sync_low_title
                            },
                            threshold,
                        ),
                    )
                }
                forecast.cycles.forEachIndexed { index, cycle ->
                    cycle.event?.let { event ->
                        add(
                            resources.getString(
                                R.string.notification_cycle_event_format,
                                labels[index],
                                resources.getString(event.labelResId()),
                            ),
                        )
                    }
                }
            }
        } else {
            emptyList()
        }

        val summaryLines = if (preferences.dailySummary) {
            forecast.cycles.mapIndexedNotNull { index, cycle ->
                if (!selected[index]) return@mapIndexedNotNull null
                resources.getString(
                    R.string.notification_cycle_value_format,
                    labels[index],
                    String.format(locale, "%+d%%", cycle.percent.roundToInt()),
                    cycle.trend.symbol(),
                )
            }
        } else {
            emptyList()
        }

        val title = when {
            preferences.importantEvents && forecast.synchronizedExtreme != null -> eventLines.first()
            preferences.importantEvents && forecast.cycles.any { it.event == BiorhythmEvent.CRITICAL } ->
                resources.getString(R.string.notification_critical_title)
            preferences.importantEvents && eventLines.isNotEmpty() ->
                resources.getString(R.string.notification_important_title)
            else -> resources.getString(R.string.notification_today_title)
        }
        val body = (eventLines + summaryLines).joinToString("\n")

        val contentIntent = PendingIntent.getActivity(
            context,
            CONTENT_INTENT_REQUEST_CODE,
            Intent(context, MainActivity::class.java)
                .setPackage(context.packageName)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = Notification.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_biorhythms)
            .setContentTitle(title)
            .setContentText(body.lineSequence().firstOrNull().orEmpty())
            .setStyle(Notification.BigTextStyle().bigText(body))
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .setCategory(Notification.CATEGORY_REMINDER)
            .build()

        manager.notify(NOTIFICATION_ID, notification)
        return true
    }

    private fun ensureChannel(
        manager: NotificationManager,
        name: String,
        description: String,
    ) {
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT).apply {
                this.description = description
            },
        )
    }

    private fun canPostNotifications(
        context: Context,
        manager: NotificationManager,
    ): Boolean {
        if (!manager.areNotificationsEnabled()) return false
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    }

    private fun BiorhythmEvent.labelResId(): Int = when (this) {
        BiorhythmEvent.CRITICAL -> R.string.cycle_event_critical
        BiorhythmEvent.PEAK -> R.string.cycle_event_peak
        BiorhythmEvent.LOW -> R.string.cycle_event_low
    }
}
