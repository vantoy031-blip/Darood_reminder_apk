package com.example.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.preferences.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DaroodReminderReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_REMINDER_ALARM = "com.example.darood.ACTION_REMINDER_ALARM"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        val appContext = context.applicationContext

        CoroutineScope(Dispatchers.Default).launch {
            try {
                val prefs = PreferenceManager(appContext).preferences.value
                if (prefs.reminderEnabled) {
                    val title = if (prefs.language == "BN") "একটু থামুন… 🤍" else "Pause for a moment… 🤍"
                    val message = if (prefs.language == "BN") {
                        "প্রিয় নবী ﷺ-এর ওপর একবার দরুদ পড়ুন।"
                    } else {
                        "Send peace and blessings upon the beloved Prophet ﷺ."
                    }

                    NotificationHelper.showReminderNotification(appContext, title, message)

                    // Reschedule next reminder
                    AlarmScheduler.scheduleNextReminder(appContext)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
