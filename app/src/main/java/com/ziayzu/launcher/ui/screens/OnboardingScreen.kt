package com.ziayzu.launcher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziayzu.launcher.ui.components.ZiayzuBackground
import com.ziayzu.launcher.ui.components.ZiayzuButton
import com.ziayzu.launcher.ui.components.ZiayzuMark
import com.ziayzu.launcher.ui.theme.Coral500
import com.ziayzu.launcher.ui.theme.Ink600
import com.ziayzu.launcher.ui.theme.Mint300
import com.ziayzu.launcher.ui.theme.Mint500
import com.ziayzu.launcher.ui.theme.TextSecondary
import com.ziayzu.launcher.ui.theme.Violet500
import kotlinx.coroutines.launch

private data class OnboardPage(val title: String, val body: String, val accent: Color)

private val pages = listOf(
    OnboardPage(
        "Java Edition,\nin your pocket",
        "Ziayzu downloads and manages official game files directly on your device — full releases, snapshots and everything.",
        Mint300
    ),
    OnboardPage(
        "Your account,\nkept yours",
        "Sign in with the official Microsoft device flow. Ziayzu never asks for your password and keeps tokens only on your device.",
        Coral500
    ),
    OnboardPage(
        "Nebula Ink,\nzero copies",
        "A theme designed from a blank canvas — deep ink surfaces, mint energy and glass cards. Nothing here is borrowed.",
        Violet500
    )
)

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pager = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    ZiayzuBackground {
        Column(Modifier.fillMaxSize().padding(28.dp)) {
            Spacer(Modifier.weight(1.1f))
            ZiayzuMark(72.dp)
            Spacer(Modifier.height(34.dp))

            HorizontalPager(state = pager, modifier = Modifier.weight(3f)) { i ->
                val p = pages[i]
                Column {
                    Text(
                        p.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = p.accent
                    )
                    Spacer(Modifier.height(14.dp))
                    Text(
                        p.body,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                        lineHeight = 26.sp
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(pages.size) { i ->
                    val active = i == pager.currentPage
                    Box(
                        Modifier
                            .size(if (active) 22.dp else 8.dp, 8.dp)
                            .background(if (active) Mint500 else Ink600, CircleShape)
                    )
                }
            }
            Spacer(Modifier.height(26.dp))

            ZiayzuButton(
                text = if (pager.currentPage == pages.lastIndex) "GET STARTED" else "NEXT",
                large = true,
                onClick = {
                    if (pager.currentPage == pages.lastIndex) onFinish()
                    else scope.launch { pager.animateScrollToPage(pager.currentPage + 1) }
                }
            )
        }
    }
}
