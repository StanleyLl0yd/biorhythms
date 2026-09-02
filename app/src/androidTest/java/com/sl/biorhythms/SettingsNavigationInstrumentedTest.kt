package com.sl.biorhythms

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
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
        val lightTheme = activity.getString(R.string.settings_theme_light)
        val languageOption = activity.getString(R.string.settings_language_option)
        val englishLanguage = activity.getString(R.string.settings_language_english)
        val aboutOption = activity.getString(R.string.settings_about_option)
        val aboutTitle = activity.getString(R.string.about_title)
        val close = activity.getString(R.string.action_close)
        val back = activity.getString(R.string.action_back)

        composeRule.onNodeWithContentDescription(settingsTitle).performClick()

        composeRule.onNodeWithText(themeOption).performScrollTo().performClick()
        composeRule.onNodeWithText(lightTheme).assertIsDisplayed()
        composeRule.onNodeWithText(close).performClick()

        composeRule.onNodeWithText(languageOption).performScrollTo().performClick()
        composeRule.onNodeWithText(englishLanguage).assertIsDisplayed()
        composeRule.onNodeWithText(close).performClick()

        composeRule.onNodeWithText(aboutOption).performScrollTo().performClick()
        composeRule.onNodeWithText(aboutTitle).assertIsDisplayed()
        composeRule.onNodeWithContentDescription(back).performClick()
        composeRule.onNodeWithText(settingsTitle).assertIsDisplayed()
    }
}
