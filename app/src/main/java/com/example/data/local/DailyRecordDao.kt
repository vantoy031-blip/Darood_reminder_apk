package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.DailyRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyRecordDao {

    @Query("SELECT * FROM daily_records WHERE date = :date LIMIT 1")
    fun getRecordByDate(date: String): Flow<DailyRecord?>

    @Query("SELECT * FROM daily_records WHERE date = :date LIMIT 1")
    suspend fun getRecordByDateSync(date: String): DailyRecord?

    @Query("SELECT * FROM daily_records ORDER BY date DESC")
    fun getAllRecords(): Flow<List<DailyRecord>>

    @Query("SELECT * FROM daily_records ORDER BY date DESC")
    suspend fun getAllRecordsSync(): List<DailyRecord>

    @Query("SELECT * FROM daily_records ORDER BY date DESC LIMIT :limit")
    fun getRecentRecords(limit: Int): Flow<List<DailyRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(record: DailyRecord)

    @Query("DELETE FROM daily_records WHERE date = :date")
    suspend fun deleteByDate(date: String)

    @Query("DELETE FROM daily_records")
    suspend fun deleteAll()
}
