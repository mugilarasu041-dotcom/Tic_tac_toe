package com.example.ui.components

import android.text.format.DateUtils
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameRecord
import com.example.ui.theme.TicTacToeTheme

@Composable
fun MatchHistorySection(
    records: List<GameRecord>,
    currentTheme: TicTacToeTheme,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .testTag("match_history_section")
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = currentTheme.accent,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Match History",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = currentTheme.onBackground
                )
            }

            if (records.isNotEmpty()) {
                IconButton(
                    onClick = { showDialog = true },
                    modifier = Modifier
                        .testTag("clear_history_btn")
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear History",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        if (records.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(currentTheme.surface.copy(alpha = 0.5f), shape = RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = currentTheme.onSurface.copy(alpha = 0.3f),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "No matches played yet offline.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = currentTheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        } else {
            // Display only some recent games to save space, let's list them inside a compact column
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                records.take(5).forEach { record ->
                    MatchHistoryRow(
                        record = record,
                        currentTheme = currentTheme
                    )
                }

                if (records.size > 5) {
                    Text(
                        text = "+ ${records.size - 5} more games saved offline",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = currentTheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 4.dp)
                    )
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Reset History?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete all offline score statistics and past match history?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearHistory()
                        showDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun MatchHistoryRow(
    record: GameRecord,
    currentTheme: TicTacToeTheme,
    modifier: Modifier = Modifier
) {
    val relativeTime = DateUtils.getRelativeTimeSpanString(
        record.timestamp,
        System.currentTimeMillis(),
        DateUtils.MINUTE_IN_MILLIS,
        DateUtils.FORMAT_ABBREV_RELATIVE
    ).toString()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(currentTheme.surface.copy(alpha = 0.7f), shape = RoundedCornerShape(12.dp))
            .border(0.5.dp, currentTheme.gridLine.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(3.dp),
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Board tag
                Text(
                    text = "${record.gridSize}x${record.gridSize}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = currentTheme.accent,
                    modifier = Modifier
                        .background(currentTheme.accent.copy(alpha = 0.12f), shape = RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )

                // Mode tags
                val modeLabel = if (record.mode == "VS_AI") {
                    "VS ${record.difficulty}"
                } else {
                    "Local 2P"
                }

                Text(
                    text = modeLabel,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = currentTheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Text(
                text = relativeTime,
                fontSize = 10.sp,
                color = currentTheme.onSurface.copy(alpha = 0.5f)
            )
        }

        // Winner badge
        val (badgeLabel, badgeColor, textCol) = when (record.winner) {
            "PLAYER_X" -> Triple("X Won", currentTheme.colorX.copy(alpha = 0.15f), currentTheme.colorX)
            "PLAYER_O" -> Triple("O Won", currentTheme.colorO.copy(alpha = 0.15f), currentTheme.colorO)
            else -> Triple("Draw", currentTheme.onSurface.copy(alpha = 0.1f), currentTheme.onSurface.copy(alpha = 0.7f))
        }

        Box(
            modifier = Modifier
                .background(badgeColor, shape = RoundedCornerShape(6.dp))
                .border(0.5.dp, textCol.copy(alpha = 0.5f), shape = RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = badgeLabel,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = textCol
            )
        }
    }
}
