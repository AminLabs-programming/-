package com.example

import com.example.data.model.DailyReport
import com.example.data.model.PlanStatus
import com.example.data.model.StudyPlanItem
import com.example.data.model.StudyType
import com.example.data.repository.ContinueRepository
import com.example.util.JalaliDate
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ContinueLogicTest {

    @Test
    fun testJalaliDateConversion() {
        val jDate = JalaliDate.fromGregorian(2026, 3, 21)
        assertEquals(1405, jDate.year)
        assertEquals(1, jDate.month)
        assertEquals(1, jDate.day)
        assertEquals("فروردین", jDate.monthName())

        val formatted = JalaliDate.toPersianDigits("28")
        assertEquals("۲۸", formatted)

        val durationFormatted = JalaliDate.formatHoursAndMinutes(342, usePersianDigits = true)
        assertEquals("۰۵:۴۲", durationFormatted)
    }

    @Test
    fun testStreakCalculation() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val reports = mutableListOf<DailyReport>()

        for (i in 5 downTo 1) {
            val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -i) }
            val iso = dateFormat.format(cal.time)
            reports.add(
                DailyReport(
                    id = i.toLong(),
                    isoDate = iso,
                    shamsiDate = "1405/07/0$i",
                    studyMinutes = 180,
                    subjects = "زیست‌شناسی · شیمی",
                    testCount = 50,
                    correctTests = 40,
                    wrongTests = 10
                )
            )
        }

        val repo = ContinueRepository(
            reportDao = object : com.example.data.db.DailyReportDao {
                override fun getAllReports() = kotlinx.coroutines.flow.emptyFlow<List<DailyReport>>()
                override fun getReportByIsoDate(isoDate: String) = kotlinx.coroutines.flow.emptyFlow<DailyReport?>()
                override suspend fun getReportByIsoDateDirect(isoDate: String) = null
                override suspend fun insert(report: DailyReport) = 0L
                override suspend fun insertAll(reports: List<DailyReport>) {}
                override suspend fun update(report: DailyReport) {}
                override suspend fun delete(report: DailyReport) {}
                override suspend fun deleteAll() {}
            },
            settingsDao = object : com.example.data.db.UserSettingsDao {
                override fun getSettings() = kotlinx.coroutines.flow.emptyFlow<com.example.data.model.UserSettings?>()
                override suspend fun getSettingsDirect() = null
                override suspend fun saveSettings(settings: com.example.data.model.UserSettings) {}
            },
            planDao = object : com.example.data.db.StudyPlanDao {
                override fun getItemsForDate(isoDate: String) = kotlinx.coroutines.flow.emptyFlow<List<StudyPlanItem>>()
                override fun getItemsForDateRange(startDate: String, endDate: String) = kotlinx.coroutines.flow.emptyFlow<List<StudyPlanItem>>()
                override suspend fun getItemsForDateRangeDirect(startDate: String, endDate: String) = emptyList<StudyPlanItem>()
                override fun getAllItems() = kotlinx.coroutines.flow.emptyFlow<List<StudyPlanItem>>()
                override suspend fun insert(item: StudyPlanItem) = 0L
                override suspend fun insertAll(items: List<StudyPlanItem>) {}
                override suspend fun update(item: StudyPlanItem) {}
                override suspend fun delete(item: StudyPlanItem) {}
                override suspend fun deleteAll() {}
            }
        )

        val records = repo.calculateRecords(reports, minimumMinutes = 30)
        assertTrue("Streak should be at least 5 days", records.currentStreak >= 5)
        assertEquals(5, records.totalActiveDays)
        assertEquals(250, records.totalTests)
        assertEquals(900L, records.totalStudyMinutes)
    }

    @Test
    fun testWeeklyReportKonkurVsFinalsSeparation() = runBlocking {
        val sampleItems = listOf(
            StudyPlanItem(
                id = 1,
                isoDate = "2026-09-22",
                subject = "زیست‌شناسی",
                title = "پارت تست ژنتیک",
                studyType = StudyType.KONKUR.code,
                plannedMinutes = 90,
                actualMinutes = 90,
                plannedTests = 30,
                actualTests = 30,
                status = PlanStatus.COMPLETED.code
            ),
            StudyPlanItem(
                id = 2,
                isoDate = "2026-09-23",
                subject = "زیست‌شناسی",
                title = "خواندن متن کتاب نهایی",
                studyType = StudyType.FINAL.code,
                plannedMinutes = 60,
                actualMinutes = 60,
                plannedTests = 0,
                actualTests = 0,
                status = PlanStatus.COMPLETED.code
            ),
            StudyPlanItem(
                id = 3,
                isoDate = "2026-09-24",
                subject = "فیزیک",
                title = "پارت ناقص مدار",
                studyType = StudyType.KONKUR.code,
                plannedMinutes = 80,
                actualMinutes = 40,
                plannedTests = 20,
                actualTests = 10,
                status = PlanStatus.PARTIAL.code,
                reason = "خستگی"
            )
        )

        val repo = ContinueRepository(
            reportDao = object : com.example.data.db.DailyReportDao {
                override fun getAllReports() = kotlinx.coroutines.flow.emptyFlow<List<DailyReport>>()
                override fun getReportByIsoDate(isoDate: String) = kotlinx.coroutines.flow.emptyFlow<DailyReport?>()
                override suspend fun getReportByIsoDateDirect(isoDate: String) = null
                override suspend fun insert(report: DailyReport) = 0L
                override suspend fun insertAll(reports: List<DailyReport>) {}
                override suspend fun update(report: DailyReport) {}
                override suspend fun delete(report: DailyReport) {}
                override suspend fun deleteAll() {}
            },
            settingsDao = object : com.example.data.db.UserSettingsDao {
                override fun getSettings() = kotlinx.coroutines.flow.emptyFlow<com.example.data.model.UserSettings?>()
                override suspend fun getSettingsDirect() = null
                override suspend fun saveSettings(settings: com.example.data.model.UserSettings) {}
            },
            planDao = object : com.example.data.db.StudyPlanDao {
                override fun getItemsForDate(isoDate: String) = kotlinx.coroutines.flow.emptyFlow<List<StudyPlanItem>>()
                override fun getItemsForDateRange(startDate: String, endDate: String) = kotlinx.coroutines.flow.emptyFlow<List<StudyPlanItem>>()
                override suspend fun getItemsForDateRangeDirect(startDate: String, endDate: String) = sampleItems
                override fun getAllItems() = kotlinx.coroutines.flow.emptyFlow<List<StudyPlanItem>>()
                override suspend fun insert(item: StudyPlanItem) = 0L
                override suspend fun insertAll(items: List<StudyPlanItem>) {}
                override suspend fun update(item: StudyPlanItem) {}
                override suspend fun delete(item: StudyPlanItem) {}
                override suspend fun deleteAll() {}
            }
        )

        val summary = repo.buildWeeklyReportSummary("2026-09-20", "2026-09-26")

        assertEquals(190, summary.totalStudyMinutes)
        assertEquals(130, summary.totalKonkurMinutes) // 90 + 40
        assertEquals(60, summary.totalFinalMinutes)   // 60
        assertEquals(40, summary.totalTests)          // 30 + 10
        assertEquals(2, summary.completedPartsCount)
        assertEquals(1, summary.partialPartsCount)
        assertEquals(0, summary.notDonePartsCount)

        val bio = summary.subjectBreakdowns.find { it.subject == "زیست‌شناسی" }
        assertEquals(150, bio?.totalMinutes)
        assertEquals(90, bio?.konkurMinutes)
        assertEquals(60, bio?.finalMinutes)
    }
}
