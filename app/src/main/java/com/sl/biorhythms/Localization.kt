package com.sl.biorhythms

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.SYSTEM }

@Composable
fun appString(
    @StringRes id: Int,
    vararg formatArgs: Any?,
): String {
    val context = LocalContext.current
    val baseResources = context.resources
    val language = LocalAppLanguage.current

    val resources = if (language == AppLanguage.SYSTEM) {
        baseResources
    } else {
        val locale: Locale = when (language) {
            AppLanguage.SYSTEM ->
                baseResources.configuration.locales[0]

            AppLanguage.RU ->
                Locale.forLanguageTag("ru")

            AppLanguage.EN ->
                Locale.forLanguageTag("en")
        }

        val config = Configuration(baseResources.configuration)
        config.setLocale(locale)
        context.createConfigurationContext(config).resources
    }

    return if (formatArgs.isEmpty()) {
        resources.getString(id)
    } else {
        resources.getString(id, *formatArgs)
    }
}