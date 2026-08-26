package com.ziayzu.launcher.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziayzu.launcher.core.Prefs
import com.ziayzu.launcher.core.Session
import com.ziayzu.launcher.core.auth.AuthManager
import com.ziayzu.launcher.ui.components.GlassCard
import com.ziayzu.launcher.ui.components.ZiayzuBackground
import com.ziayzu.launcher.ui.components.ZiayzuButton
import com.ziayzu.launcher.ui.components.ZiayzuMark
import com.ziayzu.launcher.ui.components.ZiayzuPill
import com.ziayzu.launcher.ui.theme.Coral500
import com.ziayzu.launcher.ui.theme.Mint300
import com.ziayzu.launcher.ui.theme.TextSecondary
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onDone: () -> Unit, onSkip: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var device by remember { mutableStateOf<AuthManager.DeviceLogin?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var job by remember { mutableStateOf<Job?>(null) }

    ZiayzuBackground {
        Column(
            Modifier.fillMaxSize().padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1f))
            ZiayzuMark(84.dp)
            Spacer(Modifier.height(20.dp))
            Text(
                "Welcome to Ziayzu",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Sign in with the Microsoft account that owns Minecraft: Java Edition. " +
                    "Ziayzu uses the official device-login flow — your password is never typed here.",
                color = TextSecondary, textAlign = TextAlign.Center, fontSize = 13.sp, lineHeight = 19.sp
            )
            Spacer(Modifier.height(26.dp))

            val d = device
            if (d == null) {
                if (!AuthManager.isConfigured()) {
                    GlassCard(Modifier.fillMaxWidth()) {
                        Text("One-time setup needed", color = Coral500, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Create a free Azure app registration and paste the client ID into core/auth/AuthManager.kt. Steps are in the README.",
                            color = TextSecondary, fontSize = 12.sp, lineHeight = 18.sp
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                }
                error?.let {
                    Text(it, color = Coral500, fontSize = 12.sp)
                    Spacer(Modifier.height(10.dp))
                }
                ZiayzuButton(
                    text = "SIGN IN WITH MICROSOFT",
                    enabled = AuthManager.isConfigured(),
                    onClick = {
                        error = null
                        job = scope.launch {
                            try {
                                val dl = AuthManager.beginDeviceFlow()
                                device = dl
                                val account = AuthManager.awaitSession(dl)
                                Session.account = account
                                Prefs.saveAccount(account)
                                onDone()
                            } catch (t: Throwable) {
                                device = null
                                error = t.message ?: "Login failed"
                            }
                        }
                    }
                )
            } else {
                GlassCard(Modifier.fillMaxWidth()) {
                    Text("ENTER THIS CODE AT", color = TextSecondary, fontSize = 11.sp, letterSpacing = 2.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(d.verifyUrl, color = Mint300, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        d.userCode,
                        fontWeight = FontWeight.Black, fontSize = 34.sp, letterSpacing = 4.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    ZiayzuPill("OPEN IN BROWSER", active = true, onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(d.verifyUrl)))
                    })
                    Spacer(Modifier.height(10.dp))
                    Text("Waiting for you to finish signing in…", color = TextSecondary, fontSize = 12.sp)
                    Spacer(Modifier.height(4.dp))
                    TextButton(onClick = { job?.cancel(); device = null }) {
                        Text("Cancel", color = TextSecondary)
                    }
                }
            }

            Spacer(Modifier.weight(1f))
            TextButton(onClick = onSkip) {
                Text("Continue without signing in", color = TextSecondary)
            }
        }
    }
}
