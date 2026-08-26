package com.ziayzu.launcher.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziayzu.launcher.core.Prefs
import com.ziayzu.launcher.core.Renderers
import com.ziayzu.launcher.ui.components.GlassCard
import com.ziayzu.launcher.ui.components.ZiayzuBackground
import com.ziayzu.launcher.ui.components.ZiayzuPill
import com.ziayzu.launcher.ui.components.ZiayzuTopBar
import com.ziayzu.launcher.ui.theme.Mint300
import com.ziayzu.launcher.ui.theme.Mint500
import com.ziayzu.launcher.ui.theme.TextSecondary

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    var renderer by remember { mutableStateOf(Prefs.renderer) }
    var memory by remember { mutableIntStateOf(Prefs.memoryMb) }
    var args by remember { mutableStateOf(Prefs.javaArgs) }
    var mouse by remember { mutableStateOf(Prefs.virtualMouse) }

    ZiayzuBackground {
        Column(Modifier.fillMaxSize().padding(20.dp)) {
            ZiayzuTopBar("Settings", onBack)
            Spacer(Modifier.height(14.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {

                item {
                    GlassCard {
                        Text("RENDERER", color = TextSecondary, fontSize = 11.sp, letterSpacing = 2.sp)
                        Spacer(Modifier.height(10.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Renderers.ALL.forEach { r ->
                                ZiayzuPill(r, active = renderer == r) {
                                    renderer = r; Prefs.renderer = r
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "How desktop OpenGL is translated to your phone's GPU. Your choice is handed to the runtime module.",
                            color = TextSecondary, fontSize = 11.sp, lineHeight = 16.sp
                        )
                    }
                }

                item {
                    GlassCard {
                        Text("MEMORY ALLOCATION", color = TextSecondary, fontSize = 11.sp, letterSpacing = 2.sp)
                        Spacer(Modifier.height(10.dp))
                        Text("$memory MB", color = Mint300, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Slider(
                            value = memory.toFloat(),
                            onValueChange = {
                                memory = (it / 256).toInt() * 256
                                Prefs.memoryMb = memory
                            },
                            valueRange = 1024f..8192f,
                            steps = 27,
                            color = Mint500
                        )
                        Text("Leave at least 2 GB of headroom for Android itself.", color = TextSecondary, fontSize = 11.sp)
                    }
                }

                item {
                    GlassCard {
                        Text("JAVA ARGUMENTS", color = TextSecondary, fontSize = 11.sp, letterSpacing = 2.sp)
                        Spacer(Modifier.height(10.dp))
                        OutlinedTextField(
                            value = args,
                            onValueChange = { args = it; Prefs.javaArgs = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                item {
                    GlassCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("Virtual mouse", fontWeight = FontWeight.Bold)
                                Text("On-screen pointer for menus and inventory.", color = TextSecondary, fontSize = 12.sp)
                            }
                            Switch(
                                checked = mouse,
                                onCheckedChange = { mouse = it; Prefs.virtualMouse = it }
                            )
                        }
                    }
                }

                item {
                    GlassCard {
                        Text("ABOUT", color = TextSecondary, fontSize = 11.sp, letterSpacing = 2.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("Ziayzu Launcher 1.0.0 · Nebula Ink", fontWeight = FontWeight.Bold)
                        Text(
                            "Original design. Ziayzu is not affiliated with Mojang Studios or Microsoft.",
                            color = TextSecondary, fontSize = 11.sp, lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}
