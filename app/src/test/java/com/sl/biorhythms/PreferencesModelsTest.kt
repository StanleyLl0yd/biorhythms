package com.sl.biorhythms

import org.junit.Assert.assertEquals
import org.junit.Test

class PreferencesModelsTest {

    @Test
    fun themeStoredValuesAreStable() {
        assertEquals(0, AppThemeMode.SYSTEM.storedValue)
        assertEquals(1, AppThemeMode.LIGHT.storedValue)
        assertEquals(2, AppThemeMode.DARK.storedValue)
    }

    @Test
    fun languageStoredValuesAreStable() {
        assertEquals(0, AppLanguage.SYSTEM.storedValue)
        assertEquals(1, AppLanguage.RU.storedValue)
        assertEquals(2, AppLanguage.EN.storedValue)
    }

    @Test
    fun unknownStoredValuesFallBackToSystem() {
        assertEquals(AppThemeMode.SYSTEM, AppThemeMode.fromStored(null))
        assertEquals(AppThemeMode.SYSTEM, AppThemeMode.fromStored(999))
        assertEquals(AppLanguage.SYSTEM, AppLanguage.fromStored(null))
        assertEquals(AppLanguage.SYSTEM, AppLanguage.fromStored(999))
    }

    @Test
    fun knownStoredValuesRoundTrip() {
        AppThemeMode.entries.forEach { mode ->
            assertEquals(mode, AppThemeMode.fromStored(mode.storedValue))
        }
        AppLanguage.entries.forEach { language ->
            assertEquals(language, AppLanguage.fromStored(language.storedValue))
        }
    }
}
