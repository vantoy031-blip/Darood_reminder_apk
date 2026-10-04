package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SoftIvoryMuted
import com.example.ui.theme.SoftIvoryText
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.WarmIvoryCard
import com.example.ui.theme.WarmIvoryCardBorder

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GoalDialog(
    currentTarget: Int,
    language: String = "BN",
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var selectedTarget by remember { mutableStateOf(currentTarget) }
    var customInput by remember { mutableStateOf("") }
    val presets = listOf(100, 300, 500, 1000)
    val isDark = isSystemInDarkTheme()

    val cardBg = if (isDark) DarkCard else WarmIvoryCard
    val titleColor = if (isDark) SoftIvoryText else TextPrimaryDark
    val textColor = if (isDark) SoftIvoryMuted else TextSecondaryDark

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, if (isDark) DarkCardBorder else WarmIvoryCardBorder),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (language == "BN") "আজকের দরুদের লক্ষ্য" else "Today's Darood Goal",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (language == "BN") "একটি বাস্তবসম্মত ও আন্তরিক লক্ষ্য নির্ধারণ করুন" else "Set a realistic and sincere daily target",
                    style = MaterialTheme.typography.bodyMedium.copy(color = textColor),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presets.forEach { preset ->
                        val isSelected = selectedTarget == preset && customInput.isEmpty()
                        Button(
                            onClick = {
                                selectedTarget = preset
                                customInput = ""
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) AntiqueGold else if (isDark) Color(0xFF1B4433) else Color(0xFFEBE3D3),
                                contentColor = if (isSelected) EmeraldDark else if (isDark) SoftIvoryText else TextPrimaryDark
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "$preset",
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = customInput,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() } && input.length <= 5) {
                            customInput = input
                            input.toIntOrNull()?.let { selectedTarget = it }
                        }
                    },
                    label = { Text(if (language == "BN") "কাস্টম লক্ষ্য সংখ্যা" else "Custom Target") },
                    placeholder = { Text("যেমন: ২৫০") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = if (language == "BN") "বাতিল" else "Cancel",
                            color = textColor
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val finalTarget = customInput.toIntOrNull() ?: selectedTarget
                            if (finalTarget > 0) {
                                onSave(finalTarget)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AntiqueGold,
                            contentColor = EmeraldDark
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (language == "BN") "সংরক্ষণ করুন" else "Save Target",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
