package com.sl.biorhythms

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.sl.biorhythms.ui.theme.BiorhythmsTheme
import org.junit.Rule
import org.junit.Test

class MainTabSelectorInstrumentedTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun mainTabsSwitchBetweenTodayAndForecast() {
        composeRule.setContent {
            BiorhythmsTheme(themeMode = AppThemeMode.LIGHT) {
                CompositionLocalProvider(LocalAppLanguage provides AppLanguage.EN) {
                    var selectedTab by remember { mutableIntStateOf(0) }

                    MainTabSelector(
                        selectedTab = selectedTab,
                        onSelectedTabChange = { selectedTab = it },
                    )
                }
            }
        }

        composeRule.onNodeWithText("Today").assertIsSelected()
        composeRule.onNodeWithText("7 days").performClick().assertIsSelected()
    }
}
