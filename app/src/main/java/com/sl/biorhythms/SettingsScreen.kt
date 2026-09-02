package com.sl.biorhythms

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
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Palette
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
import androidx.compose.ui.unit.dp
import com.sl.biorhythms.notification.NotificationPreferences
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

    val locale = appLocale()
    val dateFormatter = remember(locale) {
        DateTimeFormatter.ofPattern("d MMMM yyyy", locale)
    }
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

            NotificationSettingsSection(
                birthDateAvailable = state.birthDate != null,
                notificationPermissionGranted = state.notificationPermissionGranted,
                preferences = state.notificationPreferences,
                onPreferencesChange = actions.onNotificationPreferencesChange,
                onOpenAndroidSettings = actions.onOpenNotificationSettings,
            )

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
