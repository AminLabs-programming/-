package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DailyReport
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyReportDao {

    @Query("SELECT * FROM daily_reports ORDER BY isoDate DESC")
    fun getAllReports(): Flow<List<DailyReport>>

    @Query("SELECT * FROM daily_reports WHERE isoDate = :isoDate LIMIT 1")
    fun getReportByIsoDate(isoDate: String): Flow<DailyReport?>

    @Query("SELECT * FROM daily_reports WHERE isoDate = :isoDate LIMIT 1")
    suspend fun getReportByIsoDateDirect(isoDate: String): DailyReport?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(report: DailyReport): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reports: List<DailyReport>)

    @Update
    suspend fun update(report: DailyReport)

    @Delete
    suspend fun delete(report: DailyReport)

    @Query("DELETE FROM daily_reports")
    suspend fun deleteAll()
}
