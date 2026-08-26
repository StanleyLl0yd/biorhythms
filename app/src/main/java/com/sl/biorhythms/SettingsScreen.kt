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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    themeMode: AppThemeMode,
    language: AppLanguage,
    birthDate: LocalDate?,
    onThemeModeChange: (AppThemeMode) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onBirthDateChange: (LocalDate) -> Unit,
    onBack: () -> Unit,
) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showThemeDialog by rememberSaveable { mutableStateOf(false) }
    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }

    val locale = appLocale()
    val dateFormatter = remember(locale) {
        DateTimeFormatter.ofPattern("d MMMM yyyy", locale)
    }

    val themeValueText = when (themeMode) {
        AppThemeMode.SYSTEM -> appString(R.string.settings_theme_system)
        AppThemeMode.LIGHT -> appString(R.string.settings_theme_light)
        AppThemeMode.DARK -> appString(R.string.settings_theme_dark)
    }

    val languageValueText = when (language) {
        AppLanguage.SYSTEM -> appString(R.string.settings_language_system)
        AppLanguage.RU -> appString(R.string.settings_language_russian)
        AppLanguage.EN -> appString(R.string.settings_language_english)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(appString(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
            SettingsSection(title = appString(R.string.settings_section_profile)) {
                SettingsOptionRow(
                    icon = Icons.Outlined.CalendarMonth,
                    label = appString(R.string.settings_birth_date_option),
                    value = birthDate?.format(dateFormatter)
                        ?: appString(R.string.action_tap_to_choose_date),
                    onClick = { showDatePicker = true },
                )
            }

            SettingsSection(title = appString(R.string.settings_section_appearance)) {
                SettingsOptionRow(
                    icon = Icons.Outlined.Palette,
                    label = appString(R.string.settings_theme_option),
                    value = themeValueText,
                    onClick = { showThemeDialog = true },
                )
            }

            SettingsSection(title = appString(R.string.settings_section_language)) {
                SettingsOptionRow(
                    icon = Icons.Outlined.Language,
                    label = appString(R.string.settings_language_option),
                    value = languageValueText,
                    onClick = { showLanguageDialog = true },
                )
            }
        }
    }

    if (showDatePicker) {
        BirthDatePickerDialog(
            initialDate = birthDate ?: LocalDate.now().minusYears(25),
            onDismiss = { showDatePicker = false },
            onDateSelected = { date ->
                showDatePicker = false
                onBirthDateChange(date)
            },
        )
    }

    if (showThemeDialog) {
        ThemeModeDialog(
            current = themeMode,
            onSelect = {
                onThemeModeChange(it)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false },
        )
    }

    if (showLanguageDialog) {
        LanguageDialog(
            current = language,
            onSelect = {
                onLanguageChange(it)
                showLanguageDialog = false
            },
            onDismiss = { showLanguageDialog = false },
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
        content()
    }
}

@Composable
private fun SettingsOptionRow(
    icon: ImageVector,
    label: String,
    value: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 64.dp)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp),
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(text = label, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
            )
        }
    }
}

@Composable
private fun ThemeModeDialog(
    current: AppThemeMode,
    onSelect: (AppThemeMode) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(appString(R.string.settings_section_appearance)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                SettingsRadioOptionRow(
                    label = appString(R.string.settings_theme_system),
                    selected = current == AppThemeMode.SYSTEM,
                    onClick = { onSelect(AppThemeMode.SYSTEM) },
                )
                SettingsRadioOptionRow(
                    label = appString(R.string.settings_theme_light),
                    selected = current == AppThemeMode.LIGHT,
                    onClick = { onSelect(AppThemeMode.LIGHT) },
                )
                SettingsRadioOptionRow(
                    label = appString(R.string.settings_theme_dark),
                    selected = current == AppThemeMode.DARK,
                    onClick = { onSelect(AppThemeMode.DARK) },
                )
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
private fun LanguageDialog(
    current: AppLanguage,
    onSelect: (AppLanguage) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(appString(R.string.settings_section_language)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                SettingsRadioOptionRow(
                    label = appString(R.string.settings_language_system),
                    selected = current == AppLanguage.SYSTEM,
                    onClick = { onSelect(AppLanguage.SYSTEM) },
                )
                SettingsRadioOptionRow(
                    label = appString(R.string.settings_language_russian),
                    selected = current == AppLanguage.RU,
                    onClick = { onSelect(AppLanguage.RU) },
                )
                SettingsRadioOptionRow(
                    label = appString(R.string.settings_language_english),
                    selected = current == AppLanguage.EN,
                    onClick = { onSelect(AppLanguage.EN) },
                )
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
