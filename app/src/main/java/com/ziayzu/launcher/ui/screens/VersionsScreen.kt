package com.ziayzu.launcher.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziayzu.launcher.core.Session
import com.ziayzu.launcher.core.net.VersionManager
import com.ziayzu.launcher.core.net.VersionManifest
import com.ziayzu.launcher.core.net.VersionManifest.McVersion
import com.ziayzu.launcher.ui.components.GlassCard
import com.ziayzu.launcher.ui.components.ZiayzuBackground
import com.ziayzu.launcher.ui.components.ZiayzuPill
import com.ziayzu.launcher.ui.components.ZiayzuTopBar
import com.ziayzu.launcher.ui.theme.Coral500
import com.ziayzu.launcher.ui.theme.Ink700
import com.ziayzu.launcher.ui.theme.Mint300
import com.ziayzu.launcher.ui.theme.Mint500
import com.ziayzu.launcher.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun VersionsScreen(onBack: () -> Unit) {
    var versions by remember { mutableStateOf<List<McVersion>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }
    var tab by remember { mutableIntStateOf(0) }
    var selected by remember { mutableStateOf(Session.selectedVersion) }
    val dl by VersionManager.state.collectAsState()
    val scope = rememberCoroutineScope()

    fun load() {
        loading = true; error = null
        scope.launch {
            runCatching { VersionManifest.fetch() }
                .onSuccess { versions = it; loading = false }
                .onFailure { error = it.message ?: "Network error"; loading = false }
        }
    }
    LaunchedEffect(Unit) { load() }

    val installedIds = remember(versions, dl) {
        versions.filter { VersionManager.isInstalled(it.id) }.map { it.id }.toSet()
    }
    val shown = when (tab) {
        0 -> versions.filter { it.type == "release" }
        1 -> versions.filter { it.type == "snapshot" }
        else -> versions.filter { it.id in installedIds }
    }

    ZiayzuBackground {
        Column(Modifier.fillMaxSize().padding(20.dp)) {
            ZiayzuTopBar("Versions", onBack)
            Spacer(Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ZiayzuPill("RELEASES", tab == 0) { tab = 0 }
                ZiayzuPill("SNAPSHOTS", tab == 1) { tab = 1 }
                ZiayzuPill("INSTALLED", tab == 2) { tab = 2 }
            }
            Spacer(Modifier.height(14.dp))

            when {
                loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Mint500)
                }
                error != null -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(error ?: "", color = Coral500)
                    Spacer(Modifier.height(10.dp))
                    ZiayzuPill("RETRY", active = true) { load() }
                }
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(shown, key = { it.id }) { v ->
                        VersionRow(
                            v = v,
                            selected = v.id == selected,
                            downloading = if (dl.active && dl.versionId == v.id) dl else null,
                            onSelect = { selected = v.id; Session.selectedVersion = v.id },
                            onInstall = { scope.launch { VersionManager.install(v) } }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VersionRow(
    v: McVersion,
    selected: Boolean,
    downloading: VersionManager.DownloadState?,
    onSelect: () -> Unit,
    onInstall: () -> Unit
) {
    GlassCard(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(v.id, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    if (selected) {
                        Spacer(Modifier.width(8.dp))
                        Text("●", color = Mint500, fontSize = 12.sp)
                    }
                }
                Text(
                    "${v.type.replaceFirstChar { it.uppercase() }} · ${v.releaseTime.take(10)}",
                    color = TextSecondary, fontSize = 12.sp
                )
            }

            when {
                downloading != null -> Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.width(110.dp)
                ) {
                    Text(
                        "${(downloading.progress * 100).toInt()}%",
                        color = Mint300, fontSize = 12.sp, fontWeight = FontWeight.Bold
                    )
                    LinearProgressIndicator(
                        progress = { downloading.progress },
                        modifier = Modifier.fillMaxWidth().height(6.dp),
                        color = Mint500,
                        trackColor = Ink700
                    )
                }
                VersionManager.isInstalled(v.id) ->
                    ZiayzuPill(if (selected) "ACTIVE" else "SELECT", active = !selected, onClick = onSelect)
                else ->
                    ZiayzuPill("INSTALL", active = false, onClick = onInstall)
            }
        }
    }
}
