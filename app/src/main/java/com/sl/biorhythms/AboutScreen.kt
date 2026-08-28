package com.sl.biorhythms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp

private const val REPOSITORY_URL = "https://github.com/StanleyLl0yd/biorhythms"
private const val LICENSE_URL = "https://polyformproject.org/licenses/noncommercial/1.0.0"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(appString(R.string.about_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = appString(R.string.action_back),
                        )
                    }
                },
            )
        },
        contentWindowInsets = WindowInsets.systemBars,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = appString(R.string.app_name),
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                    text = appString(R.string.about_version, BuildConfig.VERSION_NAME),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = appString(R.string.about_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp),
                )
            }

            SectionBlock(title = appString(R.string.about_author_title)) {
                SectionRow(
                    icon = Icons.Outlined.Person,
                    label = appString(R.string.about_author_name),
                )
            }

            SectionBlock(title = appString(R.string.about_license_title)) {
                SectionRow(
                    icon = Icons.Outlined.Gavel,
                    label = appString(R.string.about_license_name),
                    value = appString(R.string.about_license_summary),
                    onClick = { runCatching { uriHandler.openUri(LICENSE_URL) } },
                )
            }

            SectionBlock(title = appString(R.string.about_source_title)) {
                SectionRow(
                    icon = Icons.Outlined.Code,
                    label = appString(R.string.about_github_label),
                    value = appString(R.string.about_github_value),
                    onClick = { runCatching { uriHandler.openUri(REPOSITORY_URL) } },
                )
            }
        }
    }
}
