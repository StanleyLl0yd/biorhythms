package com.sl.biorhythms

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.PI
import kotlin.math.sin

private fun biorhythmValue(daysFromBirth: Long, period: Double): Double {
    return sin(2.0 * PI * daysFromBirth / period)
}

@Composable
fun BiorhythmChart(
    birthDate: LocalDate,
    referenceDate: LocalDate,
    pastDays: Int,
    futureDays: Int,
    modifier: Modifier = Modifier
) {
    val startDate = remember(referenceDate, pastDays) { referenceDate.minusDays(pastDays.toLong()) }
    val endDate = remember(referenceDate, futureDays) { referenceDate.plusDays(futureDays.toLong()) }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd MMM", Locale.getDefault()) }

    val daysOffsets = remember(pastDays, futureDays) {
        (-pastDays..futureDays).toList()
    }

    val physicalValues = remember(birthDate, referenceDate, pastDays, futureDays) {
        daysOffsets.map { offset ->
            val date = referenceDate.plusDays(offset.toLong())
            val days = ChronoUnit.DAYS.between(birthDate, date)
            biorhythmValue(days, 23.0)
        }
    }

    val emotionalValues = remember(birthDate, referenceDate, pastDays, futureDays) {
        daysOffsets.map { offset ->
            val date = referenceDate.plusDays(offset.toLong())
            val days = ChronoUnit.DAYS.between(birthDate, date)
            biorhythmValue(days, 28.0)
        }
    }

    val intellectualValues = remember(birthDate, referenceDate, pastDays, futureDays) {
        daysOffsets.map { offset ->
            val date = referenceDate.plusDays(offset.toLong())
            val days = ChronoUnit.DAYS.between(birthDate, date)
            biorhythmValue(days, 33.0)
        }
    }

    // Считываем цвета из темы ЗДЕСЬ, в @Composable-контексте
    val axisColor = MaterialTheme.colorScheme.outlineVariant
    val gridColor = axisColor.copy(alpha = 0.3f)
    val physicalColor = Color(0xFFE53935)   // красный
    val emotionalColor = Color(0xFF43A047)  // зелёный
    val intellectualColor = Color(0xFF1E88E5) // синий

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val width = size.width
            val height = size.height
            val centerY = height / 2f
            val amplitude = height * 0.35f

            val pointCount = daysOffsets.size
            if (pointCount < 2) return@Canvas

            val stepX = width / (pointCount - 1)

            // Сетка по дням
            daysOffsets.forEachIndexed { index, _ ->
                val x = index * stepX
                drawLine(
                    color = gridColor,
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 0.5.dp.toPx()
                )
            }

            // Горизонтальная ось (0)
            drawLine(
                color = axisColor,
                start = Offset(0f, centerY),
                end = Offset(width, centerY),
                strokeWidth = 1.dp.toPx()
            )

            // Вертикальная линия "сегодня" (смещение 0)
            val todayIndex = pastDays
            val todayX = todayIndex * stepX
            drawLine(
                color = axisColor,
                start = Offset(todayX, 0f),
                end = Offset(todayX, height),
                strokeWidth = 1.dp.toPx()
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
                        strokeWidth = 2.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }

            // Красная — физический
            drawCurve(
                values = physicalValues,
                color = physicalColor
            )

            // Зелёная — эмоциональный
            drawCurve(
                values = emotionalValues,
                color = emotionalColor
            )

            // Синяя — интеллектуальный
            drawCurve(
                values = intellectualValues,
                color = intellectualColor
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = dateFormatter.format(startDate),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = dateFormatter.format(endDate),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun BiorhythmLegend(
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        LegendItem(label = "Physical", color = Color(0xFFE53935))
        Spacer(modifier = Modifier.width(12.dp))
        LegendItem(label = "Emotional", color = Color(0xFF43A047))
        Spacer(modifier = Modifier.width(12.dp))
        LegendItem(label = "Intellectual", color = Color(0xFF1E88E5))
    }
}

@Composable
private fun LegendItem(
    label: String,
    color: Color
) {
    Row {
        Box(
            modifier = Modifier
                .size(14.dp)
                .height(14.dp),
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawRect(color = color)
            }
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}