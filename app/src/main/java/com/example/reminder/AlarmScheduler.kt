package com.example.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.data.preferences.PreferenceManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object AlarmScheduler {
    const val REQUEST_CODE = 2001

    fun scheduleNextReminder(context: Context) {
        val prefManager = PreferenceManager(context)
        val prefs = prefManager.preferences.value

        if (!prefs.reminderEnabled) {
            cancelReminder(context)
            return
        }

        val nextTriggerMillis = calculateNextTriggerMillis(prefs) ?: return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DaroodReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    nextTriggerMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    nextTriggerMillis,
                    pendingIntent
                )
            }
        } catch (_: SecurityException) {
            // In case exact alarm permission is restricted on Android 12+, fallback to normal set
            alarmManager.set(AlarmManager.RTC_WAKEUP, nextTriggerMillis, pendingIntent)
        }
    }

    fun cancelReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DaroodReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun getNextReminderTimeString(context: Context): String {
        val prefManager = PreferenceManager(context)
        val prefs = prefManager.preferences.value
        if (!prefs.reminderEnabled) {
            return if (prefs.language == "BN") "রিমাইন্ডার বন্ধ" else "Reminder Disabled"
        }
        val nextMillis = calculateNextTriggerMillis(prefs) ?: return "--:--"
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date(nextMillis))
    }

    private fun calculateNextTriggerMillis(prefs: com.example.data.preferences.UserPreferences): Long? {
        val now = Calendar.getInstance()

        return when (prefs.reminderMode) {
            "INTERVAL" -> {
                val startParts = prefs.startTime.split(":").mapNotNull { it.toIntOrNull() }
                val endParts = prefs.endTime.split(":").mapNotNull { it.toIntOrNull() }
                val startHour = if (startParts.size >= 2) startParts[0] else 8
                val startMin = if (startParts.size >= 2) startParts[1] else 0
                val endHour = if (endParts.size >= 2) endParts[0] else 22
                val endMin = if (endParts.size >= 2) endParts[1] else 0

                val startCal = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, startHour)
                    set(Calendar.MINUTE, startMin)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                val endCal = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, endHour)
                    set(Calendar.MINUTE, endMin)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                val intervalMillis = prefs.intervalMinutes * 60 * 1000L

                if (now.before(startCal)) {
                    // Before start time today -> schedule at start time
                    startCal.timeInMillis
                } else if (now.after(endCal)) {
                    // After end time today -> schedule at start time tomorrow
                    startCal.add(Calendar.DAY_OF_YEAR, 1)
                    startCal.timeInMillis
                } else {
                    // Within today's active window
                    val nextTime = now.timeInMillis + intervalMillis
                    if (nextTime > endCal.timeInMillis) {
                        // Exceeds today's end time, wrap to tomorrow start
                        startCal.add(Calendar.DAY_OF_YEAR, 1)
                        startCal.timeInMillis
                    } else {
                        nextTime
                    }
                }
            }

            "SCHEDULED" -> {
                val scheduled = prefs.scheduledTimes.sorted()
                if (scheduled.isEmpty()) return null

                for (timeStr in scheduled) {
                    val parts = timeStr.split(":").mapNotNull { it.toIntOrNull() }
                    if (parts.size >= 2) {
                        val cal = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, parts[0])
                            set(Calendar.MINUTE, parts[1])
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        if (cal.after(now)) {
                            return cal.timeInMillis
                        }
                    }
                }

                // If all passed today, pick first time tomorrow
                val firstParts = scheduled.first().split(":").mapNotNull { it.toIntOrNull() }
                val calTomorrow = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, 1)
                    set(Calendar.HOUR_OF_DAY, firstParts.getOrElse(0) { 9 })
                    set(Calendar.MINUTE, firstParts.getOrElse(1) { 0 })
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                calTomorrow.timeInMillis
            }

            "PRAYER" -> {
                // Approximate standard daily prayer reminder times
                val prayerTimes = mutableListOf<Pair<String, Pair<Int, Int>>>()
                if (prefs.prayerFajr) prayerTimes.add("Fajr" to (5 to 15))
                if (prefs.prayerDhuhr) prayerTimes.add("Dhuhr" to (12 to 45))
                if (prefs.prayerAsr) prayerTimes.add("Asr" to (16 to 15))
                if (prefs.prayerMaghrib) prayerTimes.add("Maghrib" to (18 to 10))
                if (prefs.prayerIsha) prayerTimes.add("Isha" to (19 to 45))

                if (prayerTimes.isEmpty()) return null

                for ((_, time) in prayerTimes) {
                    val cal = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, time.first)
                        set(Calendar.MINUTE, time.second)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    if (cal.after(now)) {
                        return cal.timeInMillis
                    }
                }

                // Next day first prayer
                val firstTime = prayerTimes.first().second
                val calTomorrow = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, 1)
                    set(Calendar.HOUR_OF_DAY, firstTime.first)
                    set(Calendar.MINUTE, firstTime.second)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                calTomorrow.timeInMillis
            }

            else -> null
        }
    }
}
