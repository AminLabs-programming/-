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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserSettings
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.CharcoalBg
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalBorderSubtle
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceCard
import com.example.ui.theme.CharcoalSurfaceElevated
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ContinueUiState
import com.example.util.JalaliDate

@Composable
fun SettingsScreen(
    uiState: ContinueUiState,
    onSaveSettings: (UserSettings) -> Unit,
    onSeedData: () -> Unit,
    onResetData: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val currentSettings = uiState.settings

    var daysRemaining by remember(currentSettings) { mutableIntStateOf(currentSettings.konkurDaysRemaining) }
    var konkurShamsi by remember(currentSettings) { mutableStateOf(currentSettings.konkurDateShamsi) }
    var minimumMinutes by remember(currentSettings) { mutableIntStateOf(currentSettings.minimumDayMinutes) }
    var targetMinutes by remember(currentSettings) { mutableIntStateOf(currentSettings.targetDayMinutes) }
    var usePersianDigits by remember(currentSettings) { mutableStateOf(currentSettings.usePersianDigits) }

    var showResetConfirm by remember { mutableStateOf(false) }
    var savedMessage by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = null,
                tint = AccentAmber,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "تنظیمات",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "شخصی‌سازی مقادیر استمرار و کنکور",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SECTION: MINIMUM DAY (استمرار و روز قابل قبول)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CharcoalSurfaceCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "حداقل روز قابل قبول (حفظ استمرار)",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "در روزهای سخت یا خستگی، مطالعه این مقدار مانع قطع شدن استمرار تو می‌شود.",
                    color = TextMuted,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (usePersianDigits) "${JalaliDate.toPersianDigits(minimumMinutes)} دقیقه" else "$minimumMinutes دقیقه",
                        color = AccentAmber,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(15, 30, 45, 60).forEach { min ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (minimumMinutes == min) AccentAmber else CharcoalSurfaceElevated)
                                    .clickable { minimumMinutes = min }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (usePersianDigits) "${JalaliDate.toPersianDigits(min)}m" else "${min}m",
                                    color = if (minimumMinutes == min) CharcoalBg else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SECTION: FULL DAY TARGET (هدف روز کامل)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CharcoalSurfaceCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "هدف روز کامل",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ساعت مطالعه ایده‌آل روزانه برای ثبت نشان «روز کامل» در تقویم استمرار.",
                    color = TextMuted,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                val targetHours = targetMinutes / 60
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (usePersianDigits) "${JalaliDate.toPersianDigits(targetHours)} ساعت" else "$targetHours ساعت",
                        color = AccentAmber,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(4, 5, 6, 8, 10).forEach { h ->
                            val mins = h * 60
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (targetMinutes == mins) AccentAmber else CharcoalSurfaceElevated)
                                    .clickable { targetMinutes = mins }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (usePersianDigits) "${JalaliDate.toPersianDigits(h)}h" else "${h}h",
                                    color = if (targetMinutes == mins) CharcoalBg else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SECTION: KONKUR COUNTDOWN & DATE
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CharcoalSurfaceCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "شمارش معکوس کنکور (Memento Mori)",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "روزهای باقیمانده:", color = TextMuted, fontSize = 12.sp)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { if (daysRemaining > 1) daysRemaining -= 10 },
                            modifier = Modifier.size(32.dp).clip(CircleShape).background(CharcoalSurfaceElevated)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (usePersianDigits) JalaliDate.toPersianDigits(daysRemaining) else daysRemaining.toString(),
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        IconButton(
                            onClick = { daysRemaining += 10 },
                            modifier = Modifier.size(32.dp).clip(CircleShape).background(CharcoalSurfaceElevated)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = konkurShamsi,
                    onValueChange = { konkurShamsi = it },
                    label = { Text("تاریخ تقریبی کنکور", fontSize = 11.sp) },
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

        Spacer(modifier = Modifier.height(14.dp))

        // SECTION: PERSIAN DIGITS TOGGLE
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CharcoalSurfaceCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "نمایش اعداد به فارسی", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text(text = "نمایش تمام اعداد و ساعت‌ها با فونت زیبای فارسی", color = TextMuted, fontSize = 11.sp)
                }
                Switch(
                    checked = usePersianDigits,
                    onCheckedChange = { usePersianDigits = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = CharcoalBg,
                        checkedTrackColor = AccentAmber,
                        uncheckedTrackColor = CharcoalSurfaceElevated
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Save Settings Button
        Button(
            onClick = {
                onSaveSettings(
                    UserSettings(
                        konkurDaysRemaining = daysRemaining,
                        konkurDateShamsi = konkurShamsi,
                        minimumDayMinutes = minimumMinutes,
                        targetDayMinutes = targetMinutes,
                        usePersianDigits = usePersianDigits
                    )
                )
                savedMessage = true
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentAmber, contentColor = CharcoalBg)
        ) {
            Text(text = if (savedMessage) "✓ تنظیمات ذخیره شد" else "ذخیره تنظیمات", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // DATA MANAGEMENT SECTION
        Text(
            text = "مدیریت داده‌ها",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onSeedData,
                modifier = Modifier.weight(1f).height(46.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
            ) {
                Icon(Icons.Outlined.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("دیتای اولیه نمونه", fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = { showResetConfirm = true },
                modifier = Modifier.weight(1f).height(46.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF7F1D1D)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF87171))
            ) {
                Icon(Icons.Outlined.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("شروع از صفر", fontSize = 12.sp)
            }
        }

        if (showResetConfirm) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF221517))
                    .border(1.dp, Color(0xFF5A1E24), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = "آیا مطمئن هستی که می‌خواهی تمام گزارش‌ها را پاک کنی؟",
                        color = Color(0xFFFCA5A5),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showResetConfirm = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("انصراف", fontSize = 12.sp)
                        }
                        Button(
                            onClick = {
                                onResetData()
                                showResetConfirm = false
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                        ) {
                            Text("بله، پاک کن", fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
