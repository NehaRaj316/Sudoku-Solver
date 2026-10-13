package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Cell
import com.example.ui.theme.CellConflictBgDark
import com.example.ui.theme.CellConflictBgLight
import com.example.ui.theme.CellConflictTextDark
import com.example.ui.theme.CellConflictTextLight
import com.example.ui.theme.CellCrosshairBgDark
import com.example.ui.theme.CellCrosshairBgLight
import com.example.ui.theme.CellGivenNumberDark
import com.example.ui.theme.CellGivenNumberLight
import com.example.ui.theme.CellMatchNumberBgDark
import com.example.ui.theme.CellMatchNumberBgLight
import com.example.ui.theme.CellSelectedBgDark
import com.example.ui.theme.CellSelectedBgLight
import com.example.ui.theme.CellStepHighlightBgDark
import com.example.ui.theme.CellStepHighlightBgLight
import com.example.ui.theme.CellUserNumberDark
import com.example.ui.theme.CellUserNumberLight

@Composable
fun SudokuGrid(
    cells: List<List<Cell>>,
    selectedRow: Int,
    selectedCol: Int,
    onCellClick: (row: Int, col: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val borderColor = if (isDark) Color(0xFF475569) else Color(0xFF94A3B8)
    val majorLineColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF334155)
    val minorLineColor = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
    val boardBackground = if (isDark) Color(0xFF1E293B) else Color(0xFFFFFFFF)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .border(2.dp, majorLineColor, RoundedCornerShape(12.dp))
            .background(boardBackground)
            .testTag("sudoku_grid")
    ) {
        // Grid cells
        Column(modifier = Modifier.fillMaxSize()) {
            for (r in 0 until 9) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    for (c in 0 until 9) {
                        val cell = cells[r][c]
                        SudokuCellComposable(
                            cell = cell,
                            isSelected = (r == selectedRow && c == selectedCol),
                            isSameRowOrColOrBox = (r == selectedRow || c == selectedCol || (r / 3 == selectedRow / 3 && c / 3 == selectedCol / 3)),
                            onCellClick = { onCellClick(r, c) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Draw 3x3 major block lines overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val step = size.width / 9f

            // Minor grid lines
            for (i in 1 until 9) {
                if (i % 3 != 0) {
                    // Vertical
                    drawLine(
                        color = minorLineColor,
                        start = Offset(i * step, 0f),
                        end = Offset(i * step, size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                    // Horizontal
                    drawLine(
                        color = minorLineColor,
                        start = Offset(0f, i * step),
                        end = Offset(size.width, i * step),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }

            // Major grid lines
            for (i in 1..2) {
                val pos = i * 3 * step
                // Vertical
                drawLine(
                    color = majorLineColor,
                    start = Offset(pos, 0f),
                    end = Offset(pos, size.height),
                    strokeWidth = 2.5.dp.toPx()
                )
                // Horizontal
                drawLine(
                    color = majorLineColor,
                    start = Offset(0f, pos),
                    end = Offset(size.width, pos),
                    strokeWidth = 2.5.dp.toPx()
                )
            }
        }
    }
}

@Composable
fun SudokuCellComposable(
    cell: Cell,
    isSelected: Boolean,
    isSameRowOrColOrBox: Boolean,
    onCellClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    // Determine cell background color
    val targetBgColor = when {
        cell.isConflicted -> if (isDark) CellConflictBgDark else CellConflictBgLight
        isSelected -> if (isDark) CellSelectedBgDark else CellSelectedBgLight
        cell.isStepTarget -> if (isDark) CellStepHighlightBgDark else CellStepHighlightBgLight
        cell.isHighlightedValue -> if (isDark) CellMatchNumberBgDark else CellMatchNumberBgLight
        isSameRowOrColOrBox -> if (isDark) CellCrosshairBgDark else CellCrosshairBgLight
        else -> Color.Transparent
    }

    val animatedBgColor by animateColorAsState(
        targetValue = targetBgColor,
        animationSpec = tween(durationMillis = 150),
        label = "cell_bg"
    )

    val textColor = when {
        cell.isConflicted -> if (isDark) CellConflictTextDark else CellConflictTextLight
        cell.isGiven -> if (isDark) CellGivenNumberDark else CellGivenNumberLight
        else -> if (isDark) CellUserNumberDark else CellUserNumberLight
    }

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(animatedBgColor)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true)
            ) { onCellClick() }
            .testTag("cell_${cell.row}_${cell.col}")
    ) {
        if (!cell.isEmpty) {
            Text(
                text = cell.value.toString(),
                fontSize = 22.sp,
                fontWeight = if (cell.isGiven) FontWeight.Bold else FontWeight.SemiBold,
                color = textColor,
                textAlign = TextAlign.Center
            )
        } else if (cell.notes.isNotEmpty()) {
            // Render 3x3 pencil marks
            PencilNotesView(notes = cell.notes)
        }
    }
}

@Composable
private fun PencilNotesView(notes: Set<Int>) {
    val isDark = isSystemInDarkTheme()
    val noteColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(1.5.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        for (row in 0 until 3) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (col in 0 until 3) {
                    val num = row * 3 + col + 1
                    Text(
                        text = if (notes.contains(num)) num.toString() else "",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = noteColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
