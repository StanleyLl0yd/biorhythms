package com.sl.biorhythms

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    themeMode: AppThemeMode,
    language: AppLanguage,
    onThemeModeChange: (AppThemeMode) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
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

            // Appearance
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = appString(R.string.settings_section_appearance),
                    style = MaterialTheme.typography.titleMedium
                )

                SettingsRadioRow(
                    label = appString(R.string.settings_theme_system),
                    selected = themeMode == AppThemeMode.SYSTEM,
                    onClick = { onThemeModeChange(AppThemeMode.SYSTEM) }
                )
                SettingsRadioRow(
                    label = appString(R.string.settings_theme_light),
                    selected = themeMode == AppThemeMode.LIGHT,
                    onClick = { onThemeModeChange(AppThemeMode.LIGHT) }
                )
                SettingsRadioRow(
                    label = appString(R.string.settings_theme_dark),
                    selected = themeMode == AppThemeMode.DARK,
                    onClick = { onThemeModeChange(AppThemeMode.DARK) }
                )
            }

            // Language
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = appString(R.string.settings_section_language),
                    style = MaterialTheme.typography.titleMedium
                )

                SettingsRadioRow(
                    label = appString(R.string.settings_language_system),
                    selected = language == AppLanguage.SYSTEM,
                    onClick = { onLanguageChange(AppLanguage.SYSTEM) }
                )
                SettingsRadioRow(
                    label = appString(R.string.settings_language_russian),
                    selected = language == AppLanguage.RU,
                    onClick = { onLanguageChange(AppLanguage.RU) }
                )
                SettingsRadioRow(
                    label = appString(R.string.settings_language_english),
                    selected = language == AppLanguage.EN,
                    onClick = { onLanguageChange(AppLanguage.EN) }
                )
            }
        }
    }
}

@Composable
private fun SettingsRadioRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}