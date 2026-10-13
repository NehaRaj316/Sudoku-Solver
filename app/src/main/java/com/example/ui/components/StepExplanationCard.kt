package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.StepExplanation

@Composable
fun StepExplanationCard(
    explanation: StepExplanation?,
    onApplyStep: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val cardBg = if (isDark) Color(0xFF422006) else Color(0xFFFEF3C7) // Warm ochre clay

    AnimatedVisibility(
        visible = explanation != null,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        if (explanation != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .claymorphic(
                        shape = RoundedCornerShape(18.dp),
                        backgroundColor = cardBg,
                        cornerRadiusDp = 18.dp,
                        elevation = 5.dp
                    )
                    .padding(14.dp)
                    .testTag("step_explanation_card")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = if (isDark) Color(0xFFFDE68A) else Color(0xFF92400E),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isDark) Color(0xFF78350F) else Color(0xFFF59E0B)
                            ) {
                                Text(
                                    text = explanation.technique.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close hint",
                                tint = if (isDark) Color(0xFFFDE68A) else Color(0xFF92400E),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Suggested Move: Place ${explanation.value} at Row ${explanation.row + 1}, Col ${explanation.col + 1}",
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isDark) Color(0xFFFEF3C7) else Color(0xFF78350F)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = explanation.explanation,
                        fontSize = 13.sp,
                        color = if (isDark) Color(0xFFFDE68A) else Color(0xFF92400E),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .height(40.dp)
                                .claymorphic(
                                    shape = RoundedCornerShape(12.dp),
                                    backgroundColor = if (isDark) Color(0xFFB45309) else Color(0xFFD97706),
                                    cornerRadiusDp = 12.dp,
                                    elevation = 3.dp
                                )
                                .bouncyClayClick { onApplyStep() }
                                .padding(horizontal = 16.dp)
                                .testTag("btn_apply_step")
                        ) {
                            Text(
                                text = "Mold Tile (Apply)",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
