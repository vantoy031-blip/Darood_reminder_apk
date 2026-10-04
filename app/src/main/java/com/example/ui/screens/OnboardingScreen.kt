package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun OnboardingScreen(
    onFinish: (target: Int, intervalMinutes: Int, start: String, end: String) -> Unit,
    onRequestNotificationPermission: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    var currentStep by remember { mutableIntStateOf(0) }

    // First time setup state
    var selectedTarget by remember { mutableIntStateOf(500) }
    var selectedInterval by remember { mutableIntStateOf(30) }
    var startTime by remember { mutableStateOf("08:00") }
    var endTime by remember { mutableStateOf("22:00") }

    val titleColor = if (isDark) SoftIvoryText else TextPrimaryDark
    val subColor = if (isDark) SoftIvoryMuted else TextSecondaryDark

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = if (isDark) DarkBackground else WarmIvoryBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                for (i in 0..3) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(6.dp)
                            .width(if (currentStep == i) 24.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (currentStep == i) AntiqueGold else subColor.copy(alpha = 0.3f))
                    )
                }
            }

            // Main Content Area
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "OnboardingContent"
            ) { step ->
                when (step) {
                    0 -> OnboardingStepView(
                        icon = "🌙",
                        title = "দরুদের মাধ্যমে দিনটাকে সুন্দর করুন ﷺ",
                        subtitle = "নিয়মিত দরুদ পাঠ আমাদের আত্মাকে শান্ত করে এবং জীবনে আল্লাহর অশেষ রহমত ও বরকত নিয়ে আসে।",
                        titleColor = titleColor,
                        subColor = subColor
                    )
                    1 -> OnboardingStepView(
                        icon = "🔔",
                        title = "আপনার সুবিধামতো Reminder সেট করুন",
                        subtitle = "ব্যস্ততার মাঝেও যাতে প্রিয় নবী ﷺ-এর প্রতি সালাত ও সালাম ভুলে না যান, সেজন্য নরম ও মার্জিত স্মরণিকা পান।",
                        titleColor = titleColor,
                        subColor = subColor
                    )
                    2 -> OnboardingStepView(
                        icon = "📿",
                        title = "প্রতিদিনের আমল সহজভাবে মনে রাখুন",
                        subtitle = "কোনো অতিরিক্ত জটিলতা ছাড়া ডিজিটাল তাসবীহের মাধ্যমে আপনার ধারাবাহিকতা ও অগ্রগতি বজায় রাখুন।",
                        titleColor = titleColor,
                        subColor = subColor
                    )
                    3 -> FirstTimeSetupView(
                        selectedTarget = selectedTarget,
                        onTargetChange = { selectedTarget = it },
                        selectedInterval = selectedInterval,
                        onIntervalChange = { selectedInterval = it },
                        startTime = startTime,
                        endTime = endTime,
                        onTimeWindowToggle = {
                            if (startTime == "08:00") {
                                startTime = "06:00"
                                endTime = "23:00"
                            } else {
                                startTime = "08:00"
                                endTime = "22:00"
                            }
                        },
                        titleColor = titleColor,
                        subColor = subColor,
                        isDark = isDark
                    )
                }
            }

            // Bottom Navigation Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        if (currentStep < 2) {
                            currentStep++
                        } else if (currentStep == 2) {
                            // Ask notification permission before step 3
                            onRequestNotificationPermission()
                            currentStep = 3
                        } else {
                            // Finish setup
                            onFinish(selectedTarget, selectedInterval, startTime, endTime)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (currentStep < 2) "পরবর্তী" else if (currentStep == 2) "শুরু করি" else "সংরক্ষণ করে শুরু করুন",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }

                if (currentStep < 3) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { currentStep = 3 }) {
                        Text(
                            text = "স্কিপ করুন",
                            color = subColor,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingStepView(
    icon: String,
    title: String,
    subtitle: String,
    titleColor: Color,
    subColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(AntiqueGold.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = icon, fontSize = 52.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = titleColor,
                lineHeight = 34.sp
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = subColor,
                lineHeight = 24.sp
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FirstTimeSetupView(
    selectedTarget: Int,
    onTargetChange: (Int) -> Unit,
    selectedInterval: Int,
    onIntervalChange: (Int) -> Unit,
    startTime: String,
    endTime: String,
    onTimeWindowToggle: () -> Unit,
    titleColor: Color,
    subColor: Color,
    isDark: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "প্রাথমিক পছন্দসমূহ",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = titleColor
            )
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "পরবর্তীতে যেকোনো সময় সেটিংসে এগুলো বদলাতে পারবেন",
            style = MaterialTheme.typography.bodySmall.copy(color = subColor),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Question 1: Daily Target
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "প্রতিদিন কতবার দরুদ পড়তে চান?",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(100, 300, 500, 1000).forEach { target ->
                        val isSel = selectedTarget == target
                        Button(
                            onClick = { onTargetChange(target) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSel) AntiqueGold else if (isDark) Color(0xFF194030) else Color(0xFFEBE3D3),
                                contentColor = if (isSel) EmeraldDark else if (isDark) SoftIvoryText else TextPrimaryDark
                            )
                        ) {
                            Text("$target", fontSize = 12.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Question 2: Interval
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "কতক্ষণ পরপর মনে করিয়ে দেব?",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(15 to "১৫ মি.", 30 to "৩০ মি.", 60 to "১ ঘণ্টা", 120 to "২ ঘণ্টা").forEach { (mins, label) ->
                        val isSel = selectedInterval == mins
                        Button(
                            onClick = { onIntervalChange(mins) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSel) AntiqueGold else if (isDark) Color(0xFF194030) else Color(0xFFEBE3D3),
                                contentColor = if (isSel) EmeraldDark else if (isDark) SoftIvoryText else TextPrimaryDark
                            )
                        ) {
                            Text(label, fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Question 3: Time Window
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "সময়সীমা (রিমাইন্ডার সময়)",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = titleColor
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "সকাল $startTime থেকে রাত $endTime",
                        style = MaterialTheme.typography.bodySmall.copy(color = AntiqueGold, fontWeight = FontWeight.SemiBold)
                    )
                }

                TextButton(onClick = onTimeWindowToggle) {
                    Text("বদলান", color = AntiqueGold)
                }
            }
        }
    }
}
