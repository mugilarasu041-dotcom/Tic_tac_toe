package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TicTacToeTheme

@Composable
fun CustomSettingsPanel(
    gridSize: Int,
    isVsAi: Boolean,
    aiDifficulty: String,
    selectedTheme: TicTacToeTheme,
    onGridSizeChange: (Int) -> Unit,
    onVsAiChange: (Boolean) -> Unit,
    onDifficultyChange: (String) -> Unit,
    onThemeChange: (TicTacToeTheme) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .testTag("settings_panel")
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = selectedTheme.surface.copy(alpha = 0.85f),
            contentColor = selectedTheme.onSurface
        ),
        border = BorderStroke(1.dp, selectedTheme.gridLine.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mode Selectors
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // VS AI button
                Button(
                    onClick = { onVsAiChange(true) },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("mode_vs_ai_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isVsAi) selectedTheme.accent else selectedTheme.gridLine.copy(alpha = 0.2f),
                        contentColor = if (isVsAi) Color.White else selectedTheme.onSurface
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Computer,
                        contentDescription = "Vs AI",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "vs AI Player",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // Multiplayer buttons
                Button(
                    onClick = { onVsAiChange(false) },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("mode_vs_human_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isVsAi) selectedTheme.accent else selectedTheme.gridLine.copy(alpha = 0.2f),
                        contentColor = if (!isVsAi) Color.White else selectedTheme.onSurface
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = "Vs Friend",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "2 Players (Local)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            // AI difficulty selector, animated visibility
            AnimatedVisibility(
                visible = isVsAi,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "AI Level Challenge",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = selectedTheme.onSurface.copy(alpha = 0.7f)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("EASY", "MEDIUM", "HARD").forEach { level ->
                            val isSelected = aiDifficulty == level
                            val btnBg = if (isSelected) {
                                selectedTheme.accent.copy(alpha = 0.15f)
                            } else {
                                Color.Transparent
                            }
                            val btnBorder = if (isSelected) {
                                BorderStroke(1.5.dp, selectedTheme.accent)
                            } else {
                                BorderStroke(1.dp, selectedTheme.gridLine.copy(alpha = 0.4f))
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(btnBg)
                                    .clickable { onDifficultyChange(level) }
                                    .testTag("diff_${level.lowercase()}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = level,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) selectedTheme.accent else selectedTheme.onSurface.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }

            Divider(color = selectedTheme.gridLine.copy(alpha = 0.3f), thickness = 1.dp)

            // Grid Size Selector row
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Grid Dimensions",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = selectedTheme.onSurface.copy(alpha = 0.7f)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(3, 4, 5).forEach { size ->
                        val isSelected = gridSize == size
                        val sizeText = "${size}x${size}"
                        val btnBg = if (isSelected) {
                            selectedTheme.accent.copy(alpha = 0.15f)
                        } else {
                            Color.Transparent
                        }
                        val btnBorder = if (isSelected) {
                            BorderStroke(1.5.dp, selectedTheme.accent)
                        } else {
                            BorderStroke(1.dp, selectedTheme.gridLine.copy(alpha = 0.4f))
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(btnBg)
                                .clickable { onGridSizeChange(size) }
                                .testTag("grid_${size}x${size}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = sizeText,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) selectedTheme.accent else selectedTheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            Divider(color = selectedTheme.gridLine.copy(alpha = 0.3f), thickness = 1.dp)

            // Dynamic Styling Themes
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Aesthetic Themes",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = selectedTheme.onSurface.copy(alpha = 0.7f)
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 4.dp)
                ) {
                    items(TicTacToeTheme.ALL_THEMES) { themeItem ->
                        val isSelected = selectedTheme.id == themeItem.id
                        val borderCol = if (isSelected) selectedTheme.accent else themeItem.gridLine.copy(alpha = 0.7f)
                        val borderWidth = if (isSelected) 2.dp else 1.dp

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(themeItem.surface)
                                .clickable { onThemeChange(themeItem) }
                                .testTag("theme_chip_${themeItem.id}")
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Mini color palette indicators
                                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(themeItem.colorX))
                                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(themeItem.colorO))
                                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(themeItem.accent))
                                }
                                Text(
                                    text = themeItem.name,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = themeItem.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
