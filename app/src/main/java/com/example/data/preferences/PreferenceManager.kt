package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserPreferences(
    val dailyTarget: Int = 500,
    val reminderEnabled: Boolean = true,
    val reminderMode: String = "INTERVAL", // INTERVAL, SCHEDULED, PRAYER
    val intervalMinutes: Int = 30,
    val startTime: String = "08:00",
    val endTime: String = "22:00",
    val scheduledTimes: List<String> = listOf("09:00", "12:00", "15:00", "18:00", "21:00"),
    val prayerFajr: Boolean = true,
    val prayerDhuhr: Boolean = true,
    val prayerAsr: Boolean = true,
    val prayerMaghrib: Boolean = true,
    val prayerIsha: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val themeMode: String = "SYSTEM", // SYSTEM, LIGHT, DARK
    val language: String = "BN", // BN, EN
    val onboardingCompleted: Boolean = false,
    val selectedDaroodId: String = "mukhtasar"
)

class PreferenceManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("darood_prefs", Context.MODE_PRIVATE)

    private val _preferences = MutableStateFlow(loadPreferences())
    val preferences: StateFlow<UserPreferences> = _preferences.asStateFlow()

    private fun loadPreferences(): UserPreferences {
        val scheduledRaw = prefs.getString(KEY_SCHEDULED_TIMES, "09:00,12:00,15:00,18:00,21:00") ?: "09:00,12:00,15:00,18:00,21:00"
        val scheduledList = scheduledRaw.split(",").map { it.trim() }.filter { it.isNotEmpty() }

        return UserPreferences(
            dailyTarget = prefs.getInt(KEY_DAILY_TARGET, 500),
            reminderEnabled = prefs.getBoolean(KEY_REMINDER_ENABLED, true),
            reminderMode = prefs.getString(KEY_REMINDER_MODE, "INTERVAL") ?: "INTERVAL",
            intervalMinutes = prefs.getInt(KEY_INTERVAL_MINUTES, 30),
            startTime = prefs.getString(KEY_START_TIME, "08:00") ?: "08:00",
            endTime = prefs.getString(KEY_END_TIME, "22:00") ?: "22:00",
            scheduledTimes = scheduledList,
            prayerFajr = prefs.getBoolean(KEY_PRAYER_FAJR, true),
            prayerDhuhr = prefs.getBoolean(KEY_PRAYER_DHUHR, true),
            prayerAsr = prefs.getBoolean(KEY_PRAYER_ASR, true),
            prayerMaghrib = prefs.getBoolean(KEY_PRAYER_MAGHRIB, true),
            prayerIsha = prefs.getBoolean(KEY_PRAYER_ISHA, true),
            vibrationEnabled = prefs.getBoolean(KEY_VIBRATION, true),
            soundEnabled = prefs.getBoolean(KEY_SOUND, true),
            themeMode = prefs.getString(KEY_THEME_MODE, "SYSTEM") ?: "SYSTEM",
            language = prefs.getString(KEY_LANGUAGE, "BN") ?: "BN",
            onboardingCompleted = prefs.getBoolean(KEY_ONBOARDING_DONE, false),
            selectedDaroodId = prefs.getString(KEY_SELECTED_DAROOD, "mukhtasar") ?: "mukhtasar"
        )
    }

    fun updateDailyTarget(target: Int) {
        prefs.edit().putInt(KEY_DAILY_TARGET, target).apply()
        _preferences.value = _preferences.value.copy(dailyTarget = target)
    }

    fun setReminderEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_REMINDER_ENABLED, enabled).apply()
        _preferences.value = _preferences.value.copy(reminderEnabled = enabled)
    }

    fun setReminderMode(mode: String) {
        prefs.edit().putString(KEY_REMINDER_MODE, mode).apply()
        _preferences.value = _preferences.value.copy(reminderMode = mode)
    }

    fun setIntervalMinutes(minutes: Int) {
        prefs.edit().putInt(KEY_INTERVAL_MINUTES, minutes).apply()
        _preferences.value = _preferences.value.copy(intervalMinutes = minutes)
    }

    fun setTimeWindow(start: String, end: String) {
        prefs.edit()
            .putString(KEY_START_TIME, start)
            .putString(KEY_END_TIME, end)
            .apply()
        _preferences.value = _preferences.value.copy(startTime = start, endTime = end)
    }

    fun setScheduledTimes(times: List<String>) {
        val joined = times.joinToString(",")
        prefs.edit().putString(KEY_SCHEDULED_TIMES, joined).apply()
        _preferences.value = _preferences.value.copy(scheduledTimes = times)
    }

    fun setPrayerReminder(prayer: String, enabled: Boolean) {
        val editor = prefs.edit()
        when (prayer) {
            "FAJR" -> editor.putBoolean(KEY_PRAYER_FAJR, enabled)
            "DHUHR" -> editor.putBoolean(KEY_PRAYER_DHUHR, enabled)
            "ASR" -> editor.putBoolean(KEY_PRAYER_ASR, enabled)
            "MAGHRIB" -> editor.putBoolean(KEY_PRAYER_MAGHRIB, enabled)
            "ISHA" -> editor.putBoolean(KEY_PRAYER_ISHA, enabled)
        }
        editor.apply()
        _preferences.value = when (prayer) {
            "FAJR" -> _preferences.value.copy(prayerFajr = enabled)
            "DHUHR" -> _preferences.value.copy(prayerDhuhr = enabled)
            "ASR" -> _preferences.value.copy(prayerAsr = enabled)
            "MAGHRIB" -> _preferences.value.copy(prayerMaghrib = enabled)
            "ISHA" -> _preferences.value.copy(prayerIsha = enabled)
            else -> _preferences.value
        }
    }

    fun setVibrationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_VIBRATION, enabled).apply()
        _preferences.value = _preferences.value.copy(vibrationEnabled = enabled)
    }

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND, enabled).apply()
        _preferences.value = _preferences.value.copy(soundEnabled = enabled)
    }

    fun setThemeMode(mode: String) {
        prefs.edit().putString(KEY_THEME_MODE, mode).apply()
        _preferences.value = _preferences.value.copy(themeMode = mode)
    }

    fun setLanguage(lang: String) {
        prefs.edit().putString(KEY_LANGUAGE, lang).apply()
        _preferences.value = _preferences.value.copy(language = lang)
    }

    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean(KEY_ONBOARDING_DONE, completed).apply()
        _preferences.value = _preferences.value.copy(onboardingCompleted = completed)
    }

    fun setSelectedDaroodId(id: String) {
        prefs.edit().putString(KEY_SELECTED_DAROOD, id).apply()
        _preferences.value = _preferences.value.copy(selectedDaroodId = id)
    }

    fun resetPreferences() {
        prefs.edit().clear().apply()
        _preferences.value = loadPreferences()
    }

    companion object {
        private const val KEY_DAILY_TARGET = "daily_target"
        private const val KEY_REMINDER_ENABLED = "reminder_enabled"
        private const val KEY_REMINDER_MODE = "reminder_mode"
        private const val KEY_INTERVAL_MINUTES = "interval_minutes"
        private const val KEY_START_TIME = "start_time"
        private const val KEY_END_TIME = "end_time"
        private const val KEY_SCHEDULED_TIMES = "scheduled_times"
        private const val KEY_PRAYER_FAJR = "prayer_fajr"
        private const val KEY_PRAYER_DHUHR = "prayer_dhuhr"
        private const val KEY_PRAYER_ASR = "prayer_asr"
        private const val KEY_PRAYER_MAGHRIB = "prayer_maghrib"
        private const val KEY_PRAYER_ISHA = "prayer_isha"
        private const val KEY_VIBRATION = "vibration_enabled"
        private const val KEY_SOUND = "sound_enabled"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_ONBOARDING_DONE = "onboarding_done"
        private const val KEY_SELECTED_DAROOD = "selected_darood"
    }
}
