package com.sl.biorhythms

import java.time.LocalDate

internal fun validatedBirthDate(
    date: LocalDate,
    today: LocalDate = LocalDate.now(),
): LocalDate {
    require(!date.isAfter(today)) { "Birth date must not be in the future" }
    return date
}

internal fun storedBirthDate(
    epochDay: Long?,
    today: LocalDate = LocalDate.now(),
): LocalDate? {
    if (epochDay == null) return null
    val date = runCatching { LocalDate.ofEpochDay(epochDay) }.getOrNull() ?: return null
    return date.takeUnless { it.isAfter(today) }
}
