package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.CenterFocusStrong
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.EventNote
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyReport
import com.example.ui.components.DailyReportDialog
import com.example.ui.components.ReportDetailDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MementoMoriScreen
import com.example.ui.screens.MirrorScreen
import com.example.ui.screens.MyJourneyScreen
import com.example.ui.screens.PlannerScreen
import com.example.ui.screens.RecordBookScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WeeklyReportScreen
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentAmberSubtle
import com.example.ui.theme.CharcoalBg
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalBorderSubtle
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceCard
import com.example.ui.theme.CharcoalSurfaceElevated
import com.example.ui.theme.ContinueTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ContinueViewModel
import com.example.ui.viewmodel.ScreenTab

class MainActivity : ComponentActivity() {

    private val viewModel: ContinueViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ContinueTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    ContinueApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun ContinueApp(viewModel: ContinueViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val isReportDialogOpen by viewModel.isReportDialogOpen.collectAsState()
    val editingReport by viewModel.editingReport.collectAsState()
    val selectedDetail by viewModel.selectedReportForDetail.collectAsState()

    // BackHandler: return to Home tab if on sub-screen
    BackHandler(enabled = currentTab != ScreenTab.HOME) {
        viewModel.selectTab(ScreenTab.HOME)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(CharcoalBg)
            .statusBarsPadding(),
        bottomBar = {
            ContinueBottomNavigation(
                currentTab = currentTab,
                onTabSelect = { viewModel.selectTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CharcoalBg)
        ) {
            Crossfade(targetState = currentTab, label = "ScreenTransition") { tab ->
                when (tab) {
                    ScreenTab.HOME -> DashboardScreen(
                        uiState = uiState,
                        onLogTodayClick = { viewModel.openNewReportDialog() },
                        onReportClick = { viewModel.selectReportDetail(it) },
                        onEditReportClick = { viewModel.openEditReportDialog(it) }
                    )
                    ScreenTab.PLANNER -> PlannerScreen(
                        viewModel = viewModel
                    )
                    ScreenTab.WEEKLY_REPORT -> WeeklyReportScreen(
                        viewModel = viewModel
                    )
                    ScreenTab.JOURNAL -> RecordBookScreen(
                        uiState = uiState,
                        onReportClick = { viewModel.selectReportDetail(it) },
                        onEditReportClick = { viewModel.openEditReportDialog(it) }
                    )
                    ScreenTab.JOURNEY -> MyJourneyScreen(
                        uiState = uiState,
                        onReportClick = { viewModel.selectReportDetail(it) },
                        onEditReportClick = { viewModel.openEditReportDialog(it) },
                        onLogDateClick = {
                            viewModel.openNewReportDialog()
                        }
                    )
                    ScreenTab.MIRROR -> MirrorScreen(
                        uiState = uiState
                    )
                    ScreenTab.MEMENTO_MORI -> MementoMoriScreen(
                        uiState = uiState
                    )
                    ScreenTab.SETTINGS -> SettingsScreen(
                        uiState = uiState,
                        onSaveSettings = { viewModel.updateSettings(it) },
                        onSeedData = { viewModel.seedSampleData() },
                        onResetData = { viewModel.resetAllData() }
                    )
                }
            }

            // Report Dialog (Create / Edit)
            if (isReportDialogOpen) {
                DailyReportDialog(
                    initialReport = editingReport,
                    onDismiss = { viewModel.closeReportDialog() },
                    onSave = { iso, shamsi, min, subj, tests, corr, wrong, uncompleted, reason ->
                        viewModel.saveReport(
                            isoDate = iso,
                            shamsiDate = shamsi,
                            studyMinutes = min,
                            subjects = subj,
                            testCount = tests,
                            correctTests = corr,
                            wrongTests = wrong,
                            uncompletedParts = uncompleted,
                            uncompletedReason = reason,
                            existingId = editingReport?.id ?: 0L
                        )
                    }
                )
            }

            // Report Detail Dialog
            if (selectedDetail != null) {
                ReportDetailDialog(
                    report = selectedDetail!!,
                    onDismiss = { viewModel.selectReportDetail(null) },
                    onEdit = {
                        val rep = selectedDetail!!
                        viewModel.selectReportDetail(null)
                        viewModel.openEditReportDialog(rep)
                    },
                    onDelete = {
                        viewModel.deleteReport(selectedDetail!!)
                    },
                    usePersianDigits = uiState.settings.usePersianDigits
                )
            }
        }
    }
}

@Composable
fun ContinueBottomNavigation(
    currentTab: ScreenTab,
    onTabSelect: (ScreenTab) -> Unit
) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = Color(0xF00A0D14),
        shadowElevation = 16.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        listOf(
                            Color(0x22F59E0B),
                            Color(0x33818CF8),
                            Color(0x2238BDF8),
                            Color(0x10FFFFFF)
                        )
                    ),
                    shape = androidx.compose.ui.graphics.RectangleShape
                )
                .padding(horizontal = 6.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavItem(
                    label = "خانه",
                    icon = Icons.Outlined.Home,
                    isSelected = currentTab == ScreenTab.HOME,
                    onClick = { onTabSelect(ScreenTab.HOME) }
                )

                NavItem(
                    label = "برنامه‌ریزی",
                    icon = Icons.Outlined.EventNote,
                    isSelected = currentTab == ScreenTab.PLANNER,
                    onClick = { onTabSelect(ScreenTab.PLANNER) }
                )

                NavItem(
                    label = "گزارش هفتگی",
                    icon = Icons.Outlined.Description,
                    isSelected = currentTab == ScreenTab.WEEKLY_REPORT,
                    onClick = { onTabSelect(ScreenTab.WEEKLY_REPORT) }
                )

                NavItem(
                    label = "دفتر اعمال",
                    icon = Icons.Outlined.AutoStories,
                    isSelected = currentTab == ScreenTab.JOURNAL,
                    onClick = { onTabSelect(ScreenTab.JOURNAL) }
                )

                NavItem(
                    label = "مسیر من",
                    icon = Icons.Outlined.TrendingUp,
                    isSelected = currentTab == ScreenTab.JOURNEY,
                    onClick = { onTabSelect(ScreenTab.JOURNEY) }
                )

                NavItem(
                    label = "آینه",
                    icon = Icons.Outlined.CenterFocusStrong,
                    isSelected = currentTab == ScreenTab.MIRROR,
                    onClick = { onTabSelect(ScreenTab.MIRROR) }
                )

                NavItem(
                    label = "Memento Mori",
                    icon = Icons.Outlined.HourglassEmpty,
                    isSelected = currentTab == ScreenTab.MEMENTO_MORI,
                    onClick = { onTabSelect(ScreenTab.MEMENTO_MORI) }
                )

                NavItem(
                    label = "تنظیمات",
                    icon = Icons.Outlined.Settings,
                    isSelected = currentTab == ScreenTab.SETTINGS,
                    onClick = { onTabSelect(ScreenTab.SETTINGS) }
                )
            }
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgModifier = if (isSelected) {
        Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        AccentAmber.copy(alpha = 0.22f),
                        Color(0x12F59E0B)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        AccentAmber.copy(alpha = 0.6f),
                        Color(0x15F59E0B)
                    )
                ),
                shape = RoundedCornerShape(14.dp)
            )
    } else {
        Modifier
    }

    Box(
        modifier = bgModifier
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) AccentAmber else TextMuted,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = label,
                color = if (isSelected) AccentAmber else TextMuted,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            if (isSelected) {
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(AccentAmber)
                )
            }
        }
    }
}
