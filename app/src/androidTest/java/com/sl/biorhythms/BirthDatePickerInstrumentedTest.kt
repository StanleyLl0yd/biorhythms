package com.sl.biorhythms

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BirthDatePickerInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun birthDatePickerOpensFromSettings() {
        val settingsTitle = composeRule.activity.getString(R.string.settings_title)
        val birthDateOption = composeRule.activity.getString(R.string.settings_birth_date_option)
        val pickerTitle = composeRule.activity.getString(R.string.action_choose_birth_date)
        val confirmLabel = composeRule.activity.getString(R.string.action_confirm)
        val cancelLabel = composeRule.activity.getString(R.string.action_cancel)

        composeRule.onNodeWithContentDescription(settingsTitle).performClick()
        composeRule.onNodeWithText(birthDateOption).performClick()

        composeRule.onNodeWithText(pickerTitle).assertIsDisplayed()
        composeRule.onNodeWithText(confirmLabel).assertIsDisplayed()
        composeRule.onNodeWithText(cancelLabel).assertIsDisplayed()
    }
}
