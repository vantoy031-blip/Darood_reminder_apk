package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.WeeklyBarData
import com.example.ui.MainViewModel
import com.example.ui.components.GlassCard
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.AntiqueGoldLight
import com.example.ui.theme.DarkCard
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SoftIvoryMuted
import com.example.ui.theme.SoftIvoryText
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark

@Composable
fun HistoryScreen(viewModel: MainViewModel) {
    val allRecords by viewModel.allRecords.collectAsState()
    val statistics by viewModel.statistics.collectAsState()
    val weeklyBarData by viewModel.weeklyBarData.collectAsState()
    val preferences by viewModel.preferences.collectAsState()

    val isDark = isSystemInDarkTheme()
    val isBn = preferences.language == "BN"

    val titleColor = if (isDark) SoftIvoryText else TextPrimaryDark
    val subColor = if (isDark) SoftIvoryMuted else TextSecondaryDark

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("history_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Page Title
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isBn) "আমলের ইতিহাস" else "Worship History",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isBn) "নিয়মিত দরুদ পড়ার মাধ্যমে আল্লাহর রহমত অর্জন করুন" else "Consistent recitation brings immense blessings",
                    style = MaterialTheme.typography.bodyMedium.copy(color = subColor),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Summary Statistics Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = if (isBn) "আজকের মোট" else "Today's Total",
                        value = "${statistics.todayTotal}",
                        subtitle = if (isBn) "বার" else "times",
                        modifier = Modifier.weight(1f),
                        isDark = isDark,
                        icon = "📿"
                    )
                    StatCard(
                        title = if (isBn) "এই সপ্তাহে" else "Weekly Total",
                        value = "${statistics.weeklyTotal}",
                        subtitle = if (isBn) "শেষ ৭ দিন" else "last 7 days",
                        modifier = Modifier.weight(1f),
                        isDark = isDark,
                        icon = "📅"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = if (isBn) "এই মাসে" else "Monthly Total",
                        value = "${statistics.monthlyTotal}",
                        subtitle = if (isBn) "চলতি মাস" else "this month",
                        modifier = Modifier.weight(1f),
                        isDark = isDark,
                        icon = "🌙"
                    )
                    StatCard(
                        title = if (isBn) "ধারাবাহিকতা" else "Current Streak",
                        value = "${statistics.currentStreak}",
                        subtitle = if (isBn) "দিন (সেরা: ${statistics.bestStreak})" else "days (Best: ${statistics.bestStreak})",
                        modifier = Modifier.weight(1f),
                        isDark = isDark,
                        icon = "🔥"
                    )
                }
            }
        }

        // Weekly Bar Chart
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                highlightGold = true
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBn) "সাপ্তাহিক চিত্র" else "Weekly Overview",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = titleColor
                            )
                        )
                        Text(
                            text = if (isBn) "মোট: ${statistics.weeklyTotal}" else "Total: ${statistics.weeklyTotal}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = AntiqueGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    WeeklyMinimalChart(
                        data = weeklyBarData,
                        isDark = isDark,
                        isBn = isBn
                    )
                }
            }
        }

        // Section Title: Daily Records Log
        item {
            Text(
                text = if (isBn) "দিনভিত্তিক তালিকা" else "Daily Records",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                ),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // If empty
        if (allRecords.isEmpty() || (allRecords.size == 1 && allRecords.first().count == 0)) {
            item {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🤍", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isBn) "আজকের আমল এখান থেকে শুরু করুন 🤍" else "Start your worship journey today 🤍",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = titleColor
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isBn) "হোম অথবা তাসবীহ পেজে গিয়ে দরুদ পাঠ গণনা শুরু করুন।" else "Go to Home or Counter to begin reciting Darood.",
                            style = MaterialTheme.typography.bodySmall.copy(color = subColor),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(allRecords.filter { it.count > 0 }) { record ->
                GlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (record.completedGoal) AntiqueGold.copy(alpha = 0.2f)
                                        else if (isDark) Color(0xFF143B2C) else Color(0xFFEBE3D3)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (record.completedGoal) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Completed",
                                        tint = AntiqueGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "Date",
                                        tint = if (isDark) SoftIvoryMuted else TextSecondaryDark,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = record.date,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = titleColor
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isBn) "লক্ষ্য: ${record.target} বার" else "Target: ${record.target}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = subColor)
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${record.count} বার",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (record.completedGoal) AntiqueGold else titleColor
                                )
                            )
                            if (record.completedGoal) {
                                Text(
                                    text = if (isBn) "লক্ষ্য পূরণ ✓" else "Target Met ✓",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF2E7D32),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
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

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    isDark: Boolean,
    icon: String
) {
    GlassCard(modifier = modifier) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = if (isDark) SoftIvoryMuted else TextSecondaryDark
                    )
                )
                Text(text = icon, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = if (isDark) SoftIvoryText else TextPrimaryDark
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = if (isDark) SoftIvoryMuted.copy(alpha = 0.8f) else TextSecondaryDark.copy(alpha = 0.8f)
                )
            )
        }
    }
}

@Composable
private fun WeeklyMinimalChart(
    data: List<WeeklyBarData>,
    isDark: Boolean,
    isBn: Boolean
) {
    val maxCount = (data.maxOfOrNull { it.count } ?: 500).coerceAtLeast(100)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            data.forEach { bar ->
                val barFraction = (bar.count.toFloat() / maxCount).coerceIn(0.04f, 1f)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (bar.count > 0) "${bar.count}" else "-",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = if (bar.isToday) FontWeight.Bold else FontWeight.Normal,
                            color = if (bar.isToday) AntiqueGold else if (isDark) SoftIvoryMuted else TextSecondaryDark
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .width(18.dp)
                            .height(90.dp * barFraction)
                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .background(
                                if (bar.isToday) AntiqueGold
                                else if (bar.count > 0) (if (isDark) Color(0xFF205E46) else EmeraldPrimary)
                                else if (isDark) Color(0xFF133627) else Color(0xFFE4DDCE)
                            )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isBn) bar.dayLabelBn.take(3) else bar.dayKey,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = if (bar.isToday) FontWeight.Bold else FontWeight.Medium,
                            color = if (bar.isToday) AntiqueGold else if (isDark) SoftIvoryText else TextPrimaryDark
                        )
                    )
                }
            }
        }
    }
}
