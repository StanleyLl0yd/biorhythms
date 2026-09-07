package com.sl.biorhythms

import android.Manifest
import android.graphics.Bitmap
import android.os.Build
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performScrollTo
import androidx.datastore.preferences.core.edit
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sl.biorhythms.notification.NotificationPreferences
import com.sl.biorhythms.ui.theme.BiorhythmsTheme
import com.sl.biorhythms.widget.WidgetConfigScreen
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileInputStream
import java.time.LocalDate

private val storeBirthDate: LocalDate = LocalDate.of(1977, 12, 1)

@RunWith(AndroidJUnit4::class)
class StoreScreenshotInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun captureCurrentStoreScreens() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            instrumentation.uiAutomation.grantRuntimePermission(
                context.packageName,
                Manifest.permission.POST_NOTIFICATIONS,
            )
        }

        runBlocking {
            context.dataStore.edit { prefs ->
                prefs[PreferencesKeys.BirthDate] = storeBirthDate.toEpochDay()
                prefs[PreferencesKeys.ThemeMode] = AppThemeMode.LIGHT.storedValue
                prefs[PreferencesKeys.Language] = AppLanguage.RU.storedValue
                val notifications = NotificationPreferences(
                    enabled = true,
                    hour = 9,
                    minute = 0,
                    dailySummary = true,
                    importantEvents = true,
                    physical = true,
                    emotional = true,
                    intellectual = true,
                )
                prefs[PreferencesKeys.NotificationEnabled] = notifications.enabled
                prefs[PreferencesKeys.NotificationHour] = notifications.hour
                prefs[PreferencesKeys.NotificationMinute] = notifications.minute
                prefs[PreferencesKeys.NotificationDailySummary] = notifications.dailySummary
                prefs[PreferencesKeys.NotificationImportantEvents] = notifications.importantEvents
                prefs[PreferencesKeys.NotificationPhysical] = notifications.physical
                prefs[PreferencesKeys.NotificationEmotional] = notifications.emotional
                prefs[PreferencesKeys.NotificationIntellectual] = notifications.intellectual
            }
        }

        composeRule.activityRule.scenario.recreate()
        waitForText("Сегодня")
        saveStoreScreenshot("01_today.png", composeRule.onRoot().captureToImage())

        composeRule.onNodeWithText("7 дней").performSemanticsAction(SemanticsActions.OnClick)
        waitForText("Прогноз на 7 дней")
        saveStoreScreenshot("02_7_days.png", composeRule.onRoot().captureToImage())

    }

    private fun waitForText(text: String) {
        composeRule.waitUntil(10_000) {
            composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.waitForIdle()
    }
}

@RunWith(AndroidJUnit4::class)
class SettingsStoreScreenshotInstrumentedTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun captureNotificationSettingsScreen() {
        composeRule.setContent {
            BiorhythmsTheme(themeMode = AppThemeMode.LIGHT) {
                CompositionLocalProvider(LocalAppLanguage provides AppLanguage.RU) {
                    SettingsScreen(
                        state = SettingsState(
                            themeMode = AppThemeMode.LIGHT,
                            language = AppLanguage.RU,
                            birthDate = storeBirthDate,
                            notificationPreferences = NotificationPreferences(
                                enabled = true,
                                hour = 9,
                                minute = 0,
                                dailySummary = true,
                                importantEvents = true,
                                physical = true,
                                emotional = true,
                                intellectual = true,
                            ),
                            notificationPermissionGranted = true,
                        ),
                        actions = SettingsActions(
                            onThemeModeChange = {},
                            onLanguageChange = {},
                            onBirthDateChange = {},
                            onNotificationPreferencesChange = {},
                            onOpenNotificationSettings = {},
                            onOpenAbout = {},
                            onBack = {},
                        ),
                    )
                }
            }
        }

        composeRule.onNodeWithText("Ежедневная сводка").performScrollTo()
        composeRule.waitForIdle()
        saveStoreScreenshot("03_notifications.png", composeRule.onRoot().captureToImage())
    }
}

@RunWith(AndroidJUnit4::class)
class AboutStoreScreenshotInstrumentedTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun captureAboutScreen() {
        composeRule.setContent {
            BiorhythmsTheme(themeMode = AppThemeMode.LIGHT) {
                CompositionLocalProvider(LocalAppLanguage provides AppLanguage.RU) {
                    AboutScreen(onBack = {})
                }
            }
        }

        composeRule.waitForIdle()
        saveStoreScreenshot("04_about.png", composeRule.onRoot().captureToImage())
    }
}

@RunWith(AndroidJUnit4::class)
class WidgetStoreScreenshotInstrumentedTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun captureWidgetSettingsScreen() {
        composeRule.setContent {
            BiorhythmsTheme(themeMode = AppThemeMode.LIGHT) {
                CompositionLocalProvider(LocalAppLanguage provides AppLanguage.RU) {
                    WidgetConfigScreen(
                        initialAlpha = 75,
                        birthDate = storeBirthDate,
                        onSave = {},
                    )
                }
            }
        }

        composeRule.waitForIdle()
        saveStoreScreenshot("05_widget.png", composeRule.onRoot().captureToImage())
    }
}

private fun saveStoreScreenshot(fileName: String, image: ImageBitmap) {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val directory = File(context.getExternalFilesDir(null), "store-screenshots").apply {
        mkdirs()
    }
    val file = File(directory, fileName)
    file.outputStream().use { output ->
        check(image.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, output))
    }

    val instrumentation = InstrumentationRegistry.getInstrumentation()
    val destination = "/sdcard/Download/biorhythms-store-screenshots"
    val command = "mkdir -p $destination && cp '" + file.absolutePath +
        "' '" + destination + "/" + fileName + "' && sync"
    instrumentation.uiAutomation.executeShellCommand(command).use { descriptor ->
        FileInputStream(descriptor.fileDescriptor).use { stream ->
            while (stream.read() != -1) {
                Unit
            }
        }
    }
}
