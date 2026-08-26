package com.ziayzu.launcher.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziayzu.launcher.core.Session
import com.ziayzu.launcher.core.net.VersionManifest
import com.ziayzu.launcher.core.net.VersionManager
import com.ziayzu.launcher.ui.components.GlassCard
import com.ziayzu.launcher.ui.components.ZiayzuBackground
import com.ziayzu.launcher.ui.components.ZiayzuButton
import com.ziayzu.launcher.ui.components.ZiayzuMark
import com.ziayzu.launcher.ui.components.ZiayzuPill
import com.ziayzu.launcher.ui.nav.Routes
import com.ziayzu.launcher.ui.theme.Coral500
import com.ziayzu.launcher.ui.theme.Mint300
import com.ziayzu.launcher.ui.theme.TextSecondary

@Composable
fun HomeScreen(nav: (String) -> Unit) {
    var latest by remember { mutableStateOf<VersionManifest.McVersion?>(null) }
    LaunchedEffect(Unit) {
        latest = runCatching { VersionManifest.fetch().firstOrNull() }.getOrNull()
    }
    val account = Session.account
    val installed = VersionManager.isInstalled(Session.selectedVersion)

    ZiayzuBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ZiayzuMark(44.dp)
                    Spacer(Modifier.width(12.dp))
                    androidx.compose.foundation.layout.Column(Modifier.weight(1f)) {
                        Text("ZIAYZU", fontWeight = FontWeight.Black, fontSize = 17.sp, letterSpacing = 3.sp)
                        Text(
                            account?.let { "Playing as ${it.name}" } ?: "Not signed in",
                            color = TextSecondary, fontSize = 12.sp
                        )
                    }
                    if (account == null) ZiayzuPill("SIGN IN", active = true) { nav(Routes.LOGIN) }
                }
            }

            item {
                GlassCard {
                    Text("SELECTED VERSION", color = TextSecondary, fontSize = 11.sp, letterSpacing = 2.sp)
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            Session.selectedVersion,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            if (installed) "INSTALLED" else "NOT INSTALLED",
                            color = if (installed) Mint300 else Coral500,
                            fontSize = 11.sp, fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(20.dp))
                    ZiayzuButton("PLAY", large = true) { nav(Routes.PLAY) }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    GlassCard(Modifier.weight(1f), onClick = { nav(Routes.VERSIONS) }) {
                        Text("VERSIONS", color = Mint300, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(Modifier.height(4.dp))
                        Text("Manage & install", color = TextSecondary, fontSize = 12.sp)
                    }
                    GlassCard(Modifier.weight(1f), onClick = { nav(Routes.SETTINGS) }) {
                        Text("SETTINGS", color = Mint300, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(Modifier.height(4.dp))
                        Text("Renderer · RAM · input", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }

            item {
                latest?.let { l ->
                    GlassCard {
                        Text("LATEST FROM MOJANG", color = TextSecondary, fontSize = 11.sp, letterSpacing = 2.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(l.id, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text(
                            "${l.type.replaceFirstChar { it.uppercase() }} · released ${l.releaseTime.take(10)}",
                            color = TextSecondary, fontSize = 12.sp
                        )
                    }
                }
            }

            item {
                Text(
                    "Ziayzu is an unofficial launcher, not affiliated with Mojang Studios or Microsoft. " +
                        "Each player must sign in with their own account that owns Minecraft: Java Edition.",
                    color = TextSecondary.copy(alpha = 0.55f),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
