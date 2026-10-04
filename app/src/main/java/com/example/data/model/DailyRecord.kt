package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_records")
data class DailyRecord(
    @PrimaryKey
    val date: String, // Format: YYYY-MM-DD
    val count: Int = 0, // Daily Durood count
    val target: Int = 500, // Daily Durood target
    val sessionCount: Int = 0, // Current Durood session lap
    val tasbihCount: Int = 0, // Daily Tasbih count (counted separately from Durood)
    val tasbihSessionCount: Int = 0, // Current Tasbih session lap
    val selectedDhikrId: String = "subhanallah", // Selected Dhikr in Tasbih mode
    val completedGoal: Boolean = false,
    val lastUpdatedMillis: Long = System.currentTimeMillis()
)
