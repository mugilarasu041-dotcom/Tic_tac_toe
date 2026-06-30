package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameDatabase
import com.example.data.GameRepository
import com.example.ui.TicTacToeViewModel
import com.example.ui.TicTacToeViewModelFactory
import com.example.ui.components.GameBoard
import com.example.ui.components.CustomSettingsPanel
import com.example.ui.components.ScoreBoard
import com.example.ui.components.MatchHistorySection
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val database = remember { GameDatabase.getDatabase(applicationContext) }
            val repository = remember { GameRepository(database.gameDao) }
            val viewModel: TicTacToeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = TicTacToeViewModelFactory(repository)
            )

            val currentTheme by viewModel.selectedTheme
            val board by viewModel.board
            val gridSize by viewModel.gridSize
            val isVsAi by viewModel.isVsAi
            val aiDifficulty by viewModel.aiDifficulty
            val currentTurn by viewModel.currentTurn
            val isGameOver by viewModel.isGameOver
            val winner by viewModel.winner
            val winningLine by viewModel.winningLineIndices
            val isAiThinking by viewModel.isAiThinking

            val historyRecords by viewModel.gameRecords.collectAsState()

            // Custom dynamic MaterialTheme color scheme wrapper matching selected theme
            val customColorScheme = if (currentTheme.isDark) {
                darkColorScheme(
                    primary = currentTheme.accent,
                    onPrimary = Color.Yellow,
                    background = currentTheme.background,
                    onBackground = currentTheme.onBackground,
                    surface = currentTheme.surface,
                    onSurface = currentTheme.onSurface
                )
            } else {
                lightColorScheme(
                    primary = currentTheme.accent,
                    onPrimary = Color.Yellow,
                    background = currentTheme.background,
                    onBackground = currentTheme.onBackground,
                    surface = currentTheme.surface,
                    onSurface = currentTheme.onSurface
                )
            }

            MaterialTheme(
                colorScheme = customColorScheme,
                typography = com.example.ui.theme.Typography
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = currentTheme.background,
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Header Title Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SportsEsports,
                                        contentDescription = null,
                                        tint = currentTheme.accent,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Text(
                                        text = "Tic-Tac-Toe",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.SansSerif,
                                        color = currentTheme.onBackground,
                                        letterSpacing = (-0.5).sp
                                    )
                                }

                                // Interactive status bubble
                                Badge(
                                    containerColor = currentTheme.accent.copy(alpha = 0.15f),
                                    contentColor = currentTheme.accent
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(RoundedCornerShape(50))
                                                .background(if (isVsAi) currentTheme.colorX else currentTheme.accent)
                                        )
                                        Text(
                                            text = "Offline Mode",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            // Active Match State Announcement Card
                            StatusAnnouncement(
                                isGameOver = isGameOver,
                                winner = winner,
                                currentTurn = currentTurn,
                                isVsAi = isVsAi,
                                isAiThinking = isAiThinking,
                                currentTheme = currentTheme,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Quick Stats Tally Tracker
                            ScoreBoard(
                                records = historyRecords,
                                isVsAi = isVsAi,
                                currentTheme = currentTheme,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Unified Game Board Grid Display
                            GameBoard(
                                board = board,
                                gridSize = gridSize,
                                winningLine = winningLine,
                                currentTheme = currentTheme,
                                isThinking = isAiThinking,
                                onCellClick = { index -> viewModel.makeMove(index) },
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .widthIn(max = 450.dp)
                            )

                            // Post Game / Quick reset main triggers
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.resetGame() },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp)
                                        .testTag("reset_game_btn"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = currentTheme.accent,
                                        contentColor = Color.White
                                    ),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Restart match"
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isGameOver) "Play Again" else "Reset Board",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                            }

                            // Game Customizer Options Expandable Panel
                            CustomSettingsPanel(
                                gridSize = gridSize,
                                isVsAi = isVsAi,
                                aiDifficulty = aiDifficulty,
                                selectedTheme = currentTheme,
                                onGridSizeChange = { size -> viewModel.setGridSize(size) },
                                onVsAiChange = { vsAi -> viewModel.setVsAiMode(vsAi) },
                                onDifficultyChange = { d -> viewModel.setAiDifficulty(d) },
                                onThemeChange = { theme -> viewModel.setTheme(theme) },
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Historical match rows
                            MatchHistorySection(
                                records = historyRecords,
                                currentTheme = currentTheme,
                                onClearHistory = { viewModel.purgeScores() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StatusAnnouncement(
    isGameOver: Boolean,
    winner: String?,
    currentTurn: String,
    isVsAi: Boolean,
    isAiThinking: Boolean,
    currentTheme: com.example.ui.theme.TicTacToeTheme,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .testTag("status_announcement_card")
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = currentTheme.surface.copy(alpha = 0.9f),
            contentColor = currentTheme.onSurface
        ),
        border = borderStrokeForStatusAndTheme(isGameOver, winner, currentTheme)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = StatusState(isGameOver, winner, currentTurn, isAiThinking, isVsAi),
                transitionSpec = {
                    fadeIn(animationSpec = tween(180)) with fadeOut(animationSpec = tween(140))
                }
            ) { target ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (target.isGameOver) {
                        when (target.winner) {
                            "TIE" -> {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = currentTheme.onSurface.copy(alpha = 0.7f),
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "It's a Draw Game! 🤝",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = currentTheme.onSurface
                                )
                            }
                            "X" -> {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = currentTheme.colorX,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "X Wins! Spectacular! 🎉",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = currentTheme.colorX
                                )
                            }
                            "O" -> {
                                val winLabel = if (target.isVsAi) "AI Wins! 🤖 Try again!" else "O Wins Match! 🎉"
                                Icon(
                                    imageVector = if (target.isVsAi) Icons.Default.SmartToy else Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = currentTheme.colorO,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = winLabel,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = currentTheme.colorO
                                )
                            }
                        }
                    } else {
                        // Game in play
                        if (target.isAiThinking) {
                            CircularProgressIndicator(
                                color = currentTheme.colorO,
                                strokeWidth = 2.5.dp,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "AI is computing moves...",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = currentTheme.colorO
                            )
                        } else {
                            val activeColor = if (target.currentTurn == "X") currentTheme.colorX else currentTheme.colorO
                            val turnName = if (target.currentTurn == "X") {
                                "X's Turn"
                            } else {
                                if (target.isVsAi) "AI Turn (O)" else "O's Turn"
                            }

                            // Pulse animation for active turn bubble
                            val infiniteTransition = rememberInfiniteTransition()
                            val pulseAlpha by infiniteTransition.animateFloat(
                                initialValue = 0.4f,
                                targetValue = 1f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(800, easing = LinearEasing),
                                    repeatMode = RepeatMode.Reverse
                                )
                            )

                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(activeColor.copy(alpha = pulseAlpha))
                            )

                            Text(
                                text = "Active Turn: ",
                                fontSize = 14.sp,
                                color = currentTheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                text = turnName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = activeColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun borderStrokeForStatusAndTheme(
    isGameOver: Boolean,
    winner: String?,
    theme: com.example.ui.theme.TicTacToeTheme
): BorderStroke {
    return if (isGameOver) {
        when (winner) {
            "X" -> BorderStroke(2.dp, theme.colorX)
            "O" -> BorderStroke(2.dp, theme.colorO)
            else -> BorderStroke(1.dp, theme.onSurface.copy(alpha = 0.3f))
        }
    } else {
        BorderStroke(1.dp, theme.gridLine.copy(alpha = 0.3f))
    }
}

private data class StatusState(
    val isGameOver: Boolean,
    val winner: String?,
    val currentTurn: String,
    val isAiThinking: Boolean,
    val isVsAi: Boolean
)
