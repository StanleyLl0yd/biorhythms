package com.sl.biorhythms

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.click
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsNavigationInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun settingsInteractiveRowsOpenWithoutCrashing() {
        val activity = composeRule.activity
        val settingsTitle = activity.getString(R.string.settings_title)
        val themeOption = activity.getString(R.string.settings_theme_option)
        val themeSystem = activity.getString(R.string.settings_theme_system)
        val languageOption = activity.getString(R.string.settings_language_option)
        val languageSystem = activity.getString(R.string.settings_language_system)
        val aboutOption = activity.getString(R.string.settings_about_option)
        val aboutTitle = activity.getString(R.string.about_title)
        val close = activity.getString(R.string.action_close)
        val back = activity.getString(R.string.action_back)

        composeRule.onNodeWithContentDescription(settingsTitle).performClick()

        composeRule.onNodeWithText(themeOption).performTouchInput { click() }
        composeRule.onNodeWithText(themeSystem).assertIsDisplayed()
        composeRule.onNodeWithText(close).performClick()

        composeRule.onNodeWithText(languageOption).performTouchInput { click() }
        composeRule.onNodeWithText(languageSystem).assertIsDisplayed()
        composeRule.onNodeWithText(close).performClick()

        composeRule.onNodeWithText(aboutOption).performTouchInput { click() }
        composeRule.onNodeWithText(aboutTitle).assertIsDisplayed()
        composeRule.onNodeWithContentDescription(back).performClick()
        composeRule.onNodeWithText(settingsTitle).assertIsDisplayed()
    }
}
