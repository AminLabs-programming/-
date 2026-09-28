package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlanStatus
import com.example.data.model.StudyType
import com.example.ui.components.AmbientGlowBackground
import com.example.ui.components.GlassCard
import com.example.ui.components.PremiumGlowButton
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.CharcoalBg
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalBorderSubtle
import com.example.ui.theme.CharcoalSurfaceCard
import com.example.ui.theme.CharcoalSurfaceElevated
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.MementoCyan
import com.example.ui.theme.RoseBerry
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VelvetPurple
import com.example.ui.viewmodel.ContinueViewModel
import com.example.util.JalaliDate

@Composable
fun WeeklyReportScreen(
    viewModel: ContinueViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val weeklyReport by viewModel.weeklyReportSummary.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val usePersianDigits = uiState.settings.usePersianDigits

    AmbientGlowBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MementoCyan.copy(alpha = 0.15f))
                            .border(1.dp, MementoCyan.copy(alpha = 0.35f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Description,
                            contentDescription = null,
                            tint = MementoCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "گزارش تحلیلی هفتگی",
                            color = TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "کارنامه تفکیکی کنکوری و نهایی + خروجی PDF",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // PRIMARY ACTION: DOWNLOAD / SHARE PDF BUTTON WITH GLOW
            PremiumGlowButton(
                text = "دریافت خروجی PDF گزارش هفتگی",
                onClick = {
                    val file = viewModel.exportWeeklyReportPdf(context)
                    if (file == null) {
                        Toast.makeText(context, "خطا در ایجاد فایل PDF", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                glowColor = MementoCyan,
                containerBrush = Brush.horizontalGradient(
                    listOf(
                        Color(0xFF0284C7),
                        Color(0xFF06B6D4),
                        Color(0xFF0EA5E9)
                    )
                ),
                contentColor = Color.White,
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.FileDownload,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.height(22.dp))

            val report = weeklyReport
            if (report == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "در حال تجمیع اطلاعات هفته...", color = TextMuted, fontSize = 13.sp)
                }
            } else {
                // Date Range Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF141A28))
                        .border(1.dp, CharcoalBorderSubtle, RoundedCornerShape(14.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "بازه تحلیلی هفته:", color = TextMuted, fontSize = 12.sp)
                        val startSh = if (usePersianDigits) JalaliDate.toPersianDigits(report.startDateShamsi) else report.startDateShamsi
                        val endSh = if (usePersianDigits) JalaliDate.toPersianDigits(report.endDateShamsi) else report.endDateShamsi
                        Text(text = "$startSh  الی  $endSh", color = AccentAmber, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 1. KPI Cards: Total Study & Daily Average
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlassCard(
                        modifier = Modifier.weight(1f),
                        glowAccentColor = AccentAmber,
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "کل مطالعه هفته", color = TextMuted, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            val totStr = JalaliDate.formatDurationText(report.totalStudyMinutes, usePersianDigits)
                            Text(text = totStr, color = AccentAmber, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    GlassCard(
                        modifier = Modifier.weight(1f),
                        glowAccentColor = SuccessGreen,
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "میانگین روزانه", color = TextMuted, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            val avgStr = JalaliDate.formatDurationText(report.dailyAverageMinutes, usePersianDigits)
                            Text(text = avgStr, color = SuccessGreen, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. CRITICAL FEATURE: KONKUR VS FINALS SEPARATION
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    glowAccentColor = ElectricIndigo,
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "تفکیک اختصاصی: کنکوری vs نهایی",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(Icons.Outlined.PieChart, contentDescription = null, tint = ElectricIndigo, modifier = Modifier.size(18.dp))
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        val totalMin = (report.totalKonkurMinutes + report.totalFinalMinutes).coerceAtLeast(1)
                        val konkurRatio = report.totalKonkurMinutes.toFloat() / totalMin.toFloat()

                        LinearProgressIndicator(
                            progress = { konkurRatio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MementoCyan,
                            trackColor = VelvetPurple
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MementoCyan))
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(text = "مطالعه کنکوری (تستی)", color = TextMuted, fontSize = 10.sp)
                                    val kStr = JalaliDate.formatDurationText(report.totalKonkurMinutes, usePersianDigits)
                                    Text(text = kStr, color = MementoCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(VelvetPurple))
                                Spacer(modifier = Modifier.width(6.dp))
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "مطالعه نهایی (تشریحی)", color = TextMuted, fontSize = 10.sp)
                                    val fStr = JalaliDate.formatDurationText(report.totalFinalMinutes, usePersianDigits)
                                    Text(text = fStr, color = VelvetPurple, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Parts Summary Card (انجام‌شده، ناقص، انجام‌نشده و تست‌ها)
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "وضعیت پارت‌های هفتگی", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Quiz, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                val tStr = if (usePersianDigits) JalaliDate.toPersianDigits(report.totalTests) else report.totalTests.toString()
                                Text(text = "$tStr تست", color = AccentAmber, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "انجام‌شده", color = TextMuted, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                val comp = if (usePersianDigits) JalaliDate.toPersianDigits(report.completedPartsCount) else report.completedPartsCount.toString()
                                Text(text = comp, color = SuccessGreen, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "ناقص انجام‌شده", color = TextMuted, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                val part = if (usePersianDigits) JalaliDate.toPersianDigits(report.partialPartsCount) else report.partialPartsCount.toString()
                                Text(text = part, color = AccentAmber, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "انجام‌نشده", color = TextMuted, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                val notDone = if (usePersianDigits) JalaliDate.toPersianDigits(report.notDonePartsCount) else report.notDonePartsCount.toString()
                                Text(text = notDone, color = Color(0xFFF87171), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 4. DETAILED LESSON BREAKDOWN TABLE (ساعت مطالعه هر درس + تفکیک کنکوری و نهایی)
                Text(
                    text = "جدول عملکرد به تفکیک هر درس",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF131824))
                        .border(1.dp, CharcoalBorderSubtle, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    // Table Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "درس", color = TextMuted, fontSize = 11.sp, modifier = Modifier.weight(1.3f))
                        Text(text = "کل ساعت", color = TextMuted, fontSize = 11.sp, modifier = Modifier.weight(1f))
                        Text(text = "کنکوری / نهایی", color = TextMuted, fontSize = 11.sp, modifier = Modifier.weight(1.5f))
                        Text(text = "تست", color = TextMuted, fontSize = 11.sp, modifier = Modifier.weight(0.7f))
                    }

                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CharcoalBorder))

                    report.subjectBreakdowns.forEach { sub ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = sub.subject, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.3f))
                            val totH = "${sub.totalMinutes / 60}h ${sub.totalMinutes % 60}m"
                            Text(text = if (usePersianDigits) JalaliDate.toPersianDigits(totH) else totH, color = AccentAmber, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))

                            val kH = "${sub.konkurMinutes / 60}h"
                            val fH = "${sub.finalMinutes / 60}h"
                            val splitText = if (usePersianDigits) "${JalaliDate.toPersianDigits(kH)} / ${JalaliDate.toPersianDigits(fH)}" else "$kH / $fH"
                            Text(text = splitText, color = TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1.5f))

                            val tests = if (usePersianDigits) JalaliDate.toPersianDigits(sub.totalTests) else sub.totalTests.toString()
                            Text(text = tests, color = MementoCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.7f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 5. UNCOMPLETED & INCOMPLETE PARTS PATHOLOGY
                Text(
                    text = "آسیب‌شناسی پارت‌های ناقص یا انجام‌نشده",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (report.uncompletedItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0F231B))
                            .border(1.dp, SuccessGreen.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "تبریک! در این هفته تمام پارت‌های مطالعه‌شده بدون نقص اجرا شده‌اند.",
                            color = SuccessGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    report.uncompletedItems.forEach { item ->
                        val isPartial = item.status == PlanStatus.PARTIAL.code
                        val statusColor = if (isPartial) AccentAmber else Color(0xFFF87171)
                        val statusLabel = if (isPartial) "ناقص" else "انجام‌نشده"

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1B161E))
                                .border(1.dp, statusColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${item.subject}: ${item.title}",
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(statusColor.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = statusLabel, color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                if (!item.reason.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "علت: ${item.reason}",
                                        color = Color(0xFFFCA5A5),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
