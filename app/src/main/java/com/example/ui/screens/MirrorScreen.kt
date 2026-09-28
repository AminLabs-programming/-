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
import androidx.compose.material.icons.outlined.CenterFocusStrong
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VelvetPurple
import com.example.ui.viewmodel.ContinueUiState

@Composable
fun MirrorScreen(
    uiState: ContinueUiState,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    AmbientGlowBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Title Header with Glowing Indigo/Cyan Emblem
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(ElectricIndigo.copy(alpha = 0.15f))
                        .border(1.dp, ElectricIndigo.copy(alpha = 0.35f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CenterFocusStrong,
                        contentDescription = null,
                        tint = ElectricIndigo,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "آینه",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "بازتاب الگوهای واقعی رفتار و مطالعه تو",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Philosophy Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                glowAccentColor = ElectricIndigo,
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    text = "«آینه قضاوتی درباره تو نمی‌کند؛ فقط واقعیت رفتار تو را بدون تحقیر و بدون توهم به خودت نشان می‌دهد تا هوشمندانه‌تر ادامه دهی.»",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (uiState.reports.size < 3) {
                // Empty / Insufficient data state
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Psychology,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(42.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "هنوز داده کافی برای پیدا کردن الگو وجود ندارد.",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "حداقل چند روز گزارش ثبت کن تا آینه رفتار واقعی مطالعه تو را تحلیل کند.",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                Text(
                    text = "الگوهای کشف‌شده",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                uiState.mirrorInsights.forEachIndexed { idx, insight ->
                    val colorAccent = when (idx % 4) {
                        0 -> SuccessGreen
                        1 -> AccentAmber
                        2 -> ElectricIndigo
                        else -> MementoCyan
                    }

                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        glowAccentColor = colorAccent,
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = insight.title,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(colorAccent.copy(alpha = 0.15f))
                                        .border(1.dp, colorAccent.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = insight.category,
                                        color = colorAccent,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = insight.description,
                                color = TextSecondary,
                                fontSize = 13.sp,
                                lineHeight = 22.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
