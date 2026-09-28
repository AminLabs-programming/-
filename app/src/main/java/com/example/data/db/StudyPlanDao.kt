package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.StudyPlanItem
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyPlanDao {

    @Query("SELECT * FROM study_plan_items WHERE isoDate = :isoDate ORDER BY id ASC")
    fun getItemsForDate(isoDate: String): Flow<List<StudyPlanItem>>

    @Query("SELECT * FROM study_plan_items WHERE isoDate BETWEEN :startDate AND :endDate ORDER BY isoDate ASC, id ASC")
    fun getItemsForDateRange(startDate: String, endDate: String): Flow<List<StudyPlanItem>>

    @Query("SELECT * FROM study_plan_items WHERE isoDate BETWEEN :startDate AND :endDate ORDER BY isoDate ASC, id ASC")
    suspend fun getItemsForDateRangeDirect(startDate: String, endDate: String): List<StudyPlanItem>

    @Query("SELECT * FROM study_plan_items ORDER BY isoDate DESC, id ASC")
    fun getAllItems(): Flow<List<StudyPlanItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: StudyPlanItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<StudyPlanItem>)

    @Update
    suspend fun update(item: StudyPlanItem)

    @Delete
    suspend fun delete(item: StudyPlanItem)

    @Query("DELETE FROM study_plan_items")
    suspend fun deleteAll()
}
