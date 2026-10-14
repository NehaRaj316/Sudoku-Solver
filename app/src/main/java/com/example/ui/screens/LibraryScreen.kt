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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SavedPuzzleEntity
import com.example.data.ScoreRecordEntity
import com.example.engine.PresetPuzzle
import com.example.engine.PuzzleLibrary
import com.example.ui.components.ClayStatMiniBox
import com.example.ui.components.bouncyClayClick
import com.example.ui.components.claymorphic
import com.example.viewmodel.AppScreen
import com.example.viewmodel.SudokuViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LibraryScreen(
    viewModel: SudokuViewModel,
    modifier: Modifier = Modifier
) {
    val savedPuzzles by viewModel.savedPuzzles.collectAsState()
    val scoreRecords by viewModel.scoreRecords.collectAsState()
    val highScore by viewModel.allTimeHighScore.collectAsState()
    val allTimeHighestCombo by viewModel.allTimeHighestCombo.collectAsState()
    val totalWins by viewModel.totalWins.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Curated", "Saved (${savedPuzzles.size})", "Scoreboard 🏆")
    val isDark = isSystemInDarkTheme()
    val cardBg = if (isDark) Color(0xFF26201B) else Color(0xFFFFFDF9)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Clay Tab Row
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title, fontWeight = FontWeight.Bold, fontSize = 12.5.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTabIndex) {
            0 -> {
                // Curated Classics List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(PuzzleLibrary.presets) { preset ->
                        PresetCard(
                            preset = preset,
                            onSolve = {
                                viewModel.loadPreset(preset, false)
                                viewModel.setScreen(AppScreen.SOLVER)
                            },
                            onPlay = {
                                viewModel.loadPreset(preset, true)
                                viewModel.setScreen(AppScreen.PLAY)
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
            1 -> {
                // Saved Puzzles from Room
                if (savedPuzzles.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No saved clay puzzles yet",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Tap 'Save' in the Solver or Play screen to preserve your sculpted boards and track history here.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(savedPuzzles) { puzzle ->
                            SavedPuzzleCard(
                                puzzle = puzzle,
                                onOpen = {
                                    viewModel.loadSavedPuzzle(puzzle)
                                    viewModel.setScreen(AppScreen.SOLVER)
                                },
                                onDelete = { viewModel.deleteSavedPuzzle(puzzle.id) }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
            2 -> {
                // Scoreboard & Leaderboard Tab
                val rankTitle = when {
                    highScore >= 50000 -> "Clay Grandmaster 🏆"
                    highScore >= 25000 -> "Clay Artisan 🎨"
                    highScore >= 10000 -> "Clay Sculptor 🗿"
                    highScore >= 2500 -> "Clay Apprentice 🪵"
                    else -> "Clay Novice 🌱"
                }
                val rankProgress = (highScore % 10000) / 10000f

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Rank Header Card
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .claymorphic(
                                    shape = RoundedCornerShape(16.dp),
                                    backgroundColor = cardBg,
                                    cornerRadiusDp = 16.dp,
                                    elevation = 4.dp
                                )
                                .padding(14.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.EmojiEvents,
                                            contentDescription = null,
                                            tint = Color(0xFFF59E0B),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Sculptor Level", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Text(
                                        text = rankTitle,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { rankProgress.coerceIn(0.05f, 1f) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp),
                                    strokeCap = StrokeCap.Round,
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = if (isDark) Color(0xFF382E25) else Color(0xFFE5DDD0)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "High Score: $highScore pts • Completed: $totalWins Puzzles",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Stat Pods
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ClayStatMiniBox(
                                title = "All-Time Best",
                                value = "$highScore pts",
                                icon = Icons.Default.Star,
                                modifier = Modifier.weight(1f)
                            )
                            ClayStatMiniBox(
                                title = "Top Streak",
                                value = "$allTimeHighestCombo Combo",
                                icon = Icons.Default.LocalFireDepartment,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Match History Title
                    item {
                        Text(
                            text = "Match History & Hall of Fame (${scoreRecords.size})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    if (scoreRecords.isEmpty()) {
                        item {
                            Text(
                                text = "No completed matches yet. Jump into Play Mode to set your first high score record!",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(scoreRecords) { record ->
                            ScoreRecordCard(record = record)
                        }
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
fun ScoreRecordCard(record: ScoreRecordEntity) {
    val isDark = isSystemInDarkTheme()
    val cardBg = if (isDark) Color(0xFF26201B) else Color(0xFFFFFDF9)
    val date = SimpleDateFormat("MMM d, yyyy • HH:mm", Locale.getDefault()).format(Date(record.completedAt))
    val m = record.timeSeconds / 60
    val s = record.timeSeconds % 60
    val timeStr = "%02d:%02d".format(m, s)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .claymorphic(
                shape = RoundedCornerShape(12.dp),
                backgroundColor = cardBg,
                cornerRadiusDp = 12.dp,
                elevation = 2.dp
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${record.difficulty} Match",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFDCFCE7)
                    ) {
                        Text(
                            text = timeStr,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF166534),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "$date • ${record.mistakes} mistakes • Peak ${record.highestCombo}x combo",
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "+${record.score} pts",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun PresetCard(
    preset: PresetPuzzle,
    onSolve: () -> Unit,
    onPlay: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val cardBg = if (isDark) Color(0xFF26201B) else Color(0xFFFFFDF9)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .claymorphic(
                shape = RoundedCornerShape(16.dp),
                backgroundColor = cardBg,
                cornerRadiusDp = 16.dp,
                elevation = 4.dp
            )
            .padding(14.dp)
            .testTag("library_preset_${preset.id}")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = preset.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = preset.difficulty.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = preset.description,
                fontSize = 12.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .height(36.dp)
                        .claymorphic(
                            shape = RoundedCornerShape(8.dp),
                            backgroundColor = if (isDark) Color(0xFF332B25) else Color(0xFFF1EBE1),
                            cornerRadiusDp = 8.dp,
                            elevation = 2.dp
                        )
                        .bouncyClayClick { onSolve() }
                        .padding(horizontal = 12.dp)
                ) {
                    Text("Analyze in Solver", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .height(36.dp)
                        .claymorphic(
                            shape = RoundedCornerShape(8.dp),
                            backgroundColor = MaterialTheme.colorScheme.primary,
                            cornerRadiusDp = 8.dp,
                            elevation = 3.dp
                        )
                        .bouncyClayClick { onPlay() }
                        .padding(horizontal = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Play Clay Game",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SavedPuzzleCard(
    puzzle: SavedPuzzleEntity,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val dateStr = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(puzzle.dateCreated))
    val cardBg = if (isDark) Color(0xFF26201B) else Color(0xFFFFFDF9)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .claymorphic(
                shape = RoundedCornerShape(16.dp),
                backgroundColor = cardBg,
                cornerRadiusDp = 16.dp,
                elevation = 4.dp
            )
            .padding(14.dp)
            .testTag("saved_puzzle_${puzzle.id}")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = puzzle.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "$dateStr • ${puzzle.difficulty}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (puzzle.isSolved) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFDCFCE7)
                        ) {
                            Text(
                                text = "Solved",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF166534),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .height(36.dp)
                        .claymorphic(
                            shape = RoundedCornerShape(8.dp),
                            backgroundColor = MaterialTheme.colorScheme.primary,
                            cornerRadiusDp = 8.dp,
                            elevation = 3.dp
                        )
                        .bouncyClayClick { onOpen() }
                        .padding(horizontal = 14.dp)
                ) {
                    Text(
                        "Open in Solver",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
        }
    }
}
