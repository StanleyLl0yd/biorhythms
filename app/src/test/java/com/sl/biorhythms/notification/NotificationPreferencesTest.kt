package com.sl.biorhythms.notification

import androidx.datastore.preferences.core.mutablePreferencesOf
import com.sl.biorhythms.PreferencesKeys
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationPreferencesTest {
    @Test
    fun legacyNotificationToggleIsUsedWhenCurrentKeyIsMissing() {
        val preferences = mutablePreferencesOf(
            PreferencesKeys.LegacyNotificationEnabled to true,
        )

        assertTrue(NotificationPreferences.fromPreferences(preferences).enabled)
    }

    @Test
    fun currentNotificationToggleOverridesLegacyValue() {
        val preferences = mutablePreferencesOf(
            PreferencesKeys.LegacyNotificationEnabled to true,
            PreferencesKeys.NotificationEnabled to false,
        )

        assertFalse(NotificationPreferences.fromPreferences(preferences).enabled)
    }
}
