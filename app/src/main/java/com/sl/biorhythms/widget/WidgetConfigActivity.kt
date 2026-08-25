package com.sl.biorhythms.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sl.biorhythms.BiorhythmsViewModel
import com.sl.biorhythms.BiorhythmsViewModelFactory
import com.sl.biorhythms.LocalAppLanguage
import com.sl.biorhythms.R
import com.sl.biorhythms.appString
import com.sl.biorhythms.dataStore
import com.sl.biorhythms.ui.theme.BiorhythmsTheme

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

            BiorhythmsTheme(themeMode = themeMode) {
                CompositionLocalProvider(LocalAppLanguage provides language) {
                    WidgetConfigScreen(
                        initialAlpha = initialAlpha,
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
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Text(
                text = appString(R.string.widget_config_transparency),
                style = MaterialTheme.typography.titleMedium,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = appString(R.string.widget_config_transparent),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.width(80.dp),
                )

                Slider(
                    value = alpha,
                    onValueChange = { alpha = it },
                    valueRange = 0f..100f,
                    modifier = Modifier.weight(1f),
                )

                Text(
                    text = appString(R.string.widget_config_opaque),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.width(80.dp),
                )
            }

            Text(
                text = appString(R.string.widget_config_alpha_value, alpha.toInt()),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onSave(alpha.toInt()) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(appString(R.string.widget_config_save))
            }
        }
    }
}
