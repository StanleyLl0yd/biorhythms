package com.sl.biorhythms

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate

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

    // Храним дату рождения как epochDay (Long) — это saveable тип,
    // поэтому rememberSaveable работает "из коробки".
    var birthDateEpochDay by rememberSaveable { mutableStateOf<Long?>(null) }
    val birthDate: LocalDate? = birthDateEpochDay?.let { LocalDate.ofEpochDay(it) }

    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        BirthDatePickerDialog(
            initialDate = birthDate ?: today.minusYears(25),
            onDismiss = { showDatePicker = false },
            onDateSelected = { date ->
                birthDateEpochDay = date.toEpochDay()
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
                    text = "Biorhythms for today ±10 days",
                    style = MaterialTheme.typography.bodyMedium
                )

                BiorhythmChart(
                    birthDate = birthDate,
                    referenceDate = today,
                    pastDays = 10,
                    futureDays = 10,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
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