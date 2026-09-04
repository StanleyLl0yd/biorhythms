package com.sl.biorhythms

import org.junit.Assert.assertEquals
import org.junit.Test

class BiorhythmCycleTypeTest {
    @Test
    fun classicCycleOrderAndPeriodsStayCanonical() {
        assertEquals(
            listOf(
                BiorhythmCycleType.PHYSICAL,
                BiorhythmCycleType.EMOTIONAL,
                BiorhythmCycleType.INTELLECTUAL,
            ),
            BiorhythmCycleType.entries,
        )
        assertEquals(
            listOf(
                BiorhythmCalculator.PHYSICAL_PERIOD,
                BiorhythmCalculator.EMOTIONAL_PERIOD,
                BiorhythmCalculator.INTELLECTUAL_PERIOD,
            ),
            BiorhythmCycleType.entries.map(BiorhythmCycleType::period),
        )
    }
}
