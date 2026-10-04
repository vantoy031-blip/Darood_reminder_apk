package com.example.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.data.preferences.PreferenceManager
import com.example.data.preferences.UserPreferences
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object AlarmScheduler {
    const val REQUEST_CODE = 2001
    private const val TAG = "AlarmScheduler"

    fun canScheduleExactAlarms(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            return alarmManager?.canScheduleExactAlarms() ?: false
        }
        return true
    }

    fun scheduleNextReminder(context: Context) {
        val prefManager = PreferenceManager(context)
        val prefs = prefManager.preferences.value

        if (!prefs.reminderEnabled) {
            cancelReminder(context)
            return
        }

        val nextTriggerMillis = calculateNextTriggerMillis(prefs) ?: return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val intent = Intent(context, DaroodReminderReceiver::class.java).apply {
            action = DaroodReminderReceiver.ACTION_REMINDER_ALARM
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            // First priority: Use setAlarmClock for highest reliability and wake from Doze/battery saving
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()) {
                    val showIntent = Intent(context, com.example.MainActivity::class.java)
                    val showPending = PendingIntent.getActivity(
                        context,
                        0,
                        showIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    alarmManager.setAlarmClock(
                        AlarmManager.AlarmClockInfo(nextTriggerMillis, showPending),
                        pendingIntent
                    )
                    Log.d(TAG, "Scheduled AlarmClock at $nextTriggerMillis")
                    return
                }
            }

            // Fallback for exact or allow while idle
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        nextTriggerMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        nextTriggerMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    nextTriggerMillis,
                    pendingIntent
                )
            }
            Log.d(TAG, "Scheduled alarm at $nextTriggerMillis")
        } catch (e: SecurityException) {
            Log.w(TAG, "Exact alarm permission restricted, falling back to setAndAllowWhileIdle", e)
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        nextTriggerMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, nextTriggerMillis, pendingIntent)
                }
            } catch (ex: Exception) {
                Log.e(TAG, "Failed to schedule alarm", ex)
            }
        }
    }

    fun scheduleTestReminderInSeconds(context: Context, delaySeconds: Int = 10) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val triggerAt = System.currentTimeMillis() + (delaySeconds * 1000L)

        val intent = Intent(context, DaroodReminderReceiver::class.java).apply {
            action = DaroodReminderReceiver.ACTION_REMINDER_ALARM
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE + 1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()) {
                    val showIntent = Intent(context, com.example.MainActivity::class.java)
                    val showPending = PendingIntent.getActivity(
                        context,
                        0,
                        showIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    alarmManager.setAlarmClock(
                        AlarmManager.AlarmClockInfo(triggerAt, showPending),
                        pendingIntent
                    )
                    return
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
                } else {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
                }
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
            }
        } catch (_: Exception) {
            NotificationHelper.showReminderNotification(context)
        }
    }

    fun cancelReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, DaroodReminderReceiver::class.java).apply {
            action = DaroodReminderReceiver.ACTION_REMINDER_ALARM
        }
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

    fun calculateNextTriggerMillis(prefs: UserPreferences): Long? {
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
                    // Before active start time today -> schedule for today's start time
                    startCal.timeInMillis
                } else if (now.after(endCal)) {
                    // Past active end time today -> schedule for tomorrow's start time
                    startCal.add(Calendar.DAY_OF_YEAR, 1)
                    startCal.timeInMillis
                } else {
                    // Within today's active window
                    val nextTime = now.timeInMillis + intervalMillis
                    if (nextTime > endCal.timeInMillis) {
                        // Would exceed today's end window, wrap to tomorrow start
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

                // Look for the next upcoming scheduled time today (at least 10 seconds in the future)
                for (timeStr in scheduled) {
                    val parts = timeStr.split(":").mapNotNull { it.toIntOrNull() }
                    if (parts.size >= 2) {
                        val cal = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, parts[0])
                            set(Calendar.MINUTE, parts[1])
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        if (cal.timeInMillis > (now.timeInMillis + 10_000L)) {
                            return cal.timeInMillis
                        }
                    }
                }

                // If all scheduled times for today have passed, pick the earliest scheduled time tomorrow
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
                    if (cal.timeInMillis > (now.timeInMillis + 10_000L)) {
                        return cal.timeInMillis
                    }
                }

                // Earliest prayer tomorrow
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
