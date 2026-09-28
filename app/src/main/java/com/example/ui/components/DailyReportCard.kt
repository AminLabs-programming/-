package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyReport
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalBorderSubtle
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.MementoCyan
import com.example.ui.theme.RoseBerry
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VelvetPurple
import com.example.util.JalaliDate

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DailyReportCard(
    report: DailyReport,
    onClick: () -> Unit,
    onEditClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    usePersianDigits: Boolean = true,
    isToday: Boolean = false
) {
    val dayNumberStr = if (usePersianDigits) JalaliDate.toPersianDigits(report.dayNumber) else report.dayNumber.toString()
    val durationText = JalaliDate.formatHoursAndMinutes(report.studyMinutes, usePersianDigits)

    val borderBrush = if (isToday) {
        Brush.linearGradient(
            listOf(
                AccentAmber.copy(alpha = 0.8f),
                SuccessGreen.copy(alpha = 0.5f),
                Color(0x33F59E0B)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color(0x33FFFFFF),
                CharcoalBorder,
                Color(0x15FFFFFF)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF161C27),
                        Color(0xFF0F141F)
                    )
                )
            )
            .border(
                width = if (isToday) 1.5.dp else 1.dp,
                brush = borderBrush,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header: Day Number and Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        AccentAmber.copy(alpha = 0.25f),
                                        Color(0x15F59E0B)
                                    )
                                )
                            )
                            .border(1.dp, AccentAmber.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "روز $dayNumberStr",
                            color = AccentAmber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (isToday) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SuccessGreen.copy(alpha = 0.2f))
                                .border(1.dp, SuccessGreen.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = null,
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "امروز",
                                    color = SuccessGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (usePersianDigits) JalaliDate.toPersianDigits(report.shamsiDate) else report.shamsiDate,
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    if (onEditClick != null) {
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(
                            onClick = onEditClick,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "ویرایش گزارش",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Stats Row: Study Time & Subject Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ساعت مطالعه",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = durationText,
                        color = TextPrimary,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Colored Subject Badges
                val subjectList = report.subjects.split("·", ",").map { it.trim() }.filter { it.isNotBlank() }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    subjectList.forEach { subj ->
                        val chipColor = when {
                            subj.contains("زیست") -> SuccessGreen
                            subj.contains("شیمی") -> AccentAmber
                            subj.contains("فیزیک") -> ElectricIndigo
                            subj.contains("ریاضی") || subj.contains("حسابان") || subj.contains("هندسه") -> MementoCyan
                            subj.contains("ادبیات") || subj.contains("فارسی") -> RoseBerry
                            else -> VelvetPurple
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(chipColor.copy(alpha = 0.14f))
                                .border(1.dp, chipColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = subj,
                                color = chipColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Optional Test Stats Row with Glowing Badges
            if (report.testCount != null && report.testCount > 0) {
                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF0F1420),
                                    Color(0xFF131826)
                                )
                            )
                        )
                        .border(1.dp, CharcoalBorderSubtle, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 9.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val testsStr = if (usePersianDigits) JalaliDate.toPersianDigits(report.testCount) else report.testCount.toString()
                        val correctStr = if (report.correctTests != null) {
                            if (usePersianDigits) JalaliDate.toPersianDigits(report.correctTests) else report.correctTests.toString()
                        } else null
                        val wrongStr = if (report.wrongTests != null) {
                            if (usePersianDigits) JalaliDate.toPersianDigits(report.wrongTests) else report.wrongTests.toString()
                        } else null

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "تست: ", color = TextMuted, fontSize = 12.sp)
                            Text(text = testsStr, color = MementoCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        if (correctStr != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "درست: ", color = TextMuted, fontSize = 12.sp)
                                Text(text = correctStr, color = SuccessGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (wrongStr != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "غلط/نزده: ", color = TextMuted, fontSize = 12.sp)
                                Text(text = wrongStr, color = Color(0xFFF87171), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Optional Uncompleted Part
            if (!report.uncompletedParts.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1E151A))
                        .border(1.dp, Color(0xFF4C1D24), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "پارت انجام‌نشده: ", color = TextMuted, fontSize = 11.sp)
                        Text(text = report.uncompletedParts, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                    if (!report.uncompletedReason.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "علت: ", color = TextMuted, fontSize = 11.sp)
                            Text(text = report.uncompletedReason, color = Color(0xFFFCA5A5), fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
