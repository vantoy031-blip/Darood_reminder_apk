package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.AntiqueGoldLight
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SoftIvoryMuted
import com.example.ui.theme.SoftIvoryText
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark

@Composable
fun ProgressRing(
    count: Int,
    target: Int,
    modifier: Modifier = Modifier,
    size: Dp = 210.dp,
    strokeWidth: Dp = 14.dp,
    language: String = "BN"
) {
    val isDark = isSystemInDarkTheme()
    val targetVal = target.coerceAtLeast(1)
    val rawProgress = (count.toFloat() / targetVal).coerceIn(0f, 1f)
    val percentage = (rawProgress * 100).toInt()

    val animatedProgress by animateFloatAsState(
        targetValue = rawProgress,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "ProgressRingAnimation"
    )

    val trackColor = if (isDark) {
        Color(0xFF133627)
    } else {
        Color(0xFFE4DDCE)
    }

    val progressGradient = Brush.sweepGradient(
        listOf(
            AntiqueGold,
            AntiqueGoldLight,
            if (isDark) EmeraldLight else EmeraldPrimary,
            AntiqueGold
        )
    )

    val textColor = if (isDark) SoftIvoryText else TextPrimaryDark
    val subTextColor = if (isDark) SoftIvoryMuted else TextSecondaryDark

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            val arcSize = size.toPx() - strokePx
            val topLeft = Offset(strokePx / 2f, strokePx / 2f)

            // Background Track
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(arcSize, arcSize),
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Progress Arc
            if (animatedProgress > 0f) {
                drawArc(
                    brush = progressGradient,
                    startAngle = -90f,
                    sweepAngle = animatedProgress * 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = Size(arcSize, arcSize),
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }
        }

        // Center Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$count",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            )
            Text(
                text = "/ $target",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = subTextColor
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            val percentLabel = if (language == "BN") "$percentage% সম্পন্ন" else "$percentage% Complete"
            Text(
                text = percentLabel,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AntiqueGold
                )
            )
        }
    }
}
