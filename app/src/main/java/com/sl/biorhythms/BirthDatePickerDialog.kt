package com.sl.biorhythms

import android.app.DatePickerDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun BirthDatePickerDialog(
    initialDate: LocalDate,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
) {
    val context = LocalContext.current
    val zoneId = remember { ZoneId.systemDefault() }
    val today = remember { LocalDate.now(zoneId) }

    DisposableEffect(Unit) {
        val dialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selected = LocalDate.of(year, month + 1, dayOfMonth)
                // не даём выбрать дату в будущем
                val clamped = if (selected.isAfter(today)) today else selected
                onDateSelected(clamped)
            },
            initialDate.year,
            initialDate.monthValue - 1,
            initialDate.dayOfMonth
        )

        // ограничиваем максимум сегодняшним днём
        val maxMillis = today
            .atStartOfDay(zoneId)
            .toInstant()
            .toEpochMilli()
        dialog.datePicker.maxDate = maxMillis

        dialog.setOnDismissListener {
            onDismiss()
        }

        dialog.show()

        onDispose {
            dialog.setOnDismissListener(null)
            dialog.dismiss()
        }
    }
}