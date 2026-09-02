package com.sl.biorhythms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

@Composable
internal fun BiorhythmForecastPanel(
    birthDate: LocalDate,
    referenceDate: LocalDate,
    selectedDate: LocalDate,
    lines: List<BiorhythmLine>,
    locale: Locale,
) {
    val selectedForecast = remember(birthDate, selectedDate) {
        BiorhythmForecast.day(birthDate, selectedDate)
    }
    val forecast = remember(birthDate, referenceDate) {
        BiorhythmForecast.days(birthDate, referenceDate)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        selectedForecast.synchronizedExtreme?.let { extreme ->
            SynchronizedExtremeCard(extreme)
        }

        if (selectedForecast.cycles.any { it.event != null }) {
            SelectedCycleEventsCard(
                forecast = selectedForecast,
                lines = lines,
            )
        }

        Text(
            text = appString(R.string.forecast_title),
            style = MaterialTheme.typography.titleMedium,
        )
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            forecast.forEachIndexed { index, day ->
                ForecastDayRow(
                    day = day,
                    lines = lines,
                    locale = locale,
                    isToday = index == 0,
                )
                if (index != forecast.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun SynchronizedExtremeCard(extreme: SynchronizedExtreme) {
    val threshold = BiorhythmForecast.SYNCHRONIZED_EXTREME_THRESHOLD.roundToInt()
    val isLow = extreme == SynchronizedExtreme.LOW
    val title = appString(
        if (isLow) R.string.sync_low_title else R.string.sync_high_title,
        threshold,
    )
    val description = appString(
        if (isLow) R.string.sync_low_description else R.string.sync_high_description,
    )

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isLow) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.tertiaryContainer
            },
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = if (isLow) {
                    MaterialTheme.colorScheme.onErrorContainer
                } else {
                    MaterialTheme.colorScheme.onTertiaryContainer
                },
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = if (isLow) {
                    MaterialTheme.colorScheme.onErrorContainer
                } else {
                    MaterialTheme.colorScheme.onTertiaryContainer
                },
            )
        }
    }
}

@Composable
private fun SelectedCycleEventsCard(
    forecast: BiorhythmDayForecast,
    lines: List<BiorhythmLine>,
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = appString(R.string.cycle_events_title),
                style = MaterialTheme.typography.titleSmall,
            )
            forecast.cycles.forEachIndexed { index, cycle ->
                cycle.event?.let { event ->
                    Text(
                        text = "${appString(lines[index].labelResId)} · ${eventLabel(event)}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun ForecastDayRow(
    day: BiorhythmDayForecast,
    lines: List<BiorhythmLine>,
    locale: Locale,
    isToday: Boolean,
) {
    val dateFormatter = remember(locale) { DateTimeFormatter.ofPattern("EEE, d MMM", locale) }
    val dateLabel = if (isToday) appString(R.string.label_today) else day.date.format(dateFormatter)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = dateLabel,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f),
            )
            day.synchronizedExtreme?.let { extreme ->
                val threshold = BiorhythmForecast.SYNCHRONIZED_EXTREME_THRESHOLD.roundToInt()
                Text(
                    text = appString(
                        if (extreme == SynchronizedExtreme.LOW) {
                            R.string.sync_low_compact
                        } else {
                            R.string.sync_high_compact
                        },
                        threshold,
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (extreme == SynchronizedExtreme.LOW) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.tertiary
                    },
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            day.cycles.forEachIndexed { index, cycle ->
                ForecastCycleCell(
                    label = appString(lines[index].labelResId),
                    cycle = cycle,
                    locale = locale,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun ForecastCycleCell(
    label: String,
    cycle: BiorhythmCycleForecast,
    locale: Locale,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
        Text(
            text = String.format(
                locale,
                "%+d%% %s",
                cycle.percent.roundToInt(),
                cycle.trend.symbol(),
            ),
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
        )
        cycle.event?.let { event ->
            Text(
                text = eventLabel(event),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun eventLabel(event: BiorhythmEvent): String = appString(
    when (event) {
        BiorhythmEvent.CRITICAL -> R.string.cycle_event_critical
        BiorhythmEvent.PEAK -> R.string.cycle_event_peak
        BiorhythmEvent.LOW -> R.string.cycle_event_low
    },
)
