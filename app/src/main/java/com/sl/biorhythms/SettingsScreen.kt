package com.sl.biorhythms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

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

    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
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
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // заголовок и back
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = appString(R.string.settings_title)
                    )
                }
                Text(
                    text = appString(R.string.settings_title),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            // Дата рождения
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = appString(R.string.title_select_birth_date),
                    style = MaterialTheme.typography.bodySmall
                )

                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = birthDate?.format(dateFormatter)
                            ?: appString(R.string.action_tap_to_choose_date),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Оформление
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = appString(R.string.settings_section_appearance),
                    style = MaterialTheme.typography.bodySmall
                )

                SettingsOptionRow(
                    label = appString(R.string.settings_theme_option),
                    value = themeValueText,
                    onClick = { showThemeDialog = true }
                )
            }

            // Язык
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = appString(R.string.settings_section_language),
                    style = MaterialTheme.typography.bodySmall
                )

                SettingsOptionRow(
                    label = appString(R.string.settings_language_option),
                    value = languageValueText,
                    onClick = { showLanguageDialog = true }
                )
            }
        }

        if (showDatePicker) {
            BirthDatePickerDialog(
                initialDate = birthDate ?: LocalDate.now().minusYears(25),
                onDismiss = { showDatePicker = false },
                onDateSelected = { date ->
                    showDatePicker = false
                    onBirthDateChange(date)
                }
            )
        }

        if (showThemeDialog) {
            ThemeModeDialog(
                current = themeMode,
                onSelect = {
                    onThemeModeChange(it)
                    showThemeDialog = false
                },
                onDismiss = { showThemeDialog = false }
            )
        }

        if (showLanguageDialog) {
            LanguageDialog(
                current = language,
                onSelect = {
                    onLanguageChange(it)
                    showLanguageDialog = false
                },
                onDismiss = { showLanguageDialog = false }
            )
        }
    }
}

@Composable
private fun SettingsOptionRow(
    label: String,
    value: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline
        )
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
        title = { Text(text = appString(R.string.settings_section_appearance)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ThemeOptionRow(
                    label = appString(R.string.settings_theme_system),
                    selected = current == AppThemeMode.SYSTEM,
                    onClick = { onSelect(AppThemeMode.SYSTEM) }
                )
                ThemeOptionRow(
                    label = appString(R.string.settings_theme_light),
                    selected = current == AppThemeMode.LIGHT,
                    onClick = { onSelect(AppThemeMode.LIGHT) }
                )
                ThemeOptionRow(
                    label = appString(R.string.settings_theme_dark),
                    selected = current == AppThemeMode.DARK,
                    onClick = { onSelect(AppThemeMode.DARK) }
                )
            }
        },
        confirmButton = {}
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
        title = { Text(text = appString(R.string.settings_section_language)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                LanguageOptionRow(
                    label = appString(R.string.settings_language_system),
                    selected = current == AppLanguage.SYSTEM,
                    onClick = { onSelect(AppLanguage.SYSTEM) }
                )
                LanguageOptionRow(
                    label = appString(R.string.settings_language_russian),
                    selected = current == AppLanguage.RU,
                    onClick = { onSelect(AppLanguage.RU) }
                )
                LanguageOptionRow(
                    label = appString(R.string.settings_language_english),
                    selected = current == AppLanguage.EN,
                    onClick = { onSelect(AppLanguage.EN) }
                )
            }
        },
        confirmButton = {}
    )
}

@Composable
private fun ThemeOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun LanguageOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}