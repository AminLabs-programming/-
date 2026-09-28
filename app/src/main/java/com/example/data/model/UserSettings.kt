package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey
    val id: Int = 1,
    val konkurDaysRemaining: Int = 537,
    val konkurDateShamsi: String = "۱۴۰۶/۰۴/۱۵",
    val minimumDayMinutes: Int = 30, // استمرار حفظ شد
    val targetDayMinutes: Int = 360, // روز کامل (۶ ساعت)
    val usePersianDigits: Boolean = true
)
