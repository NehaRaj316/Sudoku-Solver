package com.example.ui.screens

import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.PuzzleLibrary
import com.example.model.Difficulty
import com.example.ui.components.ClayHeroCard
import com.example.ui.components.Keypad
import com.example.ui.components.SolverActionsBar
import com.example.ui.components.SolverStatusBanner
import com.example.ui.components.StepExplanationCard
import com.example.ui.components.SudokuGrid
import com.example.ui.components.bouncyClayClick
import com.example.ui.components.claymorphic
import com.example.viewmodel.SudokuViewModel

@Composable
fun SolverScreen(
    viewModel: SudokuViewModel,
    modifier: Modifier = Modifier
) {
    val cells by viewModel.cells.collectAsState()
    val selectedRow by viewModel.selectedRow.collectAsState()
    val selectedCol by viewModel.selectedCol.collectAsState()
    val inputMode by viewModel.inputMode.collectAsState()
    val solveStatus by viewModel.solveStatus.collectAsState()
    val solveTimeMs by viewModel.solveTimeMs.collectAsState()
    val solutionCount by viewModel.solutionCount.collectAsState()
    val stepExplanation by viewModel.stepExplanation.collectAsState()
    val isAnimating by viewModel.isAnimating.collectAsState()
    val animationSpeedMs by viewModel.animationSpeedMs.collectAsState()
    val canUndo by viewModel.canUndo.collectAsState()
    val canRedo by viewModel.canRedo.collectAsState()

    var showPresetsDialog by remember { mutableStateOf(false) }
    var showGenerateDialog by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var saveTitleInput by remember { mutableStateOf("") }
    var showClearConfirm by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Claymation Stop-motion Studio Hero Banner
        ClayHeroCard()

        // Quick Clay Toolbar: Presets, Generate, Clear, Save
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ClayToolbarButton(
                icon = Icons.Default.FolderOpen,
                label = "Presets",
                modifier = Modifier.weight(1f),
                testTag = "btn_open_presets",
                onClick = { showPresetsDialog = true }
            )

            ClayToolbarButton(
                icon = Icons.Default.Casino,
                label = "Generate",
                modifier = Modifier.weight(1f),
                testTag = "btn_open_generate",
                onClick = { showGenerateDialog = true }
            )

            ClayToolbarButton(
                icon = Icons.Default.DeleteSweep,
                label = "Clear",
                modifier = Modifier.weight(0.9f),
                testTag = "btn_clear_board",
                onClick = { showClearConfirm = true }
            )

            ClayToolbarButton(
                icon = Icons.Default.BookmarkBorder,
                label = "Save",
                modifier = Modifier.weight(0.9f),
                testTag = "btn_save_board",
                onClick = {
                    saveTitleInput = "Clay Puzzle #${System.currentTimeMillis() % 10000}"
                    showSaveDialog = true
                }
            )
        }

        // Status banner
        SolverStatusBanner(
            status = solveStatus,
            clueCount = viewModel.getCluesCount(),
            conflictCount = viewModel.getConflictCount(),
            solveTimeMs = solveTimeMs,
            solutionCount = solutionCount
        )

        // Sudoku 9x9 Grid (Clay slab with tactile bouncy cells)
        SudokuGrid(
            cells = cells,
            selectedRow = selectedRow,
            selectedCol = selectedCol,
            onCellClick = { r, c -> viewModel.selectCell(r, c) }
        )

        // Step-by-Step Logic Explanation Card
        StepExplanationCard(
            explanation = stepExplanation,
            onApplyStep = { viewModel.applyStep() },
            onDismiss = { viewModel.dismissStep() }
        )

        // Solver Hero Action Buttons
        SolverActionsBar(
            isAnimating = isAnimating,
            animationSpeedMs = animationSpeedMs,
            onSolveInstant = { viewModel.solveInstantly() },
            onSolveStep = { viewModel.requestStep() },
            onToggleAnimate = { viewModel.toggleAnimation() },
            onChangeSpeed = { speed -> viewModel.setAnimationSpeed(speed) }
        )

        // Keypad for cell editing
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

    // Presets Dialog
    if (showPresetsDialog) {
        AlertDialog(
            onDismissRequest = { showPresetsDialog = false },
            title = { Text("Load Preset Clay Puzzle", fontWeight = FontWeight.Black) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    PuzzleLibrary.presets.forEach { preset ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("preset_item_${preset.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(preset.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(
                                        "${preset.difficulty.label} • ${preset.description}",
                                        fontSize = 11.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Button(
                                    onClick = {
                                        viewModel.loadPreset(preset, false)
                                        showPresetsDialog = false
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Load", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showPresetsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Generate Dialog
    if (showGenerateDialog) {
        AlertDialog(
            onDismissRequest = { showGenerateDialog = false },
            title = { Text("Generate Clay Sudoku", fontWeight = FontWeight.Black) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select difficulty tier for newly sculpted puzzle:")
                    Difficulty.values().forEach { diff ->
                        OutlinedButton(
                            onClick = {
                                viewModel.generateNewPuzzle(diff, false)
                                showGenerateDialog = false
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("${diff.label} (${diff.cluesCount} clues)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showGenerateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Save Dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save Clay Board", fontWeight = FontWeight.Black) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter a name for this handcrafted puzzle in your library:")
                    OutlinedTextField(
                        value = saveTitleInput,
                        onValueChange = { saveTitleInput = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveCurrentBoard(saveTitleInput)
                        showSaveDialog = false
                    }
                ) {
                    Text("Save to Clay Box")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Clear Confirmation Dialog
    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Reset Clay Board?", fontWeight = FontWeight.Black) },
            text = { Text("Do you want to reset all clay cells to empty, or only roll back your user entries?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearBoard(keepGivensOnly = false)
                        showClearConfirm = false
                    }
                ) {
                    Text("Roll Back All")
                }
            },
            dismissButton = {
                Row {
                    OutlinedButton(
                        onClick = {
                            viewModel.clearBoard(keepGivensOnly = true)
                            showClearConfirm = false
                        }
                    ) {
                        Text("Reset User Only")
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    TextButton(onClick = { showClearConfirm = false }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }
}

@Composable
fun ClayToolbarButton(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color(0xFF26201B) else Color(0xFFFFFDF9)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(42.dp)
            .claymorphic(
                shape = RoundedCornerShape(12.dp),
                backgroundColor = bgColor,
                cornerRadiusDp = 12.dp,
                elevation = 3.dp
            )
            .bouncyClayClick { onClick() }
            .testTag(testTag)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
