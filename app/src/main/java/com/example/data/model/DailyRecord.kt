package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_records")
data class DailyRecord(
    @PrimaryKey
    val date: String, // Format: YYYY-MM-DD
    val count: Int = 0,
    val target: Int = 500,
    val sessionCount: Int = 0,
    val completedGoal: Boolean = false,
    val lastUpdatedMillis: Long = System.currentTimeMillis()
)
