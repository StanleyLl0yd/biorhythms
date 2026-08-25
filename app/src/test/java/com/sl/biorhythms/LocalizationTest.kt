package com.sl.biorhythms

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class LocalizationTest {

    @Test
    fun systemLanguageUsesProvidedSystemLocale() {
        val systemLocale = Locale.forLanguageTag("de-DE")

        assertEquals(systemLocale, resolveLocale(AppLanguage.SYSTEM, systemLocale))
    }

    @Test
    fun russianLanguageUsesRussianLocale() {
        assertEquals(
            "ru",
            resolveLocale(AppLanguage.RU, Locale.ENGLISH).language,
        )
    }

    @Test
    fun englishLanguageUsesEnglishLocale() {
        assertEquals(
            "en",
            resolveLocale(AppLanguage.EN, Locale.forLanguageTag("ru")).language,
        )
    }
}
