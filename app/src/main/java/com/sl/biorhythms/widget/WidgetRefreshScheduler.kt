package com.sl.biorhythms.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import java.time.ZonedDateTime

internal object WidgetRefreshScheduler {
    const val ACTION_DAILY_REFRESH = "com.sl.biorhythms.widget.action.DAILY_REFRESH"

    fun schedule(context: Context) {
        val appContext = context.applicationContext
        val manager = AppWidgetManager.getInstance(appContext)
        val component = ComponentName(appContext, BiorhythmsWidgetProvider::class.java)
        if (manager.getAppWidgetIds(component).isEmpty()) {
            cancel(appContext)
            return
        }

        appContext.getSystemService(AlarmManager::class.java).setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            nextWidgetRefreshEpochMillis(ZonedDateTime.now()),
            refreshIntent(appContext),
        )
    }

    fun cancel(context: Context) {
        val appContext = context.applicationContext
        val pendingIntent = PendingIntent.getBroadcast(
            appContext,
            0,
            Intent(appContext, BiorhythmsWidgetProvider::class.java)
                .setAction(ACTION_DAILY_REFRESH),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
        ) ?: return

        appContext.getSystemService(AlarmManager::class.java).cancel(pendingIntent)
        pendingIntent.cancel()
    }

    private fun refreshIntent(context: Context): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, BiorhythmsWidgetProvider::class.java)
                .setAction(ACTION_DAILY_REFRESH),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
}

internal fun nextWidgetRefreshEpochMillis(now: ZonedDateTime): Long =
    now.toLocalDate()
        .plusDays(1)
        .atStartOfDay(now.zone)
        .toInstant()
        .toEpochMilli()
