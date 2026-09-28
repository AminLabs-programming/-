package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.EventNote
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.PendingActions
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.PlanStatus
import com.example.data.model.StudyPlanItem
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun PlannerScreen(
    viewModel: ContinueViewModel,
    modifier: Modifier = Modifier
) {
    val selectedDate by viewModel.selectedPlanDateIso.collectAsState()
    val planItems by viewModel.planItemsForDate.collectAsState()
    val allPlanItems by viewModel.allWeeklyPlanItems.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val usePersianDigits = uiState.settings.usePersianDigits

    var isAddPartDialogOpen by remember { mutableStateOf(false) }
    var partToEditStatus by remember { mutableStateOf<StudyPlanItem?>(null) }
    var activePlannerTab by remember { mutableStateOf("DAILY") } // "DAILY" or "WEEKLY"

    AmbientGlowBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                            .background(AccentAmber.copy(alpha = 0.15f))
                            .border(1.dp, AccentAmber.copy(alpha = 0.35f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.EventNote,
                            contentDescription = null,
                            tint = AccentAmber,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "برنامه‌ریزی مطالعه",
                            color = TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "مدیریت پارت‌های روزانه و اهداف هفتگی",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }

                // Add Part Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(AccentAmber)
                        .clickable { isAddPartDialogOpen = true }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color(0xFF0F141F),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "پارت جدید",
                            color = Color(0xFF0F141F),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Daily vs Weekly Switch Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF131824))
                    .border(1.dp, CharcoalBorderSubtle, RoundedCornerShape(14.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (activePlannerTab == "DAILY") AccentAmber else Color.Transparent)
                        .clickable { activePlannerTab = "DAILY" }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "برنامه روزانه",
                        color = if (activePlannerTab == "DAILY") Color(0xFF0F141F) else TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (activePlannerTab == "WEEKLY") AccentAmber else Color.Transparent)
                        .clickable { activePlannerTab = "WEEKLY" }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "نمای کل هفته",
                        color = if (activePlannerTab == "WEEKLY") Color(0xFF0F141F) else TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (activePlannerTab == "DAILY") {
                // Days of current week horizontal strip (Sat to Fri)
                DaysOfWeekStrip(
                    selectedIso = selectedDate,
                    onSelectDate = { viewModel.setSelectedPlanDate(it) },
                    usePersianDigits = usePersianDigits
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stats overview for selected date
                val totalPlannedMin = planItems.sumOf { it.plannedMinutes }
                val totalActualMin = planItems.filter { it.status != PlanStatus.NOT_DONE.code }.sumOf { it.actualMinutes }
                val konkurMin = planItems.filter { it.studyType == StudyType.KONKUR.code && it.status != PlanStatus.NOT_DONE.code }.sumOf { it.actualMinutes }
                val finalMin = planItems.filter { it.studyType == StudyType.FINAL.code && it.status != PlanStatus.NOT_DONE.code }.sumOf { it.actualMinutes }
                val completedCount = planItems.count { it.status == PlanStatus.COMPLETED.code }

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    glowAccentColor = AccentAmber,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "مطالعه امروز", color = TextMuted, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            val timeStr = JalaliDate.formatDurationText(totalActualMin, usePersianDigits)
                            Text(text = timeStr, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "کنکوری / نهایی", color = TextMuted, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            val kStr = "${konkurMin / 60}h"
                            val fStr = "${finalMin / 60}h"
                            Text(
                                text = if (usePersianDigits) "${JalaliDate.toPersianDigits(kStr)} / ${JalaliDate.toPersianDigits(fStr)}" else "$kStr / $fStr",
                                color = MementoCyan,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "پیشرفت پارت‌ها", color = TextMuted, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            val compStr = if (usePersianDigits) "${JalaliDate.toPersianDigits(completedCount)} از ${JalaliDate.toPersianDigits(planItems.size)}" else "$completedCount/${planItems.size}"
                            Text(text = compStr, color = SuccessGreen, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // List of Study Parts for the selected day
                if (planItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Outlined.PendingActions,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "هیچ پارتی برای این روز تعریف نشده است.",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "با زدن «پارت جدید»، برنامه مطالعه امروزت را بنویس.",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(planItems, key = { it.id }) { item ->
                            StudyPlanItemCard(
                                item = item,
                                usePersianDigits = usePersianDigits,
                                onStatusClick = { partToEditStatus = item },
                                onDeleteClick = { viewModel.deletePlanItem(item) }
                            )
                        }
                    }
                }
            } else {
                // WEEKLY OVERVIEW: All week items categorized by subject and type
                WeeklyOverviewTab(
                    items = allPlanItems,
                    usePersianDigits = usePersianDigits
                )
            }
        }

        // Add Part Dialog
        if (isAddPartDialogOpen) {
            AddStudyPartDialog(
                defaultIsoDate = selectedDate,
                onDismiss = { isAddPartDialogOpen = false },
                onSave = { iso, subj, title, type, planMin, planTest ->
                    viewModel.addOrUpdatePlanItem(
                        isoDate = iso,
                        subject = subj,
                        title = title,
                        studyType = type,
                        plannedMinutes = planMin,
                        actualMinutes = planMin,
                        plannedTests = planTest,
                        actualTests = planTest,
                        status = PlanStatus.COMPLETED,
                        reason = null
                    )
                    isAddPartDialogOpen = false
                }
            )
        }

        // Status Update Dialog (Completed, Partial, Not Done)
        if (partToEditStatus != null) {
            UpdatePartStatusDialog(
                item = partToEditStatus!!,
                onDismiss = { partToEditStatus = null },
                onSave = { status, actualMin, actualTest, reason ->
                    viewModel.updatePlanStatus(
                        item = partToEditStatus!!,
                        newStatus = status,
                        actualMin = actualMin,
                        actualTests = actualTest,
                        reason = reason
                    )
                    partToEditStatus = null
                }
            )
        }
    }
}

