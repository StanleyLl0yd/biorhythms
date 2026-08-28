package com.sl.biorhythms

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.sl.biorhythms.ui.theme.BiorhythmsTheme
import com.sl.biorhythms.widget.WidgetConfigScreen
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class AccessibilityInstrumentedTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun chartExposesSelectedValuesToAccessibility() {
        composeRule.setContent {
            BiorhythmsTheme(themeMode = AppThemeMode.LIGHT) {
                CompositionLocalProvider(LocalAppLanguage provides AppLanguage.EN) {
                    BiorhythmChart(
                        birthDate = LocalDate.of(1990, 1, 1),
                        referenceDate = LocalDate.of(2026, 8, 28),
                        pastDays = 15,
                        futureDays = 15,
                        lines = rememberBiorhythmLines(),
                        selectedOffset = 0,
                        onSelectedOffsetChange = {},
                    )
                }
            }
        }

        composeRule
            .onNodeWithContentDescription("Biorhythm chart", substring = true)
            .fetchSemanticsNode()
    }

    @Test
    fun widgetPreviewExposesCycleLabelsAsText() {
        composeRule.setContent {
            BiorhythmsTheme(themeMode = AppThemeMode.LIGHT) {
                CompositionLocalProvider(LocalAppLanguage provides AppLanguage.EN) {
                    WidgetConfigScreen(
                        initialAlpha = 75,
                        birthDate = LocalDate.of(1990, 1, 1),
                        onSave = {},
                    )
                }
            }
        }

        composeRule.onNodeWithText("Physical").fetchSemanticsNode()
        composeRule.onNodeWithText("Emotional").fetchSemanticsNode()
        composeRule.onNodeWithText("Intellectual").fetchSemanticsNode()
    }
}
