package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Check
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DailyReport
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentAmberSubtle
import com.example.ui.theme.CharcoalBg
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalBorderSubtle
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceCard
import com.example.ui.theme.CharcoalSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.JalaliDate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DailyReportDialog(
    initialReport: DailyReport?,
    onDismiss: () -> Unit,
    onSave: (
        isoDate: String,
        shamsiDate: String,
        studyMinutes: Int,
        subjects: String,
        testCount: Int?,
        correctTests: Int?,
        wrongTests: Int?,
        uncompletedParts: String?,
        uncompletedReason: String?
    ) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }
    val todayCal = remember { Calendar.getInstance() }
    val defaultIsoDate = remember { dateFormat.format(todayCal.time) }
    val defaultShamsi = remember { JalaliDate.fromIsoDate(defaultIsoDate).format() }

    val isoDate = remember { initialReport?.isoDate ?: defaultIsoDate }
    val shamsiDate = remember { initialReport?.shamsiDate ?: defaultShamsi }

    // Required fields: Study Time & Subjects
    var studyHours by remember { mutableIntStateOf((initialReport?.studyMinutes ?: 300) / 60) }
    var studyMinutesPart by remember { mutableIntStateOf((initialReport?.studyMinutes ?: 300) % 60) }

    val initialSubjectList = remember {
        initialReport?.subjects?.split("·", ",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: listOf("زیست‌شناسی", "شیمی")
    }
    val selectedSubjects = remember { mutableStateListOf<String>().apply { addAll(initialSubjectList) } }
    var customSubjectText by remember { mutableStateOf("") }

    // Progressive disclosure states
    var showTestCountSection by remember { mutableStateOf(initialReport?.testCount != null) }
    var testCountText by remember { mutableStateOf(initialReport?.testCount?.toString() ?: "") }

    var showTestPerformanceSection by remember { mutableStateOf(initialReport?.correctTests != null || initialReport?.wrongTests != null) }
    var correctTestsText by remember { mutableStateOf(initialReport?.correctTests?.toString() ?: "") }
    var wrongTestsText by remember { mutableStateOf(initialReport?.wrongTests?.toString() ?: "") }

    var showUncompletedPartSection by remember { mutableStateOf(!initialReport?.uncompletedParts.isNullOrBlank()) }
    var uncompletedPartText by remember { mutableStateOf(initialReport?.uncompletedParts ?: "") }

    var showUncompletedReasonSection by remember { mutableStateOf(!initialReport?.uncompletedReason.isNullOrBlank()) }
    var uncompletedReasonText by remember { mutableStateOf(initialReport?.uncompletedReason ?: "") }

    val popularSubjects = listOf(
        "زیست‌شناسی", "شیمی", "ریاضی", "فیزیک", "ادبیات", "عربی", "زبان", "زمین‌شناسی", "هندسه", "حسابان", "گسسته"
    )

    val commonReasons = listOf("خستگی", "کمبود وقت", "سختی مبحث", "کار پیش‌بینی نشده", "بی‌دقتی در زمان‌بندی")

    var validationError by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(26.dp))
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            AccentAmber.copy(alpha = 0.6f),
                            Color(0x33818CF8),
                            Color(0x18FFFFFF)
                        )
                    ),
                    shape = RoundedCornerShape(26.dp)
                ),
            color = Color(0xF2111520)
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with «به نام خدا» as explicitly instructed
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "به نام خدا",
                            color = AccentAmber,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (initialReport != null) "ویرایش گزارش روزانه" else "ثبت گزارش روزانه",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(CharcoalSurfaceElevated)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Date display
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CharcoalSurfaceCard)
                        .border(1.dp, CharcoalBorderSubtle, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "تاریخ گزارش:", color = TextMuted, fontSize = 12.sp)
                        Text(
                            text = JalaliDate.toPersianDigits(shamsiDate),
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Required 1: Study Time
                Text(
                    text = "ساعت مطالعه (اجباری)",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CharcoalSurfaceCard)
                        .border(1.dp, CharcoalBorderSubtle, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Hours picker
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "ساعت", color = TextMuted, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { if (studyHours > 0) studyHours-- },
                                        modifier = Modifier.size(32.dp).clip(CircleShape).background(CharcoalSurfaceElevated)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = JalaliDate.toPersianDigits(studyHours),
                                        color = TextPrimary,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    IconButton(
                                        onClick = { if (studyHours < 24) studyHours++ },
                                        modifier = Modifier.size(32.dp).clip(CircleShape).background(CharcoalSurfaceElevated)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(40.dp)
                                    .background(CharcoalBorder)
                            )

                            // Minutes picker
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "دقیقه", color = TextMuted, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            studyMinutesPart = (studyMinutesPart - 15 + 60) % 60
                                        },
                                        modifier = Modifier.size(32.dp).clip(CircleShape).background(CharcoalSurfaceElevated)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = JalaliDate.toPersianDigits(String.format("%02d", studyMinutesPart)),
                                        color = TextPrimary,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    IconButton(
                                        onClick = {
                                            studyMinutesPart = (studyMinutesPart + 15) % 60
                                        },
                                        modifier = Modifier.size(32.dp).clip(CircleShape).background(CharcoalSurfaceElevated)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick presets chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            listOf(30 to "+30m", 60 to "+1h", 120 to "+2h", 180 to "+3h").forEach { (min, label) ->
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CharcoalSurfaceElevated)
                                        .clickable {
                                            val total = studyHours * 60 + studyMinutesPart + min
                                            studyHours = total / 60
                                            studyMinutesPart = total % 60
                                        }
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(text = label, color = TextSecondary, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Required 2: Subjects Studied
                Text(
                    text = "درس‌های مطالعه‌شده (اجباری)",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    popularSubjects.forEach { subject ->
                        val isSelected = selectedSubjects.contains(subject)
                        val subjectColor = when {
                            subject.contains("زیست") -> com.example.ui.theme.SuccessGreen
                            subject.contains("شیمی") -> AccentAmber
                            subject.contains("فیزیک") -> com.example.ui.theme.ElectricIndigo
                            subject.contains("ریاضی") || subject.contains("حسابان") || subject.contains("هندسه") || subject.contains("گسسته") -> com.example.ui.theme.MementoCyan
                            subject.contains("ادبیات") || subject.contains("فارسی") -> com.example.ui.theme.RoseBerry
                            else -> com.example.ui.theme.VelvetPurple
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) subjectColor.copy(alpha = 0.22f) else CharcoalSurfaceCard)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) subjectColor else CharcoalBorder,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    if (isSelected) {
                                        selectedSubjects.remove(subject)
                                    } else {
                                        selectedSubjects.add(subject)
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = subject,
                                color = if (isSelected) subjectColor else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Custom subject input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = customSubjectText,
                        onValueChange = { customSubjectText = it },
                        placeholder = { Text("درس دیگر...", fontSize = 12.sp, color = TextMuted) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CharcoalSurfaceCard,
                            unfocusedContainerColor = CharcoalSurfaceCard,
                            focusedBorderColor = AccentAmber,
                            unfocusedBorderColor = CharcoalBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = {
                            val trimmed = customSubjectText.trim()
                            if (trimmed.isNotEmpty() && !selectedSubjects.contains(trimmed)) {
                                selectedSubjects.add(trimmed)
                                customSubjectText = ""
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentAmber)
                    ) {
                        Text("افزودن", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // PROGRESSIVE DISCLOSURE SECTION (اطلاعات تکمیلی)
                Text(
                    text = "اطلاعات تکمیلی (اختیاری)",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(10.dp))

                // 1. Toggle: Add Test Count
                if (!showTestCountSection) {
                    DisclosureButton(
                        label = "+ افزودن تعداد تست",
                        onClick = { showTestCountSection = true }
                    )
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CharcoalSurfaceCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "تعداد تست‌های امروز", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                IconButton(
                                    onClick = {
                                        showTestCountSection = false
                                        testCountText = ""
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = testCountText,
                                onValueChange = { if (it.all { ch -> ch.isDigit() }) testCountText = it },
                                placeholder = { Text("مثلاً ۸۲", color = TextMuted, fontSize = 12.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentAmber,
                                    unfocusedBorderColor = CharcoalBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 2. Toggle: Add Test Performance (Correct & Wrong)
                if (!showTestPerformanceSection) {
                    DisclosureButton(
                        label = "+ افزودن عملکرد تست (درست / غلط و نزده)",
                        onClick = { showTestPerformanceSection = true }
                    )
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CharcoalSurfaceCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "عملکرد تست", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                IconButton(
                                    onClick = {
                                        showTestPerformanceSection = false
                                        correctTestsText = ""
                                        wrongTestsText = ""
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = correctTestsText,
                                    onValueChange = { if (it.all { ch -> ch.isDigit() }) correctTestsText = it },
                                    label = { Text("تعداد درست", fontSize = 11.sp) },
                                    placeholder = { Text("۶۳", color = TextMuted) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AccentAmber,
                                        unfocusedBorderColor = CharcoalBorder,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    )
                                )
                                OutlinedTextField(
                                    value = wrongTestsText,
                                    onValueChange = { if (it.all { ch -> ch.isDigit() }) wrongTestsText = it },
                                    label = { Text("غلط و نزده", fontSize = 11.sp) },
                                    placeholder = { Text("۱۹", color = TextMuted) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AccentAmber,
                                        unfocusedBorderColor = CharcoalBorder,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 3. Toggle: Add Uncompleted Part
                if (!showUncompletedPartSection) {
                    DisclosureButton(
                        label = "+ افزودن پارت انجام‌نشده",
                        onClick = { showUncompletedPartSection = true }
                    )
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CharcoalSurfaceCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "پارت‌های انجام‌نشده", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                IconButton(
                                    onClick = {
                                        showUncompletedPartSection = false
                                        uncompletedPartText = ""
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = uncompletedPartText,
                                onValueChange = { uncompletedPartText = it },
                                placeholder = { Text("مثلاً: فیزیک — پارت ۳", color = TextMuted, fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentAmber,
                                    unfocusedBorderColor = CharcoalBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 4. Toggle: Add Reason for uncompleted part
                if (!showUncompletedReasonSection) {
                    DisclosureButton(
                        label = "+ افزودن علت عدم انجام",
                        onClick = { showUncompletedReasonSection = true }
                    )
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CharcoalSurfaceCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "علت عدم انجام پارت", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                IconButton(
                                    onClick = {
                                        showUncompletedReasonSection = false
                                        uncompletedReasonText = ""
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                commonReasons.forEach { reason ->
                                    val isSelected = uncompletedReasonText == reason
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) AccentAmber.copy(alpha = 0.2f) else CharcoalSurfaceElevated)
                                            .border(1.dp, if (isSelected) AccentAmber else CharcoalBorderSubtle, RoundedCornerShape(8.dp))
                                            .clickable { uncompletedReasonText = reason }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(text = reason, color = if (isSelected) AccentAmber else TextSecondary, fontSize = 11.sp)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = uncompletedReasonText,
                                onValueChange = { uncompletedReasonText = it },
                                placeholder = { Text("علت دیگر...", color = TextMuted, fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentAmber,
                                    unfocusedBorderColor = CharcoalBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                        }
                    }
                }

                // Error indicator
                if (validationError != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = validationError!!, color = Color(0xFFF87171), fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Actions: Save & Cancel
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                    ) {
                        Text("انصراف", fontSize = 14.sp)
                    }

                    Button(
                        onClick = {
                            val totalMinutes = studyHours * 60 + studyMinutesPart
                            if (totalMinutes <= 0) {
                                validationError = "لطفاً ساعت مطالعه را مشخص کن."
                                return@Button
                            }
                            if (selectedSubjects.isEmpty()) {
                                validationError = "حداقل یک درس مطالعه‌شده را انتخاب کن."
                                return@Button
                            }

                            validationError = null
                            val subjectsStr = selectedSubjects.joinToString(" · ")
                            val testCount = testCountText.toIntOrNull()
                            val correct = correctTestsText.toIntOrNull()
                            val wrong = wrongTestsText.toIntOrNull()
                            val uncompletedPart = uncompletedPartText.takeIf { it.isNotBlank() }
                            val uncompletedReason = uncompletedReasonText.takeIf { it.isNotBlank() }

                            onSave(
                                isoDate,
                                shamsiDate,
                                totalMinutes,
                                subjectsStr,
                                testCount,
                                correct,
                                wrong,
                                uncompletedPart,
                                uncompletedReason
                            )
                        },
                        modifier = Modifier.weight(1.5f).height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentAmber,
                            contentColor = CharcoalBg
                        )
                    ) {
                        Text(
                            text = if (initialReport != null) "ذخیره تغییرات" else "ثبت گزارش امروز",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DisclosureButton(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CharcoalSurfaceCard)
            .border(1.dp, CharcoalBorderSubtle, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = AccentAmber,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = AccentAmber,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
