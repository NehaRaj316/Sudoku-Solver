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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.claymorphic

data class TechniqueItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val difficultyLevel: String,
    val description: String,
    val ruleSummary: String
)

@Composable
fun TechniquesScreen(
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val cardBg = if (isDark) Color(0xFF26201B) else Color(0xFFFFFDF9)

    val techniques = listOf(
        TechniqueItem(
            title = "1. Fundamental Sudoku Rules",
            subtitle = "Rows, Columns, and 3x3 Boxes",
            icon = Icons.Default.GridView,
            difficultyLevel = "Basic",
            description = "Sudoku is played on a 9x9 grid divided into nine 3x3 subgrids. The objective is to fill every cell with a digit from 1 to 9 so that each row, each column, and each 3x3 box contains all numbers from 1 to 9 exactly once without repeats.",
            ruleSummary = "No duplicate numbers in any row, column, or 3x3 box."
        ),
        TechniqueItem(
            title = "2. Naked Single",
            subtitle = "The only possible number in a cell",
            icon = Icons.Default.CheckCircle,
            difficultyLevel = "Beginner",
            description = "A Naked Single occurs when a cell has only one possible candidate remaining. By scanning its intersecting row, column, and 3x3 block, you find that 8 other numbers (1 through 9) are already present. The remaining number must belong in this cell.",
            ruleSummary = "Example: If Row 1 has 1,2,3,4, Column 1 has 5,6,7, and Box 1 has 8, then Cell R1C1 must be 9."
        ),
        TechniqueItem(
            title = "3. Hidden Single",
            subtitle = "The only place a number can go",
            icon = Icons.Default.Search,
            difficultyLevel = "Intermediate",
            description = "Even when a cell has several candidates, a digit may only be eligible to fit in one specific cell within a given row, column, or 3x3 box. All other empty cells in that unit are blocked from taking that digit.",
            ruleSummary = "Rule: Look at each digit 1-9 across a unit. If a digit can only legally fit in one square, place it immediately!"
        ),
        TechniqueItem(
            title = "4. Pencil Marks & Candidate Elimination",
            subtitle = "Taking small notes in empty squares",
            icon = Icons.Default.Lightbulb,
            difficultyLevel = "All Levels",
            description = "Using the 'Notes' toggle, you can mark possible candidates inside empty cells. As you place more numbers on the board, candidate notes automatically update, making complex patterns visible at a glance.",
            ruleSummary = "Tip: Keep notes updated to effortlessly spot Naked Pairs and Triples."
        ),
        TechniqueItem(
            title = "5. Naked Pair",
            subtitle = "Two cells locked with two numbers",
            icon = Icons.Default.AutoAwesome,
            difficultyLevel = "Advanced",
            description = "If two cells within the same row, column, or 3x3 box both contain identical candidate notes of only [A, B] and nothing else, those two cells will claim digits A and B. You can safely remove A and B as candidates from all other cells in that unit.",
            ruleSummary = "Rule: 2 cells with candidates [3, 7] eliminates 3 and 7 from the rest of the unit."
        ),
        TechniqueItem(
            title = "6. Pointing Pairs / Box-Line Reduction",
            subtitle = "Locked candidates across intersections",
            icon = Icons.Default.Psychology,
            difficultyLevel = "Master",
            description = "When a candidate appears only within a single row or column inside a 3x3 box, it is guaranteed that the candidate must be placed along that line. Therefore, that candidate can be eliminated from the rest of that row or column outside the box.",
            ruleSummary = "Eliminates candidates outside the box along the matching row/column."
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(techniques.size) { index ->
            val item = techniques[index]
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
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item.title,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = item.difficultyLevel,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.subtitle,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = item.description,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = item.ruleSummary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}
