package com.sl.biorhythms

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import java.util.Locale

val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.SYSTEM }

fun resolveLocale(language: AppLanguage, systemLocale: Locale): Locale = when (language) {
    AppLanguage.SYSTEM -> systemLocale
    AppLanguage.RU -> Locale.forLanguageTag("ru")
    AppLanguage.EN -> Locale.forLanguageTag("en")
}

@Composable
fun appLocale(): Locale {
    val configuration = LocalConfiguration.current
    return resolveLocale(LocalAppLanguage.current, configuration.locales[0])
}

@Composable
fun appString(
    @StringRes id: Int,
    vararg formatArgs: Any?,
): String {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val baseResources = LocalResources.current
    val language = LocalAppLanguage.current

    val resources = if (language == AppLanguage.SYSTEM) {
        baseResources
    } else {
        remember(context, configuration, language) {
            val config = Configuration(configuration)
            config.setLocale(resolveLocale(language, configuration.locales[0]))
            context.createConfigurationContext(config).resources
        }
    }

    return if (formatArgs.isEmpty()) {
        resources.getString(id)
    } else {
        resources.getString(id, *formatArgs)
    }
}
