package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_reports",
    indices = [Index(value = ["isoDate"], unique = true)]
)
data class DailyReport(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val isoDate: String, // "YYYY-MM-DD"
    val shamsiDate: String, // "1405/07/07"
    val dayNumber: Int = 1, // e.g. روز ۲۸
    val studyMinutes: Int, // Study time in minutes
    val subjects: String, // "ریاضی · فیزیک · شیمی"
    val testCount: Int? = null,
    val correctTests: Int? = null,
    val wrongTests: Int? = null,
    val uncompletedParts: String? = null, // e.g. "فیزیک — پارت ۳"
    val uncompletedReason: String? = null, // e.g. "خستگی"
    val notes: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
