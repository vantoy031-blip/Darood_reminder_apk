package com.example.data.repository

import com.example.data.local.DailyRecordDao
import com.example.data.model.DailyRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class WeeklyBarData(
    val dayKey: String, // "SAT", "SUN", etc.
    val dayLabelBn: String, // "শনিবার", etc.
    val dayLabelEn: String, // "Saturday", etc.
    val dateString: String, // "YYYY-MM-DD"
    val count: Int, // Durood count
    val tasbihCount: Int = 0, // Tasbih count
    val isToday: Boolean
)

data class StatisticsData(
    val todayTotal: Int = 0, // Today's Durood total
    val weeklyTotal: Int = 0, // Weekly Durood total
    val monthlyTotal: Int = 0, // Monthly Durood total
    val todayTasbihTotal: Int = 0, // Today's Tasbih total (separate)
    val weeklyTasbihTotal: Int = 0, // Weekly Tasbih total (separate)
    val monthlyTasbihTotal: Int = 0, // Monthly Tasbih total (separate)
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val yesterdayCount: Int = 0,
    val last30DaysCount: Int = 0
)

class DaroodRepository(
    private val dao: DailyRecordDao
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun getTodayDateString(): String = dateFormat.format(Date())

    fun getTodayRecord(target: Int = 500): Flow<DailyRecord> {
        val today = getTodayDateString()
        return dao.getRecordByDate(today).map { record ->
            record ?: DailyRecord(date = today, count = 0, target = target, tasbihCount = 0)
        }
    }

    val allRecords: Flow<List<DailyRecord>> = dao.getAllRecords()

    suspend fun incrementToday(delta: Int = 1, defaultTarget: Int = 500): DailyRecord = withContext(Dispatchers.IO) {
        val today = getTodayDateString()
        val current = dao.getRecordByDateSync(today) ?: DailyRecord(date = today, count = 0, target = defaultTarget)
        val newCount = (current.count + delta).coerceAtLeast(0)
        val newSession = (current.sessionCount + delta).coerceAtLeast(0)
        val completed = newCount >= current.target

        val updated = current.copy(
            count = newCount,
            sessionCount = newSession,
            completedGoal = completed,
            lastUpdatedMillis = System.currentTimeMillis()
        )
        dao.insertOrUpdate(updated)
        updated
    }

    suspend fun undoToday(): DailyRecord? = withContext(Dispatchers.IO) {
        val today = getTodayDateString()
        val current = dao.getRecordByDateSync(today) ?: return@withContext null
        if (current.count <= 0) return@withContext current

        val newCount = (current.count - 1).coerceAtLeast(0)
        val newSession = (current.sessionCount - 1).coerceAtLeast(0)
        val completed = newCount >= current.target

        val updated = current.copy(
            count = newCount,
            sessionCount = newSession,
            completedGoal = completed,
            lastUpdatedMillis = System.currentTimeMillis()
        )
        dao.insertOrUpdate(updated)
        updated
    }

    suspend fun resetSession(): DailyRecord? = withContext(Dispatchers.IO) {
        val today = getTodayDateString()
        val current = dao.getRecordByDateSync(today) ?: return@withContext null
        val updated = current.copy(
            sessionCount = 0,
            lastUpdatedMillis = System.currentTimeMillis()
        )
        dao.insertOrUpdate(updated)
        updated
    }

    // Separate Tasbih counting operations
    suspend fun incrementTasbih(dhikrId: String, delta: Int = 1, defaultTarget: Int = 500): DailyRecord = withContext(Dispatchers.IO) {
        val today = getTodayDateString()
        val current = dao.getRecordByDateSync(today) ?: DailyRecord(date = today, count = 0, target = defaultTarget)

        val updated = if (dhikrId == "durood") {
            val newCount = (current.count + delta).coerceAtLeast(0)
            val newSession = (current.sessionCount + delta).coerceAtLeast(0)
            current.copy(
                count = newCount,
                sessionCount = newSession,
                selectedDhikrId = dhikrId,
                completedGoal = newCount >= current.target,
                lastUpdatedMillis = System.currentTimeMillis()
            )
        } else {
            // Counted separately under tasbihCount
            val newTasbihCount = (current.tasbihCount + delta).coerceAtLeast(0)
            val newTasbihSession = (current.tasbihSessionCount + delta).coerceAtLeast(0)
            current.copy(
                tasbihCount = newTasbihCount,
                tasbihSessionCount = newTasbihSession,
                selectedDhikrId = dhikrId,
                lastUpdatedMillis = System.currentTimeMillis()
            )
        }

        dao.insertOrUpdate(updated)
        updated
    }

    suspend fun undoTasbih(dhikrId: String): DailyRecord? = withContext(Dispatchers.IO) {
        val today = getTodayDateString()
        val current = dao.getRecordByDateSync(today) ?: return@withContext null

        val updated = if (dhikrId == "durood") {
            if (current.count <= 0) return@withContext current
            val newCount = (current.count - 1).coerceAtLeast(0)
            val newSession = (current.sessionCount - 1).coerceAtLeast(0)
            current.copy(
                count = newCount,
                sessionCount = newSession,
                completedGoal = newCount >= current.target,
                lastUpdatedMillis = System.currentTimeMillis()
            )
        } else {
            if (current.tasbihCount <= 0) return@withContext current
            val newTasbihCount = (current.tasbihCount - 1).coerceAtLeast(0)
            val newTasbihSession = (current.tasbihSessionCount - 1).coerceAtLeast(0)
            current.copy(
                tasbihCount = newTasbihCount,
                tasbihSessionCount = newTasbihSession,
                lastUpdatedMillis = System.currentTimeMillis()
            )
        }

        dao.insertOrUpdate(updated)
        updated
    }

    suspend fun resetTasbihSession(): DailyRecord? = withContext(Dispatchers.IO) {
        val today = getTodayDateString()
        val current = dao.getRecordByDateSync(today) ?: return@withContext null
        val updated = current.copy(
            tasbihSessionCount = 0,
            lastUpdatedMillis = System.currentTimeMillis()
        )
        dao.insertOrUpdate(updated)
        updated
    }

    suspend fun setSelectedDhikr(dhikrId: String) = withContext(Dispatchers.IO) {
        val today = getTodayDateString()
        val current = dao.getRecordByDateSync(today) ?: DailyRecord(date = today)
        val updated = current.copy(
            selectedDhikrId = dhikrId,
            tasbihSessionCount = 0,
            lastUpdatedMillis = System.currentTimeMillis()
        )
        dao.insertOrUpdate(updated)
    }

    suspend fun updateTarget(newTarget: Int) = withContext(Dispatchers.IO) {
        val today = getTodayDateString()
        val current = dao.getRecordByDateSync(today) ?: DailyRecord(date = today, count = 0, target = newTarget)
        val updated = current.copy(
            target = newTarget,
            completedGoal = current.count >= newTarget,
            lastUpdatedMillis = System.currentTimeMillis()
        )
        dao.insertOrUpdate(updated)
    }

    suspend fun calculateStats(allRecords: List<DailyRecord>): StatisticsData = withContext(Dispatchers.Default) {
        val todayStr = getTodayDateString()
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayStr = dateFormat.format(cal.time)

        val recordMap = allRecords.associateBy { it.date }

        val todayTotal = recordMap[todayStr]?.count ?: 0
        val todayTasbihTotal = recordMap[todayStr]?.tasbihCount ?: 0
        val yesterdayCount = recordMap[yesterdayStr]?.count ?: 0

        // Weekly totals (last 7 days)
        var weeklyTotal = 0
        var weeklyTasbihTotal = 0
        val cal7 = Calendar.getInstance()
        for (i in 0 until 7) {
            val dStr = dateFormat.format(cal7.time)
            weeklyTotal += recordMap[dStr]?.count ?: 0
            weeklyTasbihTotal += recordMap[dStr]?.tasbihCount ?: 0
            cal7.add(Calendar.DAY_OF_YEAR, -1)
        }

        // Monthly totals (this month)
        val calMonth = Calendar.getInstance()
        val currentMonth = calMonth.get(Calendar.MONTH)
        val currentYear = calMonth.get(Calendar.YEAR)
        var monthlyTotal = 0
        var monthlyTasbihTotal = 0
        for (r in allRecords) {
            try {
                val d = dateFormat.parse(r.date)
                if (d != null) {
                    val c = Calendar.getInstance().apply { time = d }
                    if (c.get(Calendar.MONTH) == currentMonth && c.get(Calendar.YEAR) == currentYear) {
                        monthlyTotal += r.count
                        monthlyTasbihTotal += r.tasbihCount
                    }
                }
            } catch (_: Exception) {}
        }

        // Last 30 days total
        var last30DaysCount = 0
        val cal30 = Calendar.getInstance()
        for (i in 0 until 30) {
            val dStr = dateFormat.format(cal30.time)
            last30DaysCount += recordMap[dStr]?.count ?: 0
            cal30.add(Calendar.DAY_OF_YEAR, -1)
        }

        // Current streak & Best streak based on Durood / Dhikr
        val currentStreak = calculateCurrentStreak(recordMap, todayStr, yesterdayStr)
        val bestStreak = calculateBestStreak(allRecords)

        StatisticsData(
            todayTotal = todayTotal,
            weeklyTotal = weeklyTotal,
            monthlyTotal = monthlyTotal,
            todayTasbihTotal = todayTasbihTotal,
            weeklyTasbihTotal = weeklyTasbihTotal,
            monthlyTasbihTotal = monthlyTasbihTotal,
            currentStreak = currentStreak,
            bestStreak = bestStreak,
            yesterdayCount = yesterdayCount,
            last30DaysCount = last30DaysCount
        )
    }

    private fun calculateCurrentStreak(recordMap: Map<String, DailyRecord>, todayStr: String, yesterdayStr: String): Int {
        var streak = 0
        val cal = Calendar.getInstance()

        val todayRec = recordMap[todayStr]
        val todayCount = (todayRec?.count ?: 0) + (todayRec?.tasbihCount ?: 0)
        if (todayCount > 0) {
            streak++
            cal.add(Calendar.DAY_OF_YEAR, -1)
        } else {
            val yesterdayRec = recordMap[yesterdayStr]
            val yesterdayCount = (yesterdayRec?.count ?: 0) + (yesterdayRec?.tasbihCount ?: 0)
            if (yesterdayCount > 0) {
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -2)
            } else {
                return 0
            }
        }

        while (true) {
            val dateStr = dateFormat.format(cal.time)
            val rec = recordMap[dateStr]
            val count = (rec?.count ?: 0) + (rec?.tasbihCount ?: 0)
            if (count > 0) {
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        return streak
    }

    private fun calculateBestStreak(allRecords: List<DailyRecord>): Int {
        if (allRecords.isEmpty()) return 0
        val positiveDates = allRecords.filter { (it.count + it.tasbihCount) > 0 }.map { it.date }.sorted()
        if (positiveDates.isEmpty()) return 0

        var maxStreak = 1
        var currentStreak = 1

        val calPrev = Calendar.getInstance()
        val calCurr = Calendar.getInstance()

        for (i in 1 until positiveDates.size) {
            try {
                val prev = dateFormat.parse(positiveDates[i - 1])
                val curr = dateFormat.parse(positiveDates[i])
                if (prev != null && curr != null) {
                    calPrev.time = prev
                    calCurr.time = curr
                    calPrev.add(Calendar.DAY_OF_YEAR, 1)
                    if (calPrev.get(Calendar.YEAR) == calCurr.get(Calendar.YEAR) &&
                        calPrev.get(Calendar.DAY_OF_YEAR) == calCurr.get(Calendar.DAY_OF_YEAR)
                    ) {
                        currentStreak++
                        if (currentStreak > maxStreak) {
                            maxStreak = currentStreak
                        }
                    } else {
                        currentStreak = 1
                    }
                }
            } catch (_: Exception) {
                currentStreak = 1
            }
        }
        return maxStreak
    }

    suspend fun getWeeklyBarData(recordMap: Map<String, DailyRecord>): List<WeeklyBarData> = withContext(Dispatchers.Default) {
        val result = mutableListOf<WeeklyBarData>()
        val todayStr = getTodayDateString()

        val dayLabels = listOf(
            Triple("শনিবার", "Saturday", "Sat"),
            Triple("রবিবার", "Sunday", "Sun"),
            Triple("সোমবার", "Monday", "Mon"),
            Triple("মঙ্গলবার", "Tuesday", "Tue"),
            Triple("বুধবার", "Wednesday", "Wed"),
            Triple("বৃহস্পতিবার", "Thursday", "Thu"),
            Triple("শুক্রবার", "Friday", "Fri")
        )

        val calIter = Calendar.getInstance()
        calIter.add(Calendar.DAY_OF_YEAR, -6)

        for (i in 0 until 7) {
            val dStr = dateFormat.format(calIter.time)
            val dayOfWeek = calIter.get(Calendar.DAY_OF_WEEK) // 1=Sun, 7=Sat
            val dayIndex = when (dayOfWeek) {
                Calendar.SATURDAY -> 0
                Calendar.SUNDAY -> 1
                Calendar.MONDAY -> 2
                Calendar.TUESDAY -> 3
                Calendar.WEDNESDAY -> 4
                Calendar.THURSDAY -> 5
                Calendar.FRIDAY -> 6
                else -> 0
            }

            val labels = dayLabels[dayIndex]
            val record = recordMap[dStr]
            val count = record?.count ?: 0
            val tasbihCount = record?.tasbihCount ?: 0
            result.add(
                WeeklyBarData(
                    dayKey = labels.third,
                    dayLabelBn = labels.first,
                    dayLabelEn = labels.second,
                    dateString = dStr,
                    count = count,
                    tasbihCount = tasbihCount,
                    isToday = dStr == todayStr
                )
            )
            calIter.add(Calendar.DAY_OF_YEAR, 1)
        }

        result
    }

    suspend fun exportToJson(): String = withContext(Dispatchers.IO) {
        val records = dao.getAllRecordsSync()
        val root = JSONObject()
        root.put("app", "Darood")
        root.put("developer", "TBT BOYz")
        root.put("exportedAt", System.currentTimeMillis())
        val array = JSONArray()
        for (r in records) {
            val item = JSONObject()
            item.put("date", r.date)
            item.put("count", r.count)
            item.put("target", r.target)
            item.put("tasbihCount", r.tasbihCount)
            item.put("selectedDhikrId", r.selectedDhikrId)
            item.put("completedGoal", r.completedGoal)
            item.put("lastUpdatedMillis", r.lastUpdatedMillis)
            array.put(item)
        }
        root.put("records", array)
        root.toString(2)
    }

    suspend fun importFromJson(jsonString: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString)
            val array = root.getJSONArray("records")
            var count = 0
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                val date = item.getString("date")
                val cnt = item.getInt("count")
                val target = item.optInt("target", 500)
                val tasbihCnt = item.optInt("tasbihCount", 0)
                val selectedDhikr = item.optString("selectedDhikrId", "subhanallah")
                val completed = item.optBoolean("completedGoal", cnt >= target)
                val updated = item.optLong("lastUpdatedMillis", System.currentTimeMillis())
                dao.insertOrUpdate(
                    DailyRecord(
                        date = date,
                        count = cnt,
                        target = target,
                        tasbihCount = tasbihCnt,
                        selectedDhikrId = selectedDhikr,
                        completedGoal = completed,
                        lastUpdatedMillis = updated
                    )
                )
                count++
            }
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAllData() = withContext(Dispatchers.IO) {
        dao.deleteAll()
    }
}
