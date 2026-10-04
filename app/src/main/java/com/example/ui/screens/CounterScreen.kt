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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import kotlinx.coroutines.launch
import com.example.data.model.DhikrItem
import com.example.data.model.DhikrList
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CounterScreen(viewModel: MainViewModel) {
    val todayRecord by viewModel.todayRecord.collectAsState()
    val preferences by viewModel.preferences.collectAsState()
    val isDark = isSystemInDarkTheme()
    val isBn = preferences.language == "BN"

    var showResetDialog by remember { mutableStateOf(false) }
    var showDhikrSelectDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val scaleAnim = remember { Animatable(1f) }

    val titleColor = if (isDark) SoftIvoryText else TextPrimaryDark
    val subColor = if (isDark) SoftIvoryMuted else TextSecondaryDark

    val currentDhikr = DhikrList.items.find { it.id == todayRecord.selectedDhikrId }
        ?: DhikrList.items.first()

    // Determine count to display: If Durood is chosen in Tasbih, show Durood count, otherwise show separate Tasbih count!
    val isDuroodActive = currentDhikr.isDurood
    val displayCount = if (isDuroodActive) todayRecord.count else todayRecord.tasbihCount
    val sessionLap = if (isDuroodActive) todayRecord.sessionCount else todayRecord.tasbihSessionCount

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
                        "এটি শুধুমাত্র আপনার বর্তমান তাসবীহ সেশনের ল্যাপ ০ করবে। আজকের মোট তাসবীহ ও দরুদের হিসাব সংরক্ষিত থাকবে।"
                    } else {
                        "This will only reset your current lap to 0. Total daily counts remain safe."
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (isDuroodActive) {
                            viewModel.resetSession()
                        } else {
                            viewModel.resetTasbihSession()
                        }
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

    // Dhikr Selection Dialog
    if (showDhikrSelectDialog) {
        AlertDialog(
            onDismissRequest = { showDhikrSelectDialog = false },
            title = {
                Text(
                    text = if (isBn) "তাসবীহ / জিকির নির্বাচন করুন" else "Select Dhikr / Tasbih",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = if (isBn) "যেকোনো জিকির নির্বাচন করে তাসবীহ গণনা করতে পারেন। দরুদ এবং তাসবীহ আলাদাভাবে হিসাব রাখা হবে।" else "Select any dhikr to recite. Durood and other Tasbih are counted separately.",
                        style = MaterialTheme.typography.bodySmall.copy(color = subColor)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    DhikrList.items.forEach { item ->
                        val isSelected = item.id == currentDhikr.id
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) AntiqueGold.copy(alpha = 0.25f) else if (isDark) Color(0xFF1B4433) else Color(0xFFF2ECE1))
                                .clickable {
                                    viewModel.setSelectedDhikr(item.id)
                                    showDhikrSelectDialog = false
                                }
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isBn) item.nameBn else item.nameEn,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) AntiqueGold else titleColor
                                    )
                                    Text(
                                        text = item.arabic,
                                        fontSize = 14.sp,
                                        color = subColor
                                    )
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = "Selected", tint = AntiqueGold)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDhikrSelectDialog = false }) {
                    Text(if (isBn) "বন্ধ" else "Close")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header Info
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isBn) "ডিজিটাল তাসবীহ" else "Digital Tasbih",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Dhikr selector button
            Surface(
                onClick = { showDhikrSelectDialog = true },
                shape = RoundedCornerShape(16.dp),
                color = if (isDark) Color(0xFF163E2E) else Color(0xFFEBE3D3),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBn) currentDhikr.nameBn else currentDhikr.nameEn,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AntiqueGold
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.ChangeCircle,
                        contentDescription = "Change Dhikr",
                        tint = AntiqueGold,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Two separate count chips: Durood & Tasbih
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isDuroodActive) AntiqueGold.copy(alpha = 0.2f) else if (isDark) DarkCard else Color(0xFFF2ECE1),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                        Text(
                            text = if (isBn) "দরুদ: ${todayRecord.count}" else "Durood: ${todayRecord.count}",
                            fontSize = 12.sp,
                            fontWeight = if (isDuroodActive) FontWeight.Bold else FontWeight.Medium,
                            color = if (isDuroodActive) AntiqueGold else subColor
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (!isDuroodActive) AntiqueGold.copy(alpha = 0.2f) else if (isDark) DarkCard else Color(0xFFF2ECE1),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                        Text(
                            text = if (isBn) "অন্যান্য তাসবীহ: ${todayRecord.tasbihCount}" else "Other Tasbih: ${todayRecord.tasbihCount}",
                            fontSize = 12.sp,
                            fontWeight = if (!isDuroodActive) FontWeight.Bold else FontWeight.Medium,
                            color = if (!isDuroodActive) AntiqueGold else subColor
                        )
                    }
                }
            }
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
                        viewModel.incrementTasbih(currentDhikr.id, 1)
                        coroutineScope.launch {
                            scaleAnim.animateTo(0.92f, tween(75, easing = FastOutSlowInEasing))
                            scaleAnim.animateTo(1f, tween(130, easing = FastOutSlowInEasing))
                        }
                    }
                    .testTag("big_counter_tap_area"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$displayCount",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 62.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftIvoryText
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (sessionLap > 0) {
                            if (isBn) "ল্যাপ: $sessionLap" else "Lap: $sessionLap"
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

            Spacer(modifier = Modifier.height(18.dp))

            // Dhikr Arabic Text
            Text(
                text = currentDhikr.arabic,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 22.sp,
                    color = titleColor,
                    lineHeight = 34.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Meaning / Virtue
            Text(
                text = currentDhikr.meaningBn,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp,
                    color = AntiqueGold
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
                    onClick = { viewModel.incrementTasbih(currentDhikr.id, 1) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+1", fontWeight = FontWeight.Bold, color = if (isDark) AntiqueGoldLight else EmeraldPrimary)
                }
                OutlinedButton(
                    onClick = { viewModel.incrementTasbih(currentDhikr.id, 5) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+5", fontWeight = FontWeight.Bold, color = if (isDark) AntiqueGoldLight else EmeraldPrimary)
                }
                OutlinedButton(
                    onClick = { viewModel.incrementTasbih(currentDhikr.id, 10) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+10", fontWeight = FontWeight.Bold, color = if (isDark) AntiqueGoldLight else EmeraldPrimary)
                }
                OutlinedButton(
                    onClick = { viewModel.incrementTasbih(currentDhikr.id, 33) },
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
                    onClick = { viewModel.undoTasbih(currentDhikr.id) },
                    enabled = displayCount > 0
                ) {
                    Icon(
                        imageVector = Icons.Default.Undo,
                        contentDescription = "Undo",
                        modifier = Modifier.size(18.dp),
                        tint = if (displayCount > 0) subColor else subColor.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBn) "পূর্বাবস্থায় (-১)" else "Undo (-1)",
                        color = if (displayCount > 0) subColor else subColor.copy(alpha = 0.4f)
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
                        text = if (isBn) "ল্যাপ রিসেট" else "Reset Lap",
                        color = subColor
                    )
                }
            }
        }
    }
}
