package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.GlassCard
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.AntiqueGoldDark
import com.example.ui.theme.AntiqueGoldLight
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SoftIvoryMuted
import com.example.ui.theme.SoftIvoryText
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import kotlinx.coroutines.launch

@Composable
fun CounterScreen(viewModel: MainViewModel) {
    val todayRecord by viewModel.todayRecord.collectAsState()
    val preferences by viewModel.preferences.collectAsState()
    val isDark = isSystemInDarkTheme()
    val isBn = preferences.language == "BN"

    var showResetDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val scaleAnim = remember { Animatable(1f) }

    val titleColor = if (isDark) SoftIvoryText else TextPrimaryDark
    val subColor = if (isDark) SoftIvoryMuted else TextSecondaryDark

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    text = if (isBn) "সেশন রিসেট করবেন?" else "Reset Current Session?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (isBn) {
                        "এটি শুধুমাত্র আপনার বর্তমান তাসবীহ সেশনের গণনা ০ করবে। আপনার আজকের মোট আমল (${todayRecord.count} বার) এবং পূর্বের ইতিহাস সুরক্ষিত থাকবে।"
                    } else {
                        "This will only reset your current counting session to 0. Today's total count (${todayRecord.count}) and history will remain safe."
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetSession()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AntiqueGold,
                        contentColor = EmeraldDark
                    )
                ) {
                    Text(if (isBn) "হ্যাঁ, রিসেট করুন" else "Yes, Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(if (isBn) "বাতিল" else "Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header Info
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isBn) "তাসবীহ কাউন্টার" else "Tasbih Counter",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isBn) "মনোযোগ সহকারে দরুদ পাঠ করুন" else "Recite Darood with deep mindfulness",
                style = MaterialTheme.typography.bodyMedium.copy(color = subColor)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Progress bar to daily target
            val targetVal = todayRecord.target.coerceAtLeast(1)
            val progressFrac = (todayRecord.count.toFloat() / targetVal).coerceIn(0f, 1f)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isBn) "আজকের মোট: ${todayRecord.count}" else "Today's Total: ${todayRecord.count}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = titleColor
                    )
                )
                Text(
                    text = if (isBn) "লক্ষ্য: ${todayRecord.target}" else "Target: ${todayRecord.target}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = AntiqueGold
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progressFrac },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = AntiqueGold,
                trackColor = if (isDark) Color(0xFF133627) else Color(0xFFE4DDCE)
            )
        }

        // Center Big Circular Touch Area
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .scale(scaleAnim.value)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                if (isDark) Color(0xFF1E533E) else Color(0xFF1B634B),
                                if (isDark) Color(0xFF0F3224) else EmeraldPrimary,
                                if (isDark) Color(0xFF081C14) else EmeraldDark
                            )
                        )
                    )
                    .border(
                        width = 4.dp,
                        brush = Brush.linearGradient(listOf(AntiqueGoldLight, AntiqueGold, AntiqueGoldDark)),
                        shape = CircleShape
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        viewModel.incrementToday(1)
                        coroutineScope.launch {
                            scaleAnim.animateTo(0.92f, tween(80, easing = FastOutSlowInEasing))
                            scaleAnim.animateTo(1f, tween(140, easing = FastOutSlowInEasing))
                        }
                    }
                    .testTag("big_counter_tap_area"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${todayRecord.count}",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 62.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftIvoryText
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (todayRecord.sessionCount > 0) {
                            if (isBn) "সেশন: ${todayRecord.sessionCount}" else "Session: ${todayRecord.sessionCount}"
                        } else {
                            if (isBn) "চাপুন (+১)" else "Tap (+1)"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 16.sp,
                            color = AntiqueGoldLight,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Short Darood hint
            Text(
                text = "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى نَبِيِّنَا مُحَمَّدٍ ﷺ",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 17.sp,
                    color = titleColor,
                    lineHeight = 28.sp
                ),
                textAlign = TextAlign.Center
            )
        }

        // Bottom Controls: Quick increments, Undo, Reset session, Vibration
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Quick increments: +1, +5, +10, +33
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = { viewModel.incrementToday(1) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+1", fontWeight = FontWeight.Bold, color = if (isDark) AntiqueGoldLight else EmeraldPrimary)
                }
                OutlinedButton(
                    onClick = { viewModel.incrementToday(5) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+5", fontWeight = FontWeight.Bold, color = if (isDark) AntiqueGoldLight else EmeraldPrimary)
                }
                OutlinedButton(
                    onClick = { viewModel.incrementToday(10) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+10", fontWeight = FontWeight.Bold, color = if (isDark) AntiqueGoldLight else EmeraldPrimary)
                }
                OutlinedButton(
                    onClick = { viewModel.incrementToday(33) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+33", fontWeight = FontWeight.Bold, color = if (isDark) AntiqueGoldLight else EmeraldPrimary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom Actions: Undo & Reset Session
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { viewModel.undoToday() },
                    enabled = todayRecord.count > 0
                ) {
                    Icon(
                        imageVector = Icons.Default.Undo,
                        contentDescription = "Undo",
                        modifier = Modifier.size(18.dp),
                        tint = if (todayRecord.count > 0) subColor else subColor.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBn) "পূর্বাবস্থায় (-১)" else "Undo (-1)",
                        color = if (todayRecord.count > 0) subColor else subColor.copy(alpha = 0.4f)
                    )
                }

                // Vibration toggle
                FilterChip(
                    selected = preferences.vibrationEnabled,
                    onClick = { viewModel.setVibrationEnabled(!preferences.vibrationEnabled) },
                    label = {
                        Text(
                            text = if (isBn) "কম্পন" else "Vibration",
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = "Vibration",
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    shape = RoundedCornerShape(10.dp)
                )

                // Reset session
                TextButton(
                    onClick = { showResetDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Session",
                        modifier = Modifier.size(18.dp),
                        tint = subColor
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBn) "সেশন রিসেট" else "Reset Lap",
                        color = subColor
                    )
                }
            }
        }
    }
}
