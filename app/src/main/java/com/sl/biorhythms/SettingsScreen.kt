package com.sl.biorhythms

import android.app.TimePickerDialog
import android.text.format.DateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sl.biorhythms.notification.NotificationPreferences
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

data class SettingsState(
    val themeMode: AppThemeMode,
    val language: AppLanguage,
    val birthDate: LocalDate?,
    val notificationPreferences: NotificationPreferences,
    val notificationPermissionGranted: Boolean,
)

data class SettingsActions(
    val onThemeModeChange: (AppThemeMode) -> Unit,
    val onLanguageChange: (AppLanguage) -> Unit,
    val onBirthDateChange: (LocalDate) -> Unit,
    val onNotificationPreferencesChange: (NotificationPreferences) -> Unit,
    val onOpenNotificationSettings: () -> Unit,
    val onOpenAbout: () -> Unit,
    val onBack: () -> Unit,
)

private data class SelectionOption<T>(
    val value: T,
    val label: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsState,
    actions: SettingsActions,
) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showThemeDialog by rememberSaveable { mutableStateOf(false) }
    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val locale = appLocale()
    val dateFormatter = remember(locale) {
        DateTimeFormatter.ofPattern("d MMMM yyyy", locale)
    }
    val notifications = state.notificationPreferences
    val notificationsSummary = when {
        state.birthDate == null -> appString(R.string.settings_notifications_birth_date_required)
        notifications.enabled && !state.notificationPermissionGranted ->
            appString(R.string.settings_notifications_permission_blocked)
        notifications.enabled && !notifications.shouldSchedule ->
            appString(R.string.settings_notifications_no_types)
        notifications.enabled -> appString(R.string.settings_notifications_enabled_summary)
        else -> appString(R.string.settings_notifications_disabled_summary)
    }
    val timeValueText = String.format(locale, "%02d:%02d", notifications.hour, notifications.minute)

    val themeValueText = when (state.themeMode) {
        AppThemeMode.SYSTEM -> appString(R.string.settings_theme_system)
        AppThemeMode.LIGHT -> appString(R.string.settings_theme_light)
        AppThemeMode.DARK -> appString(R.string.settings_theme_dark)
    }

    val languageValueText = when (state.language) {
        AppLanguage.SYSTEM -> appString(R.string.settings_language_system)
        AppLanguage.RU -> appString(R.string.settings_language_russian)
        AppLanguage.EN -> appString(R.string.settings_language_english)
    }

    fun updateNotifications(transform: (NotificationPreferences) -> NotificationPreferences) {
        actions.onNotificationPreferencesChange(transform(notifications))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(appString(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = actions.onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = appString(R.string.action_back),
                        )
                    }
                },
            )
        },
        contentWindowInsets = WindowInsets.systemBars,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            SectionBlock(title = appString(R.string.settings_section_profile)) {
                SectionRow(
                    icon = Icons.Outlined.CalendarMonth,
                    label = appString(R.string.settings_birth_date_option),
                    value = state.birthDate?.format(dateFormatter)
                        ?: appString(R.string.action_tap_to_choose_date),
                    onClick = { showDatePicker = true },
                )
            }

            SectionBlock(title = appString(R.string.settings_section_notifications)) {
                SettingsSwitchRow(
                    icon = Icons.Outlined.Notifications,
                    label = appString(R.string.settings_notifications_option),
                    summary = notificationsSummary,
                    checked = notifications.enabled,
                    enabled = state.birthDate != null,
                    onCheckedChange = { enabled -> updateNotifications { it.copy(enabled = enabled) } },
                )

                if (notifications.enabled && !state.notificationPermissionGranted) {
                    SectionRow(
                        icon = Icons.Outlined.Settings,
                        label = appString(R.string.settings_notifications_android_settings),
                        onClick = actions.onOpenNotificationSettings,
                    )
                }

                SectionRow(
                    icon = Icons.Outlined.Schedule,
                    label = appString(R.string.settings_notifications_time),
                    value = timeValueText,
                    onClick = if (notifications.enabled) {
                        {
                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    updateNotifications { it.copy(hour = hour, minute = minute) }
                                },
                                notifications.hour,
                                notifications.minute,
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
                    checked = notifications.dailySummary,
                    enabled = notifications.enabled,
                    onCheckedChange = { checked -> updateNotifications { it.copy(dailySummary = checked) } },
                )

                SettingsSwitchRow(
                    icon = Icons.Outlined.Event,
                    label = appString(R.string.settings_notifications_important_events),
                    summary = appString(
                        R.string.settings_notifications_important_events_description,
                        BiorhythmForecast.SYNCHRONIZED_EXTREME_THRESHOLD.roundToInt(),
                    ),
                    checked = notifications.importantEvents,
                    enabled = notifications.enabled,
                    onCheckedChange = { checked -> updateNotifications { it.copy(importantEvents = checked) } },
                )

                SettingsSwitchRow(
                    icon = Icons.Outlined.DirectionsRun,
                    label = appString(R.string.legend_physical),
                    summary = appString(R.string.settings_notifications_include_cycle),
                    checked = notifications.physical,
                    enabled = notifications.enabled && notifications.dailySummary,
                    onCheckedChange = { checked ->
                        if (checked || notifications.emotional || notifications.intellectual) {
                            updateNotifications { it.copy(physical = checked) }
                        }
                    },
                )

                SettingsSwitchRow(
                    icon = Icons.Outlined.FavoriteBorder,
                    label = appString(R.string.legend_emotional),
                    summary = appString(R.string.settings_notifications_include_cycle),
                    checked = notifications.emotional,
                    enabled = notifications.enabled && notifications.dailySummary,
                    onCheckedChange = { checked ->
                        if (checked || notifications.physical || notifications.intellectual) {
                            updateNotifications { it.copy(emotional = checked) }
                        }
                    },
                )

                SettingsSwitchRow(
                    icon = Icons.Outlined.Psychology,
                    label = appString(R.string.legend_intellectual),
                    summary = appString(R.string.settings_notifications_include_cycle),
                    checked = notifications.intellectual,
                    enabled = notifications.enabled && notifications.dailySummary,
                    onCheckedChange = { checked ->
                        if (checked || notifications.physical || notifications.emotional) {
                            updateNotifications { it.copy(intellectual = checked) }
                        }
                    },
                )
            }

            SectionBlock(title = appString(R.string.settings_section_appearance)) {
                SectionRow(
                    icon = Icons.Outlined.Palette,
                    label = appString(R.string.settings_theme_option),
                    value = themeValueText,
                    onClick = { showThemeDialog = true },
                )
            }

            SectionBlock(title = appString(R.string.settings_section_language)) {
                SectionRow(
                    icon = Icons.Outlined.Language,
                    label = appString(R.string.settings_language_option),
                    value = languageValueText,
                    onClick = { showLanguageDialog = true },
                )
            }

            SectionBlock(title = appString(R.string.settings_section_about)) {
                SectionRow(
                    icon = Icons.Outlined.Info,
                    label = appString(R.string.settings_about_option),
                    value = appString(R.string.settings_about_summary),
                    onClick = actions.onOpenAbout,
                )
            }
        }
    }

    if (showDatePicker) {
        BirthDatePickerDialog(
            initialDate = state.birthDate ?: LocalDate.now().minusYears(25),
            onDismiss = { showDatePicker = false },
            onDateSelected = { date ->
                showDatePicker = false
                actions.onBirthDateChange(date)
            },
        )
    }

    if (showThemeDialog) {
        SelectionDialog(
            title = appString(R.string.settings_section_appearance),
            current = state.themeMode,
            options = listOf(
                SelectionOption(AppThemeMode.SYSTEM, appString(R.string.settings_theme_system)),
                SelectionOption(AppThemeMode.LIGHT, appString(R.string.settings_theme_light)),
                SelectionOption(AppThemeMode.DARK, appString(R.string.settings_theme_dark)),
            ),
            onSelect = { selected ->
                actions.onThemeModeChange(selected)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false },
        )
    }

    if (showLanguageDialog) {
        SelectionDialog(
            title = appString(R.string.settings_section_language),
            current = state.language,
            options = listOf(
                SelectionOption(AppLanguage.SYSTEM, appString(R.string.settings_language_system)),
                SelectionOption(AppLanguage.RU, appString(R.string.settings_language_russian)),
                SelectionOption(AppLanguage.EN, appString(R.string.settings_language_english)),
            ),
            onSelect = { selected ->
                actions.onLanguageChange(selected)
                showLanguageDialog = false
            },
            onDismiss = { showLanguageDialog = false },
        )
    }
}

@Composable
private fun <T> SelectionDialog(
    title: String,
    current: T,
    options: List<SelectionOption<T>>,
    onSelect: (T) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                options.forEach { option ->
                    SettingsRadioOptionRow(
                        label = option.label,
                        selected = current == option.value,
                        onClick = { onSelect(option.value) },
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(appString(R.string.action_close))
            }
        },
    )
}

@Composable
private fun SettingsRadioOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}
