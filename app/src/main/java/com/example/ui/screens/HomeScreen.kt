package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyReport
import com.example.ui.components.DailyReportCard
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentAmberSubtle
import com.example.ui.theme.CharcoalBg
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalBorderSubtle
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceCard
import com.example.ui.theme.CharcoalSurfaceElevated
import com.example.ui.theme.MementoCyan
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenSubtle
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ContinueUiState
import com.example.util.JalaliDate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HomeScreen(
    uiState: ContinueUiState,
    onLogTodayClick: () -> Unit,
    onReportClick: (DailyReport) -> Unit,
    onEditReportClick: (DailyReport) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val usePersianDigits = uiState.settings.usePersianDigits

    val streakStr = if (usePersianDigits) JalaliDate.toPersianDigits(uiState.records.currentStreak) else uiState.records.currentStreak.toString()
    val bestStreakStr = if (usePersianDigits) JalaliDate.toPersianDigits(uiState.records.bestStreak) else uiState.records.bestStreak.toString()
    val konkurDaysStr = if (usePersianDigits) JalaliDate.toPersianDigits(uiState.settings.konkurDaysRemaining) else uiState.settings.konkurDaysRemaining.toString()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // TOP HEADER: Today Shamsi Date & Konkur Countdown
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "امروز",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = uiState.todayShamsiFormatted,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Secondary Memento Mori Countdown Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(CharcoalSurfaceCard)
                    .border(1.dp, CharcoalBorderSubtle, RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(MementoCyan)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$konkurDaysStr روز تا کنکور",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // CENTRAL HERO CARD: CONSISTENCY / استمرار
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            CharcoalSurfaceCard,
                            Color(0xFF131720)
                        )
                    )
                )
                .border(1.dp, CharcoalBorder, RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Subtle Minimal Icon Indicator
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(AccentAmber.copy(alpha = 0.12f))
                        .border(1.dp, AccentAmber.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.TrendingUp,
                        contentDescription = "استمرار",
                        tint = AccentAmber,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Big Bold Numbers
                Text(
                    text = streakStr,
                    color = TextPrimary,
                    fontSize = 68.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 72.sp
                )

                Text(
                    text = "روز استمرار",
                    color = AccentAmber,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Personal Record under the streak
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(CharcoalSurfaceElevated)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "رکورد شخصی: $bestStreakStr روز",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Recent 7-Day Consistency Visualization
                RecentSevenDaysStrip(
                    reports = uiState.reports,
                    minimumMinutes = uiState.settings.minimumDayMinutes,
                    usePersianDigits = usePersianDigits
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // PRIMARY CTA
        if (uiState.todayReport == null) {
            // Not logged today yet
            Button(
                onClick = onLogTodayClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentAmber,
                    contentColor = CharcoalBg
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ثبت گزارش امروز",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            // Logged today: show confirmation and summary card
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SuccessGreenSubtle)
                        .border(1.dp, SuccessGreen.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "امروز ثبت شد — استمرار حفظ شد",
                            color = SuccessGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Text(
                        text = "ویرایش",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .clickable { onEditReportClick(uiState.todayReport) }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                DailyReportCard(
                    report = uiState.todayReport,
                    onClick = { onReportClick(uiState.todayReport) },
                    onEditClick = { onEditReportClick(uiState.todayReport) },
                    usePersianDigits = usePersianDigits,
                    isToday = true
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // NEXT RECORD MILESTONE CARD (رکورد بعدی)
        val currentStreak = uiState.records.currentStreak
        val bestStreak = uiState.records.bestStreak
        val nextMilestoneText = when {
            bestStreak == 0 -> "اولین قدم استمرار را بردار."
            currentStreak >= bestStreak && currentStreak > 0 -> "رکورد شخصی جدید! هر روز ادامه‌دادن، مرزهای تو را گسترش می‌دهد."
            else -> {
                val diff = bestStreak - currentStreak
                val diffStr = if (usePersianDigits) JalaliDate.toPersianDigits(diff) else diff.toString()
                "$diffStr روز تا شکستن رکورد شخصی ($bestStreakStr روز)"
            }
        }

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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(AccentAmberSubtle),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎯",
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "رکورد بعدی",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = nextMilestoneText,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // WEEKLY ANALYSIS SNIPPET (این هفته)
        val weekly = uiState.weeklyStats
        val thisWeekH = weekly.thisWeekMinutes / 60
        val thisWeekM = weekly.thisWeekMinutes % 60
        val thisWeekStr = if (usePersianDigits) {
            "${JalaliDate.toPersianDigits(thisWeekH)}h ${JalaliDate.toPersianDigits(thisWeekM)}m"
        } else {
            "${thisWeekH}h ${thisWeekM}m"
        }

        val deltaSign = if (weekly.deltaMinutes >= 0) "+" else "-"
        val absDeltaH = kotlin.math.abs(weekly.deltaMinutes) / 60
        val absDeltaM = kotlin.math.abs(weekly.deltaMinutes) % 60
        val deltaStr = if (usePersianDigits) {
            "$deltaSign${JalaliDate.toPersianDigits(absDeltaH)}h ${JalaliDate.toPersianDigits(absDeltaM)}m"
        } else {
            "$deltaSign${absDeltaH}h ${absDeltaM}m"
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CharcoalSurfaceCard)
                .border(1.dp, CharcoalBorderSubtle, RoundedCornerShape(16.dp))
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "این هفته",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (weekly.deltaMinutes >= 0) SuccessGreen.copy(alpha = 0.15f) else CharcoalSurfaceElevated)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "$deltaStr نسبت به هفته قبل",
                            color = if (weekly.deltaMinutes >= 0) SuccessGreen else TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "مجموع مطالعه", color = TextMuted, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = thisWeekStr, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "روزهای فعال", color = TextMuted, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        val activeDaysStr = if (usePersianDigits) JalaliDate.toPersianDigits(weekly.thisWeekActiveDays) else weekly.thisWeekActiveDays.toString()
                        Text(text = "$activeDaysStr از ۷", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "تست‌های این هفته", color = TextMuted, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        val testsStr = if (usePersianDigits) JalaliDate.toPersianDigits(weekly.thisWeekTests) else weekly.thisWeekTests.toString()
                        Text(text = testsStr, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // PHILOSOPHICAL FOOTER REFLECTION
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "«هدف، یک روز عالی نیست؛ هدف، ادامه دادن است.»",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "دیروز تمام شد. امروز هنوز فرصت ادامه دادن است.",
                color = TextMuted,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun RecentSevenDaysStrip(
    reports: List<DailyReport>,
    minimumMinutes: Int,
    usePersianDigits: Boolean
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val reportsByDate = reports.associateBy { it.isoDate }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Last 7 days, left-to-right (or oldest to today)
        for (i in 6 downTo 0) {
            val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -i) }
            val iso = dateFormat.format(cal.time)
            val report = reportsByDate[iso]
            val hasMinStudy = (report?.studyMinutes ?: 0) >= minimumMinutes
            val isToday = i == 0
            val dayName = when (cal.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SATURDAY -> "ش"
                Calendar.SUNDAY -> "ی"
                Calendar.MONDAY -> "د"
                Calendar.TUESDAY -> "س"
                Calendar.WEDNESDAY -> "چ"
                Calendar.THURSDAY -> "پ"
                Calendar.FRIDAY -> "ج"
                else -> ""
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when {
                                hasMinStudy && (report?.studyMinutes ?: 0) >= 300 -> AccentAmber
                                hasMinStudy -> AccentAmber.copy(alpha = 0.55f)
                                isToday -> CharcoalBorder
                                else -> CharcoalSurfaceElevated
                            }
                        )
                        .border(
                            width = if (isToday) 1.5.dp else 1.dp,
                            color = if (isToday) AccentAmber else CharcoalBorderSubtle,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (hasMinStudy) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = if ((report?.studyMinutes ?: 0) >= 300) CharcoalBg else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    } else if (isToday) {
                        Text(
                            text = "امروز",
                            color = AccentAmber,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dayName,
                    color = if (isToday) AccentAmber else TextMuted,
                    fontSize = 11.sp,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}
