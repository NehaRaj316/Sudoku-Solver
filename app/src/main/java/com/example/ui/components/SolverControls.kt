package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
    val (bgColor, icon, text) = when {
        conflictCount > 0 -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            Icons.Default.ErrorOutline,
            "Conflict: $conflictCount invalid placement${if (conflictCount > 1) "s" else ""}"
        )
        status == SolveStatus.SOLVED -> Triple(
            Color(0xFFDCFCE7),
            Icons.Default.CheckCircle,
            "Solved in ${solveTimeMs}ms! ${if (solutionCount == 1) "(Unique Solution)" else "(Multiple Solutions)"}"
        )
        status == SolveStatus.UNSOLVABLE -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            Icons.Default.ErrorOutline,
            "No solution exists for this configuration"
        )
        status == SolveStatus.SOLVING -> Triple(
            MaterialTheme.colorScheme.primaryContainer,
            Icons.Default.Bolt,
            "Solving board..."
        )
        else -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            Icons.Default.CheckCircle,
            if (clueCount == 0) "Empty Board (Tap cells to enter numbers)" else "Valid Board ($clueCount numbers entered)"
        )
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = bgColor,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (conflictCount > 0 || status == SolveStatus.UNSOLVABLE) {
                    MaterialTheme.colorScheme.error
                } else if (status == SolveStatus.SOLVED) {
                    Color(0xFF166534)
                } else {
                    MaterialTheme.colorScheme.primary
                },
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (conflictCount > 0 || status == SolveStatus.UNSOLVABLE) {
                    MaterialTheme.colorScheme.onErrorContainer
                } else if (status == SolveStatus.SOLVED) {
                    Color(0xFF166534)
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

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
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Main action buttons: Solve & Step
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Solve Instantly
            Button(
                onClick = onSolveInstant,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .weight(1.3f)
                    .height(48.dp)
                    .testTag("btn_solve_instant")
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Solve Now",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            // Step-by-step
            FilledTonalButton(
                onClick = onSolveStep,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                modifier = Modifier
                    .weight(1.1f)
                    .height(48.dp)
                    .testTag("btn_solve_step")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Step Logic",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }

            // Animated Solver toggle
            OutlinedButton(
                onClick = onToggleAnimate,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("btn_toggle_animate")
            ) {
                Icon(
                    imageVector = if (isAnimating) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isAnimating) "Stop" else "Animate",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Animation speed bar if animating
        AnimatedVisibility(visible = isAnimating) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Speed:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                listOf(
                    Pair("Slow", 250L),
                    Pair("Normal", 100L),
                    Pair("Fast", 25L)
                ).forEach { (label, speed) ->
                    val isSelected = animationSpeedMs == speed
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp)
                    ) {
                        Button(
                            onClick = { onChangeSpeed(speed) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            elevation = null,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}
