package com.ziayzu.launcher.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziayzu.launcher.core.Session
import com.ziayzu.launcher.core.net.VersionManager
import com.ziayzu.launcher.core.runtime.RuntimeRegistry
import com.ziayzu.launcher.ui.components.GlassCard
import com.ziayzu.launcher.ui.components.ZiayzuBackground
import com.ziayzu.launcher.ui.components.ZiayzuButton
import com.ziayzu.launcher.ui.components.ZiayzuTopBar
import com.ziayzu.launcher.ui.theme.Coral500
import com.ziayzu.launcher.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun PlayScreen(onBack: () -> Unit) {
    val version = Session.selectedVersion
    val installed = remember { VersionManager.isInstalled(version) }
    val runtime = remember { RuntimeRegistry.active() }
    var logs by remember { mutableStateOf(listOf("[ziayzu] session ready · version $version")) }
    val scope = rememberCoroutineScope()

    ZiayzuBackground {
        Column(Modifier.fillMaxSize().padding(20.dp)) {
            ZiayzuTopBar("Launch", onBack)
            Spacer(Modifier.height(14.dp))

            if (!installed) {
                GlassCard {
                    Text("$version is not installed", color = Coral500, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Grab it from the Versions screen first — then this becomes your launch pad.",
                        color = TextSecondary, fontSize = 13.sp
                    )
                }
                Spacer(Modifier.height(14.dp))
            }

            val rt = runtime
            GlassCard {
                Text("RUNTIME", color = TextSecondary, fontSize = 11.sp, letterSpacing = 2.sp)
                Spacer(Modifier.height(8.dp))

                if (rt == null) {
                    Text("Engine module not connected", color = Coral500, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "The shell is fully working: accounts, downloads and settings. The engine that " +
                            "executes the game (JVM + OpenGL translation + touch layer) plugs into " +
                            "GameRuntime.kt — see README, Phase 2.",
                        color = TextSecondary, fontSize = 13.sp, lineHeight = 19.sp
                    )
                } else {
                    Text(rt.label, fontWeight = FontWeight.Bold)
                    Text(rt.description, color = TextSecondary, fontSize = 13.sp)
                    Spacer(Modifier.height(14.dp))
                    ZiayzuButton(
                        text = "START GAME",
                        large = true,
                        enabled = installed && Session.account != null,
                        onClick = {
                            scope.launch {
                                try {
                                    logs = logs + "[runtime] preparing $version …"
                                    rt.prepare(VersionManager.versionDir(version), Session.options()) { p, label ->
                                        logs = logs + "[runtime] $label · ${(p * 100).toInt()}%"
                                    }
                                    logs = logs + "[runtime] starting JVM …"
                                    rt.launch(Session.account!!, Session.options())
                                } catch (t: Throwable) {
                                    logs = logs + "[error] ${t.message}"
                                }
                            }
                        }
                    )
                    if (Session.account == null) {
                        Spacer(Modifier.height(8.dp))
                        Text("Sign in before starting.", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            GlassCard(Modifier.weight(1f)) {
                Text("CONSOLE", color = TextSecondary, fontSize = 11.sp, letterSpacing = 2.sp)
                Spacer(Modifier.height(8.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(logs.size) { i ->
                        Text(
                            logs[i],
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            fontFamily = FontFamily.Monospace,
                            color = if (logs[i].startsWith("[error]")) Coral500 else TextSecondary
                        )
                    }
                }
            }
        }
    }
}
