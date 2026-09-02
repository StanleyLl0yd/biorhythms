package com.sl.biorhythms

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sl.biorhythms.notification.NotificationPreferences
import com.sl.biorhythms.notification.NotificationPublisher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class NotificationPublisherInstrumentedTest {
    @Test
    fun dailySummaryPostsLocalizedNotification() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        instrumentation.uiAutomation.grantRuntimePermission(
            context.packageName,
            Manifest.permission.POST_NOTIFICATIONS,
        )
        notificationManager.cancelAll()

        try {
            val posted = NotificationPublisher.show(
                context = context,
                birthDate = LocalDate.of(1977, 12, 1),
                language = AppLanguage.EN,
                preferences = NotificationPreferences(
                    enabled = true,
                    dailySummary = true,
                    importantEvents = false,
                ),
                date = LocalDate.of(2026, 9, 2),
            )

            assertTrue(posted)
            val notification = notificationManager.activeNotifications.single().notification
            assertEquals(
                localizedResources(context, AppLanguage.EN).getString(R.string.notification_today_title),
                notification.extras.getString(Notification.EXTRA_TITLE),
            )
        } finally {
            notificationManager.cancelAll()
        }
    }
}
