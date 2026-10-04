package com.example.ui

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.DaroodAudioPlayer
import com.example.data.local.AppDatabase
import com.example.data.model.DailyRecord
import com.example.data.preferences.PreferenceManager
import com.example.data.preferences.UserPreferences
import com.example.data.repository.DaroodRepository
import com.example.data.repository.StatisticsData
import com.example.data.repository.WeeklyBarData
import com.example.reminder.AlarmScheduler
import com.example.reminder.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private val database = AppDatabase.getInstance(context)
    val repository = DaroodRepository(database.dailyRecordDao())
    val prefManager = PreferenceManager(context)
    val audioPlayer = DaroodAudioPlayer(context)

    val preferences: StateFlow<UserPreferences> = prefManager.preferences

    val todayRecord: StateFlow<DailyRecord> = repository.getTodayRecord(
        target = prefManager.preferences.value.dailyTarget
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DailyRecord(date = repository.getTodayDateString(), count = 0, target = prefManager.preferences.value.dailyTarget)
    )

    val allRecords: StateFlow<List<DailyRecord>> = repository.allRecords.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _statistics = MutableStateFlow(StatisticsData())
    val statistics: StateFlow<StatisticsData> = _statistics.asStateFlow()

    private val _weeklyBarData = MutableStateFlow<List<WeeklyBarData>>(emptyList())
    val weeklyBarData: StateFlow<List<WeeklyBarData>> = _weeklyBarData.asStateFlow()

    private val _nextReminderTime = MutableStateFlow(AlarmScheduler.getNextReminderTimeString(context))
    val nextReminderTime: StateFlow<String> = _nextReminderTime.asStateFlow()

    init {
        viewModelScope.launch {
            allRecords.collect { records ->
                val stats = repository.calculateStats(records)
                _statistics.value = stats
                val barData = repository.getWeeklyBarData(records.associateBy { it.date })
                _weeklyBarData.value = barData
            }
        }

        viewModelScope.launch {
            preferences.collect {
                _nextReminderTime.value = AlarmScheduler.getNextReminderTimeString(context)
            }
        }
    }

    fun incrementToday(delta: Int = 1) {
        viewModelScope.launch {
            repository.incrementToday(delta, defaultTarget = preferences.value.dailyTarget)
            triggerHapticFeedback()
        }
    }

    fun undoToday() {
        viewModelScope.launch {
            repository.undoToday()
            triggerHapticFeedback()
        }
    }

    fun resetSession() {
        viewModelScope.launch {
            repository.resetSession()
            triggerHapticFeedback()
        }
    }

    fun updateTarget(newTarget: Int) {
        prefManager.updateDailyTarget(newTarget)
        viewModelScope.launch {
            repository.updateTarget(newTarget)
        }
    }

    fun toggleReminder(enabled: Boolean) {
        prefManager.setReminderEnabled(enabled)
        if (enabled) {
            AlarmScheduler.scheduleNextReminder(context)
        } else {
            AlarmScheduler.cancelReminder(context)
        }
        _nextReminderTime.value = AlarmScheduler.getNextReminderTimeString(context)
    }

    fun setReminderMode(mode: String) {
        prefManager.setReminderMode(mode)
        AlarmScheduler.scheduleNextReminder(context)
        _nextReminderTime.value = AlarmScheduler.getNextReminderTimeString(context)
    }

    fun setIntervalMinutes(minutes: Int) {
        prefManager.setIntervalMinutes(minutes)
        AlarmScheduler.scheduleNextReminder(context)
        _nextReminderTime.value = AlarmScheduler.getNextReminderTimeString(context)
    }

    fun setTimeWindow(start: String, end: String) {
        prefManager.setTimeWindow(start, end)
        AlarmScheduler.scheduleNextReminder(context)
        _nextReminderTime.value = AlarmScheduler.getNextReminderTimeString(context)
    }

    fun setScheduledTimes(times: List<String>) {
        prefManager.setScheduledTimes(times)
        AlarmScheduler.scheduleNextReminder(context)
        _nextReminderTime.value = AlarmScheduler.getNextReminderTimeString(context)
    }

    fun addScheduledTime(time: String) {
        val current = preferences.value.scheduledTimes.toMutableList()
        if (!current.contains(time)) {
            current.add(time)
            current.sort()
            setScheduledTimes(current)
        }
    }

    fun removeScheduledTime(time: String) {
        val current = preferences.value.scheduledTimes.toMutableList()
        if (current.remove(time)) {
            setScheduledTimes(current)
        }
    }

    fun scheduleTestAlarm(seconds: Int = 10) {
        AlarmScheduler.scheduleTestReminderInSeconds(context, seconds)
        triggerHapticFeedback()
    }

    fun setPrayerReminder(prayer: String, enabled: Boolean) {
        prefManager.setPrayerReminder(prayer, enabled)
        AlarmScheduler.scheduleNextReminder(context)
        _nextReminderTime.value = AlarmScheduler.getNextReminderTimeString(context)
    }

    fun setVibrationEnabled(enabled: Boolean) {
        prefManager.setVibrationEnabled(enabled)
    }

    fun setSoundEnabled(enabled: Boolean) {
        prefManager.setSoundEnabled(enabled)
    }

    fun setThemeMode(mode: String) {
        prefManager.setThemeMode(mode)
    }

    fun setLanguage(lang: String) {
        prefManager.setLanguage(lang)
    }

    fun finishOnboarding(target: Int, intervalMinutes: Int, start: String, end: String) {
        prefManager.updateDailyTarget(target)
        prefManager.setIntervalMinutes(intervalMinutes)
        prefManager.setTimeWindow(start, end)
        prefManager.setOnboardingCompleted(true)
        AlarmScheduler.scheduleNextReminder(context)
        _nextReminderTime.value = AlarmScheduler.getNextReminderTimeString(context)
        viewModelScope.launch {
            repository.updateTarget(target)
        }
    }

    fun triggerTestNotification() {
        val prefs = preferences.value
        val title = if (prefs.language == "BN") "একটু থামুন… 🤍" else "Pause for a moment… 🤍"
        val message = if (prefs.language == "BN") {
            "প্রিয় নবী ﷺ-এর ওপর একবার দরুদ পড়ুন।"
        } else {
            "Send peace and blessings upon the beloved Prophet ﷺ."
        }
        NotificationHelper.showReminderNotification(context, title, message)
        triggerHapticFeedback()
    }

    suspend fun exportDataJson(): String {
        return repository.exportToJson()
    }

    fun importDataJson(json: String, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.importFromJson(json)
            result.onSuccess { count ->
                val msg = if (preferences.value.language == "BN") {
                    "$count টি দিনের তথ্য সফলভাবে পুনরুদ্ধার করা হয়েছে।"
                } else {
                    "Successfully imported $count days of records."
                }
                onComplete(true, msg)
            }.onFailure { err ->
                val msg = if (preferences.value.language == "BN") {
                    "তথ্য পুনরুদ্ধার ব্যর্থ হয়েছে: ${err.localizedMessage}"
                } else {
                    "Import failed: ${err.localizedMessage}"
                }
                onComplete(false, msg)
            }
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.deleteAllData()
            prefManager.resetPreferences()
            AlarmScheduler.cancelReminder(context)
            _nextReminderTime.value = AlarmScheduler.getNextReminderTimeString(context)
        }
    }

    fun triggerHapticFeedback() {
        if (!preferences.value.vibrationEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(
                        VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(35)
                }
            }
        } catch (_: Exception) {}
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stop()
    }
}
