package com.sl.biorhythms

import android.app.TimePickerDialog
import android.text.format.DateFormat
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import com.sl.biorhythms.notification.NotificationPreferences
import kotlin.math.roundToInt

@Composable
internal fun NotificationSettingsSection(
    birthDateAvailable: Boolean,
    notificationPermissionGranted: Boolean,
    preferences: NotificationPreferences,
    onPreferencesChange: (NotificationPreferences) -> Unit,
    onOpenAndroidSettings: () -> Unit,
) {
    val context = LocalContext.current
    val locale = appLocale()
    val summary = when {
        !birthDateAvailable -> appString(R.string.settings_notifications_birth_date_required)
        preferences.enabled && !notificationPermissionGranted ->
            appString(R.string.settings_notifications_permission_blocked)
        preferences.enabled && !preferences.shouldSchedule ->
            appString(R.string.settings_notifications_no_types)
        preferences.enabled -> appString(R.string.settings_notifications_enabled_summary)
        else -> appString(R.string.settings_notifications_disabled_summary)
    }
    val timeText = String.format(locale, "%02d:%02d", preferences.hour, preferences.minute)

    fun update(transform: (NotificationPreferences) -> NotificationPreferences) {
        onPreferencesChange(transform(preferences))
    }

    SectionBlock(title = appString(R.string.settings_section_notifications)) {
        SettingsSwitchRow(
            icon = Icons.Outlined.Notifications,
            label = appString(R.string.settings_notifications_option),
            summary = summary,
            checked = preferences.enabled,
            enabled = birthDateAvailable,
            onCheckedChange = { enabled -> update { it.copy(enabled = enabled) } },
        )

        if (preferences.enabled && !notificationPermissionGranted) {
            SectionRow(
                icon = Icons.Outlined.Settings,
                label = appString(R.string.settings_notifications_android_settings),
                onClick = onOpenAndroidSettings,
            )
        }

        SectionRow(
            icon = Icons.Outlined.Schedule,
            label = appString(R.string.settings_notifications_time),
            value = timeText,
            onClick = if (preferences.enabled) {
                {
                    TimePickerDialog(
                        context,
                        { _, hour, minute -> update { it.copy(hour = hour, minute = minute) } },
                        preferences.hour,
                        preferences.minute,
                        DateFormat.is24HourFormat(context),
                    ).show()
                }
            } else {
                null
            },
        )

        SettingsSwitchRow(
            icon = Icons.Outlined.ListAlt,
            label = appString(R.string.settings_notifications_daily_summary),
            summary = appString(R.string.settings_notifications_daily_summary_description),
            checked = preferences.dailySummary,
            enabled = preferences.enabled,
            onCheckedChange = { checked -> update { it.copy(dailySummary = checked) } },
        )

        SettingsSwitchRow(
            icon = Icons.Outlined.Event,
            label = appString(R.string.settings_notifications_important_events),
            summary = appString(
                R.string.settings_notifications_important_events_description,
                BiorhythmForecast.SYNCHRONIZED_EXTREME_THRESHOLD.roundToInt(),
            ),
            checked = preferences.importantEvents,
            enabled = preferences.enabled,
            onCheckedChange = { checked -> update { it.copy(importantEvents = checked) } },
        )

        NotificationCycleSwitch(
            icon = Icons.Outlined.DirectionsRun,
            label = appString(R.string.legend_physical),
            checked = preferences.physical,
            enabled = preferences.enabled && preferences.dailySummary,
            canDisable = preferences.emotional || preferences.intellectual,
            onCheckedChange = { checked -> update { it.copy(physical = checked) } },
        )
        NotificationCycleSwitch(
            icon = Icons.Outlined.FavoriteBorder,
            label = appString(R.string.legend_emotional),
            checked = preferences.emotional,
            enabled = preferences.enabled && preferences.dailySummary,
            canDisable = preferences.physical || preferences.intellectual,
            onCheckedChange = { checked -> update { it.copy(emotional = checked) } },
        )
        NotificationCycleSwitch(
            icon = Icons.Outlined.Psychology,
            label = appString(R.string.legend_intellectual),
            checked = preferences.intellectual,
            enabled = preferences.enabled && preferences.dailySummary,
            canDisable = preferences.physical || preferences.emotional,
            onCheckedChange = { checked -> update { it.copy(intellectual = checked) } },
        )
    }
}

@Composable
private fun NotificationCycleSwitch(
    icon: ImageVector,
    label: String,
    checked: Boolean,
    enabled: Boolean,
    canDisable: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    SettingsSwitchRow(
        icon = icon,
        label = label,
        summary = appString(R.string.settings_notifications_include_cycle),
        checked = checked,
        enabled = enabled,
        onCheckedChange = { selected ->
            if (selected || canDisable) {
                onCheckedChange(selected)
            }
        },
    )
}
