package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DaroodRepositoryList
import com.example.ui.MainViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.GoalDialog
import com.example.ui.components.ProgressRing
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.AntiqueGoldLight
import com.example.ui.theme.DarkCard
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ForestGreenAccent
import com.example.ui.theme.GoldAccentDark
import com.example.ui.theme.SoftIvoryMuted
import com.example.ui.theme.SoftIvoryText
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TextTertiaryDark

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToDaroodDetails: () -> Unit
) {
    val todayRecord by viewModel.todayRecord.collectAsState()
    val preferences by viewModel.preferences.collectAsState()
    val statistics by viewModel.statistics.collectAsState()
    val nextReminderTime by viewModel.nextReminderTime.collectAsState()

    val isDark = isSystemInDarkTheme()
    var showGoalDialog by remember { mutableStateOf(false) }

    val audioPlaying by viewModel.audioPlayer.isPlaying.collectAsState()
    val audioProgress by viewModel.audioPlayer.progress.collectAsState()

    val lang = preferences.language
    val isBn = lang == "BN"

    val titleColor = if (isDark) SoftIvoryText else TextPrimaryDark
    val subColor = if (isDark) SoftIvoryMuted else TextSecondaryDark

    val primaryDarood = DaroodRepositoryList.items.find { it.id == preferences.selectedDaroodId }
        ?: DaroodRepositoryList.items.first()

    if (showGoalDialog) {
        GoalDialog(
            currentTarget = todayRecord.target,
            language = lang,
            onDismiss = { showGoalDialog = false },
            onSave = { newTarget ->
                viewModel.updateTarget(newTarget)
                showGoalDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Islamic Bismillah & Greeting
        Text(
            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = AntiqueGold
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (isBn) "আসসালামু আলাইকুম" else "Assalamu Alaikum",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = titleColor
            ),
            textAlign = TextAlign.Center
        )

        Text(
            text = if (isBn) "একটু থামুন, দরুদ পড়ুন ﷺ" else "Pause for a moment, recite Darood ﷺ",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = subColor,
                fontSize = 14.sp
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Streak Banner / Gentle Reflection
        if (statistics.currentStreak > 0) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isDark) Color(0xFF163829) else Color(0xFFEBE3D3),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔥",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBn) "${statistics.currentStreak} দিনের ধারাবাহিক আমল" else "${statistics.currentStreak} Day Consistency Streak",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) AntiqueGoldLight else EmeraldDark
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Large Circular Progress Ring
        ProgressRing(
            count = todayRecord.count,
            target = todayRecord.target,
            language = lang,
            modifier = Modifier.testTag("progress_ring")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Target edit button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isBn) "আজকের লক্ষ্য: ${todayRecord.target}" else "Daily Target: ${todayRecord.target}",
                style = MaterialTheme.typography.bodyMedium.copy(color = subColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            TextButton(
                onClick = { showGoalDialog = true },
                modifier = Modifier.testTag("change_goal_button")
            ) {
                Text(
                    text = if (isBn) "পরিবর্তন" else "Change",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = AntiqueGold,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        // Peaceful Goal Completion Message
        AnimatedVisibility(
            visible = todayRecord.count >= todayRecord.target && todayRecord.target > 0,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut()
        ) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                highlightGold = true
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🤍", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isBn) "আলহামদুলিল্লাহ 🤍" else "Alhamdulillah 🤍",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) GoldAccentDark else EmeraldPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isBn) "আজকের দরুদের লক্ষ্য পূর্ণ হয়েছে। আল্লাহ তাআলা আপনার আমল কবুল করুন।" else "Today's Darood goal is fulfilled. May Allah accept your sincere worship.",
                            style = MaterialTheme.typography.bodySmall.copy(color = subColor)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Main Primary Button (+ দরুদ পড়েছি)
        Button(
            onClick = { viewModel.incrementToday(1) },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .testTag("increment_button"),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = EmeraldPrimary,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isBn) "+ দরুদ পড়েছি" else "+ Recited Darood",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Secondary Action Controls: Undo and Quick additions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Undo button
            TextButton(
                onClick = { viewModel.undoToday() },
                enabled = todayRecord.count > 0,
                modifier = Modifier.testTag("undo_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Undo,
                    contentDescription = "Undo",
                    modifier = Modifier.size(16.dp),
                    tint = if (todayRecord.count > 0) subColor else subColor.copy(alpha = 0.4f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isBn) "পূর্বাবস্থায় (-১)" else "Undo (-1)",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = if (todayRecord.count > 0) subColor else subColor.copy(alpha = 0.4f)
                    )
                )
            }

            // Quick +5 and +10 chips
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { viewModel.incrementToday(5) },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("add_5_button")
                ) {
                    Text("+5", fontWeight = FontWeight.SemiBold, color = if (isDark) AntiqueGoldLight else EmeraldPrimary)
                }

                OutlinedButton(
                    onClick = { viewModel.incrementToday(10) },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("add_10_button")
                ) {
                    Text("+10", fontWeight = FontWeight.SemiBold, color = if (isDark) AntiqueGoldLight else EmeraldPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Main Darood Card
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("darood_card"),
            highlightGold = true,
            onClick = onNavigateToDaroodDetails
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Arabic text with tashkeel
                Text(
                    text = primaryDarood.arabic,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 38.sp,
                        color = titleColor
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Bengali Pronunciation
                Text(
                    text = "“${primaryDarood.pronunciationBn}”",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = AntiqueGold,
                        lineHeight = 24.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Hadith virtue reference
                Text(
                    text = primaryDarood.reference,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        color = subColor,
                        lineHeight = 20.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Calm Darood Audio Player bar
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isDark) Color(0xFF102E23) else Color(0xFFF3ECE0),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = { viewModel.audioPlayer.togglePlayPause() },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(AntiqueGold)
                            ) {
                                Icon(
                                    imageVector = if (audioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (audioPlaying) "Pause" else "Play",
                                    tint = EmeraldDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isBn) "শান্ত সুরে দরুদ পাঠ শুনুন" else "Serene Darood Recitation",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = titleColor
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (audioPlaying) {
                                        if (isBn) "বাজছে… শান্ত মনে স্মরণ করুন" else "Playing peaceful tone…"
                                    } else {
                                        if (isBn) "শুনতে চাপুন" else "Tap to listen"
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        color = subColor
                                    )
                                )
                            }
                        }

                        if (audioPlaying || audioProgress > 0f) {
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { audioProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = AntiqueGold,
                                trackColor = if (isDark) Color(0xFF1D4535) else Color(0xFFDFD7C7)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Reminder Status Card
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("reminder_card"),
            onClick = onNavigateToSettings
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (preferences.reminderEnabled) AntiqueGold.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Reminder",
                            tint = if (preferences.reminderEnabled) AntiqueGold else subColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isBn) "রিমাইন্ডার" else "Reminder",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = titleColor
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (preferences.reminderEnabled) {
                                    if (isBn) "● সক্রিয়" else "● Active"
                                } else {
                                    if (isBn) "○ বন্ধ" else "○ Inactive"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (preferences.reminderEnabled) Color(0xFF2E7D32) else subColor
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        val intervalText = when (preferences.reminderMode) {
                            "INTERVAL" -> if (isBn) "প্রতি ${preferences.intervalMinutes} মিনিট পরপর" else "Every ${preferences.intervalMinutes} mins"
                            "SCHEDULED" -> if (isBn) "নির্দিষ্ট নির্ধারিত সময়ে" else "Scheduled times"
                            "PRAYER" -> if (isBn) "নামাজের পরে" else "Prayer times"
                            else -> ""
                        }

                        Text(
                            text = if (preferences.reminderEnabled) {
                                if (isBn) "$intervalText (পরবর্তী: $nextReminderTime)" else "$intervalText (Next: $nextReminderTime)"
                            } else {
                                if (isBn) "রিমাইন্ডার চালু করতে চাপুন" else "Tap to enable reminder"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(color = subColor)
                        )
                    }
                }

                TextButton(
                    onClick = onNavigateToSettings,
                    modifier = Modifier.testTag("edit_reminder_button")
                ) {
                    Text(
                        text = if (isBn) "পরিবর্তন" else "Edit",
                        color = AntiqueGold,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
