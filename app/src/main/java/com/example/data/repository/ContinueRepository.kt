package com.example.data.repository

import com.example.data.db.DailyReportDao
import com.example.data.db.StudyPlanDao
import com.example.data.db.UserSettingsDao
import com.example.data.model.DailyReport
import com.example.data.model.PlanStatus
import com.example.data.model.StudyPlanItem
import com.example.data.model.StudyType
import com.example.data.model.UserSettings
import com.example.util.JalaliDate
import com.example.util.SubjectBreakdown
import com.example.util.WeeklyReportSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class PersonalRecords(
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val totalActiveDays: Int = 0,
    val maxStudyMinutesInDay: Int = 0,
    val maxTestsInDay: Int = 0,
    val totalStudyMinutes: Long = 0,
    val totalTests: Int = 0
)

data class WeeklyStats(
    val thisWeekMinutes: Int = 0,
    val lastWeekMinutes: Int = 0,
    val deltaMinutes: Int = 0,
    val thisWeekActiveDays: Int = 0,
    val thisWeekTests: Int = 0,
    val lastWeekTests: Int = 0,
    val dailyAverageMinutes: Int = 0
)

data class MirrorInsight(
    val title: String,
    val description: String,
    val category: String = "الگو"
)

class ContinueRepository(
    private val reportDao: DailyReportDao,
    private val settingsDao: UserSettingsDao,
    private val planDao: StudyPlanDao
) {
    val allReports: Flow<List<DailyReport>> = reportDao.getAllReports()

    val settings: Flow<UserSettings> = settingsDao.getSettings().map {
        it ?: UserSettings()
    }

    val allPlanItems: Flow<List<StudyPlanItem>> = planDao.getAllItems()

    fun getPlanItemsForDate(isoDate: String): Flow<List<StudyPlanItem>> {
        return planDao.getItemsForDate(isoDate)
    }

    fun getPlanItemsForRange(startDate: String, endDate: String): Flow<List<StudyPlanItem>> {
        return planDao.getItemsForDateRange(startDate, endDate)
    }

    suspend fun insertStudyPlan(item: StudyPlanItem): Long {
        return planDao.insert(item)
    }

    suspend fun updateStudyPlan(item: StudyPlanItem) {
        planDao.update(item)
    }

    suspend fun deleteStudyPlan(item: StudyPlanItem) {
        planDao.delete(item)
    }

    suspend fun insertReport(report: DailyReport): Long {
        return reportDao.insert(report)
    }

    suspend fun updateReport(report: DailyReport) {
        reportDao.update(report)
    }

    suspend fun deleteReport(report: DailyReport) {
        reportDao.delete(report)
    }

    suspend fun deleteAllReports() {
        reportDao.deleteAll()
        planDao.deleteAll()
    }

    suspend fun saveSettings(settings: UserSettings) {
        settingsDao.saveSettings(settings)
    }

    suspend fun getReportByDate(isoDate: String): DailyReport? {
        return reportDao.getReportByIsoDateDirect(isoDate)
    }

    suspend fun buildWeeklyReportSummary(startDate: String, endDate: String): WeeklyReportSummary {
        val planItems = planDao.getItemsForDateRangeDirect(startDate, endDate)

        var totalMinutes = 0
        var totalKonkurMinutes = 0
        var totalFinalMinutes = 0
        var totalTests = 0
        var completedCount = 0
        var partialCount = 0
        var notDoneCount = 0

        val subjectMap = mutableMapOf<String, MutableList<StudyPlanItem>>()

        for (item in planItems) {
            val minutes = if (item.status == PlanStatus.COMPLETED.code) item.actualMinutes
            else if (item.status == PlanStatus.PARTIAL.code) item.actualMinutes
            else 0

            totalMinutes += minutes
            if (item.studyType == StudyType.KONKUR.code) {
                totalKonkurMinutes += minutes
            } else {
                totalFinalMinutes += minutes
            }

            totalTests += item.actualTests

            when (item.status) {
                PlanStatus.COMPLETED.code -> completedCount++
                PlanStatus.PARTIAL.code -> partialCount++
                else -> notDoneCount++
            }

            subjectMap.getOrPut(item.subject) { mutableListOf() }.add(item)
        }

        val breakdowns = subjectMap.map { (subject, items) ->
            var subTotalMin = 0
            var subKonkurMin = 0
            var subFinalMin = 0
            var subTests = 0
            var subComp = 0
            var subPart = 0
            var subNotDone = 0

            for (it in items) {
                val m = if (it.status != PlanStatus.NOT_DONE.code) it.actualMinutes else 0
                subTotalMin += m
                if (it.studyType == StudyType.KONKUR.code) subKonkurMin += m else subFinalMin += m
                subTests += it.actualTests

                when (it.status) {
                    PlanStatus.COMPLETED.code -> subComp++
                    PlanStatus.PARTIAL.code -> subPart++
                    else -> subNotDone++
                }
            }

            SubjectBreakdown(
                subject = subject,
                totalMinutes = subTotalMin,
                konkurMinutes = subKonkurMin,
                finalMinutes = subFinalMin,
                totalTests = subTests,
                completedParts = subComp,
                partialParts = subPart,
                notDoneParts = subNotDone
            )
        }.sortedByDescending { it.totalMinutes }

        val startShamsi = JalaliDate.fromIsoDate(startDate).format()
        val endShamsi = JalaliDate.fromIsoDate(endDate).format()
        val uncompleted = planItems.filter { it.status != PlanStatus.COMPLETED.code }

        return WeeklyReportSummary(
            startDateIso = startDate,
            endDateIso = endDate,
            startDateShamsi = startShamsi,
            endDateShamsi = endShamsi,
            totalStudyMinutes = totalMinutes,
            dailyAverageMinutes = if (planItems.isNotEmpty()) totalMinutes / 7 else 0,
            totalKonkurMinutes = totalKonkurMinutes,
            totalFinalMinutes = totalFinalMinutes,
            totalTests = totalTests,
            completedPartsCount = completedCount,
            partialPartsCount = partialCount,
            notDonePartsCount = notDoneCount,
            subjectBreakdowns = breakdowns,
            uncompletedItems = uncompleted
        )
    }

    fun calculateRecords(reports: List<DailyReport>, minimumMinutes: Int): PersonalRecords {
        if (reports.isEmpty()) {
            return PersonalRecords()
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val validReports = reports.filter { it.studyMinutes >= minimumMinutes }
            .sortedBy { it.isoDate }

        var maxStreak = 0
        var currentRunningStreak = 0
        var prevDate: Calendar? = null

        val activeDatesSet = validReports.map { it.isoDate }.toSet()

        for (report in validReports) {
            val date = Calendar.getInstance()
            try {
                date.time = dateFormat.parse(report.isoDate) ?: Date()
            } catch (e: Exception) {
                continue
            }

            if (prevDate == null) {
                currentRunningStreak = 1
            } else {
                val diffDays = ((date.timeInMillis - prevDate.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
                if (diffDays == 1) {
                    currentRunningStreak++
                } else if (diffDays > 1) {
                    currentRunningStreak = 1
                }
            }
            if (currentRunningStreak > maxStreak) {
                maxStreak = currentRunningStreak
            }
            prevDate = date
        }

        val todayCal = Calendar.getInstance()
        val todayIso = dateFormat.format(todayCal.time)
        val yesterdayCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val yesterdayIso = dateFormat.format(yesterdayCal.time)

        var streakFromNow = 0
        var checkCal = Calendar.getInstance()

        if (activeDatesSet.contains(todayIso)) {
            while (activeDatesSet.contains(dateFormat.format(checkCal.time))) {
                streakFromNow++
                checkCal.add(Calendar.DAY_OF_YEAR, -1)
            }
        } else if (activeDatesSet.contains(yesterdayIso)) {
            checkCal.add(Calendar.DAY_OF_YEAR, -1)
            while (activeDatesSet.contains(dateFormat.format(checkCal.time))) {
                streakFromNow++
                checkCal.add(Calendar.DAY_OF_YEAR, -1)
            }
        }

        val totalMinutes = reports.sumOf { it.studyMinutes.toLong() }
        val totalTests = reports.mapNotNull { it.testCount }.sum()
        val maxStudyMin = reports.maxOfOrNull { it.studyMinutes } ?: 0
        val maxTests = reports.mapNotNull { it.testCount }.maxOfOrNull { it } ?: 0

        val bestStreak = maxOf(maxStreak, streakFromNow)

        return PersonalRecords(
            currentStreak = streakFromNow,
            bestStreak = bestStreak,
            totalActiveDays = validReports.size,
            maxStudyMinutesInDay = maxStudyMin,
            maxTestsInDay = maxTests,
            totalStudyMinutes = totalMinutes,
            totalTests = totalTests
        )
    }

    fun calculateWeeklyStats(reports: List<DailyReport>): WeeklyStats {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val reportMap = reports.associateBy { it.isoDate }

        var thisWeekMin = 0
        var lastWeekMin = 0
        var thisWeekActive = 0
        var thisWeekTests = 0
        var lastWeekTests = 0

        for (i in 0 until 7) {
            val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -i) }
            val iso = dateFormat.format(cal.time)
            val r = reportMap[iso]
            if (r != null) {
                thisWeekMin += r.studyMinutes
                if (r.studyMinutes > 0) thisWeekActive++
                thisWeekTests += r.testCount ?: 0
            }
        }

        for (i in 7 until 14) {
            val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -i) }
            val iso = dateFormat.format(cal.time)
            val r = reportMap[iso]
            if (r != null) {
                lastWeekMin += r.studyMinutes
                lastWeekTests += r.testCount ?: 0
            }
        }

        val delta = thisWeekMin - lastWeekMin
        val avgMin = if (thisWeekActive > 0) thisWeekMin / 7 else 0

        return WeeklyStats(
            thisWeekMinutes = thisWeekMin,
            lastWeekMinutes = lastWeekMin,
            deltaMinutes = delta,
            thisWeekActiveDays = thisWeekActive,
            thisWeekTests = thisWeekTests,
            lastWeekTests = lastWeekTests,
            dailyAverageMinutes = avgMin
        )
    }

    fun generateMirrorInsights(reports: List<DailyReport>, weeklyStats: WeeklyStats): List<MirrorInsight> {
        if (reports.size < 3) {
            return emptyList()
        }

        val insights = mutableListOf<MirrorInsight>()

        val deltaHours = kotlin.math.abs(weeklyStats.deltaMinutes) / 60
        val deltaMins = kotlin.math.abs(weeklyStats.deltaMinutes) % 60
        val timeDeltaStr = if (deltaHours > 0) "${deltaHours}h ${deltaMins}m" else "${deltaMins} دقیقه"
        if (weeklyStats.deltaMinutes > 0) {
            insights.add(
                MirrorInsight(
                    title = "افزایش حجم مطالعه هفتگی",
                    description = "این هفته $timeDeltaStr نسبت به هفته قبل بیشتر مطالعه داشته‌ای.",
                    category = "پیشرفت"
                )
            )
        } else if (weeklyStats.deltaMinutes < 0) {
            insights.add(
                MirrorInsight(
                    title = "تغییر ساعات این هفته",
                    description = "این هفته $timeDeltaStr کمتر از هفته قبل مطالعه ثبت شده است. هدف استمرار است، نه یک روز ایده‌آل.",
                    category = "تحلیل"
                )
            )
        }

        val uncompletedWithReasons = reports.filter { !it.uncompletedReason.isNullOrBlank() }
        if (uncompletedWithReasons.isNotEmpty()) {
            val reasonsCount = uncompletedWithReasons.groupingBy { it.uncompletedReason!!.trim() }.eachCount()
            val mostCommon = reasonsCount.maxByOrNull { it.value }
            if (mostCommon != null) {
                insights.add(
                    MirrorInsight(
                        title = "علت اصلی پارت‌های مطالعه‌نشده",
                        description = "در ${mostCommon.value} مورد از پارت‌های انجام‌نشده، علت اصلی «${mostCommon.key}» ثبت شده است.",
                        category = "پارت‌ها"
                    )
                )
            }
        }

        val allSubjects = reports.flatMap { it.subjects.split("·", ",").map { s -> s.trim() } }
            .filter { it.isNotBlank() }
        if (allSubjects.isNotEmpty()) {
            val subjectCounts = allSubjects.groupingBy { it }.eachCount()
            val topSubject = subjectCounts.maxByOrNull { it.value }
            if (topSubject != null) {
                insights.add(
                    MirrorInsight(
                        title = "بیشترین تمرکز درسی",
                        description = "درس «${topSubject.key}» با حضور در ${topSubject.value} روز گزارش، بیشترین استمرار را داشته است.",
                        category = "دروس"
                    )
                )
            }
        }

        val totalCorrect = reports.mapNotNull { it.correctTests }.sum()
        val totalTests = reports.mapNotNull { it.testCount }.sum()
        if (totalTests > 30) {
            val accuracy = (totalCorrect * 100) / totalTests
            insights.add(
                MirrorInsight(
                    title = "دقت پاسخ‌گویی تست‌ها",
                    description = "از مجموع $totalTests تست ثبت‌شده، $totalCorrect پاسخ درست (${accuracy}٪) به ثبت رسیده است.",
                    category = "تست"
                )
            )
        }

        return insights
    }

    suspend fun seedInitialDataIfEmpty() {
        val existing = reportDao.getAllReports().firstOrNull()
        if (existing.isNullOrEmpty()) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val sampleReports = mutableListOf<DailyReport>()
            val subjectsList = listOf(
                "زیست‌شناسی · شیمی · ریاضی",
                "فیزیک · ریاضی · ادبیات",
                "شیمی · زیست‌شناسی · زبان",
                "ریاضی · فیزیک · شیمی",
                "زیست‌شناسی · زمین‌شناسی · فیزیک"
            )

            for (i in 28 downTo 1) {
                val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -(i - 1)) }
                val iso = dateFormat.format(cal.time)
                val jDate = JalaliDate.fromIsoDate(iso)

                val studyMinutes = when (i % 6) {
                    0 -> 342
                    1 -> 400
                    2 -> 290
                    3 -> 360
                    4 -> 220
                    else -> 342
                }

                val hasTests = i % 2 == 0
                val testCount = if (hasTests) 82 else null
                val correctTests = if (hasTests) 63 else null
                val wrongTests = if (hasTests) 19 else null

                val hasUncompleted = i == 5 || i == 12
                val uncompletedPart = if (hasUncompleted) "فیزیک — پارت ۳" else null
                val uncompletedReason = if (hasUncompleted) "خستگی" else null

                sampleReports.add(
                    DailyReport(
                        isoDate = iso,
                        shamsiDate = jDate.format(),
                        dayNumber = 29 - i,
                        studyMinutes = studyMinutes,
                        subjects = subjectsList[i % subjectsList.size],
                        testCount = testCount,
                        correctTests = correctTests,
                        wrongTests = wrongTests,
                        uncompletedParts = uncompletedPart,
                        uncompletedReason = uncompletedReason
                    )
                )
            }

            for (i in 115 downTo 73) {
                val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -i) }
                val iso = dateFormat.format(cal.time)
                val jDate = JalaliDate.fromIsoDate(iso)
                sampleReports.add(
                    DailyReport(
                        isoDate = iso,
                        shamsiDate = jDate.format(),
                        dayNumber = 116 - i,
                        studyMinutes = 330,
                        subjects = "شیمی · ریاضی · فیزیک",
                        testCount = 65,
                        correctTests = 52,
                        wrongTests = 13
                    )
                )
            }

            reportDao.insertAll(sampleReports)

            settingsDao.saveSettings(
                UserSettings(
                    konkurDaysRemaining = 537,
                    konkurDateShamsi = "۱۴۰۶/۰۴/۱۵",
                    minimumDayMinutes = 30,
                    targetDayMinutes = 360,
                    usePersianDigits = true
                )
            )

            // Seed study plan items for the past 7 days (Weekly Plan & Report items)
            val samplePlanItems = mutableListOf<StudyPlanItem>()
            val daysBack = 7
            for (d in daysBack downTo 0) {
                val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -d) }
                val iso = dateFormat.format(cal.time)

                // Part 1: Biology (Konkur)
                samplePlanItems.add(
                    StudyPlanItem(
                        isoDate = iso,
                        subject = "زیست‌شناسی",
                        title = "پارت ۱: گفتار ۲ فصل گوارش (تستی)",
                        studyType = StudyType.KONKUR.code,
                        plannedMinutes = 90,
                        actualMinutes = 90,
                        plannedTests = 30,
                        actualTests = 30,
                        status = PlanStatus.COMPLETED.code
                    )
                )

                // Part 2: Chemistry (Finals / School exam focus)
                samplePlanItems.add(
                    StudyPlanItem(
                        isoDate = iso,
                        subject = "شیمی",
                        title = "پارت ۲: متن کتاب درسی و حل تمرینات دوره نهایی",
                        studyType = StudyType.FINAL.code,
                        plannedMinutes = 75,
                        actualMinutes = if (d == 2) 40 else 75,
                        plannedTests = 0,
                        actualTests = 0,
                        status = if (d == 2) PlanStatus.PARTIAL.code else PlanStatus.COMPLETED.code,
                        reason = if (d == 2) "کمبود وقت برای حل دوره دوم نمونه سوالات" else null
                    )
                )

                // Part 3: Physics (Konkur)
                samplePlanItems.add(
                    StudyPlanItem(
                        isoDate = iso,
                        subject = "فیزیک",
                        title = "پارت ۳: تست‌های مدار و جریان الکتریکی",
                        studyType = StudyType.KONKUR.code,
                        plannedMinutes = 90,
                        actualMinutes = if (d == 4) 0 else 90,
                        plannedTests = 25,
                        actualTests = if (d == 4) 0 else 25,
                        status = if (d == 4) PlanStatus.NOT_DONE.code else PlanStatus.COMPLETED.code,
                        reason = if (d == 4) "خستگی بعد از مدرسه و امتحان تشریحی" else null
                    )
                )

                // Part 4: Mathematics (Final or Konkur)
                val isFinalMath = d % 2 == 0
                samplePlanItems.add(
                    StudyPlanItem(
                        isoDate = iso,
                        subject = "ریاضی",
                        title = if (isFinalMath) "پارت ۴: تسلط بر تمرین‌های تشریحی حسابان" else "پارت ۴: بانک تست مشتق و کاربرد",
                        studyType = if (isFinalMath) StudyType.FINAL.code else StudyType.KONKUR.code,
                        plannedMinutes = 80,
                        actualMinutes = 80,
                        plannedTests = if (isFinalMath) 0 else 20,
                        actualTests = if (isFinalMath) 0 else 20,
                        status = PlanStatus.COMPLETED.code
                    )
                )
            }

            planDao.insertAll(samplePlanItems)
        }
    }
}
