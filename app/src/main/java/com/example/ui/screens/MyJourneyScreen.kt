package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.QueryBuilder
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyReport
import com.example.ui.components.AmbientGlowBackground
import com.example.ui.components.DailyReportCard
import com.example.ui.components.GlassCard
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.CharcoalBg
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalBorderSubtle
import com.example.ui.theme.CharcoalSurfaceCard
import com.example.ui.theme.CharcoalSurfaceElevated
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.MementoCyan
import com.example.ui.theme.RoseBerry
import com.example.ui.theme.StreakHeatmapFull
import com.example.ui.theme.StreakHeatmapLow
import com.example.ui.theme.StreakHeatmapMid
import com.example.ui.theme.StreakHeatmapNone
import com.example.ui.theme.StreakHeatmapRecord
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VelvetPurple
import com.example.ui.viewmodel.ContinueUiState
import com.example.util.JalaliDate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MyJourneyScreen(
    uiState: ContinueUiState,
    onReportClick: (DailyReport) -> Unit,
    onEditReportClick: (DailyReport) -> Unit,
    onLogDateClick: (isoDate: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val usePersianDigits = uiState.settings.usePersianDigits
    val reportsByDate = remember(uiState.reports) { uiState.reports.associateBy { it.isoDate } }

    var selectedCellReport by remember { mutableStateOf<DailyReport?>(null) }
    var selectedCellDateIso by remember { mutableStateOf<String?>(null) }

    AmbientGlowBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Title Header with Glowing Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(AccentAmber.copy(alpha = 0.15f))
                        .border(1.dp, AccentAmber.copy(alpha = 0.35f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.TrendingUp,
                        contentDescription = null,
                        tint = AccentAmber,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "مسیر من",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "تقویم استمرار و رکوردهای شخصی",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // SECTION 1: تقویم استمرار (Consistency Calendar Heatmap with subtle Glassmorphism)
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                glowAccentColor = AccentAmber,
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "تقویم استمرار",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CharcoalSurfaceElevated)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "۱۴ هفته اخیر",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Heatmap Grid: 7 rows x 14 columns
                    HeatmapGrid(
                        reportsByDate = reportsByDate,
                        minimumMinutes = uiState.settings.minimumDayMinutes,
                        targetMinutes = uiState.settings.targetDayMinutes,
                        maxDayMinutes = uiState.records.maxStudyMinutesInDay,
                        onCellClick = { iso, report ->
                            selectedCellDateIso = iso
                            selectedCellReport = report
                        }
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Heatmap Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "کمتر", color = TextMuted, fontSize = 10.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            LegendBox(color = StreakHeatmapNone)
                            LegendBox(color = StreakHeatmapLow)
                            LegendBox(color = StreakHeatmapMid)
                            LegendBox(color = StreakHeatmapFull)
                            LegendBox(color = StreakHeatmapRecord)
                        }
                        Text(text = "بیشتر", color = TextMuted, fontSize = 10.sp)
                    }
                }
            }

            // Selected Day Details Card (if clicked on a cell)
            if (selectedCellDateIso != null) {
                Spacer(modifier = Modifier.height(14.dp))
                val rep = selectedCellReport
                if (rep != null) {
                    DailyReportCard(
                        report = rep,
                        onClick = { onReportClick(rep) },
                        onEditClick = { onEditReportClick(rep) },
                        usePersianDigits = usePersianDigits
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(CharcoalSurfaceCard)
                            .border(1.dp, CharcoalBorderSubtle, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val jDate = JalaliDate.fromIsoDate(selectedCellDateIso!!)
                            Column {
                                Text(
                                    text = "این روز ثبت نشد.",
                                    color = TextMuted,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = jDate.formatFull(usePersianDigits = usePersianDigits),
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AccentAmber.copy(alpha = 0.15f))
                                    .border(1.dp, AccentAmber.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                    .clickable { onLogDateClick(selectedCellDateIso!!) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "+ ثبت این روز",
                                    color = AccentAmber,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // SECTION 2: رکوردهای من (Personal Records)
            Text(
                text = "رکوردهای من",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "مقایسه صرفاً با گذشته خودت، بدون رقابت با دیگران",
                color = TextMuted,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            val records = uiState.records

            // 2-Column Grid of Records with Distinct Rich Colors
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GlowRecordStatCard(
                    title = "استمرار فعلی",
                    value = if (usePersianDigits) "${JalaliDate.toPersianDigits(records.currentStreak)} روز" else "${records.currentStreak} روز",
                    icon = Icons.Outlined.LocalFireDepartment,
                    accentColor = AccentAmber,
                    modifier = Modifier.weight(1f)
                )

                GlowRecordStatCard(
                    title = "بهترین استمرار",
                    value = if (usePersianDigits) "${JalaliDate.toPersianDigits(records.bestStreak)} روز" else "${records.bestStreak} روز",
                    icon = Icons.Outlined.EmojiEvents,
                    accentColor = StreakHeatmapRecord,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GlowRecordStatCard(
                    title = "مجموع روزهای فعال",
                    value = if (usePersianDigits) "${JalaliDate.toPersianDigits(records.totalActiveDays)} روز" else "${records.totalActiveDays} روز",
                    icon = Icons.Outlined.CalendarMonth,
                    accentColor = SuccessGreen,
                    modifier = Modifier.weight(1f)
                )

                val maxH = records.maxStudyMinutesInDay / 60
                val maxM = records.maxStudyMinutesInDay % 60
                val maxTimeStr = if (usePersianDigits) "${JalaliDate.toPersianDigits(maxH)}:${JalaliDate.toPersianDigits(String.format("%02d", maxM))}" else String.format("%02d:%02d", maxH, maxM)
                GlowRecordStatCard(
                    title = "بیشترین ساعت در یک روز",
                    value = maxTimeStr,
                    icon = Icons.Outlined.QueryBuilder,
                    accentColor = VelvetPurple,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val totalHours = records.totalStudyMinutes / 60
                GlowRecordStatCard(
                    title = "مجموع ساعات مطالعه",
                    value = if (usePersianDigits) "${JalaliDate.toPersianDigits(totalHours)} ساعت" else "$totalHours ساعت",
                    icon = Icons.Outlined.QueryBuilder,
                    accentColor = ElectricIndigo,
                    modifier = Modifier.weight(1f)
                )

                GlowRecordStatCard(
                    title = "بیشترین تست در یک روز",
                    value = if (usePersianDigits) JalaliDate.toPersianDigits(records.maxTestsInDay) else records.maxTestsInDay.toString(),
                    icon = Icons.Outlined.Quiz,
                    accentColor = MementoCyan,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            GlowRecordStatCard(
                title = "مجموع تست‌های پاسخ‌داده‌شده",
                value = if (usePersianDigits) JalaliDate.toPersianDigits(records.totalTests) else records.totalTests.toString(),
                icon = Icons.Outlined.Quiz,
                accentColor = RoseBerry,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun GlowRecordStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        glowAccentColor = accentColor,
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f))
                        .border(1.dp, accentColor.copy(alpha = 0.35f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                color = TextPrimary,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun HeatmapGrid(
    reportsByDate: Map<String, DailyReport>,
    minimumMinutes: Int,
    targetMinutes: Int,
    maxDayMinutes: Int,
    onCellClick: (isoDate: String, report: DailyReport?) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }
    val scrollState = rememberScrollState()

    val weeks = 14
    val daysTotal = weeks * 7

    val gridDates = remember {
        val list = mutableListOf<String>()
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -(daysTotal - 1))
        for (i in 0 until daysTotal) {
            list.add(dateFormat.format(cal.time))
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (w in 0 until weeks) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (d in 0 until 7) {
                        val index = w * 7 + d
                        if (index < gridDates.size) {
                            val iso = gridDates[index]
                            val report = reportsByDate[iso]
                            val minutes = report?.studyMinutes ?: 0

                            val cellColor = when {
                                minutes <= 0 -> StreakHeatmapNone
                                minutes < minimumMinutes -> StreakHeatmapLow
                                minutes < targetMinutes -> StreakHeatmapMid
                                maxDayMinutes > 0 && minutes >= maxDayMinutes -> StreakHeatmapRecord
                                else -> StreakHeatmapFull
                            }

                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(cellColor)
                                    .border(0.5.dp, Color(0x22FFFFFF), RoundedCornerShape(4.dp))
                                    .clickable { onCellClick(iso, report) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendBox(color: Color) {
    Box(
        modifier = Modifier
            .size(13.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(color)
            .border(0.5.dp, Color(0x33FFFFFF), RoundedCornerShape(3.dp))
    )
}
