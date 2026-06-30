package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ripple
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.TicTacToeTheme

@Composable
fun GameBoard(
    board: List<String>,
    gridSize: Int,
    winningLine: List<Int>,
    currentTheme: TicTacToeTheme,
    isThinking: Boolean,
    onCellClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Render the grid on a square layout using BoxWithConstraints
    BoxWithConstraints(
        modifier = modifier
            .testTag("game_board_container")
            .fillMaxWidth()
            .aspectRatio(1f) // Keep it perfect square
            .background(currentTheme.surface, shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        val totalWidth = constraints.maxWidth.toFloat()
        val cellSize = totalWidth / gridSize

        // Main Board Canvas drawing background grid wires
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    val strokeWidth = 5.dp.toPx()

                    // Draw inner vertical dividers
                    for (i in 1 until gridSize) {
                        val x = i * cellSize
                        drawLine(
                            color = currentTheme.gridLine,
                            start = Offset(x, 0f),
                            end = Offset(x, totalWidth),
                            strokeWidth = strokeWidth,
                            cap = StrokeCap.Round
                        )
                    }

                    // Draw inner horizontal dividers
                    for (i in 1 until gridSize) {
                        val y = i * cellSize
                        drawLine(
                            color = currentTheme.gridLine,
                            start = Offset(0f, y),
                            end = Offset(totalWidth, y),
                            strokeWidth = strokeWidth,
                            cap = StrokeCap.Round
                        )
                    }
                }
        ) {
            // Interactive Overlaid cells
            for (index in board.indices) {
                val row = index / gridSize
                val col = index % gridSize

                // Convert pixel positions to DP equivalents for compose positioning
                val density = androidx.compose.ui.platform.LocalDensity.current
                val leftDp = with(density) { (col * cellSize).toDp() }
                val topDp = with(density) { (row * cellSize).toDp() }
                val sizeDp = with(density) { cellSize.toDp() }

                val isCellInWinningLine = winningLine.contains(index)

                // Interactive touch boundaries
                Box(
                    modifier = Modifier
                        .offset(x = leftDp, y = topDp)
                        .size(sizeDp)
                        .testTag("cell_${index}")
                        .clickable(
                            enabled = board[index] == "" && !isThinking,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(
                                bounded = true,
                                color = currentTheme.accent
                            ),
                            onClick = { onCellClick(index) }
                        )
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val mark = board[index]
                    if (mark != "") {
                        CellSymbol(
                            symbol = mark,
                            theme = currentTheme,
                            isWinning = isCellInWinningLine,
                            gridSize = gridSize
                        )
                    }
                }
            }

            // Draw glowing winning line highlights if there's a winner
            if (winningLine.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            try {
                                val firstIdx = winningLine.first()
                                val lastIdx = winningLine.last()

                                val r1 = firstIdx / gridSize
                                val c1 = firstIdx % gridSize
                                val r2 = lastIdx / gridSize
                                val c2 = lastIdx % gridSize

                                val p1 = Offset(
                                    x = c1 * cellSize + cellSize / 2f,
                                    y = r1 * cellSize + cellSize / 2f
                                )
                                val p2 = Offset(
                                    x = c2 * cellSize + cellSize / 2f,
                                    y = r2 * cellSize + cellSize / 2f
                                )

                                val brushStroke = 12.dp.toPx()

                                // If glowing enabled, draw soft background glow first
                                if (currentTheme.useGlow) {
                                    drawLine(
                                        color = currentTheme.accent.copy(alpha = 0.35f),
                                        start = p1,
                                        end = p2,
                                        strokeWidth = brushStroke * 2.2f,
                                        cap = StrokeCap.Round
                                    )
                                }

                                // Main high-contrast core line
                                drawLine(
                                    color = currentTheme.accent,
                                    start = p1,
                                    end = p2,
                                    strokeWidth = brushStroke,
                                    cap = StrokeCap.Round
                                )
                            } catch (e: Exception) {
                                // Fallback safe skip
                            }
                        }
                )
            }
        }
    }
}

@Composable
fun CellSymbol(
    symbol: String,
    theme: TicTacToeTheme,
    isWinning: Boolean,
    gridSize: Int
) {
    // Animatable stroke reveal factor
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(symbol) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 250)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                val padFraction = when (gridSize) {
                    4 -> 0.22f
                    5 -> 0.26f
                    else -> 0.18f
                }
                val padding = size.width * padFraction
                val w = size.width - padding * 2
                val h = size.height - padding * 2

                val strokeWidth = when (gridSize) {
                    4 -> 5.dp.toPx()
                    5 -> 4.dp.toPx()
                    else -> 6.5.dp.toPx()
                }

                if (symbol == "X") {
                    val color = if (isWinning) theme.accent else theme.colorX

                    if (theme.useGlow) {
                        // Draw glow background blur
                        val glowColor = if (isWinning) theme.accent.copy(alpha = 0.45f) else theme.glowColorX
                        drawLine(
                            color = glowColor,
                            start = Offset(padding, padding),
                            end = Offset(padding + w, padding + h),
                            strokeWidth = strokeWidth * 2.5f,
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = glowColor,
                            start = Offset(padding + w, padding),
                            end = Offset(padding, padding + h),
                            strokeWidth = strokeWidth * 2.5f,
                            cap = StrokeCap.Round
                        )
                    }

                    // Animated Stroke line 1
                    val line1Progress = if (animationProgress.value < 0.5f) {
                        animationProgress.value * 2f
                    } else {
                        1f
                    }
                    drawLine(
                        color = color,
                        start = Offset(padding, padding),
                        end = Offset(padding + w * line1Progress, padding + h * line1Progress),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )

                    // Animated Stroke line 2
                    if (animationProgress.value > 0.5f) {
                        val line2Progress = (animationProgress.value - 0.5f) * 2f
                        drawLine(
                            color = color,
                            start = Offset(padding + w, padding),
                            end = Offset(padding + w - w * line2Progress, padding + h * line2Progress),
                            strokeWidth = strokeWidth,
                            cap = StrokeCap.Round
                        )
                    }
                } else if (symbol == "O") {
                    val color = if (isWinning) theme.accent else theme.colorO

                    if (theme.useGlow) {
                        val glowColor = if (isWinning) theme.accent.copy(alpha = 0.45f) else theme.glowColorO
                        drawArc(
                            color = glowColor,
                            startAngle = 0f,
                            sweepAngle = 360f * animationProgress.value,
                            useCenter = false,
                            topLeft = Offset(padding, padding),
                            size = Size(w, h),
                            style = Stroke(width = strokeWidth * 2.5f)
                        )
                    }

                    drawArc(
                        color = color,
                        startAngle = 0f,
                        sweepAngle = 360f * animationProgress.value,
                        useCenter = false,
                        topLeft = Offset(padding, padding),
                        size = Size(w, h),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
            }
    )
}
