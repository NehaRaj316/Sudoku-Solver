package com.example.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ScoreRecordEntity
import com.example.model.Difficulty
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ClayScoreboardDialog(
    currentScore: Int,
    highScore: Int,
    highestCombo: Int,
    totalWins: Int,
    isCountdownMode: Boolean,
    countdownDurationMinutes: Int,
    records: List<ScoreRecordEntity>,
    onSelectTimerMode: (isCountdown: Boolean, durationMinutes: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val cardBg = if (isDark) Color(0xFF26201B) else Color(0xFFFFFDF9)

    // Compute player rank
    val rankTitle = when {
        highScore >= 50000 -> "Clay Grandmaster 🏆"
        highScore >= 25000 -> "Clay Artisan 🎨"
        highScore >= 10000 -> "Clay Sculptor 🗿"
        highScore >= 2500 -> "Clay Apprentice 🪵"
        else -> "Clay Novice 🌱"
    }

    val rankProgress = (highScore % 10000) / 10000f

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Clay Scoreboard & Rank", fontWeight = FontWeight.Black, fontSize = 18.sp)
                    Text("Stop-Motion Hall of Fame", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Rank Card
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .claymorphic(
                                shape = RoundedCornerShape(14.dp),
                                backgroundColor = cardBg,
                                cornerRadiusDp = 14.dp,
                                elevation = 3.dp
                            )
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Current Rank:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = rankTitle,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { rankProgress.coerceIn(0.05f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp),
                                strokeCap = StrokeCap.Round,
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = if (isDark) Color(0xFF382E25) else Color(0xFFE5DDD0)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "High Score: $highScore pts • Total Wins: $totalWins",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Urgency Mode Selector (Stopwatch vs Urgent Countdown)
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .claymorphic(
                                shape = RoundedCornerShape(14.dp),
                                backgroundColor = if (isDark) Color(0xFF2C241D) else Color(0xFFF6F0E6),
                                cornerRadiusDp = 14.dp,
                                elevation = 2.dp
                            )
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "⏱ Gamified Timer Mode:",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Black
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                ClayModeSelectButton(
                                    label = "Stopwatch",
                                    isSelected = !isCountdownMode,
                                    onClick = { onSelectTimerMode(false, 0) },
                                    modifier = Modifier.weight(1f)
                                )

                                ClayModeSelectButton(
                                    label = "5m Rush",
                                    isSelected = isCountdownMode && countdownDurationMinutes == 5,
                                    onClick = { onSelectTimerMode(true, 5) },
                                    modifier = Modifier.weight(1f)
                                )

                                ClayModeSelectButton(
                                    label = "8m Rush",
                                    isSelected = isCountdownMode && countdownDurationMinutes == 8,
                                    onClick = { onSelectTimerMode(true, 8) },
                                    modifier = Modifier.weight(1f)
                                )

                                ClayModeSelectButton(
                                    label = "12m Rush",
                                    isSelected = isCountdownMode && countdownDurationMinutes == 12,
                                    onClick = { onSelectTimerMode(true, 12) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Stats Grid (High Score, Best Streak, Fast Speed)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // High Score
                        ClayStatMiniBox(
                            title = "High Score",
                            value = "$highScore",
                            icon = Icons.Default.Star,
                            modifier = Modifier.weight(1f)
                        )
                        // Best Combo
                        ClayStatMiniBox(
                            title = "Best Combo",
                            value = "$highestCombo Streak",
                            icon = Icons.Default.LocalFireDepartment,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Recent match records list
                item {
                    Text(
                        text = "Recent Matches:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (records.isEmpty()) {
                    item {
                        Text(
                            text = "No games recorded yet. Complete a puzzle to write your score in clay history!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(records.take(8)) { record ->
                        val date = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(record.completedAt))
                        val m = record.timeSeconds / 60
                        val s = record.timeSeconds % 60
                        val duration = "%02d:%02d".format(m, s)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .claymorphic(
                                    shape = RoundedCornerShape(10.dp),
                                    backgroundColor = cardBg,
                                    cornerRadiusDp = 10.dp,
                                    elevation = 2.dp
                                )
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${record.difficulty} • $duration",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "$date • ${record.mistakes} mistakes",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = "+${record.score} pts",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("btn_close_scoreboard")
            ) {
                Text("Got It", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun ClayModeSelectButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(34.dp)
            .claymorphic(
                shape = RoundedCornerShape(8.dp),
                backgroundColor = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    if (isDark) Color(0xFF382E25) else Color(0xFFFFFDF9)
                },
                cornerRadiusDp = 8.dp,
                elevation = if (isSelected) 3.dp else 1.dp
            )
            .bouncyClayClick { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ClayStatMiniBox(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    Box(
        modifier = modifier
            .claymorphic(
                shape = RoundedCornerShape(12.dp),
                backgroundColor = if (isDark) Color(0xFF26201B) else Color(0xFFFFFDF9),
                cornerRadiusDp = 12.dp,
                elevation = 3.dp
            )
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(title, fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(value, fontSize = 13.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}
