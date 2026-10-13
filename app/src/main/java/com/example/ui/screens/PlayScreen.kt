package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Difficulty
import com.example.ui.components.Keypad
import com.example.ui.components.StepExplanationCard
import com.example.ui.components.SudokuGrid
import com.example.viewmodel.SudokuViewModel

@Composable
fun PlayScreen(
    viewModel: SudokuViewModel,
    modifier: Modifier = Modifier
) {
    val cells by viewModel.cells.collectAsState()
    val selectedRow by viewModel.selectedRow.collectAsState()
    val selectedCol by viewModel.selectedCol.collectAsState()
    val inputMode by viewModel.inputMode.collectAsState()
    val canUndo by viewModel.canUndo.collectAsState()
    val canRedo by viewModel.canRedo.collectAsState()
    val playDifficulty by viewModel.playDifficulty.collectAsState()
    val timerSeconds by viewModel.timerSeconds.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()
    val mistakesCount by viewModel.mistakesCount.collectAsState()
    val isGameWon by viewModel.isGameWon.collectAsState()
    val stepExplanation by viewModel.stepExplanation.collectAsState()

    var showNewGameDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val minutes = timerSeconds / 60
    val seconds = timerSeconds % 60
    val timeFormatted = "%02d:%02d".format(minutes, seconds)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Play HUD Header: Difficulty, Timer, Mistakes, New Game
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Difficulty Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = playDifficulty.label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // Timer with play/pause
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.toggleTimer() },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Toggle Timer",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = timeFormatted,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Mistakes Counter
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (mistakesCount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Mistakes: $mistakesCount",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (mistakesCount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // New Game Button
                FilledTonalButton(
                    onClick = { showNewGameDialog = true },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("New", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Sudoku Grid
        SudokuGrid(
            cells = cells,
            selectedRow = selectedRow,
            selectedCol = selectedCol,
            onCellClick = { r, c -> viewModel.selectCell(r, c) }
        )

        // Hint bar & Step Explanation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalButton(
                onClick = { viewModel.requestStep() },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .testTag("btn_play_hint")
            ) {
                Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Get Smart Hint / Next Step", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        StepExplanationCard(
            explanation = stepExplanation,
            onApplyStep = { viewModel.applyStep() },
            onDismiss = { viewModel.dismissStep() }
        )

        // Keypad
        Keypad(
            digitCounts = viewModel.getDigitCounts(),
            inputMode = inputMode,
            canUndo = canUndo,
            canRedo = canRedo,
            onDigitClick = { digit -> viewModel.inputDigit(digit) },
            onEraseClick = { viewModel.eraseCurrentCell() },
            onToggleInputMode = { viewModel.toggleInputMode() },
            onUndoClick = { viewModel.undo() },
            onRedoClick = { viewModel.redo() }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }

    // New Game Dialog
    if (showNewGameDialog) {
        AlertDialog(
            onDismissRequest = { showNewGameDialog = false },
            title = { Text("Start New Sudoku Game", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select puzzle difficulty:")
                    Difficulty.values().forEach { diff ->
                        Button(
                            onClick = {
                                viewModel.generateNewPuzzle(diff, isForPlayMode = true)
                                showNewGameDialog = false
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("${diff.label} (${diff.cluesCount} initial clues)")
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showNewGameDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Win Celebration Dialog
    if (isGameWon) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissWinDialog() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = Color(0xFFEAB308),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Puzzle Completed!", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "Congratulations! You completed the ${playDifficulty.label} puzzle in $timeFormatted with $mistakesCount mistakes.",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Great job exercising your brain logic!",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.dismissWinDialog()
                        showNewGameDialog = true
                    }
                ) {
                    Text("Play Again")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissWinDialog() }) {
                    Text("View Board")
                }
            }
        )
    }
}
