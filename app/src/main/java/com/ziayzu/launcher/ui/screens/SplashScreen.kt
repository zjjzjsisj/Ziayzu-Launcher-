package com.ziayzu.launcher.ui.screens

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziayzu.launcher.ui.components.ZiayzuMark
import com.ziayzu.launcher.ui.theme.Ink800
import com.ziayzu.launcher.ui.theme.Ink900
import com.ziayzu.launcher.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNext: () -> Unit) {
    LaunchedEffect(Unit) { delay(1400); onNext() }

    val pulse = rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(1000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "scale"
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Ink900, Ink800))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.scale(pulse.value)) { ZiayzuMark(92.dp) }
            Spacer(Modifier.height(22.dp))
            Text("ZIAYZU", fontWeight = FontWeight.Black, fontSize = 32.sp, letterSpacing = 10.sp)
            Spacer(Modifier.height(4.dp))
            Text("L A U N C H E R", color = TextSecondary, fontSize = 12.sp, letterSpacing = 6.sp)
        }
    }
}
