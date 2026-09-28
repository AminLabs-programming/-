package com.example.util

import java.util.Calendar

data class JalaliDate(
    val year: Int,
    val month: Int,
    val day: Int
) : Comparable<JalaliDate> {

    override fun compareTo(other: JalaliDate): Int {
        if (year != other.year) return year.compareTo(other.year)
        if (month != other.month) return month.compareTo(other.month)
        return day.compareTo(other.day)
    }

    fun format(usePersianDigits: Boolean = true): String {
        val y = if (usePersianDigits) toPersianDigits(year) else year.toString()
        val m = if (usePersianDigits) toPersianDigits(String.format("%02d", month)) else String.format("%02d", month)
        val d = if (usePersianDigits) toPersianDigits(String.format("%02d", day)) else String.format("%02d", day)
        return "$y/$m/$d"
    }

    fun formatFull(dayOfWeek: String? = null, usePersianDigits: Boolean = true): String {
        val d = if (usePersianDigits) toPersianDigits(day) else day.toString()
        val y = if (usePersianDigits) toPersianDigits(year) else year.toString()
        val mName = monthName()
        return if (dayOfWeek != null) "$dayOfWeek، $d $mName $y" else "$d $mName $y"
    }

    fun monthName(): String {
        return when (month) {
            1 -> "فروردین"
            2 -> "اردیبهشت"
            3 -> "خرداد"
            4 -> "تیر"
            5 -> "مرداد"
            6 -> "شهریور"
            7 -> "مهر"
            8 -> "آبان"
            9 -> "آذر"
            10 -> "دی"
            11 -> "بهمن"
            12 -> "اسفند"
            else -> ""
        }
    }

    companion object {
        fun now(): JalaliDate {
            val cal = Calendar.getInstance()
            return fromGregorian(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH)
            )
        }

        fun todayPersianFull(): String {
            val cal = Calendar.getInstance()
            val dayOfWeek = getDayOfWeekPersian(cal)
            val jDate = now()
            return jDate.formatFull(dayOfWeek)
        }

        fun fromIsoDate(isoDate: String): JalaliDate {
            val parts = isoDate.split("-")
            if (parts.size == 3) {
                val gy = parts[0].toIntOrNull() ?: 2026
                val gm = parts[1].toIntOrNull() ?: 1
                val gd = parts[2].toIntOrNull() ?: 1
                return fromGregorian(gy, gm, gd)
            }
            return now()
        }

        fun fromGregorian(gy: Int, gm: Int, gd: Int): JalaliDate {
            val gDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
            val jDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

            val gy2 = gy - 1600
            val gm2 = gm - 1
            val gd2 = gd - 1

            var gDayNo = 365 * gy2 + (gy2 + 3) / 4 - (gy2 + 99) / 100 + (gy2 + 399) / 400
            for (i in 0 until gm2) {
                gDayNo += gDaysInMonth[i]
            }
            if (gm2 > 1 && ((gy2 % 4 == 0 && gy2 % 100 != 0) || (gy2 % 400 == 0))) {
                gDayNo++
            }
            gDayNo += gd2

            var jDayNo = gDayNo - 79
            val jNp = jDayNo / 12053
            jDayNo %= 12053

            var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
            jDayNo %= 1461

            if (jDayNo >= 366) {
                jy += (jDayNo - 1) / 365
                jDayNo = (jDayNo - 1) % 365
            }

            var jm = 0
            for (i in 0 until 11) {
                if (jDayNo < jDaysInMonth[i]) {
                    jm = i
                    break
                }
                jDayNo -= jDaysInMonth[i]
                jm = i + 1
            }
            val jd = jDayNo + 1
            return JalaliDate(jy, jm + 1, jd)
        }

        fun toGregorian(jy: Int, jm: Int, jd: Int): Triple<Int, Int, Int> {
            val gDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
            val jDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

            val jy2 = jy - 979
            val jm2 = jm - 1
            val jd2 = jd - 1

            var jDayNo = 365 * jy2 + (jy2 / 33) * 8 + ((jy2 % 33) + 3) / 4
            for (i in 0 until jm2) {
                jDayNo += jDaysInMonth[i]
            }
            jDayNo += jd2

            var gDayNo = jDayNo + 79
            var gy = 1600 + 400 * (gDayNo / 146097)
            gDayNo %= 146097

            var leap = true
            if (gDayNo >= 36525) {
                gDayNo--
                gy += 100 * (gDayNo / 36524)
                gDayNo %= 36524

                if (gDayNo >= 365) {
                    gDayNo++
                } else {
                    leap = false
                }
            }

            gy += 4 * (gDayNo / 1461)
            gDayNo %= 1461

            if (gDayNo >= 366) {
                leap = false
                gDayNo--
                gy += gDayNo / 365
                gDayNo %= 365
            }

            var gm = 0
            for (i in 0 until 12) {
                val dim = if (i == 1 && leap) 29 else gDaysInMonth[i]
                if (gDayNo < dim) {
                    gm = i
                    break
                }
                gDayNo -= dim
                gm = i + 1
            }
            val gd = gDayNo + 1
            return Triple(gy, gm + 1, gd)
        }

        fun getDayOfWeekPersian(cal: Calendar): String {
            return when (cal.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SATURDAY -> "شنبه"
                Calendar.SUNDAY -> "یکشنبه"
                Calendar.MONDAY -> "دوشنبه"
                Calendar.TUESDAY -> "سه‌شنبه"
                Calendar.WEDNESDAY -> "چهارشنبه"
                Calendar.THURSDAY -> "پنج‌شنبه"
                Calendar.FRIDAY -> "جمعه"
                else -> ""
            }
        }

        fun toPersianDigits(value: Any): String {
            val str = value.toString()
            val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
            val sb = StringBuilder()
            for (ch in str) {
                if (ch in '0'..'9') {
                    sb.append(persianDigits[ch - '0'])
                } else {
                    sb.append(ch)
                }
            }
            return sb.toString()
        }

        fun formatHoursAndMinutes(totalMinutes: Int, usePersianDigits: Boolean = true): String {
            val hours = totalMinutes / 60
            val minutes = totalMinutes % 60
            val formatted = String.format("%02d:%02d", hours, minutes)
            return if (usePersianDigits) toPersianDigits(formatted) else formatted
        }

        fun formatDurationText(totalMinutes: Int, usePersianDigits: Boolean = true): String {
            val hours = totalMinutes / 60
            val minutes = totalMinutes % 60
            val hStr = if (usePersianDigits) toPersianDigits(hours) else hours.toString()
            val mStr = if (usePersianDigits) toPersianDigits(minutes) else minutes.toString()
            return when {
                hours > 0 && minutes > 0 -> "$hStr ساعت و $mStr دقیقه"
                hours > 0 -> "$hStr ساعت"
                else -> "$mStr دقیقه"
            }
        }
    }
}
