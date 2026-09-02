package com.sl.biorhythms

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
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

fun localizedResources(
    context: Context,
    language: AppLanguage,
    configuration: Configuration = context.resources.configuration,
): Resources {
    if (language == AppLanguage.SYSTEM) return context.resources
    val config = Configuration(configuration)
    config.setLocale(resolveLocale(language, configuration.locales[0]))
    return context.createConfigurationContext(config).resources
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
            localizedResources(context, language, configuration)
        }
    }

    return if (formatArgs.isEmpty()) {
        resources.getString(id)
    } else {
        resources.getString(id, *formatArgs)
    }
}