@Composable
fun StudyPlanItemCard(
    item: StudyPlanItem,
    usePersianDigits: Boolean,
    onStatusClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val isKonkur = item.studyType == StudyType.KONKUR.code
    val status = PlanStatus.fromCode(item.status)

    val statusColor = when (status) {
        PlanStatus.COMPLETED -> SuccessGreen
        PlanStatus.PARTIAL -> AccentAmber
        PlanStatus.NOT_DONE -> Color(0xFFF87171)
    }

    val typeBadgeColor = if (isKonkur) MementoCyan else VelvetPurple
    val typeTitle = if (isKonkur) "کنکوری" else "نهایی"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF161B26),
                        Color(0xFF0F141E)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        statusColor.copy(alpha = 0.5f),
                        CharcoalBorderSubtle
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Type Badge: Konkur vs Final
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(typeBadgeColor.copy(alpha = 0.15f))
                            .border(1.dp, typeBadgeColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = typeTitle,
                            color = typeBadgeColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = item.subject,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Interactive Status Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusColor.copy(alpha = 0.18f))
                        .border(1.dp, statusColor.copy(alpha = 0.45f), RoundedCornerShape(8.dp))
                        .clickable { onStatusClick() }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val icon = when (status) {
                            PlanStatus.COMPLETED -> Icons.Outlined.CheckCircle
                            PlanStatus.PARTIAL -> Icons.Outlined.WarningAmber
                            PlanStatus.NOT_DONE -> Icons.Outlined.RemoveCircleOutline
                        }
                        Icon(icon, contentDescription = null, tint = statusColor, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = status.title,
                            color = statusColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.title,
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val minText = if (usePersianDigits) JalaliDate.toPersianDigits(item.actualMinutes) else item.actualMinutes.toString()
                    Text(text = "مدت: ", color = TextMuted, fontSize = 11.sp)
                    Text(text = "$minText دقیقه", color = AccentAmber, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    if (item.actualTests > 0) {
                        Spacer(modifier = Modifier.width(14.dp))
                        val testText = if (usePersianDigits) JalaliDate.toPersianDigits(item.actualTests) else item.actualTests.toString()
                        Text(text = "تست: ", color = TextMuted, fontSize = 11.sp)
                        Text(text = testText, color = MementoCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "حذف",
                        tint = TextMuted,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            // Reason display if partial or not done
            if (!item.reason.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF22161A))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "علت: ${item.reason}",
                        color = Color(0xFFFCA5A5),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun DaysOfWeekStrip(
    selectedIso: String,
    onSelectDate: (String) -> Unit,
    usePersianDigits: Boolean
) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }
    val todayCal = remember { Calendar.getInstance() }

    // Strip showing past 3 days, today, and next 3 days
    val dates = remember {
        val list = mutableListOf<Calendar>()
        for (i in -3..3) {
            val c = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, i) }
            list.add(c)
        }
        list
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        dates.forEach { cal ->
            val iso = dateFormat.format(cal.time)
            val isSelected = iso == selectedIso
            val jDate = JalaliDate.fromIsoDate(iso)
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
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) AccentAmber else Color(0xFF141924))
                    .border(1.dp, if (isSelected) AccentAmber else CharcoalBorderSubtle, RoundedCornerShape(12.dp))
                    .clickable { onSelectDate(iso) }
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Text(
                    text = dayName,
                    color = if (isSelected) Color(0xFF0F141F) else TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                val dayNum = if (usePersianDigits) JalaliDate.toPersianDigits(jDate.day) else jDate.day.toString()
                Text(
                    text = dayNum,
                    color = if (isSelected) Color(0xFF0F141F) else TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun WeeklyOverviewTab(
    items: List<StudyPlanItem>,
    usePersianDigits: Boolean
) {
    val totalKonkurMin = items.filter { it.studyType == StudyType.KONKUR.code && it.status != PlanStatus.NOT_DONE.code }.sumOf { it.actualMinutes }
    val totalFinalMin = items.filter { it.studyType == StudyType.FINAL.code && it.status != PlanStatus.NOT_DONE.code }.sumOf { it.actualMinutes }
    val totalTests = items.sumOf { it.actualTests }

    Column(modifier = Modifier.fillMaxSize()) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            glowAccentColor = ElectricIndigo,
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "خلاصه کل برنامه هفتگی",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "ساعت کنکوری", color = TextMuted, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(3.dp))
                        val kText = "${totalKonkurMin / 60} ساعت"
                        Text(text = if (usePersianDigits) JalaliDate.toPersianDigits(kText) else kText, color = MementoCyan, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "ساعت نهایی", color = TextMuted, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(3.dp))
                        val fText = "${totalFinalMin / 60} ساعت"
                        Text(text = if (usePersianDigits) JalaliDate.toPersianDigits(fText) else fText, color = VelvetPurple, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "تست‌های کل", color = TextMuted, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(3.dp))
                        val tText = if (usePersianDigits) JalaliDate.toPersianDigits(totalTests) else totalTests.toString()
                        Text(text = tText, color = AccentAmber, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "تمام پارت‌های ثبت‌شده در این هفته",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items, key = { it.id }) { item ->
                StudyPlanItemCard(
                    item = item,
                    usePersianDigits = usePersianDigits,
                    onStatusClick = {},
                    onDeleteClick = {}
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddStudyPartDialog(
    defaultIsoDate: String,
    onDismiss: () -> Unit,
    onSave: (isoDate: String, subject: String, title: String, type: StudyType, plannedMin: Int, plannedTest: Int) -> Unit
) {
    val subjects = listOf("زیست‌شناسی", "شیمی", "فیزیک", "ریاضی", "ادبیات", "عربی", "زبان", "زمین‌شناسی")
    var selectedSubject by remember { mutableStateOf(subjects[0]) }
    var title by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(StudyType.KONKUR) }
    var minutes by remember { mutableIntStateOf(90) }
    var testsCount by remember { mutableIntStateOf(30) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(22.dp))
                .border(1.dp, CharcoalBorder, RoundedCornerShape(22.dp)),
            color = Color(0xF2121722)
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "تعریف پارت مطالعه جدید",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Subject Selector
                Text(text = "درس:", color = TextMuted, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    subjects.forEach { subj ->
                        val isSelected = subj == selectedSubject
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) AccentAmber.copy(alpha = 0.25f) else CharcoalSurfaceCard)
                                .border(1.dp, if (isSelected) AccentAmber else CharcoalBorderSubtle, RoundedCornerShape(8.dp))
                                .clickable { selectedSubject = subj }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(text = subj, color = if (isSelected) AccentAmber else TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان پارت / مبحث", fontSize = 12.sp) },
                    placeholder = { Text("مثلاً: گفتار ۲ فصل گوارش یا تست‌های مدار", color = TextMuted, fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentAmber,
                        unfocusedBorderColor = CharcoalBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // CRITICAL REQUIREMENT: TYPE OF STUDY (KONKUR VS FINAL)
                Text(text = "نوع مطالعه:", color = TextMuted, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedType == StudyType.KONKUR) MementoCyan.copy(alpha = 0.25f) else CharcoalSurfaceCard)
                            .border(1.dp, if (selectedType == StudyType.KONKUR) MementoCyan else CharcoalBorderSubtle, RoundedCornerShape(10.dp))
                            .clickable { selectedType = StudyType.KONKUR }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "مطالعه کنکوری (تستی)",
                            color = if (selectedType == StudyType.KONKUR) MementoCyan else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedType == StudyType.FINAL) VelvetPurple.copy(alpha = 0.25f) else CharcoalSurfaceCard)
                            .border(1.dp, if (selectedType == StudyType.FINAL) VelvetPurple else CharcoalBorderSubtle, RoundedCornerShape(10.dp))
                            .clickable { selectedType = StudyType.FINAL }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "مطالعه نهایی (تشریحی)",
                            color = if (selectedType == StudyType.FINAL) VelvetPurple else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Duration & Tests
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "زمان (دقیقه):", color = TextMuted, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            listOf(45, 60, 90, 120).forEach { m ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (minutes == m) AccentAmber else CharcoalSurfaceElevated)
                                        .clickable { minutes = m }
                                        .padding(horizontal = 6.dp, vertical = 4.dp)
                                ) {
                                    Text(text = "${m}m", color = if (minutes == m) Color(0xFF0F141F) else TextSecondary, fontSize = 10.sp)
                                }
                                Spacer(modifier = Modifier.width(3.dp))
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "هدف تست:", color = TextMuted, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            listOf(0, 20, 30, 50).forEach { t ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (testsCount == t) MementoCyan else CharcoalSurfaceElevated)
                                        .clickable { testsCount = t }
                                        .padding(horizontal = 6.dp, vertical = 4.dp)
                                ) {
                                    Text(text = "$t", color = if (testsCount == t) Color(0xFF0F141F) else TextSecondary, fontSize = 10.sp)
                                }
                                Spacer(modifier = Modifier.width(3.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = {
                        val partTitle = title.ifBlank { "مطالعه ${selectedSubject}" }
                        onSave(defaultIsoDate, selectedSubject, partTitle, selectedType, minutes, testsCount)
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentAmber, contentColor = Color(0xFF0F141F))
                ) {
                    Text(text = "ذخیره در برنامه", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun UpdatePartStatusDialog(
    item: StudyPlanItem,
    onDismiss: () -> Unit,
    onSave: (status: PlanStatus, actualMin: Int, actualTest: Int, reason: String?) -> Unit
) {
    var selectedStatus by remember { mutableStateOf(PlanStatus.fromCode(item.status)) }
    var actualMinText by remember { mutableStateOf(item.actualMinutes.toString()) }
    var actualTestText by remember { mutableStateOf(item.actualTests.toString()) }
    var reasonText by remember { mutableStateOf(item.reason ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, CharcoalBorder, RoundedCornerShape(20.dp)),
            color = Color(0xF2121722)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "ثبت وضعیت اجرای پارت", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${item.subject}: ${item.title}", color = TextMuted, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(16.dp))

                // Status Choices
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PlanStatus.values().forEach { st ->
                        val isSelected = selectedStatus == st
                        val color = when (st) {
                            PlanStatus.COMPLETED -> SuccessGreen
                            PlanStatus.PARTIAL -> AccentAmber
                            PlanStatus.NOT_DONE -> Color(0xFFF87171)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) color.copy(alpha = 0.25f) else CharcoalSurfaceCard)
                                .border(1.dp, if (isSelected) color else CharcoalBorderSubtle, RoundedCornerShape(8.dp))
                                .clickable { selectedStatus = st }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = st.title, color = if (isSelected) color else TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (selectedStatus != PlanStatus.NOT_DONE) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = actualMinText,
                            onValueChange = { actualMinText = it },
                            label = { Text("دقیقه واقعی", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = actualTestText,
                            onValueChange = { actualTestText = it },
                            label = { Text("تست واقعی", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (selectedStatus != PlanStatus.COMPLETED) {
                    OutlinedTextField(
                        value = reasonText,
                        onValueChange = { reasonText = it },
                        label = { Text("علت عدم انجام کامل (جهت گزارش هفتگی)", fontSize = 11.sp) },
                        placeholder = { Text("مثلاً: خستگی، کمبود وقت...", fontSize = 11.sp, color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }

                Button(
                    onClick = {
                        val min = actualMinText.toIntOrNull() ?: item.actualMinutes
                        val tests = actualTestText.toIntOrNull() ?: item.actualTests
                        onSave(selectedStatus, min, tests, reasonText.ifBlank { null })
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentAmber, contentColor = Color(0xFF0F141F))
                ) {
                    Text(text = "تأیید و به‌روزرسانی", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
