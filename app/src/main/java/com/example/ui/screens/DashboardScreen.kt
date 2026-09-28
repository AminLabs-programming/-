package com.example.ui.screens

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
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyReport
import com.example.ui.components.AmbientGlowBackground
import com.example.ui.components.DailyReportCard
import com.example.ui.components.GlassCard
import com.example.ui.components.PremiumGlowButton
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentAmberGlow
import com.example.ui.theme.AccentAmberLight
import com.example.ui.theme.CharcoalBg
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalBorderSubtle
import com.example.ui.theme.CharcoalSurfaceCard
import com.example.ui.theme.CharcoalSurfaceElevated
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.MementoCyan
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenGlow
import com.example.ui.theme.SuccessGreenSubtle
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VelvetPurple
import com.example.ui.viewmodel.ContinueUiState
import com.example.util.JalaliDate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun Dashboard(
    uiState: ContinueUiState,
    onLogTodayClick: () -> Unit,
    onReportClick: (DailyReport) -> Unit,
    onEditReportClick: (DailyReport) -> Unit,
    modifier: Modifier = Modifier
) {
    DashboardScreen(
        uiState = uiState,
        onLogTodayClick = onLogTodayClick,
        onReportClick = onReportClick,
        onEditReportClick = onEditReportClick,
        modifier = modifier
    )
}

@Composable
fun DashboardScreen(
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
    val examDaysRemainingStr = if (usePersianDigits) JalaliDate.toPersianDigits(uiState.settings.konkurDaysRemaining) else uiState.settings.konkurDaysRemaining.toString()

    AmbientGlowBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .testTag("dashboard_root")
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // TOP HEADER: Today's date and Exam countdown as a secondary element
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_header_row"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. TODAY'S DATE
                Column(
                    modifier = Modifier.testTag("today_date_section")
                ) {
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
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("today_date_text")
                    )
                }

                // 2. EXAM COUNTDOWN (Secondary Element with Cyan Ambient Glow)
                Box(
                    modifier = Modifier
                        .testTag("exam_countdown_badge")
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF101726),
                                    Color(0xFF0C1D2A)
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.horizontalGradient(
                                listOf(
                                    MementoCyan.copy(alpha = 0.5f),
                                    Color(0x2238BDF8)
                                )
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(MementoCyan)
                                .drawBehind {
                                    drawCircle(
                                        color = MementoCyan.copy(alpha = 0.6f),
                                        radius = size.width * 1.5f
                                    )
                                }
                        )
                        Spacer(modifier = Modifier.width(7.dp))
                        Text(
                            text = "$examDaysRemainingStr روز تا کنکور",
                            color = MementoCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.testTag("exam_countdown_text")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // CENTRAL HERO SECTION: STUDY CONSISTENCY STREAK COUNT WITH AMBER GLOW
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("consistency_streak_hero"),
                glowAccentColor = AccentAmber,
                shape = RoundedCornerShape(26.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .drawBehind {
                            // Radial amber glow behind the streak number
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0x35F59E0B),
                                        Color(0x10F59E0B),
                                        Color.Transparent
                                    ),
                                    center = Offset(size.width * 0.5f, size.height * 0.35f),
                                    radius = size.width * 0.5f
                                )
                            )
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Minimalist trend icon badge with amber outer ring
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        AccentAmber.copy(alpha = 0.25f),
                                        Color(0x11F59E0B)
                                    )
                                )
                            )
                            .border(1.dp, AccentAmber.copy(alpha = 0.45f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.TrendingUp,
                            contentDescription = "استمرار",
                            tint = AccentAmberLight,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Dominant Streak Count
                    Text(
                        text = streakStr,
                        color = TextPrimary,
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 76.sp,
                        modifier = Modifier.testTag("streak_count_text")
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(AccentAmber)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "روز استمرار",
                            color = AccentAmber,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.testTag("streak_label_text")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Personal best streak badge
                    Box(
                        modifier = Modifier
                            .testTag("personal_best_badge")
                            .clip(RoundedCornerShape(20.dp))
                            .background(CharcoalSurfaceElevated)
                            .border(1.dp, CharcoalBorderSubtle, RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.EmojiEvents,
                                contentDescription = null,
                                tint = AccentAmber,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "رکورد شخصی: $bestStreakStr روز",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Recent 7-Day Consistency Visualization Strip
                    RecentSevenDaysStrip(
                        reports = uiState.reports,
                        minimumMinutes = uiState.settings.minimumDayMinutes,
                        usePersianDigits = usePersianDigits
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // PRIMARY CTA: GLOWING BUTTON OR SUMMARY CARD
            if (uiState.todayReport == null) {
                // Not registered today
                PremiumGlowButton(
                    text = "+ ثبت گزارش امروز",
                    onClick = onLogTodayClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("log_today_button"),
                    glowColor = AccentAmber,
                    containerBrush = Brush.horizontalGradient(
                        listOf(
                            Color(0xFFF59E0B),
                            Color(0xFFEA580C)
                        )
                    ),
                    contentColor = Color(0xFF0F141F),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color(0xFF0F141F),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )
            } else {
                // Already registered today
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("today_registered_summary")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF0D251C),
                                        Color(0xFF0E1A17)
                                    )
                                )
                            )
                            .border(
                                width = 1.dp,
                                brush = Brush.horizontalGradient(
                                    listOf(
                                        SuccessGreen.copy(alpha = 0.6f),
                                        Color(0x2210B981)
                                    )
                                ),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = null,
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "امروز ثبت شد — استمرار حفظ شد",
                                    color = SuccessGreen,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x3310B981))
                                    .clickable { onEditReportClick(uiState.todayReport) }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "ویرایش",
                                    color = SuccessGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.testTag("edit_today_report_button")
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

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

            // NEXT RECORD MILESTONE CARD (رکورد بعدی with Indigo/Purple glow)
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

            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("next_milestone_card"),
                glowAccentColor = ElectricIndigo,
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(ElectricIndigo.copy(alpha = 0.15f))
                            .border(1.dp, ElectricIndigo.copy(alpha = 0.35f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🎯",
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "رکورد بعدی",
                            color = ElectricIndigo,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(3.dp))
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

            // WEEKLY ANALYSIS OVERVIEW (Multi-Colored Glass Card)
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

            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("weekly_overview_card"),
                glowAccentColor = VelvetPurple,
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "این هفته",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (weekly.deltaMinutes >= 0) SuccessGreen.copy(alpha = 0.15f) else CharcoalSurfaceElevated)
                                .border(
                                    width = 1.dp,
                                    color = if (weekly.deltaMinutes >= 0) SuccessGreen.copy(alpha = 0.4f) else CharcoalBorderSubtle,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "$deltaStr نسبت به هفته قبل",
                                color = if (weekly.deltaMinutes >= 0) SuccessGreen else TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "مجموع مطالعه", color = TextMuted, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = thisWeekStr, color = VelvetPurple, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "روزهای فعال", color = TextMuted, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            val activeDaysStr = if (usePersianDigits) JalaliDate.toPersianDigits(weekly.thisWeekActiveDays) else weekly.thisWeekActiveDays.toString()
                            Text(text = "$activeDaysStr از ۷", color = SuccessGreen, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "تست‌های این هفته", color = TextMuted, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            val testsStr = if (usePersianDigits) JalaliDate.toPersianDigits(weekly.thisWeekTests) else weekly.thisWeekTests.toString()
                            Text(text = testsStr, color = MementoCyan, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

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

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
