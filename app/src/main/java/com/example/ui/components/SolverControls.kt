package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SolveStatus

@Composable
fun SolverStatusBanner(
    status: SolveStatus,
    clueCount: Int,
    conflictCount: Int,
    solveTimeMs: Long,
    solutionCount: Int,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    val (bgColor, icon, text, textColor) = when {
        conflictCount > 0 -> Quadruple(
            if (isDark) Color(0xFF5A1A1A) else Color(0xFFFEE2E2),
            Icons.Default.ErrorOutline,
            "Conflict: $conflictCount invalid placement${if (conflictCount > 1) "s" else ""}",
            if (isDark) Color(0xFFFCA5A5) else Color(0xFFDC2626)
        )
        status == SolveStatus.SOLVED -> Quadruple(
            if (isDark) Color(0xFF14532D) else Color(0xFFDCFCE7),
            Icons.Default.CheckCircle,
            "Solved in ${solveTimeMs}ms! ${if (solutionCount == 1) "(Unique Solution)" else "(Multiple Solutions)"}",
            if (isDark) Color(0xFF86EFAC) else Color(0xFF166534)
        )
        status == SolveStatus.UNSOLVABLE -> Quadruple(
            if (isDark) Color(0xFF5A1A1A) else Color(0xFFFEE2E2),
            Icons.Default.ErrorOutline,
            "No solution exists for this configuration",
            if (isDark) Color(0xFFFCA5A5) else Color(0xFFDC2626)
        )
        status == SolveStatus.SOLVING -> Quadruple(
            if (isDark) Color(0xFF7C2D12) else Color(0xFFFFEDD5),
            Icons.Default.Bolt,
            "Clay solver molding solution...",
            if (isDark) Color(0xFFFED7AA) else Color(0xFFC2410C)
        )
        else -> Quadruple(
            if (isDark) Color(0xFF26201B) else Color(0xFFF1EBE1),
            Icons.Default.CheckCircle,
            if (clueCount == 0) "Clay Board Ready (Tap tiles to enter numbers)" else "Valid Board ($clueCount clay tiles placed)",
            if (isDark) Color(0xFFEDE4D8) else Color(0xFF42372E)
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .claymorphic(
                shape = RoundedCornerShape(14.dp),
                backgroundColor = bgColor,
                cornerRadiusDp = 14.dp,
                elevation = 3.dp
            )
            .padding(horizontal = 14.dp, vertical = 9.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(19.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun SolverActionsBar(
    isAnimating: Boolean,
    animationSpeedMs: Long,
    onSolveInstant: () -> Unit,
    onSolveStep: () -> Unit,
    onToggleAnimate: () -> Unit,
    onChangeSpeed: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Chunky clay action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Solve Now (Terracotta Clay Hero Button)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1.3f)
                    .height(48.dp)
                    .claymorphic(
                        shape = RoundedCornerShape(14.dp),
                        backgroundColor = if (isDark) Color(0xFFC2410C) else Color(0xFFEA580C),
                        cornerRadiusDp = 14.dp,
                        elevation = 5.dp
                    )
                    .bouncyClayClick { onSolveInstant() }
                    .testTag("btn_solve_instant")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(19.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Solve Now",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }

            // Step Logic (Cobalt Clay Button)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1.1f)
                    .height(48.dp)
                    .claymorphic(
                        shape = RoundedCornerShape(14.dp),
                        backgroundColor = if (isDark) Color(0xFF1E3A8A) else Color(0xFF3B82F6),
                        cornerRadiusDp = 14.dp,
                        elevation = 4.dp
                    )
                    .bouncyClayClick { onSolveStep() }
                    .testTag("btn_solve_step")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Step Logic",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }

            // Animated Stop-motion solver toggle
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .claymorphic(
                        shape = RoundedCornerShape(14.dp),
                        backgroundColor = if (isDark) Color(0xFF2B241E) else Color(0xFFFFFDF9),
                        cornerRadiusDp = 14.dp,
                        elevation = 3.dp
                    )
                    .bouncyClayClick { onToggleAnimate() }
                    .testTag("btn_toggle_animate")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isAnimating) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = if (isDark) Color(0xFFF97316) else Color(0xFFC2410C),
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isAnimating) "Stop" else "Animate",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFF97316) else Color(0xFFC2410C)
                    )
                }
            }
        }

        // Animation speed selector
        AnimatedVisibility(visible = isAnimating) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Speed:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                listOf(
                    Pair("Slow", 250L),
                    Pair("Normal", 100L),
                    Pair("Fast", 25L)
                ).forEach { (label, speed) ->
                    val isSelected = animationSpeedMs == speed
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .claymorphic(
                                shape = RoundedCornerShape(10.dp),
                                backgroundColor = if (isSelected) {
                                    if (isDark) Color(0xFFC2410C) else Color(0xFFEA580C)
                                } else {
                                    if (isDark) Color(0xFF26201B) else Color(0xFFF1EBE1)
                                },
                                cornerRadiusDp = 10.dp,
                                elevation = if (isSelected) 3.dp else 1.dp
                            )
                            .bouncyClayClick { onChangeSpeed(speed) }
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
