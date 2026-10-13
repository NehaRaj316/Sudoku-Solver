package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.ClayCellConflictBgDark
import com.example.ui.theme.ClayCellConflictBgLight
import com.example.ui.theme.ClayCellConflictTextDark
import com.example.ui.theme.ClayCellConflictTextLight
import com.example.ui.theme.ClayCellCrosshairBgDark
import com.example.ui.theme.ClayCellCrosshairBgLight
import com.example.ui.theme.ClayCellGivenNumberDark
import com.example.ui.theme.ClayCellGivenNumberLight
import com.example.ui.theme.ClayCellMatchNumberBgDark
import com.example.ui.theme.ClayCellMatchNumberBgLight
import com.example.ui.theme.ClayCellSelectedBgDark
import com.example.ui.theme.ClayCellSelectedBgLight
import com.example.ui.theme.ClayCellStepHighlightBgDark
import com.example.ui.theme.ClayCellStepHighlightBgLight
import com.example.ui.theme.ClayCellUserNumberDark
import com.example.ui.theme.ClayCellUserNumberLight

@Composable
fun SudokuGrid(
    cells: List<List<Cell>>,
    selectedRow: Int,
    selectedCol: Int,
    onCellClick: (row: Int, col: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val slabBackground = if (isDark) Color(0xFF231D18) else Color(0xFFFFFFFF)
    val majorGrooveColor = if (isDark) Color(0xFF6B5847) else Color(0xFF8C7B6C)
    val minorLineColor = if (isDark) Color(0xFF382F27) else Color(0xFFE8DFD3)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .claymorphic(
                shape = RoundedCornerShape(20.dp),
                backgroundColor = slabBackground,
                cornerRadiusDp = 20.dp,
                elevation = 8.dp
            )
            .padding(4.dp)
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

        // Draw 3x3 major block grooves & minor lines overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val step = size.width / 9f

            // Minor grid lines (soft indented plasticine score lines)
            for (i in 1 until 9) {
                if (i % 3 != 0) {
                    // Vertical
                    drawLine(
                        color = minorLineColor,
                        start = Offset(i * step, 0f),
                        end = Offset(i * step, size.height),
                        strokeWidth = 1.2.dp.toPx()
                    )
                    // Horizontal
                    drawLine(
                        color = minorLineColor,
                        start = Offset(0f, i * step),
                        end = Offset(size.width, i * step),
                        strokeWidth = 1.2.dp.toPx()
                    )
                }
            }

            // Major block grooves (deep clay seams)
            for (i in 1..2) {
                val pos = i * 3 * step
                // Vertical groove shadow & highlight
                drawLine(
                    color = majorGrooveColor,
                    start = Offset(pos, 0f),
                    end = Offset(pos, size.height),
                    strokeWidth = 3.dp.toPx()
                )
                // Horizontal groove shadow & highlight
                drawLine(
                    color = majorGrooveColor,
                    start = Offset(0f, pos),
                    end = Offset(size.width, pos),
                    strokeWidth = 3.dp.toPx()
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

    // Determine clay cell color
    val targetBgColor = when {
        cell.isConflicted -> if (isDark) ClayCellConflictBgDark else ClayCellConflictBgLight
        isSelected -> if (isDark) ClayCellSelectedBgDark else ClayCellSelectedBgLight
        cell.isStepTarget -> if (isDark) ClayCellStepHighlightBgDark else ClayCellStepHighlightBgLight
        cell.isHighlightedValue -> if (isDark) ClayCellMatchNumberBgDark else ClayCellMatchNumberBgLight
        isSameRowOrColOrBox -> if (isDark) ClayCellCrosshairBgDark else ClayCellCrosshairBgLight
        else -> Color.Transparent
    }

    val animatedBgColor by animateColorAsState(
        targetValue = targetBgColor,
        animationSpec = tween(durationMillis = 150),
        label = "clay_cell_bg"
    )

    val textColor = when {
        cell.isConflicted -> if (isDark) ClayCellConflictTextDark else ClayCellConflictTextLight
        cell.isGiven -> if (isDark) ClayCellGivenNumberDark else ClayCellGivenNumberLight
        else -> if (isDark) ClayCellUserNumberDark else ClayCellUserNumberLight
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(6.dp))
            .background(animatedBgColor)
            .bouncyClayClick { onCellClick() }
            .testTag("cell_${cell.row}_${cell.col}")
    ) {
        if (!cell.isEmpty) {
            Text(
                text = cell.value.toString(),
                fontSize = 23.sp,
                fontWeight = if (cell.isGiven) FontWeight.Black else FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center
            )
        } else if (cell.notes.isNotEmpty()) {
            PencilNotesView(notes = cell.notes)
        }
    }
}

@Composable
private fun PencilNotesView(notes: Set<Int>) {
    val isDark = isSystemInDarkTheme()
    val noteColor = if (isDark) Color(0xFFA89F91) else Color(0xFF786D5F)

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
                        fontWeight = FontWeight.Bold,
                        color = noteColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
