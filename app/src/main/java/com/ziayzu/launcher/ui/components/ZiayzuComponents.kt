package com.ziayzu.launcher.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziayzu.launcher.ui.theme.Ink600
import com.ziayzu.launcher.ui.theme.Ink800
import com.ziayzu.launcher.ui.theme.Ink900
import com.ziayzu.launcher.ui.theme.Mint300
import com.ziayzu.launcher.ui.theme.Mint500
import com.ziayzu.launcher.ui.theme.TextSecondary
import com.ziayzu.launcher.ui.theme.Violet500

/** Full-bleed Nebula Ink gradient background with safe-area content inside. */
@Composable
fun ZiayzuBackground(content: @Composable BoxScope.() -> Unit) {
    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Ink900, Ink800))))
    Box(Modifier.fillMaxSize().systemBarsPadding()) { content() }
}

/** The Ziayzu brand mark — gradient tile with a bold Z. */
@Composable
fun ZiayzuMark(size: Dp = 64.dp) {
    Box(
        Modifier
            .size(size)
            .clip(RoundedCornerShape(percent = 30))
            .background(Brush.linearGradient(listOf(Mint500, Violet500))),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "Z",
            color = Ink900,
            fontWeight = FontWeight.Black,
            fontSize = with(LocalDensity.current) { (size * 0.5f).toSp() }
        )
    }
}

/** Signature gradient action button. */
@Composable
fun ZiayzuButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    large: Boolean = false
) {
    val brush = if (enabled)
        Brush.linearGradient(listOf(Mint500, Mint300))
    else
        Brush.linearGradient(listOf(Ink600, Ink600))

    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(brush)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = if (large) 18.dp else 13.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            color = if (enabled) Ink900 else TextSecondary,
            fontWeight = FontWeight.ExtraBold,
            fontSize = if (large) 17.sp else 14.sp,
            letterSpacing = 2.sp
        )
    }
}

/** Small selectable pill — tabs, tags, inline actions. */
@Composable
fun ZiayzuPill(text: String, active: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(if (active) Mint500 else Ink600)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text,
            color = if (active) Ink900 else TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

/** Frosted "glass" card — the core surface of the Ziayzu layout. */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val clickable = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    Surface(
        modifier = modifier.then(clickable),
        shape = RoundedCornerShape(20.dp),
        color = Ink600.copy(alpha = 0.45f),
        border = BorderStroke(1.dp, Mint500.copy(alpha = 0.15f))
    ) {
        Column(Modifier.padding(18.dp), content = content)
    }
}

@Composable
fun ZiayzuTopBar(title: String, onBack: (() -> Unit)? = null) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextSecondary)
            }
        }
        Spacer(Modifier.width(4.dp))
        Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    }
}
