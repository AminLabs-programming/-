package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AmbientGlowBackground
import com.example.ui.components.GlassCard
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.CharcoalBg
import com.example.ui.theme.CharcoalBorderSubtle
import com.example.ui.theme.CharcoalSurfaceElevated
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.MementoCyan
import com.example.ui.theme.MementoCyanGlow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ContinueUiState
import com.example.util.JalaliDate

@Composable
fun MementoMoriScreen(
    uiState: ContinueUiState,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val usePersianDigits = uiState.settings.usePersianDigits
    val daysRemaining = uiState.settings.konkurDaysRemaining
    val daysRemainingStr = if (usePersianDigits) JalaliDate.toPersianDigits(daysRemaining) else daysRemaining.toString()

    AmbientGlowBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with Glowing Cyan Hourglass
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MementoCyan.copy(alpha = 0.15f))
                        .border(1.dp, MementoCyan.copy(alpha = 0.35f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.HourglassEmpty,
                        contentDescription = null,
                        tint = MementoCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "MEMENTO MORI",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "یادآوری ارزش زمان و فرصت اکنون",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Center Countdown Card with Cyan Aura
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                glowAccentColor = MementoCyan,
                shape = RoundedCornerShape(26.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp)
                        .drawBehind {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0x3538BDF8),
                                        Color(0x1038BDF8),
                                        Color.Transparent
                                    ),
                                    center = Offset(size.width * 0.5f, size.height * 0.4f),
                                    radius = size.width * 0.5f
                                )
                            )
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0x2238BDF8))
                            .border(1.dp, Color(0x4438BDF8), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "«زمان محدود است.»",
                            color = MementoCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = daysRemainingStr,
                        color = TextPrimary,
                        fontSize = 76.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 80.sp
                    )

                    Text(
                        text = "روز تا کنکور سراسری",
                        color = TextSecondary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "تاریخ تقریبی: ${uiState.settings.konkurDateShamsi}",
                        color = TextMuted,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    // Progress Bar: Journey elapsed vs remaining
                    val totalEstJourneyDays = 730f
                    val elapsed = (totalEstJourneyDays - daysRemaining).coerceAtLeast(0f)
                    val progress = (elapsed / totalEstJourneyDays).coerceIn(0f, 1f)

                    Column(modifier = Modifier.fillMaxWidth()) {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(7.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MementoCyan,
                            trackColor = CharcoalSurfaceElevated
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "مسیر طی‌شده", color = TextMuted, fontSize = 11.sp)
                            Text(text = "روز موعود کنکور", color = MementoCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Philosophical Meditations (Reflective, Calm, Non-threatening)
            val reflections = listOf(
                Triple("«زمان در حال کم‌شدن است، اما امروز هنوز مال توست.»", "نه ترسی در کار است و نه اضطرابی؛ فقط آگاهی بر ارزش ثانیه‌هایی که هم‌اکنون در دستان تو جاریست.", MementoCyan),
                Triple("«امروز دیگر تکرار نمی‌شود، اما هنوز می‌توانی آن را بسازی.»", "تمام کنکور در یک روز خلاصه نمی‌شود؛ در زنجیره‌ای از همین تصمیم‌های روزانه شکل می‌گیرد.", ElectricIndigo),
                Triple("«دیروز به تاریخ پیوست؛ امروز تنها دارایی واقعی توست.»", "برای جبران گذشته نجنگ، برای ساختن ساعات پیشِ‌رو متمرکز شو و ادامه بده.", AccentAmber)
            )

            reflections.forEach { (quote, subtext, color) ->
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    glowAccentColor = color,
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = quote,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = subtext,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
