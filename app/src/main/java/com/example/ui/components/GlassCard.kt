package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.WarmIvoryCard
import com.example.ui.theme.WarmIvoryCardBorder

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    elevation: Dp = 2.dp,
    highlightGold: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = isSystemInDarkTheme()

    val bgColor = if (isDark) {
        if (highlightGold) Color(0xFF163B2C) else DarkCard
    } else {
        if (highlightGold) Color(0xFFFFFDF8) else WarmIvoryCard
    }

    val borderColor = if (highlightGold) {
        AntiqueGold.copy(alpha = if (isDark) 0.45f else 0.55f)
    } else {
        if (isDark) DarkCardBorder else WarmIvoryCardBorder
    }

    val clickModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = if (isDark) Color.Black.copy(alpha = 0.5f) else Color(0x1A0D3E2E),
                spotColor = if (isDark) Color.Black.copy(alpha = 0.5f) else Color(0x1A0D3E2E)
            )
            .clip(shape)
            .background(bgColor)
            .border(width = 1.dp, color = borderColor, shape = shape)
            .then(clickModifier)
    ) {
        content()
    }
}
