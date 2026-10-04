package com.example.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.data.local.AppDatabase
import com.example.data.preferences.PreferenceManager
import com.example.data.repository.DaroodRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DaroodActionReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_INCREMENT_DAROOD = "com.example.ACTION_INCREMENT_DAROOD"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_INCREMENT_DAROOD) {
            val pendingResult = goAsync()
            val appContext = context.applicationContext

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getInstance(appContext)
                    val repo = DaroodRepository(db.dailyRecordDao())
                    val prefManager = PreferenceManager(appContext)
                    val prefs = prefManager.preferences.value

                    val updated = repo.incrementToday(delta = 1, defaultTarget = prefs.dailyTarget)

                    // Subtle vibration if enabled
                    if (prefs.vibrationEnabled) {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                val vibratorManager = appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                                vibratorManager?.defaultVibrator?.vibrate(
                                    VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE)
                                )
                            } else {
                                @Suppress("DEPRECATION")
                                val vibrator = appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    vibrator?.vibrate(
                                        VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE)
                                    )
                                } else {
                                    @Suppress("DEPRECATION")
                                    vibrator?.vibrate(45)
                                }
                            }
                        } catch (_: Exception) {}
                    }

                    // Update notification to show success feedback
                    NotificationHelper.showSuccessFeedbackNotification(appContext, updated.count)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
