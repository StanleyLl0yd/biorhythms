package com.sl.biorhythms

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.SYSTEM }

fun resolveLocale(language: AppLanguage, systemLocale: Locale): Locale = when (language) {
    AppLanguage.SYSTEM -> systemLocale
    AppLanguage.RU -> Locale.forLanguageTag("ru")
    AppLanguage.EN -> Locale.forLanguageTag("en")
}

@Composable
fun appLocale(): Locale {
    val context = LocalContext.current
    val systemLocale = context.resources.configuration.locales[0]
    return resolveLocale(LocalAppLanguage.current, systemLocale)
}

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
        val config = Configuration(baseResources.configuration)
        config.setLocale(resolveLocale(language, baseResources.configuration.locales[0]))
        context.createConfigurationContext(config).resources
    }

    return if (formatArgs.isEmpty()) {
        resources.getString(id)
    } else {
        resources.getString(id, *formatArgs)
    }
}
