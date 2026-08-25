package com.sl.biorhythms.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent

object WidgetUpdater {
    fun requestUpdate(context: Context) {
        val appContext = context.applicationContext
        val manager = AppWidgetManager.getInstance(appContext)
        val component = ComponentName(appContext, BiorhythmsWidgetProvider::class.java)
        val ids = manager.getAppWidgetIds(component)
        if (ids.isEmpty()) return

        val intent = Intent(appContext, BiorhythmsWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        }
        appContext.sendBroadcast(intent)
    }
}
