package com.sl.biorhythms.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sl.biorhythms.BiorhythmCalculator
import com.sl.biorhythms.BiorhythmsViewModel
import com.sl.biorhythms.BiorhythmsViewModelFactory
import com.sl.biorhythms.LocalAppLanguage
import com.sl.biorhythms.R
import com.sl.biorhythms.appLocale
import com.sl.biorhythms.appString
import com.sl.biorhythms.dataStore
import com.sl.biorhythms.rememberBiorhythmLines
import com.sl.biorhythms.ui.theme.BiorhythmsTheme
import java.time.LocalDate
import kotlin.math.roundToInt

class WidgetConfigActivity : ComponentActivity() {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)

        appWidgetId = intent?.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID,
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        val initialAlpha = WidgetPreferences(this).getAlpha(appWidgetId)
        enableEdgeToEdge()

        setContent {
            val vm: BiorhythmsViewModel = viewModel(
                factory = BiorhythmsViewModelFactory(applicationContext.dataStore),
            )
            val themeMode by vm.themeMode.collectAsState()
            val language by vm.language.collectAsState()
            val birthDate by vm.birthDate.collectAsState()

            BiorhythmsTheme(themeMode = themeMode) {
                CompositionLocalProvider(LocalAppLanguage provides language) {
                    WidgetConfigScreen(
                        initialAlpha = initialAlpha,
                        birthDate = birthDate,
                        onSave = ::saveWidgetConfig,
                    )
                }
            }
        }
    }

    private fun saveWidgetConfig(alpha: Int) {
        WidgetPreferences(this).setAlpha(appWidgetId, alpha)
        WidgetUpdater.requestUpdate(this)

        val resultValue = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        setResult(RESULT_OK, resultValue)
        finish()
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun WidgetConfigScreen(
    initialAlpha: Int,
    birthDate: LocalDate?,
    onSave: (Int) -> Unit,
) {
    var alpha by remember(initialAlpha) {
        mutableFloatStateOf(initialAlpha.coerceIn(0, 100).toFloat())
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(appString(R.string.widget_config_title)) })
        },
        contentWindowInsets = WindowInsets.systemBars,
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                text = appString(R.string.widget_config_preview),
                style = MaterialTheme.typography.titleMedium,
            )

            WidgetPreview(
                birthDate = birthDate,
                alphaPercent = alpha.toInt(),
                modifier = Modifier.fillMaxWidth(),
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = appString(R.string.widget_config_transparency),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = appString(R.string.widget_config_alpha_value, alpha.toInt()),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                Slider(
                    value = alpha,
                    onValueChange = { alpha = it },
                    valueRange = 0f..100f,
                    modifier = Modifier.fillMaxWidth(),
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = appString(R.string.widget_config_transparent),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = appString(R.string.widget_config_opaque),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Button(
                onClick = { onSave(alpha.toInt()) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(appString(R.string.widget_config_save))
            }
        }
    }
}

@Composable
private fun WidgetPreview(
    birthDate: LocalDate?,
    alphaPercent: Int,
    modifier: Modifier = Modifier,
) {
    val lines = rememberBiorhythmLines()
    val locale = appLocale()
    val today = LocalDate.now()
    val alpha = alphaPercent.coerceIn(0, 100) / 100f
    val values = remember(lines, birthDate, today) {
        if (birthDate == null) {
            emptyMap()
        } else {
            lines.associateWith { line ->
                BiorhythmCalculator.percent(
                    BiorhythmCalculator.value(
                        birthDate = birthDate,
                        date = today,
                        period = line.period,
                    ),
                )
            }
        }
    }

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface.copy(alpha = alpha),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        tonalElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = if (birthDate == null) {
                    appString(R.string.widget_no_birth_date)
                } else {
                    appString(R.string.widget_title)
                },
                style = MaterialTheme.typography.titleMedium,
            )

            if (birthDate != null) {
                lines.forEach { line ->
                    val value = values[line] ?: 0.0
                    WidgetPreviewLine(
                        label = appString(line.labelResId),
                        percentText = String.format(locale, "%+d%%", value.roundToInt()),
                        fraction = ((value + 100.0) / 200.0).toFloat().coerceIn(0f, 1f),
                        color = line.color,
                    )
                }
            }
        }
    }
}

@Composable
private fun WidgetPreviewLine(
    label: String,
    percentText: String,
    fraction: Float,
    color: Color,
) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = percentText,
                style = MaterialTheme.typography.bodySmall,
                color = color,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth(0.62f)
                .height(5.dp)
                .background(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    shape = MaterialTheme.shapes.small,
                ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(5.dp)
                    .background(color = color, shape = MaterialTheme.shapes.small),
            )
        }
    }
}
