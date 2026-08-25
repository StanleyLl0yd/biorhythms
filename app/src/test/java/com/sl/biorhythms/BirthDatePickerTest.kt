package com.sl.biorhythms

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class BirthDatePickerTest {

    @Test
    fun pastDateIsKept() {
        val today = LocalDate.of(2026, 8, 25)
        val selected = LocalDate.of(1977, 12, 1)

        assertEquals(selected, clampBirthDate(selected, today))
    }

    @Test
    fun todayIsKept() {
        val today = LocalDate.of(2026, 8, 25)

        assertEquals(today, clampBirthDate(today, today))
    }

    @Test
    fun futureDateIsClampedToToday() {
        val today = LocalDate.of(2026, 8, 25)
        val selected = LocalDate.of(2026, 8, 26)

        assertEquals(today, clampBirthDate(selected, today))
    }
}
