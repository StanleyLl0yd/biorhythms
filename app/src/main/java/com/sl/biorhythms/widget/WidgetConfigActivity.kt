package com.sl.biorhythms.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sl.biorhythms.BiorhythmForecast
import com.sl.biorhythms.BiorhythmsViewModel
import com.sl.biorhythms.BiorhythmsViewModelFactory
import com.sl.biorhythms.LocalAppLanguage
import com.sl.biorhythms.R
import com.sl.biorhythms.SynchronizedExtreme
import com.sl.biorhythms.appLocale
import com.sl.biorhythms.appString
import com.sl.biorhythms.dataStore
import com.sl.biorhythms.rememberBiorhythmLines
import com.sl.biorhythms.symbol
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

        val manager = AppWidgetManager.getInstance(this)
        val expectedProvider = ComponentName(this, BiorhythmsWidgetProvider::class.java)
        val actualProvider = if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            null
        } else {
            manager.getAppWidgetInfo(appWidgetId)?.provider
        }
        if (actualProvider != expectedProvider) {
            finish()
            return
        }

        val initialAlpha = WidgetPreferences(this).getAlpha(appWidgetId)
        enableEdgeToEdge()

        setContent {
            val vm: BiorhythmsViewModel = viewModel(
                factory = BiorhythmsViewModelFactory(applicationContext.dataStore),
            )
            val themeMode by vm.themeMode.collectAsStateWithLifecycle()
            val language by vm.language.collectAsStateWithLifecycle()
            val birthDate by vm.birthDate.collectAsStateWithLifecycle()

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
    val alpha = alphaPercent / 100f
    val shape = MaterialTheme.shapes.large
    val checkerLight = MaterialTheme.colorScheme.surfaceVariant
    val checkerDark = MaterialTheme.colorScheme.outlineVariant
    val forecast = remember(birthDate, today) {
        birthDate?.let { BiorhythmForecast.day(it, today) }
    }
    val percentTexts = remember(forecast, locale) {
        forecast?.cycles?.map { cycle ->
            String.format(
                locale,
                "%+d%% %s",
                cycle.percent.roundToInt(),
                cycle.trend.symbol(),
            )
        }.orEmpty()
    }

    Box(
        modifier = modifier
            .clip(shape)
            .drawBehind {
                drawCheckerboard(
                    lightColor = checkerLight,
                    darkColor = checkerDark,
                    tileSize = 20.dp.toPx(),
                )
            },
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = shape,
            color = MaterialTheme.colorScheme.surface.copy(alpha = alpha),
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 0.dp,
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = appString(R.string.widget_title),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                    )
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = null,
                    )
                }

                if (birthDate == null) {
                    Text(
                        text = appString(R.string.widget_no_birth_date),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    lines.forEachIndexed { index, line ->
                        WidgetPreviewLine(
                            label = appString(line.labelResId),
                            percentText = percentTexts[index],
                        )
                    }
                    forecast?.synchronizedExtreme?.let { extreme ->
                        val threshold = BiorhythmForecast.SYNCHRONIZED_EXTREME_THRESHOLD.roundToInt()
                        Text(
                            text = appString(
                                if (extreme == SynchronizedExtreme.LOW) {
                                    R.string.sync_low_compact
                                } else {
                                    R.string.sync_high_compact
                                },
                                threshold,
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawCheckerboard(
    lightColor: Color,
    darkColor: Color,
    tileSize: Float,
) {
    var row = 0
    var y = 0f
    while (y < size.height) {
        var column = 0
        var x = 0f
        while (x < size.width) {
            drawRect(
                color = if ((row + column) % 2 == 0) lightColor else darkColor,
                topLeft = Offset(x, y),
                size = Size(tileSize, tileSize),
            )
            column++
            x += tileSize
        }
        row++
        y += tileSize
    }
}

@Composable
private fun WidgetPreviewLine(
    label: String,
    percentText: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = percentText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
