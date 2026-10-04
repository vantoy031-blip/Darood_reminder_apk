package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.screens.CounterScreen
import com.example.ui.screens.DaroodVirtuesScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.AntiqueGoldLight
import com.example.ui.theme.DaroodTheme
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCard
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SoftIvoryMuted
import com.example.ui.theme.SoftIvoryText
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.WarmIvoryBackground
import com.example.ui.theme.WarmIvorySurface

enum class AppTab {
    HOME, COUNTER, HISTORY, SETTINGS, DAROOD_DETAILS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel()
            val preferences by viewModel.preferences.collectAsState()

            val isSystemDark = isSystemInDarkTheme()
            val isDarkTheme = when (preferences.themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> isSystemDark
            }

            DaroodTheme(darkTheme = isDarkTheme) {
                DaroodAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun DaroodAppContent(viewModel: MainViewModel) {
    val preferences by viewModel.preferences.collectAsState()
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current

    var selectedTab by remember { mutableStateOf(AppTab.HOME) }

    // Permission launcher for Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.toggleReminder(true)
        }
    }

    val requestNotificationPermission = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    if (!preferences.onboardingCompleted) {
        OnboardingScreen(
            onFinish = { target, interval, start, end ->
                viewModel.finishOnboarding(target, interval, start, end)
            },
            onRequestNotificationPermission = requestNotificationPermission
        )
    } else {
        BackHandler(enabled = selectedTab != AppTab.HOME) {
            selectedTab = AppTab.HOME
        }

        val isBn = preferences.language == "BN"

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            containerColor = if (isDark) DarkBackground else WarmIvoryBackground,
            bottomBar = {
                if (selectedTab != AppTab.DAROOD_DETAILS) {
                    NavigationBar(
                        modifier = Modifier
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .testTag("bottom_nav"),
                        containerColor = if (isDark) DarkCard else WarmIvorySurface,
                        tonalElevation = 6.dp
                    ) {
                        NavigationBarItem(
                            selected = selectedTab == AppTab.HOME,
                            onClick = { selectedTab = AppTab.HOME },
                            icon = {
                                Icon(
                                    imageVector = if (selectedTab == AppTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                    contentDescription = "Home"
                                )
                            },
                            label = { Text(if (isBn) "হোম" else "Home", fontSize = 12.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = EmeraldDark,
                                selectedTextColor = if (isDark) AntiqueGoldLight else EmeraldPrimary,
                                indicatorColor = AntiqueGold,
                                unselectedIconColor = if (isDark) SoftIvoryMuted else TextSecondaryDark,
                                unselectedTextColor = if (isDark) SoftIvoryMuted else TextSecondaryDark
                            )
                        )

                        NavigationBarItem(
                            selected = selectedTab == AppTab.COUNTER,
                            onClick = { selectedTab = AppTab.COUNTER },
                            icon = {
                                Icon(
                                    imageVector = if (selectedTab == AppTab.COUNTER) Icons.Filled.Repeat else Icons.Outlined.Repeat,
                                    contentDescription = "Counter"
                                )
                            },
                            label = { Text(if (isBn) "তাসবীহ" else "Counter", fontSize = 12.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = EmeraldDark,
                                selectedTextColor = if (isDark) AntiqueGoldLight else EmeraldPrimary,
                                indicatorColor = AntiqueGold,
                                unselectedIconColor = if (isDark) SoftIvoryMuted else TextSecondaryDark,
                                unselectedTextColor = if (isDark) SoftIvoryMuted else TextSecondaryDark
                            )
                        )

                        NavigationBarItem(
                            selected = selectedTab == AppTab.HISTORY,
                            onClick = { selectedTab = AppTab.HISTORY },
                            icon = {
                                Icon(
                                    imageVector = if (selectedTab == AppTab.HISTORY) Icons.Filled.BarChart else Icons.Outlined.BarChart,
                                    contentDescription = "History"
                                )
                            },
                            label = { Text(if (isBn) "ইতিহাস" else "History", fontSize = 12.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = EmeraldDark,
                                selectedTextColor = if (isDark) AntiqueGoldLight else EmeraldPrimary,
                                indicatorColor = AntiqueGold,
                                unselectedIconColor = if (isDark) SoftIvoryMuted else TextSecondaryDark,
                                unselectedTextColor = if (isDark) SoftIvoryMuted else TextSecondaryDark
                            )
                        )

                        NavigationBarItem(
                            selected = selectedTab == AppTab.SETTINGS,
                            onClick = { selectedTab = AppTab.SETTINGS },
                            icon = {
                                Icon(
                                    imageVector = if (selectedTab == AppTab.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                                    contentDescription = "Settings"
                                )
                            },
                            label = { Text(if (isBn) "সেটিংস" else "Settings", fontSize = 12.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = EmeraldDark,
                                selectedTextColor = if (isDark) AntiqueGoldLight else EmeraldPrimary,
                                indicatorColor = AntiqueGold,
                                unselectedIconColor = if (isDark) SoftIvoryMuted else TextSecondaryDark,
                                unselectedTextColor = if (isDark) SoftIvoryMuted else TextSecondaryDark
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "TabContentAnimation"
                ) { tab ->
                    when (tab) {
                        AppTab.HOME -> HomeScreen(
                            viewModel = viewModel,
                            onNavigateToSettings = { selectedTab = AppTab.SETTINGS },
                            onNavigateToDaroodDetails = { selectedTab = AppTab.DAROOD_DETAILS }
                        )
                        AppTab.COUNTER -> CounterScreen(
                            viewModel = viewModel
                        )
                        AppTab.HISTORY -> HistoryScreen(
                            viewModel = viewModel
                        )
                        AppTab.SETTINGS -> SettingsScreen(
                            viewModel = viewModel,
                            onRequestNotificationPermission = requestNotificationPermission
                        )
                        AppTab.DAROOD_DETAILS -> DaroodVirtuesScreen(
                            viewModel = viewModel,
                            onBack = { selectedTab = AppTab.HOME }
                        )
                    }
                }
            }
        }
    }
}
