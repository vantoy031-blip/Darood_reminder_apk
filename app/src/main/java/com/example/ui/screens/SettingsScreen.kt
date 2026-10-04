package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import com.example.reminder.AlarmScheduler
import com.example.ui.MainViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.GoalDialog
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.AntiqueGoldLight
import com.example.ui.theme.DarkCard
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.SoftIvoryMuted
import com.example.ui.theme.SoftIvoryText
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onRequestNotificationPermission: () -> Unit
) {
    val preferences by viewModel.preferences.collectAsState()
    val todayRecord by viewModel.todayRecord.collectAsState()
    val isDark = isSystemInDarkTheme()
    val isBn = preferences.language == "BN"
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    var showGoalDialog by remember { mutableStateOf(false) }
    var showResetSafetyDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showAddTimeDialog by remember { mutableStateOf(false) }
    var showTimeWindowDialog by remember { mutableStateOf(false) }

    var exportJsonText by remember { mutableStateOf("") }
    var importJsonText by remember { mutableStateOf("") }

    val areNotificationsEnabled = remember(context) {
        NotificationManagerCompat.from(context).areNotificationsEnabled()
    }
    val canScheduleExact = remember(context) {
        AlarmScheduler.canScheduleExactAlarms(context)
    }

    val titleColor = if (isDark) SoftIvoryText else TextPrimaryDark
    val subColor = if (isDark) SoftIvoryMuted else TextSecondaryDark

    if (showGoalDialog) {
        GoalDialog(
            currentTarget = todayRecord.target,
            language = preferences.language,
            onDismiss = { showGoalDialog = false },
            onSave = {
                viewModel.updateTarget(it)
                showGoalDialog = false
            }
        )
    }

    // Add Specific Time Dialog
    if (showAddTimeDialog) {
        var hourInput by remember { mutableStateOf("09") }
        var minuteInput by remember { mutableStateOf("00") }

        AlertDialog(
            onDismissRequest = { showAddTimeDialog = false },
            title = {
                Text(
                    text = if (isBn) "নির্দিষ্ট সময় যোগ করুন" else "Add Specific Time",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = if (isBn) "২৪ ঘণ্টার ফরম্যাটে সময় দিন (যেমন: 09:30 বা 18:45):" else "Enter time in 24h format (e.g., 09:30 or 18:45):",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = hourInput,
                            onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) hourInput = it },
                            label = { Text("HH") },
                            modifier = Modifier.width(70.dp),
                            singleLine = true
                        )
                        Text(" : ", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = minuteInput,
                            onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) minuteInput = it },
                            label = { Text("MM") },
                            modifier = Modifier.width(70.dp),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = if (isBn) "জনপ্রিয় সময়গুলো:" else "Quick Suggestions:",
                        style = MaterialTheme.typography.labelSmall.copy(color = subColor)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("07:00", "09:30", "12:15", "15:30", "18:00", "20:30", "22:00").forEach { preset ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isDark) Color(0xFF1B4433) else Color(0xFFEBE3D3))
                                    .clickable {
                                        val parts = preset.split(":")
                                        hourInput = parts[0]
                                        minuteInput = parts[1]
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(preset, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val h = hourInput.toIntOrNull() ?: 9
                        val m = minuteInput.toIntOrNull() ?: 0
                        if (h in 0..23 && m in 0..59) {
                            val formatted = String.format("%02d:%02d", h, m)
                            viewModel.addScheduledTime(formatted)
                            showAddTimeDialog = false
                            Toast.makeText(context, if (isBn) "$formatted সময়টি যোগ করা হয়েছে" else "Added $formatted", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, if (isBn) "সঠিক সময় দিন (ঘণ্টা ০০-২৩, মিনিট ০০-৫৯)" else "Invalid time", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold, contentColor = EmeraldDark)
                ) {
                    Text(if (isBn) "যোগ করুন" else "Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTimeDialog = false }) {
                    Text(if (isBn) "বাতিল" else "Cancel")
                }
            }
        )
    }

    // Time Window Dialog
    if (showTimeWindowDialog) {
        var startH by remember { mutableStateOf(preferences.startTime.split(":").getOrElse(0) { "08" }) }
        var endH by remember { mutableStateOf(preferences.endTime.split(":").getOrElse(0) { "22" }) }

        AlertDialog(
            onDismissRequest = { showTimeWindowDialog = false },
            title = {
                Text(
                    text = if (isBn) "রিমাইন্ডার সময়সীমা" else "Active Time Window",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = if (isBn) "সকাল কয়টা থেকে রাত কয়টা পর্যন্ত রিমাইন্ডার পাবেন:" else "Select start and end hours:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(if (isBn) "শুরুর সময়:" else "Start Time:")
                        OutlinedTextField(
                            value = startH,
                            onValueChange = { if (it.length <= 5) startH = it },
                            placeholder = { Text("08:00") },
                            modifier = Modifier.width(90.dp),
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(if (isBn) "শেষ সময়:" else "End Time:")
                        OutlinedTextField(
                            value = endH,
                            onValueChange = { if (it.length <= 5) endH = it },
                            placeholder = { Text("22:00") },
                            modifier = Modifier.width(90.dp),
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val s = if (startH.contains(":")) startH else "$startH:00"
                        val e = if (endH.contains(":")) endH else "$endH:00"
                        viewModel.setTimeWindow(s, e)
                        showTimeWindowDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold, contentColor = EmeraldDark)
                ) {
                    Text(if (isBn) "সংরক্ষণ" else "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimeWindowDialog = false }) {
                    Text(if (isBn) "বাতিল" else "Cancel")
                }
            }
        )
    }

    if (showResetSafetyDialog) {
        AlertDialog(
            onDismissRequest = { showResetSafetyDialog = false },
            title = {
                Text(
                    text = if (isBn) "আপনি কি নিশ্চিত?" else "Are you sure?",
                    fontWeight = FontWeight.Bold,
                    color = ErrorRed
                )
            },
            text = {
                Text(
                    text = if (isBn) {
                        "সব হিস্টোরি ও ডেটা মুছে যাবে। এই কাজটি ফিরিয়ে আনা যাবে না।"
                    } else {
                        "All history and data will be erased. This action cannot be undone."
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAllData()
                        showResetSafetyDialog = false
                        Toast.makeText(
                            context,
                            if (isBn) "সমস্ত ডেটা মুছে ফেলা হয়েছে" else "All data reset successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed, contentColor = Color.White)
                ) {
                    Text(if (isBn) "হ্যাঁ, মুছে ফেলুন" else "Yes, Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetSafetyDialog = false }) {
                    Text(if (isBn) "বাতিল" else "Cancel")
                }
            }
        )
    }

    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text(if (isBn) "ডেটা এক্সপোর্ট (ব্যাকআপ)" else "Export Data (Backup)", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = if (isBn) "নিচের ডেটা কপি করে সুরক্ষিত স্থানে রাখুন:" else "Copy this data to save as backup:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = exportJsonText,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(exportJsonText))
                        Toast.makeText(context, if (isBn) "ক্লিপবোর্ডে কপি করা হয়েছে" else "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        showExportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold, contentColor = EmeraldDark)
                ) {
                    Text(if (isBn) "কপি করুন" else "Copy")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text(if (isBn) "বন্ধ" else "Close")
                }
            }
        )
    }

    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text(if (isBn) "ডেটা ইমপোর্ট (রিস্টোর)" else "Import Data (Restore)", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = if (isBn) "আপনার পূর্ববর্তী ব্যাকআপ ডেটা এখানে পেস্ট করুন:" else "Paste your backup JSON data here:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = importJsonText,
                        onValueChange = { importJsonText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.importDataJson(importJsonText) { success, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            if (success) showImportDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold, contentColor = EmeraldDark)
                ) {
                    Text(if (isBn) "পুনরুদ্ধার করুন" else "Restore")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text(if (isBn) "বাতিল" else "Cancel")
                }
            }
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text(text = "Darood • দরুদ", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "একটু থামুন, দরুদ পড়ুন ﷺ\n\nঅ্যাপ সংস্করণ: ১.০.০ (1.0.0)\n\nএই অ্যাপটি মুসলিমদের দৈনন্দিন জীবনে প্রিয় নবী মুহাম্মদ ﷺ-এর ওপর দরুদ পাঠের আমলকে সহজ, নিয়মিত ও শান্তিপূর্ণ করার উদ্দেশ্যে তৈরি করা হয়েছে।\n\nরেফারেন্স: সহিহ মুসলিম: ৩৮৪, সুনান তিরমিজি: ৪৮৪, সুনান আন-নাসায়ী: ১২৯৭।",
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text(if (isBn) "ঠিক আছে" else "OK")
                }
            }
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text(if (isBn) "গোপনীয়তা ও ডেটা সুরক্ষা" else "Privacy & Data Safety", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = if (isBn) {
                        "১. Darood অ্যাপ কোনো ব্যক্তিগত তথ্য সংগ্রহ করে না।\n২. আপনার সমস্ত আমলের হিসাব, লক্ষ্য এবং রিমাইন্ডার কেবল আপনার নিজস্ব ডিভাইসেই সংরক্ষিত থাকে (১০০% অফলাইন)।\n৩. কোনো অ্যাকাউন্ট বা লগইন প্রয়োজন নেই।"
                    } else {
                        "1. Darood does not collect any personal data.\n2. All your counts, targets, and reminders stay 100% on your device locally.\n3. No login or cloud tracking is required."
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp)
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text(if (isBn) "বুঝেছি" else "Understood")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("settings_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isBn) "সেটিংস" else "Settings",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isBn) "আপনার সুবিধামতো রিমাইন্ডার ও অ্যাপ সাজিয়ে নিন" else "Customize reminders and preferences",
                    style = MaterialTheme.typography.bodyMedium.copy(color = subColor)
                )
            }
        }

        // Notification Permission Alert Banner if blocked in System Settings
        if (!areNotificationsEnabled) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF5A2A18)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = "Alert", tint = Color.Yellow)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isBn) "নোটিফিকেশন পারমিশন বন্ধ আছে" else "Notifications Are Disabled",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isBn) "রিমাইন্ডার নোটিফিকেশন পেতে ডিভাইসের সেটিংসে গিয়ে Darood অ্যাপের জন্য নোটিফিকেশন চালু করুন।" else "Enable notifications in device settings to receive reminders.",
                            fontSize = 12.sp,
                            color = Color(0xFFF0DFDA)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                }
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF5A2A18)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(if (isBn) "সেটিংস খুলুন" else "Open Settings", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section 1: Reminder Settings
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Reminder",
                                tint = AntiqueGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isBn) "দরুদ রিমাইন্ডার" else "Darood Reminder",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = titleColor
                                )
                            )
                        }

                        Switch(
                            checked = preferences.reminderEnabled,
                            onCheckedChange = { isChecked ->
                                if (isChecked) {
                                    onRequestNotificationPermission()
                                }
                                viewModel.toggleReminder(isChecked)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = AntiqueGold,
                                checkedTrackColor = EmeraldPrimary
                            )
                        )
                    }

                    if (preferences.reminderEnabled) {
                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = if (isDark) Color(0xFF1D4333) else Color(0xFFE4DDCE))
                        Spacer(modifier = Modifier.height(14.dp))

                        // Reminder Modes Selector (Interval, Scheduled, Prayer)
                        Text(
                            text = if (isBn) "রিমাইন্ডার মোড বেছে নিন" else "Select Reminder Mode",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = titleColor
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = preferences.reminderMode == "INTERVAL",
                                onClick = { viewModel.setReminderMode("INTERVAL") },
                                label = { Text(if (isBn) "নির্দিষ্ট বিরতি" else "Interval") },
                                shape = RoundedCornerShape(10.dp)
                            )
                            FilterChip(
                                selected = preferences.reminderMode == "SCHEDULED",
                                onClick = { viewModel.setReminderMode("SCHEDULED") },
                                label = { Text(if (isBn) "নির্ধারিত সময়" else "Scheduled") },
                                shape = RoundedCornerShape(10.dp)
                            )
                            FilterChip(
                                selected = preferences.reminderMode == "PRAYER",
                                onClick = { viewModel.setReminderMode("PRAYER") },
                                label = { Text(if (isBn) "নামাজের পর" else "Prayer") },
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        when (preferences.reminderMode) {
                            "INTERVAL" -> {
                                Text(
                                    text = if (isBn) "কতক্ষণ পরপর মনে করিয়ে দেব?" else "Reminder Interval",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = subColor)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(15 to "১৫ মি.", 30 to "৩০ মি.", 60 to "১ ঘণ্টা", 120 to "২ ঘণ্টা").forEach { (mins, label) ->
                                        val isSel = preferences.intervalMinutes == mins
                                        Button(
                                            onClick = { viewModel.setIntervalMinutes(mins) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isSel) AntiqueGold else if (isDark) Color(0xFF1B4433) else Color(0xFFEBE3D3),
                                                contentColor = if (isSel) EmeraldDark else if (isDark) SoftIvoryText else TextPrimaryDark
                                            )
                                        ) {
                                            Text(label, fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Active window
                                Text(
                                    text = if (isBn) "রিমাইন্ডার সক্রিয় সময়সীমা" else "Active Time Window",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = subColor)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${preferences.startTime}  —  ${preferences.endTime}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = AntiqueGold
                                        )
                                    )
                                    TextButton(onClick = { showTimeWindowDialog = true }) {
                                        Text(if (isBn) "বদলান" else "Change", color = AntiqueGold)
                                    }
                                }
                            }

                            "SCHEDULED" -> {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isBn) "নির্ধারিত রিমাইন্ডারের সময়গুলো:" else "Scheduled Daily Times:",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = subColor)
                                    )
                                    TextButton(onClick = { showAddTimeDialog = true }) {
                                        Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp), tint = AntiqueGold)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (isBn) "সময় যোগ করুন" else "Add Time", color = AntiqueGold, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                if (preferences.scheduledTimes.isEmpty()) {
                                    Text(
                                        text = if (isBn) "কোনো নির্ধারিত সময় নেই। '+ সময় যোগ করুন' বাটনে চাপুন।" else "No scheduled times. Tap '+ Add Time'.",
                                        style = MaterialTheme.typography.bodySmall.copy(color = subColor)
                                    )
                                } else {
                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        preferences.scheduledTimes.forEach { time ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(if (isDark) Color(0xFF1B4433) else Color(0xFFEBE3D3))
                                                    .padding(start = 10.dp, end = 6.dp, top = 4.dp, bottom = 4.dp)
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(14.dp), tint = AntiqueGold)
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = time,
                                                        style = MaterialTheme.typography.labelMedium.copy(
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isDark) SoftIvoryText else TextPrimaryDark
                                                        )
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    IconButton(
                                                        onClick = { viewModel.removeScheduledTime(time) },
                                                        modifier = Modifier.size(20.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Close,
                                                            contentDescription = "Delete",
                                                            modifier = Modifier.size(14.dp),
                                                            tint = subColor
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            "PRAYER" -> {
                                Text(
                                    text = if (isBn) "যেসব নামাজের পর স্মরণ করিয়ে দেব:" else "Remind after prayers:",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = subColor)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Column {
                                    PrayerToggleRow("ফজর (Fajr ~05:15 AM)", preferences.prayerFajr) { viewModel.setPrayerReminder("FAJR", it) }
                                    PrayerToggleRow("যোহর (Dhuhr ~12:45 PM)", preferences.prayerDhuhr) { viewModel.setPrayerReminder("DHUHR", it) }
                                    PrayerToggleRow("আসর (Asr ~04:15 PM)", preferences.prayerAsr) { viewModel.setPrayerReminder("ASR", it) }
                                    PrayerToggleRow("মাগরিব (Maghrib ~06:10 PM)", preferences.prayerMaghrib) { viewModel.setPrayerReminder("MAGHRIB", it) }
                                    PrayerToggleRow("এশা (Isha ~07:45 PM)", preferences.prayerIsha) { viewModel.setPrayerReminder("ISHA", it) }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))
                        HorizontalDivider(color = if (isDark) Color(0xFF1D4333) else Color(0xFFE4DDCE))
                        Spacer(modifier = Modifier.height(14.dp))

                        // Test Options
                        Text(
                            text = if (isBn) "রিমাইন্ডার নোটিফিকেশন পরীক্ষা করুন:" else "Test Reminders & Alarms:",
                            style = MaterialTheme.typography.labelMedium.copy(color = subColor)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    onRequestNotificationPermission()
                                    viewModel.triggerTestNotification()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Notifications, contentDescription = "Test", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isBn) "এখনই দেখুন" else "Test Now", fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    onRequestNotificationPermission()
                                    viewModel.scheduleTestAlarm(10)
                                    Toast.makeText(
                                        context,
                                        if (isBn) "১০ সেকেন্ড পর অ্যালার্ম সেট হয়েছে! অ্যাপ মিনিমাইজ বা ফোন লক করে পরীক্ষা করুন।" else "Alarm set for 10s! Lock or minimize to test.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold, contentColor = EmeraldDark)
                            ) {
                                Icon(imageVector = Icons.Default.Alarm, contentDescription = "Alarm", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isBn) "১০ সেকেন্ড টেস্ট" else "10s Alarm", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Daily Goal
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TrackChanges,
                            contentDescription = "Daily Goal",
                            tint = AntiqueGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isBn) "প্রতিদিনের লক্ষ্য" else "Daily Target",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = titleColor
                                )
                            )
                            Text(
                                text = "${preferences.dailyTarget} ${if (isBn) "বার" else "times"}",
                                style = MaterialTheme.typography.bodySmall.copy(color = subColor)
                            )
                        }
                    }

                    Button(
                        onClick = { showGoalDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AntiqueGold,
                            contentColor = EmeraldDark
                        )
                    ) {
                        Text(if (isBn) "পরিবর্তন" else "Change", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section 3: Appearance & Language
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Brightness4,
                                contentDescription = "Appearance",
                                tint = AntiqueGold,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isBn) "থিম ডিজাইন" else "Theme",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = titleColor
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = preferences.themeMode == "SYSTEM",
                            onClick = { viewModel.setThemeMode("SYSTEM") },
                            label = { Text(if (isBn) "সিস্টেম" else "System") },
                            shape = RoundedCornerShape(8.dp)
                        )
                        FilterChip(
                            selected = preferences.themeMode == "LIGHT",
                            onClick = { viewModel.setThemeMode("LIGHT") },
                            label = { Text(if (isBn) "লাইট (আইভরি)" else "Light") },
                            shape = RoundedCornerShape(8.dp)
                        )
                        FilterChip(
                            selected = preferences.themeMode == "DARK",
                            onClick = { viewModel.setThemeMode("DARK") },
                            label = { Text(if (isBn) "ডার্ক (এমারেল্ড)" else "Dark") },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = if (isDark) Color(0xFF1D4333) else Color(0xFFE4DDCE))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Language
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Language",
                                tint = AntiqueGold,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isBn) "ভাষা" else "Language",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = titleColor
                                )
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = preferences.language == "BN",
                                onClick = { viewModel.setLanguage("BN") },
                                label = { Text("বাংলা") },
                                shape = RoundedCornerShape(8.dp)
                            )
                            FilterChip(
                                selected = preferences.language == "EN",
                                onClick = { viewModel.setLanguage("EN") },
                                label = { Text("English") },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }
            }
        }

        // Section 4: Vibration & Sound
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Vibration,
                                contentDescription = "Vibration",
                                tint = AntiqueGold,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isBn) "হ্যাপটিক কম্পন (Vibration)" else "Haptic Vibration",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = titleColor
                                    )
                                )
                                Text(
                                    text = if (isBn) "দরুদ গণনার সময় মৃদু কম্পন" else "Subtle vibration on count",
                                    style = MaterialTheme.typography.bodySmall.copy(color = subColor)
                                )
                            }
                        }

                        Switch(
                            checked = preferences.vibrationEnabled,
                            onCheckedChange = { viewModel.setVibrationEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = AntiqueGold,
                                checkedTrackColor = EmeraldPrimary
                            )
                        )
                    }
                }
            }
        }

        // Section 5: Data Backup, Restore & Reset
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (isBn) "ডেটা ব্যবস্থাপনা ও ব্যাকআপ" else "Data Management",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = titleColor
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    exportJsonText = viewModel.exportDataJson()
                                    showExportDialog = true
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = "Export", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isBn) "এক্সপোর্ট" else "Export", fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                importJsonText = ""
                                showImportDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = "Import", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isBn) "ইমপোর্ট" else "Import", fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { showResetSafetyDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = "Reset", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isBn) "সকল হিস্টোরি ও ডেটা মুছে ফেলুন" else "Reset All Data")
                    }
                }
            }
        }

        // Section 6: About & Privacy
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showAboutDialog = true }) {
                            Icon(Icons.Default.Info, contentDescription = "About", tint = AntiqueGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isBn) "দরুদ অ্যাপ সম্পর্কে" else "About Darood", color = titleColor)
                        }

                        TextButton(onClick = { showPrivacyDialog = true }) {
                            Icon(Icons.Default.Security, contentDescription = "Privacy", tint = AntiqueGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isBn) "গোপনীয়তা" else "Privacy", color = titleColor)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isBn) "সংস্করণ: ১.০.০ • ১০০% অফলাইন ও ব্যক্তিগত" else "Version 1.0.0 • 100% Offline & Private",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = subColor,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun PrayerToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.bodySmall)
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = AntiqueGold)
        )
    }
}
