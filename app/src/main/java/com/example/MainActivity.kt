package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.PlayScreen
import com.example.ui.screens.SolverScreen
import com.example.ui.screens.TechniquesScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.SudokuViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var darkThemePreference by remember { mutableStateOf<Boolean?>(null) }
            val systemDark = isSystemInDarkTheme()
            val isDark = darkThemePreference ?: systemDark

            MyApplicationTheme(darkTheme = isDark) {
                SudokuApp(
                    isDarkTheme = isDark,
                    onToggleTheme = {
                        darkThemePreference = !isDark
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SudokuApp(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    viewModel: SudokuViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    var showAboutDialog by remember { mutableStateOf(false) }

    // Handle system back navigation to return to Solver tab
    BackHandler(enabled = currentScreen != AppScreen.SOLVER) {
        viewModel.setScreen(AppScreen.SOLVER)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = when (currentScreen) {
                            AppScreen.SOLVER -> "Sudoku Solver"
                            AppScreen.PLAY -> "Sudoku Play"
                            AppScreen.LIBRARY -> "Puzzle Library"
                            AppScreen.TECHNIQUES -> "Solving Guide"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp
                    )
                },
                actions = {
                    IconButton(
                        onClick = onToggleTheme,
                        modifier = Modifier.testTag("btn_toggle_theme")
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Dark/Light theme",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = { showAboutDialog = true },
                        modifier = Modifier.testTag("btn_about")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "About Sudoku Solver",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.testTag("bottom_nav")
            ) {
                NavigationBarItem(
                    selected = currentScreen == AppScreen.SOLVER,
                    onClick = { viewModel.setScreen(AppScreen.SOLVER) },
                    icon = {
                        Icon(
                            Icons.Default.Bolt,
                            contentDescription = "Solver",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = { Text("Solver", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("nav_tab_solver")
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.PLAY,
                    onClick = { viewModel.setScreen(AppScreen.PLAY) },
                    icon = {
                        Icon(
                            Icons.Default.PlayCircle,
                            contentDescription = "Play",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = { Text("Play", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("nav_tab_play")
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.LIBRARY,
                    onClick = { viewModel.setScreen(AppScreen.LIBRARY) },
                    icon = {
                        Icon(
                            Icons.Default.VideoLibrary,
                            contentDescription = "Library",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = { Text("Library", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("nav_tab_library")
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.TECHNIQUES,
                    onClick = { viewModel.setScreen(AppScreen.TECHNIQUES) },
                    icon = {
                        Icon(
                            Icons.Default.MenuBook,
                            contentDescription = "Guide",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = { Text("Guide", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("nav_tab_guide")
                )
            }
        }
    ) { innerPadding ->
        when (currentScreen) {
            AppScreen.SOLVER -> SolverScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
            AppScreen.PLAY -> PlayScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
            AppScreen.LIBRARY -> LibraryScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
            AppScreen.TECHNIQUES -> TechniquesScreen(
                modifier = Modifier.padding(innerPadding)
            )
        }
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("Sudoku Solver & Player", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Featuring:\n" +
                    "• High-speed MRV backtracking solver\n" +
                    "• Step-by-step logic deductions with human explanations\n" +
                    "• Animated solver with adjustable playback speed\n" +
                    "• Real-time conflict & multiple solution validation\n" +
                    "• Interactive player with candidate notes, timer, and mistake tracking\n" +
                    "• Curated classic puzzles (including Arto Inkala's hardest) and on-device puzzle generator\n" +
                    "• Local Room database to save boards and game history.",
                    fontSize = 13.5.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Got it")
                }
            }
        )
    }
}
