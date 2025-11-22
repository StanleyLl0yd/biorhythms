package com.sl.biorhythms

import androidx.compose.foundation.Canvas
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
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.PI
import kotlin.math.sin

private fun biorhythmValue(daysFromBirth: Long, period: Double): Double =
    sin(2.0 * PI * daysFromBirth / period)

data class BiorhythmLine(
    val labelResId: Int,
    val period: Double,
    val color: Color,
)

@Composable
fun rememberBiorhythmLines(): List<BiorhythmLine> {
    return remember {
        listOf(
            BiorhythmLine(
                labelResId = R.string.legend_physical,
                period = 23.0,
                color = PhysicalLineColor,
            ),
            BiorhythmLine(
                labelResId = R.string.legend_emotional,
                period = 28.0,
                color = EmotionalLineColor,
            ),
            BiorhythmLine(
                labelResId = R.string.legend_intellectual,
                period = 33.0,
                color = IntellectualLineColor,
            ),
        )
    }
}

@Composable
fun BiorhythmChart(
    birthDate: LocalDate,
    referenceDate: LocalDate,
    pastDays: Int,
    futureDays: Int,
    lines: List<BiorhythmLine>,
    modifier: Modifier = Modifier,
) {
    val startDate = remember(referenceDate, pastDays) {
        referenceDate.minusDays(pastDays.toLong())
    }
    val endDate = remember(referenceDate, futureDays) {
        referenceDate.plusDays(futureDays.toLong())
    }

    val daysOffsets = remember(pastDays, futureDays) {
        (-pastDays..futureDays).toList()
    }

    val axisColor = MaterialTheme.colorScheme.outlineVariant
    val gridColor = axisColor.copy(alpha = 0.3f)
    val verticalGridColor = gridColor.copy(alpha = 0.3f)

    val lineValues: Map<BiorhythmLine, List<Double>> = remember(
        birthDate,
        referenceDate,
        daysOffsets,
        lines,
    ) {
        lines.associateWith { line ->
            daysOffsets.map { offset ->
                val date = referenceDate.plusDays(offset.toLong())
                val days = ChronoUnit.DAYS.between(birthDate, date)
                biorhythmValue(days, line.period)
            }
        }
    }

    val todayIndex = pastDays.coerceAtMost(daysOffsets.lastIndex.coerceAtLeast(0))
    val todayValues: Map<BiorhythmLine, Double> = lines.associateWith { line ->
        lineValues[line]?.getOrNull(todayIndex) ?: 0.0
    }

    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("dd MMM", Locale.getDefault())
    }

    val header = appString(
        R.string.chart_a11y_description,
        startDate.format(dateFormatter),
        endDate.format(dateFormatter),
    )

    val parts = mutableListOf<String>()
    for (line in lines) {
        val label = appString(line.labelResId)
        val value = todayValues[line] ?: 0.0
        parts += "$label ${"%.2f".format(value)}"
    }
    val chartDescription = "$header ${parts.joinToString(", ")}"

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .semantics { contentDescription = chartDescription },
        ) {
            val width = size.width
            val height = size.height
            val centerY = height / 2f
            val amplitude = height * 0.4f

            val stepsCount = daysOffsets.size
            val stepX = if (stepsCount > 1) {
                width / (stepsCount - 1)
            } else {
                width
            }

            val zeroY = centerY
            val topY = centerY - amplitude
            val bottomY = centerY + amplitude

            // вертикальная сетка по дням
            if (stepsCount > 1) {
                val stroke = 0.5.dp.toPx()
                for (i in 0..daysOffsets.lastIndex) {
                    val x = i * stepX
                    drawLine(
                        color = verticalGridColor,
                        start = Offset(x, 0f),
                        end = Offset(x, height),
                        strokeWidth = stroke,
                    )
                }
            }

            // горизонтальная сетка для +1 и -1
            drawLine(
                color = gridColor,
                start = Offset(0f, topY),
                end = Offset(width, topY),
                strokeWidth = 1.dp.toPx(),
            )
            drawLine(
                color = gridColor,
                start = Offset(0f, bottomY),
                end = Offset(width, bottomY),
                strokeWidth = 1.dp.toPx(),
            )

            // линия 0
            drawLine(
                color = axisColor,
                start = Offset(0f, zeroY),
                end = Offset(width, zeroY),
                strokeWidth = 1.dp.toPx(),
            )

            // вертикальная линия "сегодня" поверх сетки
            val todayX = stepX * todayIndex
            drawLine(
                color = axisColor,
                start = Offset(todayX, 0f),
                end = Offset(todayX, height),
                strokeWidth = 1.dp.toPx(),
            )

            fun drawCurve(values: List<Double>, color: Color) {
                for (i in 0 until values.lastIndex) {
                    val x1 = i * stepX
                    val x2 = (i + 1) * stepX

                    val y1 = centerY - (values[i].toFloat() * amplitude)
                    val y2 = centerY - (values[i + 1].toFloat() * amplitude)

                    drawLine(
                        color = color,
                        start = Offset(x1, y1),
                        end = Offset(x2, y2),
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                }
            }

            lines.forEach { line ->
                val values = lineValues[line].orEmpty()
                if (values.isNotEmpty()) {
                    drawCurve(values, line.color)
                }
            }
        }

        // подписи дат: начало / сегодня / конец
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = startDate.format(dateFormatter),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = appString(R.string.label_today),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = endDate.format(dateFormatter),
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
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        lines.forEach { line ->
            LegendItem(
                label = appString(line.labelResId),
                color = line.color,
            )
        }
    }
}

@Composable
private fun LegendItem(
    label: String,
    color: Color,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(14.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(color = color)
            }
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BiorhythmChartPreview() {
    val lines = rememberBiorhythmLines()
    BiorhythmsTheme(themeMode = AppThemeMode.SYSTEM) {
        BiorhythmChart(
            birthDate = LocalDate.of(1990, 1, 1),
            referenceDate = LocalDate.now(),
            pastDays = 15,
            futureDays = 15,
            lines = lines,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
    }
}
