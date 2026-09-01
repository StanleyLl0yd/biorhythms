package com.sl.biorhythms

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sl.biorhythms.ui.theme.BiorhythmsTheme
import com.sl.biorhythms.widget.WidgetUpdater
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

private const val DEFAULT_RANGE_DAYS = 15

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val vm: BiorhythmsViewModel = viewModel(
                factory = BiorhythmsViewModelFactory(applicationContext.dataStore),
            )
            BiorhythmsRoot(viewModel = vm)
        }
    }
}

private enum class AppScreen {
    MAIN,
    SETTINGS,
    ABOUT,
}

@Composable
fun BiorhythmsRoot(viewModel: BiorhythmsViewModel) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val language by viewModel.language.collectAsStateWithLifecycle()
    val birthDate by viewModel.birthDate.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var currentScreen by rememberSaveable { mutableStateOf(AppScreen.MAIN) }

    DisposableEffect(lifecycleOwner, viewModel, context) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshReferenceDate()
                WidgetUpdater.requestUpdate(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    BackHandler(enabled = currentScreen != AppScreen.MAIN) {
        currentScreen = when (currentScreen) {
            AppScreen.ABOUT -> AppScreen.SETTINGS
            AppScreen.SETTINGS -> AppScreen.MAIN
            AppScreen.MAIN -> AppScreen.MAIN
        }
    }

    BiorhythmsTheme(themeMode = themeMode) {
        CompositionLocalProvider(LocalAppLanguage provides language) {
            when (currentScreen) {
                AppScreen.MAIN -> MainScreen(
                    viewModel = viewModel,
                    birthDate = birthDate,
                    onOpenSettings = { currentScreen = AppScreen.SETTINGS },
                )

                AppScreen.SETTINGS -> SettingsScreen(
                    state = SettingsState(
                        themeMode = themeMode,
                        language = language,
                        birthDate = birthDate,
                    ),
                    actions = SettingsActions(
                        onThemeModeChange = { mode ->
                            viewModel.onThemeModeSelected(mode) {
                                WidgetUpdater.requestUpdate(context)
                            }
                        },
                        onLanguageChange = { selectedLanguage ->
                            viewModel.onLanguageSelected(selectedLanguage) {
                                WidgetUpdater.requestUpdate(context)
                            }
                        },
                        onBirthDateChange = { date ->
                            viewModel.onBirthDateSelected(date) {
                                WidgetUpdater.requestUpdate(context)
                            }
                        },
                        onOpenAbout = { currentScreen = AppScreen.ABOUT },
                        onBack = { currentScreen = AppScreen.MAIN },
                    ),
                )

                AppScreen.ABOUT -> AboutScreen(
                    onBack = { currentScreen = AppScreen.SETTINGS },
                )
            }
        }
    }
}

@Composable
private fun MainScreen(
    viewModel: BiorhythmsViewModel,
    birthDate: LocalDate?,
    onOpenSettings: () -> Unit,
) {
    val referenceDate by viewModel.referenceDate.collectAsStateWithLifecycle()
    val biorhythmLines = rememberBiorhythmLines()
    val context = LocalContext.current
    val locale = appLocale()
    val selectedDateFormatter = remember(locale) {
        DateTimeFormatter.ofPattern("EEE, d MMM", locale)
    }

    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var selectedOffset by rememberSaveable(referenceDate) { mutableIntStateOf(0) }

    Scaffold(contentWindowInsets = WindowInsets.systemBars) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
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
                        contentDescription = appString(R.string.settings_title),
                    )
                }
            }

            if (birthDate != null) {
                val selectedDate = referenceDate.plusDays(selectedOffset.toLong())
                val selectedDateLabel = if (selectedOffset == 0) {
                    appString(R.string.label_today)
                } else {
                    selectedDate.format(selectedDateFormatter)
                }

                SelectedBiorhythmSummary(
                    title = selectedDateLabel,
                    lines = biorhythmLines,
                    birthDate = birthDate,
                    date = selectedDate,
                    locale = locale,
                )

                Text(
                    text = appString(R.string.chart_title_today_range, DEFAULT_RANGE_DAYS),
                    style = MaterialTheme.typography.titleMedium,
                )

                BiorhythmChart(
                    birthDate = birthDate,
                    referenceDate = referenceDate,
                    range = BiorhythmChartRange(
                        pastDays = DEFAULT_RANGE_DAYS,
                        futureDays = DEFAULT_RANGE_DAYS,
                    ),
                    lines = biorhythmLines,
                    selectedOffset = selectedOffset,
                    onSelectedOffsetChange = { selectedOffset = it },
                    modifier = Modifier.fillMaxWidth(),
                )

                Text(
                    text = appString(R.string.chart_interaction_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                BirthDateOnboardingCard(onChooseDate = { showDatePicker = true })
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
    }

    if (showDatePicker) {
        BirthDatePickerDialog(
            initialDate = birthDate ?: referenceDate.minusYears(25),
            onDismiss = { showDatePicker = false },
            onDateSelected = { date ->
                showDatePicker = false
                viewModel.onBirthDateSelected(date) {
                    WidgetUpdater.requestUpdate(context)
                }
            },
        )
    }
}

@Composable
private fun BirthDateOnboardingCard(onChooseDate: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = appString(R.string.onboarding_title),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = appString(R.string.onboarding_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(onClick = onChooseDate) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(appString(R.string.action_choose_birth_date))
            }
        }
    }
}

@Composable
private fun SelectedBiorhythmSummary(
    title: String,
    lines: List<BiorhythmLine>,
    birthDate: LocalDate,
    date: LocalDate,
    locale: Locale,
) {
    val values = remember(lines, birthDate, date) {
        lines.map { line ->
            BiorhythmCalculator.percent(birthDate, date, line.period)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        lines.forEachIndexed { index, line ->
            BiorhythmSummaryCard(
                label = appString(line.labelResId),
                value = String.format(locale, "%+d%%", values[index].roundToInt()),
                line = line,
            )
        }
    }
}

@Composable
private fun BiorhythmSummaryCard(
    label: String,
    value: String,
    line: BiorhythmLine,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = line.color.copy(alpha = 0.10f),
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(line.color, CircleShape),
            )
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
