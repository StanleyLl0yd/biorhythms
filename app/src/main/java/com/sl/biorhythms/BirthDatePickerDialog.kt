package com.sl.biorhythms

import android.app.DatePickerDialog
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.time.LocalDate
import java.time.ZoneId

internal fun clampBirthDate(selected: LocalDate, today: LocalDate): LocalDate =
    if (selected.isAfter(today)) today else selected

@Composable
fun BirthDatePickerDialog(
    initialDate: LocalDate,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
) {
    val baseContext = LocalContext.current
    val configuration = LocalConfiguration.current
    val locale = appLocale()
    val zoneId = remember { ZoneId.systemDefault() }
    val today = remember { LocalDate.now(zoneId) }
    val context = remember(baseContext, configuration, locale) {
        val config = Configuration(configuration)
        config.setLocale(locale)
        baseContext.createConfigurationContext(config)
    }

    DisposableEffect(context, initialDate, today) {
        val dialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selected = LocalDate.of(year, month + 1, dayOfMonth)
                onDateSelected(clampBirthDate(selected, today))
            },
            initialDate.year,
            initialDate.monthValue - 1,
            initialDate.dayOfMonth,
        )

        dialog.datePicker.maxDate = today
            .atStartOfDay(zoneId)
            .toInstant()
            .toEpochMilli()

        dialog.setOnDismissListener { onDismiss() }
        dialog.show()

        onDispose {
            dialog.setOnDismissListener(null)
            dialog.dismiss()
        }
    }
}
