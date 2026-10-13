package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Custom claymorphic styling modifier.
 * Creates the soft, tactile 3D plasticine / clay look:
 * - Soft exterior shadow
 * - Subtle inner bevel gradient
 * - Gentle rounded border mimicking hand-sculpted clay
 */
@Composable
fun Modifier.claymorphic(
    shape: Shape = RoundedCornerShape(16.dp),
    backgroundColor: Color,
    cornerRadiusDp: Dp = 16.dp,
    elevation: Dp = 6.dp,
    isPressed: Boolean = false
): Modifier {
    val isDark = isSystemInDarkTheme()
    val highlightColor = if (isDark) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.55f)
    val shadowColor = if (isDark) Color.Black.copy(alpha = 0.45f) else Color(0xFF6B5847).copy(alpha = 0.22f)

    return this
        .shadow(
            elevation = if (isPressed) 2.dp else elevation,
            shape = shape,
            clip = false,
            ambientColor = shadowColor,
            spotColor = shadowColor
        )
        .clip(shape)
        .background(backgroundColor)
        .drawBehind {
            // Draw soft top-left clay highlight bevel
            val strokeWidth = 1.5.dp.toPx()
            val radiusPx = cornerRadiusDp.toPx()

            // Top highlight
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(highlightColor, highlightColor.copy(alpha = 0.1f))
                ),
                start = Offset(radiusPx / 2, strokeWidth / 2),
                end = Offset(size.width - radiusPx / 2, strokeWidth / 2),
                strokeWidth = strokeWidth
            )

            // Left highlight
            drawLine(
                brush = Brush.verticalGradient(
                    colors = listOf(highlightColor, highlightColor.copy(alpha = 0.1f))
                ),
                start = Offset(strokeWidth / 2, radiusPx / 2),
                end = Offset(strokeWidth / 2, size.height - radiusPx / 2),
                strokeWidth = strokeWidth
            )
        }
        .border(
            width = 1.dp,
            color = if (isDark) Color(0xFF3F352B) else Color(0xFFE5DDD0),
            shape = shape
        )
}

/**
 * Bouncy clay click modifier that provides tactile squishy feedback
 * mimicking pressed plasticine modeling clay.
 */
@Composable
fun Modifier.bouncyClayClick(
    enabled: Boolean = true,
    hapticClick: Boolean = true,
    onClick: () -> Unit
): Modifier {
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }
    val interactionSource = remember { MutableInteractionSource() }

    return this
        .scale(scale.value)
        .clickable(
            enabled = enabled,
            interactionSource = interactionSource,
            indication = ripple(bounded = true, color = Color.White.copy(alpha = 0.3f))
        ) {
            if (hapticClick) {
                haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
            }
            coroutineScope.launch {
                // Squeeze clay
                scale.animateTo(
                    targetValue = 0.90f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
                // Spring back
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioHighBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
            onClick()
        }
}
