package com.sl.biorhythms.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.ZonedDateTime

object NotificationScheduler {
    const val ACTION_DAILY_NOTIFICATION = "com.sl.biorhythms.action.DAILY_NOTIFICATION"
    private const val REQUEST_CODE = 1600

    fun scheduleDaily(
        context: Context,
        hour: Int = NotificationPreferences.DEFAULT_HOUR,
        minute: Int = NotificationPreferences.DEFAULT_MINUTE,
    ) {
        runCatching {
            val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
            val triggerAt = nextTrigger(ZonedDateTime.now(), hour, minute)
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAt.toInstant().toEpochMilli(),
                pendingIntent(context),
            )
        }
    }

    fun reconcile(context: Context, preferences: NotificationPreferences) {
        if (preferences.shouldSchedule) {
            scheduleDaily(context, preferences.hour, preferences.minute)
        } else {
            cancel(context)
        }
    }

    fun cancel(context: Context) {
        runCatching {
            val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
            alarmManager.cancel(pendingIntent(context))
        }
    }

    internal fun nextTrigger(
        now: ZonedDateTime,
        hour: Int,
        minute: Int,
    ): ZonedDateTime {
        require(hour in 0..23) { "hour must be within 0..23" }
        require(minute in 0..59) { "minute must be within 0..59" }

        var candidate = now.toLocalDate().atTime(hour, minute).atZone(now.zone)
        if (!candidate.isAfter(now)) {
            candidate = now.toLocalDate().plusDays(1).atTime(hour, minute).atZone(now.zone)
        }
        return candidate
    }

    private fun pendingIntent(context: Context): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            Intent(context, BiorhythmNotificationReceiver::class.java)
                .setPackage(context.packageName)
                .setAction(ACTION_DAILY_NOTIFICATION),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
}
