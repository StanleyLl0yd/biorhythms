package com.sl.biorhythms

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sl.biorhythms.ui.theme.BiorhythmsTheme

private const val DEFAULT_RANGE_DAYS = 15

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "biorhythms_prefs",
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val vm: BiorhythmsViewModel = viewModel(
                factory = BiorhythmsViewModelFactory(applicationContext.dataStore)
            )

            BiorhythmsRoot(viewModel = vm)
        }
    }
}

private enum class AppScreen {
    MAIN,
    SETTINGS
}

@Composable
fun BiorhythmsRoot(
    viewModel: BiorhythmsViewModel,
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val language by viewModel.language.collectAsState()
    val birthDate by viewModel.birthDate.collectAsState()

    var currentScreen by rememberSaveable { mutableStateOf(AppScreen.MAIN) }

    BiorhythmsTheme(themeMode = themeMode) {
        CompositionLocalProvider(
            LocalAppLanguage provides language
        ) {
            when (currentScreen) {
                AppScreen.MAIN -> MainScreen(
                    viewModel = viewModel,
                    onOpenSettings = { currentScreen = AppScreen.SETTINGS }
                )

                AppScreen.SETTINGS -> SettingsScreen(
                    themeMode = themeMode,
                    language = language,
                    birthDate = birthDate,
                    onThemeModeChange = { viewModel.onThemeModeSelected(it) },
                    onLanguageChange = { viewModel.onLanguageSelected(it) },
                    onBirthDateChange = { viewModel.onBirthDateSelected(it) },
                    onBack = { currentScreen = AppScreen.MAIN }
                )
            }
        }
    }
}

@Composable
private fun MainScreen(
    viewModel: BiorhythmsViewModel,
    onOpenSettings: () -> Unit,
) {
    val birthDate by viewModel.birthDate.collectAsState()
    val referenceDate by viewModel.referenceDate.collectAsState()

    val biorhythmLines = rememberBiorhythmLines()

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = appString(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onOpenSettings) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = appString(R.string.settings_title)
                    )
                }
            }

            if (birthDate != null) {
                Text(
                    text = appString(
                        R.string.chart_title_today_range,
                        DEFAULT_RANGE_DAYS
                    ),
                    style = MaterialTheme.typography.titleMedium
                )

                BiorhythmChart(
                    birthDate = birthDate!!,
                    referenceDate = referenceDate,
                    pastDays = DEFAULT_RANGE_DAYS,
                    futureDays = DEFAULT_RANGE_DAYS,
                    lines = biorhythmLines,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))

                BiorhythmLegend(
                    lines = biorhythmLines,
                    birthDate = birthDate!!,
                    referenceDate = referenceDate,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
                Text(
                    text = appString(R.string.placeholder_pick_birth_date),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}
