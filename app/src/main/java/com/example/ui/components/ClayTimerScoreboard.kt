package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ClayTimerScoreboard(
    timeFormatted: String,
    isTimerRunning: Boolean,
    isCountdownMode: Boolean,
    progressFraction: Float, // 0f to 1f (remaining in countdown or par ratio)
    isUrgent: Boolean, // True when in countdown danger zone (< 60s) or over par
    score: Int,
    comboStreak: Int,
    comboMultiplier: Int,
    onToggleTimer: () -> Unit,
    onOpenScoreboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    // Urgent pulsing heart-thump scale & color animation
    val infiniteTransition = rememberInfiniteTransition(label = "urgent_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isUrgent) 1.07f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val timerBorderColor by animateColorAsState(
        targetValue = when {
            isUrgent -> if (isDark) Color(0xFFEF4444) else Color(0xFFDC2626)
            isCountdownMode -> if (isDark) Color(0xFFF97316) else Color(0xFFEA580C)
            else -> if (isDark) Color(0xFF6B5847) else Color(0xFFD6CBC0)
        },
        animationSpec = tween(300),
        label = "timer_border_color"
    )

    val cardBg = if (isDark) Color(0xFF26201B) else Color(0xFFFFFDF9)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Pod 1: Clay Tactile Timer Dial
        Box(
            modifier = Modifier
                .weight(1.15f)
                .height(68.dp)
                .scale(if (isUrgent) pulseScale else 1f)
                .claymorphic(
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = if (isUrgent) {
                        if (isDark) Color(0xFF451313) else Color(0xFFFEE2E2)
                    } else cardBg,
                    cornerRadiusDp = 16.dp,
                    elevation = if (isUrgent) 6.dp else 4.dp
                )
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("clay_timer_pod")
        ) {
            Row(
                modifier = Modifier.matchParentSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Miniature Circular Clay Clock Gauge
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(42.dp)
                ) {
                    Canvas(modifier = Modifier.size(38.dp)) {
                        val strokeWidth = 3.5.dp.toPx()
                        // Track background groove
                        drawArc(
                            color = if (isDark) Color(0xFF4A3E34) else Color(0xFFE5DDD0),
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = strokeWidth)
                        )
                        // Active clay indicator arc
                        val sweep = if (isCountdownMode) progressFraction * 360f else (progressFraction.coerceIn(0f, 1f)) * 360f
                        val progressColor = if (isUrgent) {
                            Color(0xFFDC2626)
                        } else if (isCountdownMode) {
                            Color(0xFFEA580C)
                        } else {
                            Color(0xFF2563EB)
                        }
                        drawArc(
                            color = progressColor,
                            startAngle = -90f,
                            sweepAngle = sweep,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    Icon(
                        imageVector = if (isUrgent) Icons.Default.HourglassBottom else if (isCountdownMode) Icons.Default.HourglassTop else Icons.Default.Timer,
                        contentDescription = null,
                        tint = if (isUrgent) {
                            if (isDark) Color(0xFFFCA5A5) else Color(0xFFDC2626)
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Time Display
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = timeFormatted,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isUrgent) {
                            if (isDark) Color(0xFFFCA5A5) else Color(0xFFDC2626)
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                    Text(
                        text = if (isUrgent) "URGENT CLAY!" else if (isCountdownMode) "Time Attack" else "Stopwatch",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUrgent) {
                            if (isDark) Color(0xFFF87171) else Color(0xFFB91C1C)
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                }

                // Pause / Play Squishy Clay Button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(32.dp)
                        .claymorphic(
                            shape = CircleShape,
                            backgroundColor = if (isDark) Color(0xFF382E25) else Color(0xFFF1EBE1),
                            cornerRadiusDp = 16.dp,
                            elevation = 2.dp
                        )
                        .bouncyClayClick { onToggleTimer() }
                        .testTag("btn_toggle_timer")
                ) {
                    Icon(
                        imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isTimerRunning) "Pause" else "Play",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
        }

        // Pod 2: Clay Scoreboard & Combo Tracker
        Box(
            modifier = Modifier
                .weight(1.05f)
                .height(68.dp)
                .claymorphic(
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = cardBg,
                    cornerRadiusDp = 16.dp,
                    elevation = 4.dp
                )
                .bouncyClayClick { onOpenScoreboard() }
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("clay_score_pod")
        ) {
            Row(
                modifier = Modifier.matchParentSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Score: $score",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Combo Multiplier Badge
                    if (comboStreak > 1) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when {
                                comboMultiplier >= 4 -> Color(0xFFDC2626) // Blazing Red
                                comboMultiplier >= 3 -> Color(0xFFEA580C) // Hot Orange
                                else -> Color(0xFFD97706) // Golden Amber
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "x$comboMultiplier ($comboStreak Streak)",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Leaderboard & Stats",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Trophy / Leaderboard Icon
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(32.dp)
                        .claymorphic(
                            shape = RoundedCornerShape(8.dp),
                            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                            cornerRadiusDp = 8.dp,
                            elevation = 2.dp
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Leaderboard,
                        contentDescription = "Open Scoreboard",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
