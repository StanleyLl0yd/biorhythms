package com.sl.biorhythms.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sl.biorhythms.BiorhythmsViewModel
import com.sl.biorhythms.BiorhythmsViewModelFactory
import com.sl.biorhythms.R
import com.sl.biorhythms.appString
import com.sl.biorhythms.dataStore
import com.sl.biorhythms.ui.theme.BiorhythmsTheme
import kotlinx.coroutines.launch

class WidgetConfigActivity : ComponentActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setResult(RESULT_CANCELED)

        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        enableEdgeToEdge()

        setContent {
            val vm: BiorhythmsViewModel = viewModel(
                factory = BiorhythmsViewModelFactory(applicationContext.dataStore)
            )

            val themeMode by vm.themeMode.collectAsState()

            BiorhythmsTheme(themeMode = themeMode) {
                WidgetConfigScreen(
                    appWidgetId = appWidgetId,
                    onSave = { alpha ->
                        saveWidgetConfig(alpha)
                    }
                )
            }
        }
    }

    private fun saveWidgetConfig(alpha: Int) {
        val prefs = WidgetPreferences(this)
        prefs.setAlpha(appWidgetId, alpha)

        // Обновляем виджет
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val intent = Intent(this, BiorhythmsWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
        }
        sendBroadcast(intent)

        // Возвращаем результат
        val resultValue = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        setResult(RESULT_OK, resultValue)
        finish()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetConfigScreen(
    appWidgetId: Int,
    onSave: (Int) -> Unit
) {
    var alpha by remember { mutableStateOf(100f) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(appString(R.string.widget_config_title)) }
            )
        },
        contentWindowInsets = WindowInsets.systemBars
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = appString(R.string.widget_config_transparency),
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = appString(R.string.widget_config_transparent),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.width(80.dp)
                )

                Slider(
                    value = alpha,
                    onValueChange = { alpha = it },
                    valueRange = 0f..100f,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = appString(R.string.widget_config_opaque),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.width(80.dp)
                )
            }

            Text(
                text = appString(R.string.widget_config_alpha_value, alpha.toInt()),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    coroutineScope.launch {
                        onSave(alpha.toInt())
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(appString(R.string.widget_config_save))
            }
        }
    }
}