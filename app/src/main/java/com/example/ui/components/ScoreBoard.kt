package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun ScoreBoard(
    records: List<GameRecord>,
    isVsAi: Boolean,
    currentTheme: TicTacToeTheme,
    modifier: Modifier = Modifier
) {
    // Filter records by mode to give specific score breakdowns
    val relevantRecords = records.filter {
        it.mode == if (isVsAi) "VS_AI" else "VS_HUMAN"
    }

    val xWins = relevantRecords.count { it.winner == "PLAYER_X" }
    val oWins = relevantRecords.count { it.winner == "PLAYER_O" }
    val ties = relevantRecords.count { it.winner == "TIE" }

    Row(
        modifier = modifier
            .testTag("scoreboard_container")
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Player X Card
        Box(
            modifier = Modifier
                .weight(1.5f)
                .background(currentTheme.surface.copy(alpha = 0.8f), shape = RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Player X (You)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = currentTheme.onSurface.copy(alpha = 0.65f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$xWins",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = currentTheme.colorX
                )
            }
        }

        // Ties Card
        Box(
            modifier = Modifier
                .weight(1f)
                .background(currentTheme.surface.copy(alpha = 0.8f), shape = RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Ties",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = currentTheme.onSurface.copy(alpha = 0.65f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$ties",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = currentTheme.onSurface
                )
            }
        }

        // Player O (AI / Player 2) Card
        Box(
            modifier = Modifier
                .weight(1.5f)
                .background(currentTheme.surface.copy(alpha = 0.8f), shape = RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isVsAi) "Player O (AI)" else "Player O",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = currentTheme.onSurface.copy(alpha = 0.65f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$oWins",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = currentTheme.colorO
                )
            }
        }
    }
}
