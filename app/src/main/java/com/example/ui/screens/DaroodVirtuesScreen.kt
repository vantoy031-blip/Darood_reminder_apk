package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DaroodRepositoryList
import com.example.ui.MainViewModel
import com.example.ui.components.GlassCard
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCard
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SoftIvoryMuted
import com.example.ui.theme.SoftIvoryText
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.WarmIvoryBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaroodVirtuesScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val preferences by viewModel.preferences.collectAsState()
    val isDark = isSystemInDarkTheme()
    val isBn = preferences.language == "BN"

    val isPlaying by viewModel.audioPlayer.isPlaying.collectAsState()
    val audioProgress by viewModel.audioPlayer.progress.collectAsState()
    val elapsedSeconds by viewModel.audioPlayer.elapsedSeconds.collectAsState()
    val totalSeconds = viewModel.audioPlayer.totalDurationSeconds

    val titleColor = if (isDark) SoftIvoryText else TextPrimaryDark
    val subColor = if (isDark) SoftIvoryMuted else TextSecondaryDark

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBn) "দরুদ ও ফযিলত" else "Darood & Virtues",
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = titleColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) DarkBackground else WarmIvoryBackground
                )
            )
        },
        containerColor = if (isDark) DarkBackground else WarmIvoryBackground
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Audio Player Hero Card
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    highlightGold = true
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(AntiqueGold),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Audio",
                                    tint = EmeraldDark,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isBn) "দরুদ তিলাওয়াত অডিও" else "Darood Audio Recitation",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = titleColor
                                    )
                                )
                                Text(
                                    text = if (isBn) "শান্ত সুরে দরুদের সুর শুনুন ও অন্তর শান্ত করুন" else "Listen to the serene recitation",
                                    style = MaterialTheme.typography.bodySmall.copy(color = subColor)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Progress bar
                        LinearProgressIndicator(
                            progress = { audioProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = AntiqueGold,
                            trackColor = if (isDark) Color(0xFF163C2D) else Color(0xFFE4DDCE)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = String.format("%02d:%02d", elapsedSeconds / 60, elapsedSeconds % 60),
                                style = MaterialTheme.typography.labelSmall.copy(color = subColor)
                            )
                            Text(
                                text = String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60),
                                style = MaterialTheme.typography.labelSmall.copy(color = subColor)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.audioPlayer.togglePlayPause() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPlaying) EmeraldPrimary else AntiqueGold,
                                contentColor = if (isPlaying) Color.White else EmeraldDark
                            )
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isPlaying) {
                                    if (isBn) "থামান (Pause)" else "Pause"
                                } else {
                                    if (isBn) "বাজান (Play Recitation)" else "Play Recitation"
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Virtues of Darood Hadith Card
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = if (isBn) "দরুদ পাঠের বিশেষ ফযিলত" else "Virtues of Reciting Darood",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AntiqueGold
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "“যে ব্যক্তি আমার ওপর একবার দরুদ পাঠায়, আল্লাহ তার ওপর দশবার রহমত নাজিল করেন, তার দশটি গুনাহ মাফ করেন এবং তার মর্যাদা দশ ধাপ বৃদ্ধি করেন।”",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 15.sp,
                                lineHeight = 24.sp,
                                color = titleColor,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "— সুনান আন-নাসায়ী: ১২৯৭, সহিহ মুসলিম: ৩৮৪",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = subColor,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        )
                    }
                }
            }

            // Authentic Daroods List
            items(DaroodRepositoryList.items) { item ->
                val isSelected = preferences.selectedDaroodId == item.id
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    highlightGold = isSelected
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isBn) item.titleBn else item.titleEn,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) AntiqueGold else titleColor
                                )
                            )

                            if (isSelected) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = AntiqueGold.copy(alpha = 0.2f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = AntiqueGold,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isBn) "হোমে সক্রিয়" else "Active on Home",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = AntiqueGold,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Arabic text
                        Text(
                            text = item.arabic,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontSize = 21.sp,
                                lineHeight = 36.sp,
                                color = titleColor,
                                fontWeight = FontWeight.SemiBold
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Bengali pronunciation
                        Text(
                            text = "উচ্চারণ: ${item.pronunciationBn}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                color = AntiqueGold,
                                lineHeight = 22.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Translation
                        Text(
                            text = "অর্থ: ${item.translationBn}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 13.sp,
                                color = subColor,
                                lineHeight = 20.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Reference
                        Text(
                            text = "রেফারেন্স: ${item.reference}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = subColor.copy(alpha = 0.8f)
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (!isSelected) {
                            OutlinedButton(
                                onClick = { viewModel.prefManager.setSelectedDaroodId(item.id) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = if (isBn) "হোম স্ক্রিনে প্রদর্শন করুন" else "Set as Home Darood",
                                    color = if (isDark) AntiqueGold else EmeraldPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
