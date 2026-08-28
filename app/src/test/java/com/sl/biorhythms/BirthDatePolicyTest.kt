package com.sl.biorhythms

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.LocalDate

class BirthDatePolicyTest {

    @Test
    fun validBirthDateIsAccepted() {
        val today = LocalDate.of(2026, 8, 28)
        val date = LocalDate.of(1977, 12, 1)

        assertEquals(date, validatedBirthDate(date, today))
    }

    @Test
    fun futureBirthDateIsRejected() {
        val today = LocalDate.of(2026, 8, 28)

        assertThrows(IllegalArgumentException::class.java) {
            validatedBirthDate(today.plusDays(1), today)
        }
    }

    @Test
    fun storedFutureBirthDateIsIgnored() {
        val today = LocalDate.of(2026, 8, 28)

        assertNull(storedBirthDate(today.plusDays(1).toEpochDay(), today))
    }

    @Test
    fun invalidStoredEpochIsIgnored() {
        assertNull(storedBirthDate(Long.MAX_VALUE, LocalDate.of(2026, 8, 28)))
    }
}
