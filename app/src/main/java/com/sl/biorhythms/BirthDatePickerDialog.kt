package com.sl.biorhythms

import android.content.res.Configuration
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

internal fun clampBirthDate(selected: LocalDate, today: LocalDate): LocalDate =
    if (selected.isAfter(today)) today else selected

internal fun LocalDate.toDatePickerMillis(): Long =
    atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

internal fun datePickerMillisToLocalDate(millis: Long): LocalDate =
    Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthDatePickerDialog(
    initialDate: LocalDate,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
) {
    val baseContext = LocalContext.current
    val baseConfiguration = LocalConfiguration.current
    val locale = appLocale()
    val today = LocalDate.now()
    val safeInitialDate = clampBirthDate(initialDate, today)
    val localizedConfiguration = remember(baseConfiguration, locale) {
        Configuration(baseConfiguration).apply { setLocale(locale) }
    }
    val localizedContext = remember(baseContext, localizedConfiguration) {
        baseContext.createConfigurationContext(localizedConfiguration)
    }
    val selectableDates = remember(today) {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                utcTimeMillis <= today.toDatePickerMillis()

            override fun isSelectableYear(year: Int): Boolean = year <= today.year
        }
    }
    val pickerState = rememberDatePickerState(
        initialSelectedDateMillis = safeInitialDate.toDatePickerMillis(),
        selectableDates = selectableDates,
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedMillis = pickerState.selectedDateMillis ?: return@TextButton
                    onDateSelected(
                        clampBirthDate(
                            datePickerMillisToLocalDate(selectedMillis),
                            today,
                        ),
                    )
                },
            ) {
                Text(appString(R.string.action_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(appString(R.string.action_cancel))
            }
        },
    ) {
        CompositionLocalProvider(
            LocalContext provides localizedContext,
            LocalConfiguration provides localizedConfiguration,
        ) {
            DatePicker(
                state = pickerState,
                title = { Text(appString(R.string.action_choose_birth_date)) },
            )
        }
    }
}
