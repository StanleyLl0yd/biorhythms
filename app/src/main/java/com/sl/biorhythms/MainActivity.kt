package com.sl.biorhythms

import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sl.biorhythms.ui.theme.BiorhythmsTheme
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate

private val Context.dataStore by preferencesDataStore(name = "biorhythm_settings")
private val BirthDateKey = longPreferencesKey("birth_date_epoch")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BiorhythmsTheme {
                BiorhythmsApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiorhythmsApp() {
    val today = remember { LocalDate.now() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val storedBirthDate by remember {
        context.dataStore.data.map { preferences ->
            preferences[BirthDateKey]?.let { LocalDate.ofEpochDay(it) }
        }
    }.collectAsState(initial = null)

    var birthDateEpochDay by rememberSaveable { mutableStateOf<Long?>(null) }
    val birthDate: LocalDate? = birthDateEpochDay?.let(LocalDate::ofEpochDay)

    LaunchedEffect(storedBirthDate) {
        birthDateEpochDay = storedBirthDate?.toEpochDay()
    }

    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        BirthDatePickerDialog(
            initialDate = birthDate ?: today.minusYears(25),
            onDismiss = { showDatePicker = false },
            onDateSelected = { date ->
                birthDateEpochDay = date.toEpochDay()
                coroutineScope.launch {
                    context.dataStore.edit { prefs ->
                        prefs[BirthDateKey] = date.toEpochDay()
                    }
                }
                showDatePicker = false
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Biorhythms") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Select your birth date",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedButton(onClick = { showDatePicker = true }) {
                Text(text = birthDate?.toString() ?: "Tap to choose date")
            }

            if (birthDate != null) {
                Text(
                    text = "Biorhythms for today ±15 days",
                    style = MaterialTheme.typography.bodyMedium
                )

                BiorhythmChart(
                    birthDate = birthDate,
                    referenceDate = today,
                    pastDays = 15,
                    futureDays = 15,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                )

                BiorhythmLegend(
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Pick your birth date to see your biorhythm chart.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}