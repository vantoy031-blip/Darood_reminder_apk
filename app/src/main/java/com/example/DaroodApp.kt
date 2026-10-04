package com.example

import android.app.Application
import com.example.data.preferences.PreferenceManager
import com.example.reminder.AlarmScheduler
import com.example.reminder.NotificationHelper

class DaroodApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)

        val prefManager = PreferenceManager(this)
        if (prefManager.preferences.value.reminderEnabled) {
            AlarmScheduler.scheduleNextReminder(this)
        }
    }
}
