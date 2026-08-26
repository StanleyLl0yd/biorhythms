package com.sl.biorhythms

import kotlin.math.roundToInt

internal fun chartOffsetForPosition(
    x: Float,
    width: Float,
    pastDays: Int,
    futureDays: Int,
): Int {
    if (width <= 0f) return 0

    val totalDays = (pastDays + futureDays).coerceAtLeast(0)
    if (totalDays == 0) return 0

    val fraction = (x / width).coerceIn(0f, 1f)
    return (fraction * totalDays)
        .roundToInt()
        .minus(pastDays)
        .coerceIn(-pastDays, futureDays)
}

internal fun chartGridOffsets(
    pastDays: Int,
    futureDays: Int,
    stepDays: Int = 5,
): List<Int> {
    require(stepDays > 0) { "stepDays must be positive" }

    val offsets = mutableSetOf(-pastDays, 0, futureDays)
    var offset = 0
    while (offset <= futureDays) {
        offsets += offset
        offset += stepDays
    }

    offset = -stepDays
    while (offset >= -pastDays) {
        offsets += offset
        offset -= stepDays
    }

    return offsets
        .filter { it in -pastDays..futureDays }
        .sorted()
}
