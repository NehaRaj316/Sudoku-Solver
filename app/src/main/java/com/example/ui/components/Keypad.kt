package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.BorderColor
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.InputMode

@Composable
fun Keypad(
    digitCounts: Map<Int, Int>,
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
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Control Bar: Undo, Redo, Erase, Pencil/Pen toggle in soft clay styling
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ClayControlActionButton(
                icon = Icons.AutoMirrored.Filled.Undo,
                label = "Undo",
                enabled = canUndo,
                testTag = "btn_undo",
                onClick = onUndoClick
            )

            ClayControlActionButton(
                icon = Icons.AutoMirrored.Filled.Redo,
                label = "Redo",
                enabled = canRedo,
                testTag = "btn_redo",
                onClick = onRedoClick
            )

            ClayControlActionButton(
                icon = Icons.Default.Backspace,
                label = "Erase",
                enabled = true,
                testTag = "btn_erase",
                onClick = onEraseClick
            )

            // Clay Pencil / Notes Toggle Button
            ClayPencilToggleButton(
                inputMode = inputMode,
                onClick = onToggleInputMode
            )
        }

        // Chunky Clay Digits 1 to 9
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (digit in 1..9) {
                val count = digitCounts[digit] ?: 0
                val isCompleted = count >= 9

                ClayDigitButton(
                    digit = digit,
                    count = count,
                    isCompleted = isCompleted,
                    onClick = { onDigitClick(digit) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ClayDigitButton(
    digit: Int,
    count: Int,
    isCompleted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    // Distinct warm plasticine button color
    val clayButtonColor = when {
        isCompleted -> if (isDark) Color(0xFF26201B) else Color(0xFFE8DFD3)
        else -> if (isDark) Color(0xFF382E25) else Color(0xFFFFFDF9)
    }

    val textColor = when {
        isCompleted -> if (isDark) Color(0xFF6B5847) else Color(0xFFA89F91)
        else -> if (isDark) Color(0xFFF97316) else Color(0xFFC2410C)
    }

    val countColor = when {
        isCompleted -> if (isDark) Color(0xFF4D4034) else Color(0xFFC7BCAD)
        else -> if (isDark) Color(0xFFB45309) else Color(0xFFD97706)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .defaultMinSize(minHeight = 56.dp)
            .claymorphic(
                shape = RoundedCornerShape(14.dp),
                backgroundColor = clayButtonColor,
                cornerRadiusDp = 14.dp,
                elevation = if (isCompleted) 1.dp else 4.dp
            )
            .bouncyClayClick { onClick() }
            .testTag("digit_button_$digit")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 5.dp)
        ) {
            Text(
                text = digit.toString(),
                fontSize = 21.sp,
                fontWeight = FontWeight.Black,
                color = textColor
            )
            Text(
                text = "$count/9",
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                color = countColor
            )
        }
    }
}

@Composable
fun ClayControlActionButton(
    icon: ImageVector,
    label: String,
    enabled: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color(0xFF26201B) else Color(0xFFF1EBE1)
    val contentColor = if (enabled) {
        if (isDark) Color(0xFFEDE4D8) else Color(0xFF42372E)
    } else {
        if (isDark) Color(0xFF524439) else Color(0xFFB5A99B)
    }

    Box(
        modifier = Modifier
            .defaultMinSize(minWidth = 64.dp, minHeight = 44.dp)
            .claymorphic(
                shape = RoundedCornerShape(12.dp),
                backgroundColor = bgColor,
                cornerRadiusDp = 12.dp,
                elevation = if (enabled) 3.dp else 1.dp
            )
            .bouncyClayClick(enabled = enabled) { onClick() }
            .testTag(testTag)
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
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}

@Composable
fun ClayPencilToggleButton(
    inputMode: InputMode,
    onClick: () -> Unit
) {
    val isPencil = inputMode == InputMode.PENCIL
    val isDark = isSystemInDarkTheme()

    // Vibrant plasticine cobalt when active!
    val bgColor = if (isPencil) {
        if (isDark) Color(0xFF2563EB) else Color(0xFF3B82F6)
    } else {
        if (isDark) Color(0xFF26201B) else Color(0xFFF1EBE1)
    }

    val contentColor = if (isPencil) {
        Color.White
    } else {
        if (isDark) Color(0xFFEDE4D8) else Color(0xFF42372E)
    }

    Box(
        modifier = Modifier
            .defaultMinSize(minWidth = 84.dp, minHeight = 44.dp)
            .claymorphic(
                shape = RoundedCornerShape(12.dp),
                backgroundColor = bgColor,
                cornerRadiusDp = 12.dp,
                elevation = 4.dp
            )
            .bouncyClayClick { onClick() }
            .testTag("btn_pencil_mode")
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
                fontWeight = FontWeight.Black,
                color = contentColor
            )
        }
    }
}
