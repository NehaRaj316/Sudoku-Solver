package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.BorderColor
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.InputMode

@Composable
fun Keypad(
    digitCounts: Map<Int, Int>, // digit -> count on board (0..9)
    inputMode: InputMode,
    canUndo: Boolean,
    canRedo: Boolean,
    onDigitClick: (Int) -> Unit,
    onEraseClick: () -> Unit,
    onToggleInputMode: () -> Unit,
    onUndoClick: () -> Unit,
    onRedoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val isDark = isSystemInDarkTheme()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Control Bar: Undo, Redo, Erase, Pencil/Pen toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ControlActionButton(
                icon = Icons.AutoMirrored.Filled.Undo,
                label = "Undo",
                enabled = canUndo,
                testTag = "btn_undo",
                onClick = onUndoClick
            )

            ControlActionButton(
                icon = Icons.AutoMirrored.Filled.Redo,
                label = "Redo",
                enabled = canRedo,
                testTag = "btn_redo",
                onClick = onRedoClick
            )

            ControlActionButton(
                icon = Icons.Default.Backspace,
                label = "Erase",
                enabled = true,
                testTag = "btn_erase",
                onClick = onEraseClick
            )

            // Pencil / Notes Toggle Button
            PencilToggleButton(
                inputMode = inputMode,
                onClick = onToggleInputMode
            )
        }

        // Digits 1 to 9
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (digit in 1..9) {
                val count = digitCounts[digit] ?: 0
                val isCompleted = count >= 9

                DigitButton(
                    digit = digit,
                    count = count,
                    isCompleted = isCompleted,
                    onClick = {
                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                        onDigitClick(digit)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun DigitButton(
    digit: Int,
    count: Int,
    isCompleted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    val containerBg = when {
        isCompleted -> if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)
        else -> MaterialTheme.colorScheme.primaryContainer
    }

    val textColor = when {
        isCompleted -> if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
        else -> MaterialTheme.colorScheme.onPrimaryContainer
    }

    val countColor = when {
        isCompleted -> if (isDark) Color(0xFF475569) else Color(0xFFCBD5E1)
        else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
    }

    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = containerBg,
        modifier = modifier
            .defaultMinSize(minHeight = 56.dp)
            .testTag("digit_button_$digit")
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true)
            ) { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 6.dp)
        ) {
            Text(
                text = digit.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = "$count/9",
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = countColor
            )
        }
    }
}

@Composable
fun ControlActionButton(
    icon: ImageVector,
    label: String,
    enabled: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val contentColor = if (enabled) {
        MaterialTheme.colorScheme.onSurface
    } else {
        if (isDark) Color(0xFF475569) else Color(0xFF94A3B8)
    }

    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
        modifier = Modifier
            .defaultMinSize(minWidth = 64.dp, minHeight = 44.dp)
            .testTag(testTag)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(bounded = true)
            ) { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = contentColor
            )
        }
    }
}

@Composable
fun PencilToggleButton(
    inputMode: InputMode,
    onClick: () -> Unit
) {
    val isPencil = inputMode == InputMode.PENCIL
    val isDark = isSystemInDarkTheme()

    val bgColor = if (isPencil) {
        MaterialTheme.colorScheme.secondary
    } else {
        if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)
    }

    val contentColor = if (isPencil) {
        MaterialTheme.colorScheme.onSecondary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        modifier = Modifier
            .defaultMinSize(minWidth = 84.dp, minHeight = 44.dp)
            .testTag("btn_pencil_mode")
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true)
            ) { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isPencil) Icons.Default.Edit else Icons.Default.BorderColor,
                contentDescription = "Toggle Notes Mode",
                tint = contentColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isPencil) "Notes ON" else "Normal",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
        }
    }
}
