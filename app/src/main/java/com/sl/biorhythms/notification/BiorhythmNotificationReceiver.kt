package com.sl.biorhythms.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.datastore.preferences.core.edit
import com.sl.biorhythms.AppLanguage
import com.sl.biorhythms.PreferencesKeys
import com.sl.biorhythms.dataStore
import com.sl.biorhythms.storedBirthDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

class BiorhythmNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        if (action !in SUPPORTED_ACTIONS) return

        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                val preferences = context.dataStore.data.first()
                val notificationPreferences = NotificationPreferences.fromPreferences(preferences)

                if (action == NotificationScheduler.ACTION_DAILY_NOTIFICATION &&
                    notificationPreferences.shouldSchedule
                ) {
                    val today = LocalDate.now()
                    val lastNotificationDay = preferences[PreferencesKeys.NotificationLastEpochDay]
                    val birthDate = storedBirthDate(preferences[PreferencesKeys.BirthDate], today)
                    if (birthDate != null && lastNotificationDay != today.toEpochDay()) {
                        val language = AppLanguage.fromStored(preferences[PreferencesKeys.Language])
                        val posted = NotificationPublisher.show(
                            context = context,
                            birthDate = birthDate,
                            language = language,
                            preferences = notificationPreferences,
                            date = today,
                        )
                        if (posted) {
                            context.dataStore.edit { mutablePreferences ->
                                mutablePreferences[PreferencesKeys.NotificationLastEpochDay] = today.toEpochDay()
                            }
                        }
                    }
                }

                NotificationScheduler.reconcile(context, notificationPreferences)
            } catch (error: Exception) {
                Log.e(TAG, "Unable to process notification schedule", error)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private companion object {
        const val TAG = "BiorhythmNotification"

        val SUPPORTED_ACTIONS = setOf(
            NotificationScheduler.ACTION_DAILY_NOTIFICATION,
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_LOCALE_CHANGED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
        )
    }
}
