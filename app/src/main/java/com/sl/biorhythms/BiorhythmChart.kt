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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerEventPass
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

data class BiorhythmChartRange(
    val pastDays: Int,
    val futureDays: Int,
)

private data class ChartColors(
    val axis: Color,
    val grid: Color,
    val verticalGrid: Color,
    val selected: Color,
)

private data class ChartGeometry(
    val stepX: Float,
    val topY: Float,
    val bottomY: Float,
    val centerY: Float,
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
    range: BiorhythmChartRange,
    lines: List<BiorhythmLine>,
    selectedOffset: Int,
    onSelectedOffsetChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pastDays = range.pastDays
    val futureDays = range.futureDays
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
    val chartColors = ChartColors(
        axis = axisColor,
        grid = gridColor,
        verticalGrid = gridColor.copy(alpha = 0.55f),
        selected = MaterialTheme.colorScheme.primary,
    )

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
    val chartDescription = chartDescription(
        header = header,
        lines = lines,
        selectedValues = selectedValues,
        locale = locale,
    )

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .semantics { contentDescription = chartDescription }
                .chartSelectionInput(pastDays, futureDays, onSelectedOffsetChange),
        ) {
            val width = size.width
            val height = size.height
            val centerY = height / 2f
            val amplitude = height * 0.4f
            val stepsCount = daysOffsets.size
            val stepX = if (stepsCount > 1) width / (stepsCount - 1) else width
            val geometry = ChartGeometry(
                stepX = stepX,
                topY = centerY - amplitude,
                bottomY = centerY + amplitude,
                centerY = centerY,
            )

            drawChartGrid(
                gridOffsets = gridOffsets,
                pastDays = pastDays,
                drawVerticalGrid = stepsCount > 1,
                geometry = geometry,
                colors = chartColors,
            )
            drawChartSelection(
                stepX = stepX,
                todayIndex = todayIndex,
                selectedIndex = selectedIndex,
                axisColor = chartColors.axis,
                selectedColor = chartColors.selected,
            )
            drawBiorhythmCurves(
                lines = lines,
                lineValues = lineValues,
                selectedIndex = selectedIndex,
                stepX = stepX,
                centerY = centerY,
                amplitude = amplitude,
            )
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
private fun chartDescription(
    header: String,
    lines: List<BiorhythmLine>,
    selectedValues: Map<BiorhythmLine, Double>,
    locale: Locale,
): String {
    val values = mutableListOf<String>()
    for (line in lines) {
        val label = appString(line.labelResId)
        val value = BiorhythmCalculator.percent(selectedValues[line] ?: 0.0)
        values += "$label ${String.format(locale, "%.0f", value)}"
    }
    return "$header ${values.joinToString(", ")}"
}

private fun Modifier.chartSelectionInput(
    pastDays: Int,
    futureDays: Int,
    onSelectedOffsetChange: (Int) -> Unit,
): Modifier = pointerInput(pastDays, futureDays, onSelectedOffsetChange) {
    awaitEachGesture {
        val down = awaitFirstDown(pass = PointerEventPass.Main)
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
}

private fun DrawScope.drawChartGrid(
    gridOffsets: List<Int>,
    pastDays: Int,
    drawVerticalGrid: Boolean,
    geometry: ChartGeometry,
    colors: ChartColors,
) {
    if (drawVerticalGrid) {
        val gridStroke = 0.6.dp.toPx()
        gridOffsets.forEach { offset ->
            val x = (offset + pastDays) * geometry.stepX
            drawLine(colors.verticalGrid, Offset(x, 0f), Offset(x, size.height), gridStroke)
        }
    }

    drawLine(colors.grid, Offset(0f, geometry.topY), Offset(size.width, geometry.topY), 1.dp.toPx())
    drawLine(colors.grid, Offset(0f, geometry.bottomY), Offset(size.width, geometry.bottomY), 1.dp.toPx())
    drawLine(colors.axis, Offset(0f, geometry.centerY), Offset(size.width, geometry.centerY), 1.dp.toPx())
}

private fun DrawScope.drawChartSelection(
    stepX: Float,
    todayIndex: Int,
    selectedIndex: Int,
    axisColor: Color,
    selectedColor: Color,
) {
    val todayX = stepX * todayIndex
    if (selectedIndex != todayIndex) {
        drawLine(
            color = axisColor,
            start = Offset(todayX, 0f),
            end = Offset(todayX, size.height),
            strokeWidth = 1.25.dp.toPx(),
        )
    }

    val selectedX = stepX * selectedIndex
    drawLine(
        color = selectedColor,
        start = Offset(selectedX, 0f),
        end = Offset(selectedX, size.height),
        strokeWidth = 2.dp.toPx(),
    )
}

private fun DrawScope.drawBiorhythmCurves(
    lines: List<BiorhythmLine>,
    lineValues: Map<BiorhythmLine, List<Double>>,
    selectedIndex: Int,
    stepX: Float,
    centerY: Float,
    amplitude: Float,
) {
    val selectedX = stepX * selectedIndex
    lines.forEach { line ->
        val values = lineValues[line].orEmpty()
        if (values.isNotEmpty()) {
            drawCurve(values, line.color, stepX, centerY, amplitude)
            values.getOrNull(selectedIndex)?.let { value ->
                drawCircle(
                    color = line.color,
                    radius = 4.5.dp.toPx(),
                    center = Offset(selectedX, centerY - value.toFloat() * amplitude),
                )
            }
        }
    }
}

private fun DrawScope.drawCurve(
    values: List<Double>,
    color: Color,
    stepX: Float,
    centerY: Float,
    amplitude: Float,
) {
    for (index in 0 until values.lastIndex) {
        drawLine(
            color = color,
            start = Offset(index * stepX, centerY - values[index].toFloat() * amplitude),
            end = Offset((index + 1) * stepX, centerY - values[index + 1].toFloat() * amplitude),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round,
        )
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
                range = BiorhythmChartRange(pastDays = 15, futureDays = 15),
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
