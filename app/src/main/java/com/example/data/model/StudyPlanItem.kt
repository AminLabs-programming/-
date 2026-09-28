package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class StudyType(val title: String, val code: String) {
    KONKUR("کنکوری", "KONKUR"),
    FINAL("نهایی", "FINAL");

    companion object {
        fun fromCode(code: String): StudyType = entries.find { it.code == code } ?: KONKUR
    }
}

enum class PlanStatus(val title: String, val code: String) {
    COMPLETED("انجام‌شده", "COMPLETED"),
    PARTIAL("ناقص انجام‌شده", "PARTIAL"),
    NOT_DONE("انجام‌نشده", "NOT_DONE");

    companion object {
        fun fromCode(code: String): PlanStatus = entries.find { it.code == code } ?: NOT_DONE
    }
}

@Entity(
    tableName = "study_plan_items",
    indices = [Index(value = ["isoDate"])]
)
data class StudyPlanItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val isoDate: String, // "YYYY-MM-DD"
    val subject: String, // e.g. "زیست‌شناسی"
    val title: String, // e.g. "پارت ۱ - گفتار ۲ گوارش"
    val studyType: String = StudyType.KONKUR.code, // "KONKUR" or "FINAL"
    val plannedMinutes: Int = 90,
    val actualMinutes: Int = 90,
    val plannedTests: Int = 30,
    val actualTests: Int = 30,
    val status: String = PlanStatus.COMPLETED.code, // "COMPLETED", "PARTIAL", "NOT_DONE"
    val reason: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
