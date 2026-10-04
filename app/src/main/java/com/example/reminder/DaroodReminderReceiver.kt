package com.example.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.preferences.PreferenceManager

class DaroodReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val prefs = PreferenceManager(context).preferences.value
        if (!prefs.reminderEnabled) return

        val title = if (prefs.language == "BN") "একটু থামুন… 🤍" else "Pause for a moment… 🤍"
        val message = if (prefs.language == "BN") {
            "প্রিয় নবী ﷺ-এর ওপর একবার দরুদ পড়ুন।"
        } else {
            "Send peace and blessings upon the beloved Prophet ﷺ."
        }

        NotificationHelper.showReminderNotification(context, title, message)

        // Reschedule next
        AlarmScheduler.scheduleNextReminder(context)
    }
}
