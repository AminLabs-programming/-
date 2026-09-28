package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentAmberGlow
import com.example.ui.theme.CharcoalBg
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalSurfaceCard
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.MementoCyan

/**
 * Ambient background illumination with multiple layered radial glows.
 * Gives the dark theme a cinematic, deep obsidian aesthetic.
 */
@Composable
fun AmbientGlowBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalBg)
            .drawBehind {
                // Top-center Amber streak glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x28F59E0B),
                            Color(0x10F59E0B),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.5f, size.height * 0.15f),
                        radius = size.width * 0.75f
                    )
                )

                // Middle-right Indigo/Purple glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x18818CF8),
                            Color(0x08818CF8),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.85f, size.height * 0.5f),
                        radius = size.width * 0.6f
                    )
                )

                // Bottom-left Cyan Memento glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x1838BDF8),
                            Color(0x0638BDF8),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.15f, size.height * 0.85f),
                        radius = size.width * 0.7f
                    )
                )
            }
    ) {
        content()
    }
}

/**
 * High-end CTA button with an ambient colored glow shadow underneath,
 * subtle top specular highlight, and smooth rounded corners.
 */
@Composable
fun PremiumGlowButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    glowColor: Color = AccentAmber,
    containerBrush: Brush = Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706), Color(0xFFB45309))),
    contentColor: Color = Color(0xFF0A0C10),
    icon: (@Composable () -> Unit)? = null
) {
    Box(
        modifier = modifier.height(56.dp),
        contentAlignment = Alignment.Center
    ) {
        // Soft ambient glow shadow underneath
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .height(44.dp)
                .offset(y = 6.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            glowColor.copy(alpha = 0.5f),
                            glowColor.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Main button surface with glass rim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .background(containerBrush)
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            Color(0x99FFFFFF),
                            Color(0x22FFFFFF),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable { onClick() }
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                androidx.compose.foundation.layout.Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (icon != null) {
                        icon()
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(start = 8.dp))
                    }
                    Text(
                        text = text,
                        color = contentColor,
                        fontSize = 16.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Premium Glass Card with subtle gradient borders, dark translucent core,
 * and optional colored edge glow.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    glowAccentColor: Color? = null,
    shape: Shape = RoundedCornerShape(20.dp),
    content: @Composable BoxScope.() -> Unit
) {
    val borderBrush = if (glowAccentColor != null) {
        Brush.linearGradient(
            colors = listOf(
                glowAccentColor.copy(alpha = 0.55f),
                CharcoalBorder,
                CharcoalBorder.copy(alpha = 0.4f),
                glowAccentColor.copy(alpha = 0.2f)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0x33FFFFFF),
                CharcoalBorder,
                Color(0x11FFFFFF)
            )
        )
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF161C27),
                        Color(0xFF0F141F)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = borderBrush,
                shape = shape
            )
    ) {
        content()
    }
}
