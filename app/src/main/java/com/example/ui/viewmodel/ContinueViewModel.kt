package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.DailyReport
import com.example.data.model.PlanStatus
import com.example.data.model.StudyPlanItem
import com.example.data.model.StudyType
import com.example.data.model.UserSettings
import com.example.data.repository.ContinueRepository
import com.example.data.repository.MirrorInsight
import com.example.data.repository.PersonalRecords
import com.example.data.repository.WeeklyStats
import com.example.util.JalaliDate
import com.example.util.PdfReportGenerator
import com.example.util.WeeklyReportSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class ContinueUiState(
    val reports: List<DailyReport> = emptyList(),
    val settings: UserSettings = UserSettings(),
    val todayReport: DailyReport? = null,
    val records: PersonalRecords = PersonalRecords(),
    val weeklyStats: WeeklyStats = WeeklyStats(),
    val mirrorInsights: List<MirrorInsight> = emptyList(),
    val todayIsoDate: String = "",
    val todayShamsiFormatted: String = "",
    val isLoading: Boolean = false
)

enum class ScreenTab(val title: String) {
    HOME("خانه"),
    PLANNER("برنامه‌ریزی"),
    WEEKLY_REPORT("گزارش هفتگی"),
    JOURNAL("دفتر اعمال"),
    JOURNEY("مسیر من"),
    MIRROR("آینه"),
    MEMENTO_MORI("Memento Mori"),
    SETTINGS("تنظیمات")
}

class ContinueViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val repository = ContinueRepository(
        database.dailyReportDao(),
        database.userSettingsDao(),
        database.studyPlanDao()
    )

    val currentTab = MutableStateFlow(ScreenTab.HOME)

    val isReportDialogOpen = MutableStateFlow(false)
    val editingReport = MutableStateFlow<DailyReport?>(null)
    val selectedReportForDetail = MutableStateFlow<DailyReport?>(null)

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val todayIsoDate: String = dateFormat.format(Calendar.getInstance().time)

    // Planner state
    val selectedPlanDateIso = MutableStateFlow(todayIsoDate)
    val planItemsForDate: StateFlow<List<StudyPlanItem>> = selectedPlanDateIso.flatMapLatest { iso ->
        repository.getPlanItemsForDate(iso)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allWeeklyPlanItems: StateFlow<List<StudyPlanItem>> = repository.allPlanItems.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Weekly Report state
    val weeklyReportSummary = MutableStateFlow<WeeklyReportSummary?>(null)
    val isGeneratingPdf = MutableStateFlow(false)

    val uiState: StateFlow<ContinueUiState> = combine(
        repository.allReports,
        repository.settings
    ) { reports, settings ->
        val todayCal = Calendar.getInstance()
        val todayIso = dateFormat.format(todayCal.time)
        val todayReport = reports.find { it.isoDate == todayIso }

        val records = repository.calculateRecords(reports, settings.minimumDayMinutes)
        val weeklyStats = repository.calculateWeeklyStats(reports)
        val insights = repository.generateMirrorInsights(reports, weeklyStats)

        ContinueUiState(
            reports = reports,
            settings = settings,
            todayReport = todayReport,
            records = records,
            weeklyStats = weeklyStats,
            mirrorInsights = insights,
            todayIsoDate = todayIso,
            todayShamsiFormatted = JalaliDate.todayPersianFull()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ContinueUiState()
    )

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
            refreshWeeklyReport()
        }
    }

    fun selectTab(tab: ScreenTab) {
        currentTab.value = tab
        if (tab == ScreenTab.WEEKLY_REPORT) {
            refreshWeeklyReport()
        }
    }

    fun setSelectedPlanDate(isoDate: String) {
        selectedPlanDateIso.value = isoDate
    }

    fun addOrUpdatePlanItem(
        id: Long = 0,
        isoDate: String,
        subject: String,
        title: String,
        studyType: StudyType,
        plannedMinutes: Int,
        actualMinutes: Int,
        plannedTests: Int,
        actualTests: Int,
        status: PlanStatus,
        reason: String?
    ) {
        viewModelScope.launch {
            val item = StudyPlanItem(
                id = id,
                isoDate = isoDate,
                subject = subject,
                title = title,
                studyType = studyType.code,
                plannedMinutes = plannedMinutes,
                actualMinutes = actualMinutes,
                plannedTests = plannedTests,
                actualTests = actualTests,
                status = status.code,
                reason = reason
            )
            repository.insertStudyPlan(item)
            refreshWeeklyReport()
        }
    }

    fun updatePlanStatus(item: StudyPlanItem, newStatus: PlanStatus, actualMin: Int? = null, actualTests: Int? = null, reason: String? = null) {
        viewModelScope.launch {
            val updated = item.copy(
                status = newStatus.code,
                actualMinutes = actualMin ?: if (newStatus == PlanStatus.COMPLETED) item.plannedMinutes else item.actualMinutes,
                actualTests = actualTests ?: if (newStatus == PlanStatus.COMPLETED) item.plannedTests else item.actualTests,
                reason = reason ?: item.reason
            )
            repository.updateStudyPlan(updated)
            refreshWeeklyReport()
        }
    }

    fun deletePlanItem(item: StudyPlanItem) {
        viewModelScope.launch {
            repository.deleteStudyPlan(item)
            refreshWeeklyReport()
        }
    }

    fun refreshWeeklyReport() {
        viewModelScope.launch {
            val endCal = Calendar.getInstance()
            val endIso = dateFormat.format(endCal.time)
            val startCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -6) }
            val startIso = dateFormat.format(startCal.time)

            val summary = repository.buildWeeklyReportSummary(startIso, endIso)
            weeklyReportSummary.value = summary
        }
    }

    fun exportWeeklyReportPdf(context: Context): File? {
        val summary = weeklyReportSummary.value ?: return null
        return PdfReportGenerator.generateAndShareWeeklyReportPdf(
            context = context,
            report = summary,
            usePersianDigits = uiState.value.settings.usePersianDigits
        )
    }

    fun openNewReportDialog() {
        editingReport.value = null
        isReportDialogOpen.value = true
    }

    fun openEditReportDialog(report: DailyReport) {
        editingReport.value = report
        isReportDialogOpen.value = true
    }

    fun closeReportDialog() {
        isReportDialogOpen.value = false
        editingReport.value = null
    }

    fun selectReportDetail(report: DailyReport?) {
        selectedReportForDetail.value = report
    }

    fun saveReport(
        isoDate: String,
        shamsiDate: String,
        studyMinutes: Int,
        subjects: String,
        testCount: Int?,
        correctTests: Int?,
        wrongTests: Int?,
        uncompletedParts: String?,
        uncompletedReason: String?,
        existingId: Long = 0
    ) {
        viewModelScope.launch {
            val currentReports = uiState.value.reports
            val dayNumber = if (existingId != 0L) {
                currentReports.find { it.id == existingId }?.dayNumber ?: (currentReports.size + 1)
            } else {
                currentReports.size + 1
            }

            val report = DailyReport(
                id = existingId,
                isoDate = isoDate,
                shamsiDate = shamsiDate,
                dayNumber = dayNumber,
                studyMinutes = studyMinutes,
                subjects = subjects,
                testCount = testCount,
                correctTests = correctTests,
                wrongTests = wrongTests,
                uncompletedParts = uncompletedParts,
                uncompletedReason = uncompletedReason
            )
            repository.insertReport(report)
            closeReportDialog()
        }
    }

    fun deleteReport(report: DailyReport) {
        viewModelScope.launch {
            repository.deleteReport(report)
            if (selectedReportForDetail.value?.id == report.id) {
                selectedReportForDetail.value = null
            }
        }
    }

    fun updateSettings(settings: UserSettings) {
        viewModelScope.launch {
            repository.saveSettings(settings)
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.deleteAllReports()
            refreshWeeklyReport()
        }
    }

    fun seedSampleData() {
        viewModelScope.launch {
            repository.deleteAllReports()
            repository.seedInitialDataIfEmpty()
            refreshWeeklyReport()
        }
    }
}
