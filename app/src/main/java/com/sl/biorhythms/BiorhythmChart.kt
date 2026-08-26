package com.sl.biorhythms

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sl.biorhythms.ui.theme.BiorhythmsTheme
import com.sl.biorhythms.ui.theme.EmotionalLineColor
import com.sl.biorhythms.ui.theme.IntellectualLineColor
import com.sl.biorhythms.ui.theme.PhysicalLineColor
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

data class BiorhythmLine(
    val labelResId: Int,
    val period: Double,
    val color: Color,
)

@Composable
fun rememberBiorhythmLines(): List<BiorhythmLine> = remember {
    listOf(
        BiorhythmLine(R.string.legend_physical, BiorhythmCalculator.PHYSICAL_PERIOD, PhysicalLineColor),
        BiorhythmLine(R.string.legend_emotional, BiorhythmCalculator.EMOTIONAL_PERIOD, EmotionalLineColor),
        BiorhythmLine(R.string.legend_intellectual, BiorhythmCalculator.INTELLECTUAL_PERIOD, IntellectualLineColor),
    )
}

@Composable
fun BiorhythmChart(
    birthDate: LocalDate,
    referenceDate: LocalDate,
    pastDays: Int,
    futureDays: Int,
    lines: List<BiorhythmLine>,
    selectedOffset: Int,
    onSelectedOffsetChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = appLocale()
    val startDate = remember(referenceDate, pastDays) { referenceDate.minusDays(pastDays.toLong()) }
    val endDate = remember(referenceDate, futureDays) { referenceDate.plusDays(futureDays.toLong()) }
    val daysOffsets = remember(pastDays, futureDays) { (-pastDays..futureDays).toList() }
    val gridOffsets = remember(pastDays, futureDays) { chartGridOffsets(pastDays, futureDays) }
    val clampedSelectedOffset = selectedOffset.coerceIn(-pastDays, futureDays)
    val selectedIndex = (clampedSelectedOffset + pastDays)
        .coerceIn(0, daysOffsets.lastIndex.coerceAtLeast(0))
    val todayIndex = pastDays.coerceIn(0, daysOffsets.lastIndex.coerceAtLeast(0))

    val axisColor = MaterialTheme.colorScheme.outlineVariant
    val gridColor = axisColor.copy(alpha = 0.35f)
    val verticalGridColor = gridColor.copy(alpha = 0.55f)
    val selectedColor = MaterialTheme.colorScheme.primary

    val lineValues = remember(birthDate, referenceDate, daysOffsets, lines) {
        lines.associateWith { line ->
            daysOffsets.map { offset ->
                BiorhythmCalculator.value(
                    birthDate = birthDate,
                    date = referenceDate.plusDays(offset.toLong()),
                    period = line.period,
                )
            }
        }
    }

    val selectedValues = lines.associateWith { line ->
        lineValues[line]?.getOrNull(selectedIndex) ?: 0.0
    }
    val dateFormatter = remember(locale) { DateTimeFormatter.ofPattern("dd MMM", locale) }
    val selectedDate = referenceDate.plusDays(clampedSelectedOffset.toLong())
    val selectedDateText = if (clampedSelectedOffset == 0) {
        appString(R.string.label_today)
    } else {
        selectedDate.format(dateFormatter)
    }

    val header = appString(
        R.string.chart_a11y_description,
        startDate.format(dateFormatter),
        endDate.format(dateFormatter),
        selectedDateText,
    )
    val descriptionParts = mutableListOf<String>()
    for (line in lines) {
        val label = appString(line.labelResId)
        val value = BiorhythmCalculator.percent(selectedValues[line] ?: 0.0)
        descriptionParts += "$label ${String.format(locale, "%.0f", value)}"
    }
    val chartDescription = "$header ${descriptionParts.joinToString(", ")}"

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .semantics { contentDescription = chartDescription }
                .pointerInput(pastDays, futureDays) {
                    awaitEachGesture {
                        val down = awaitFirstDown()
                        onSelectedOffsetChange(
                            chartOffsetForPosition(
                                x = down.position.x,
                                width = size.width.toFloat(),
                                pastDays = pastDays,
                                futureDays = futureDays,
                            ),
                        )

                        do {
                            val event = awaitPointerEvent()
                            event.changes.forEach { change ->
                                if (change.pressed) {
                                    onSelectedOffsetChange(
                                        chartOffsetForPosition(
                                            x = change.position.x,
                                            width = size.width.toFloat(),
                                            pastDays = pastDays,
                                            futureDays = futureDays,
                                        ),
                                    )
                                    change.consume()
                                }
                            }
                        } while (event.changes.any { it.pressed })
                    }
                },
        ) {
            val width = size.width
            val height = size.height
            val centerY = height / 2f
            val amplitude = height * 0.4f
            val stepsCount = daysOffsets.size
            val stepX = if (stepsCount > 1) width / (stepsCount - 1) else width
            val topY = centerY - amplitude
            val bottomY = centerY + amplitude

            if (stepsCount > 1) {
                val gridStroke = 0.6.dp.toPx()
                gridOffsets.forEach { offset ->
                    val index = offset + pastDays
                    val x = index * stepX
                    drawLine(verticalGridColor, Offset(x, 0f), Offset(x, height), gridStroke)
                }
            }

            drawLine(gridColor, Offset(0f, topY), Offset(width, topY), 1.dp.toPx())
            drawLine(gridColor, Offset(0f, bottomY), Offset(width, bottomY), 1.dp.toPx())
            drawLine(axisColor, Offset(0f, centerY), Offset(width, centerY), 1.dp.toPx())

            val todayX = stepX * todayIndex
            if (selectedIndex != todayIndex) {
                drawLine(
                    color = axisColor,
                    start = Offset(todayX, 0f),
                    end = Offset(todayX, height),
                    strokeWidth = 1.25.dp.toPx(),
                )
            }

            val selectedX = stepX * selectedIndex
            drawLine(
                color = selectedColor,
                start = Offset(selectedX, 0f),
                end = Offset(selectedX, height),
                strokeWidth = 2.dp.toPx(),
            )

            fun drawCurve(values: List<Double>, color: Color) {
                for (i in 0 until values.lastIndex) {
                    drawLine(
                        color = color,
                        start = Offset(i * stepX, centerY - values[i].toFloat() * amplitude),
                        end = Offset((i + 1) * stepX, centerY - values[i + 1].toFloat() * amplitude),
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                }
            }

            lines.forEach { line ->
                val values = lineValues[line].orEmpty()
                if (values.isNotEmpty()) {
                    drawCurve(values, line.color)
                    values.getOrNull(selectedIndex)?.let { value ->
                        drawCircle(
                            color = line.color,
                            radius = 4.5.dp.toPx(),
                            center = Offset(
                                selectedX,
                                centerY - value.toFloat() * amplitude,
                            ),
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                startDate.format(dateFormatter),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f),
            )
            Text(
                selectedDateText,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f),
            )
            Text(
                endDate.format(dateFormatter),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
fun BiorhythmLegend(
    lines: List<BiorhythmLine>,
    birthDate: LocalDate,
    referenceDate: LocalDate,
    modifier: Modifier = Modifier,
) {
    val locale = appLocale()
    val daysFromBirth = remember(birthDate, referenceDate) {
        BiorhythmCalculator.daysFromBirth(birthDate, referenceDate)
    }
    val values = remember(lines, daysFromBirth) {
        lines.associateWith { line -> BiorhythmCalculator.value(daysFromBirth, line.period) }
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        lines.forEach { line ->
            LegendItem(
                label = appString(line.labelResId),
                valuePercent = BiorhythmCalculator.percent(values[line] ?: 0.0),
                baseColor = line.color,
                locale = locale,
            )
        }
    }
}

@Composable
private fun LegendItem(
    label: String,
    valuePercent: Double,
    baseColor: Color,
    locale: Locale,
) {
    val valueInt = valuePercent.roundToInt()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(modifier = Modifier.size(12.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) { drawRect(color = baseColor) }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
        Text(
            text = String.format(locale, "%+d%%", valueInt),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.End,
            color = baseColor,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BiorhythmChartPreview() {
    val lines = rememberBiorhythmLines()
    BiorhythmsTheme(themeMode = AppThemeMode.SYSTEM) {
        Column(modifier = Modifier.padding(16.dp)) {
            BiorhythmChart(
                birthDate = LocalDate.of(1990, 1, 1),
                referenceDate = LocalDate.now(),
                pastDays = 15,
                futureDays = 15,
                lines = lines,
                selectedOffset = 0,
                onSelectedOffsetChange = {},
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))
            BiorhythmLegend(
                lines = lines,
                birthDate = LocalDate.of(1990, 1, 1),
                referenceDate = LocalDate.now(),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
