package com.example.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.GameRecord
import com.example.data.GameRepository
import com.example.ui.theme.NeonGalactic
import com.example.ui.theme.TicTacToeTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

class TicTacToeViewModel(private val repository: GameRepository) : ViewModel() {

    // Target Grid settings: 3, 4, 5
    var gridSize = mutableStateOf(3)
        private set

    // Game Mode: true for human vs AI, false for human vs human
    var isVsAi = mutableStateOf(true)
        private set

    // AI Challenge level: "EASY", "MEDIUM", "HARD"
    var aiDifficulty = mutableStateOf("HARD")
        private set

    // Active Theme
    var selectedTheme = mutableStateOf<TicTacToeTheme>(NeonGalactic)
        private set

    // Native board positions
    var board = mutableStateOf<List<String>>(List(9) { "" })
        private set

    // Current turn: "X" or "O"
    var currentTurn = mutableStateOf("X")
        private set

    // Game Ending Status
    var isGameOver = mutableStateOf(false)
        private set

    var winner = mutableStateOf<String?>(null) // "X", "O", "TIE", or null
        private set

    // Highlighted winning line indices
    var winningLineIndices = mutableStateOf<List<Int>>(emptyList())
        private set

    // Tracking if the AI is processing its turn
    var isAiThinking = mutableStateOf(false)
        private set

    // Read game stats from historical DAO flow
    val gameRecords: StateFlow<List<GameRecord>> = repository.allRecords
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        resetGame()
    }

    // Setters
    fun setGridSize(size: Int) {
        if (size in 3..5) {
            gridSize.value = size
            resetGame()
        }
    }

    fun setVsAiMode(vsAi: Boolean) {
        isVsAi.value = vsAi
        resetGame()
    }

    fun setAiDifficulty(diff: String) {
        aiDifficulty.value = diff
        resetGame()
    }

    fun setTheme(theme: TicTacToeTheme) {
        selectedTheme.value = theme
    }

    // Reset board and start fresh game
    fun resetGame() {
        val size = gridSize.value
        board.value = List(size * size) { "" }
        currentTurn.value = "X"
        isGameOver.value = false
        winner.value = null
        winningLineIndices.value = emptyList()
        isAiThinking.value = false
    }

    // Process a cellular selection
    fun makeMove(index: Int) {
        if (isGameOver.value || isAiThinking.value) return
        if (board.value[index] != "") return

        // Player X or O moves
        val updatedBoard = board.value.toMutableList()
        updatedBoard[index] = currentTurn.value
        board.value = updatedBoard

        // Check winner or tie
        val winningLine = checkWinningLine(board.value, gridSize.value)
        if (winningLine != null) {
            endGame(winner = currentTurn.value, lineIndices = winningLine)
            return
        } else if (!board.value.contains("")) {
            endGame(winner = "TIE", lineIndices = emptyList())
            return
        }

        // Switch Turn
        currentTurn.value = if (currentTurn.value == "X") "O" else "X"

        // If VS AI mode and O's turn, activate AI
        if (isVsAi.value && currentTurn.value == "O") {
            triggerAiMove()
        }
    }

    private fun triggerAiMove() {
        isAiThinking.value = true
        viewModelScope.launch {
            // Give a soft thinking latency for native visual feedback
            delay(500)
            if (isGameOver.value) {
                isAiThinking.value = false
                return@launch
            }

            val size = gridSize.value
            val aiIndex = calculateAiMove(board.value, size, aiDifficulty.value)

            if (aiIndex != -1) {
                val updatedBoard = board.value.toMutableList()
                updatedBoard[aiIndex] = "O"
                board.value = updatedBoard

                // Check winner or tie
                val winningLine = checkWinningLine(board.value, size)
                if (winningLine != null) {
                    endGame(winner = "O", lineIndices = winningLine)
                } else if (!board.value.contains("")) {
                    endGame(winner = "TIE", lineIndices = emptyList())
                } else {
                    // Turn back to human
                    currentTurn.value = "X"
                }
            }
            isAiThinking.value = false
        }
    }

    private fun endGame(winner: String, lineIndices: List<Int>) {
        this.winner.value = winner
        this.isGameOver.value = true
        this.winningLineIndices.value = lineIndices

        // Save progress to database
        viewModelScope.launch {
            repository.insertRecord(
                GameRecord(
                    gridSize = gridSize.value,
                    mode = if (isVsAi.value) "VS_AI" else "VS_HUMAN",
                    difficulty = if (isVsAi.value) aiDifficulty.value else "NONE",
                    winner = if (winner == "TIE") "TIE" else if (winner == "X") "PLAYER_X" else "PLAYER_O"
                )
            )
        }
    }

    fun purgeScores() {
        viewModelScope.launch {
            repository.clearHistory()
        }
        resetGame()
    }

    // AI Math Core
    private fun calculateAiMove(boardList: List<String>, size: Int, difficulty: String): Int {
        val emptySpots = boardList.indices.filter { boardList[it] == "" }
        if (emptySpots.isEmpty()) return -1

        return when (difficulty) {
            "EASY" -> {
                // Return random empty cell
                emptySpots[Random.nextInt(emptySpots.size)]
            }
            "MEDIUM" -> {
                // 1. Can AI Win right now?
                for (spot in emptySpots) {
                    val testBoard = boardList.toMutableList()
                    testBoard[spot] = "O"
                    if (checkWinningLine(testBoard, size) != null) {
                        return spot
                    }
                }
                // 2. Can Player block?
                for (spot in emptySpots) {
                    val testBoard = boardList.toMutableList()
                    testBoard[spot] = "X"
                    if (checkWinningLine(testBoard, size) != null) {
                        return spot
                    }
                }
                // 3. Fallback to random helper
                emptySpots[Random.nextInt(emptySpots.size)]
            }
            else -> { // HARD / PERFECT CHALENGE
                if (size == 3) {
                    // Perfect Minimax Algorithm for unbeatable 3x3 challenge
                    findBestMove3x3(boardList)
                } else {
                    // Optimized tactical heuristic scoring for 4x4 and 5x5 grids
                    findBestStrategicMove(boardList, size)
                }
            }
        }
    }

    // --- 3x3 Minimax Perfect Player ---
    private fun findBestMove3x3(boardList: List<String>): Int {
        var bestVal = -1000
        var bestMove = -1
        for (i in boardList.indices) {
            if (boardList[i] == "") {
                val nextBoard = boardList.toMutableList()
                nextBoard[i] = "O"
                val moveVal = minimax3x3(nextBoard, 0, false)
                if (moveVal > bestVal) {
                    bestMove = i
                    bestVal = moveVal
                }
            }
        }
        return if (bestMove != -1) bestMove else boardList.indexOfFirst { it == "" }
    }

    private fun minimax3x3(boardList: List<String>, depth: Int, isMax: Boolean): Int {
        val score = evaluateBoard3x3(boardList)
        if (score == 10) return score - depth
        if (score == -10) return score + depth
        if (!boardList.contains("")) return 0

        if (isMax) {
            var best = -1000
            for (i in boardList.indices) {
                if (boardList[i] == "") {
                    val nextBoard = boardList.toMutableList()
                    nextBoard[i] = "O"
                    best = maxOf(best, minimax3x3(nextBoard, depth + 1, false))
                }
            }
            return best
        } else {
            var best = 1000
            for (i in boardList.indices) {
                if (boardList[i] == "") {
                    val nextBoard = boardList.toMutableList()
                    nextBoard[i] = "X"
                    best = minOf(best, minimax3x3(nextBoard, depth + 1, true))
                }
            }
            return best
        }
    }

    private fun evaluateBoard3x3(b: List<String>): Int {
        // Horizontal lines
        for (i in 0..2) {
            if (b[i * 3] != "" && b[i * 3] == b[i * 3 + 1] && b[i * 3 + 1] == b[i * 3 + 2]) {
                return if (b[i * 3] == "O") 10 else -10
            }
        }
        // Vertical lines
        for (i in 0..2) {
            if (b[i] != "" && b[i] == b[i + 3] && b[i + 3] == b[i + 6]) {
                return if (b[i] == "O") 10 else -10
            }
        }
        // Diagonals
        if (b[0] != "" && b[0] == b[4] && b[4] == b[8]) {
            return if (b[0] == "O") 10 else -10
        }
        if (b[2] != "" && b[2] == b[4] && b[4] == b[6]) {
            return if (b[2] == "O") 10 else -10
        }
        return 0
    }

    // --- 4x4 and 5x5 General Heuristic Solver ---
    private fun findBestStrategicMove(boardList: List<String>, size: Int): Int {
        val emptySpots = boardList.indices.filter { boardList[it] == "" }
        if (emptySpots.isEmpty()) return -1

        // 1. Quick wins check
        for (spot in emptySpots) {
            val testBoard = boardList.toMutableList()
            testBoard[spot] = "O"
            if (checkWinningLine(testBoard, size) != null) {
                return spot
            }
        }

        // 2. Quick blocks check
        for (spot in emptySpots) {
            val testBoard = boardList.toMutableList()
            testBoard[spot] = "X"
            if (checkWinningLine(testBoard, size) != null) {
                return spot
            }
        }

        // 3. Score other spaces strategically
        val winningLines = getWinningLinesList(size)
        val spotScores = mutableMapOf<Int, Int>()

        for (spot in emptySpots) {
            var score = 0
            val row = spot / size
            val col = spot % size

            // Central positional bias
            val middle = (size - 1) / 2.0
            val distToCenter = Math.abs(row - middle) + Math.abs(col - middle)
            val centerValue = (size * 2 - distToCenter * 2).toInt() // closer to center is better
            score += centerValue

            // Evaluate all potential winning lines crossing this cell
            for (line in winningLines) {
                if (spot in line) {
                    val lineMarks = line.map { boardList[it] }
                    val oCount = lineMarks.count { it == "O" }
                    val xCount = lineMarks.count { it == "X" }

                    if (oCount > 0 && xCount > 0) {
                        // Dead line, blocked for both
                        continue
                    } else if (oCount > 0) {
                        // AI-friendly line, prioritize getting closer to win
                        score += when (oCount) {
                            size - 2 -> 1500  // Create a massive dual threat
                            size - 3 -> 120
                            else -> 10
                        }
                    } else if (xCount > 0) {
                        // Opponent line, block early
                        score += when (xCount) {
                            size - 2 -> 1000  // Heavily block a dual alignment
                            size - 3 -> 80
                            else -> 8
                        }
                    } else {
                        // Fresh empty line
                        score += 3
                    }
                }
            }
            spotScores[spot] = score
        }

        val highestScore = spotScores.values.maxOrNull() ?: 0
        val bestSpotsRange = spotScores.filter { it.value == highestScore }.keys.toList()
        return bestSpotsRange[Random.nextInt(bestSpotsRange.size)]
    }

    // Dynamic Winning Line Detector for ANY grid sizes (3, 4, 5)
    private fun checkWinningLine(b: List<String>, size: Int): List<Int>? {
        // Rows
        for (r in 0 until size) {
            val startIdx = r * size
            val cellVal = b[startIdx]
            if (cellVal != "") {
                var isWin = true
                val indices = mutableListOf<Int>()
                for (c in 0 until size) {
                    val currentIdx = startIdx + c
                    indices.add(currentIdx)
                    if (b[currentIdx] != cellVal) {
                        isWin = false
                        break
                    }
                }
                if (isWin) return indices
            }
        }

        // Columns
        for (c in 0 until size) {
            val cellVal = b[c]
            if (cellVal != "") {
                var isWin = true
                val indices = mutableListOf<Int>()
                for (r in 0 until size) {
                    val currentIdx = r * size + c
                    indices.add(currentIdx)
                    if (b[currentIdx] != cellVal) {
                        isWin = false
                        break
                    }
                }
                if (isWin) return indices
            }
        }

        // Main Diagonal
        val mainStartIdx = 0
        val mainVal = b[mainStartIdx]
        if (mainVal != "") {
            var isWin = true
            val indices = mutableListOf<Int>()
            for (i in 0 until size) {
                val currentIdx = i * size + i
                indices.add(currentIdx)
                if (b[currentIdx] != mainVal) {
                    isWin = false
                    break
                }
            }
            if (isWin) return indices
        }

        // Off Diagonal
        val offStartIdx = size - 1
        val offVal = b[offStartIdx]
        if (offVal != "") {
            var isWin = true
            val indices = mutableListOf<Int>()
            for (i in 0 until size) {
                val currentIdx = i * size + (size - 1 - i)
                indices.add(currentIdx)
                if (b[currentIdx] != offVal) {
                    isWin = false
                    break
                }
            }
            if (isWin) return indices
        }

        return null
    }

    // Build lists of index-vectors corresponding to lines
    private fun getWinningLinesList(size: Int): List<List<Int>> {
        val lists = mutableListOf<List<Int>>()
        // Rows
        for (r in 0 until size) {
            lists.add((r * size until (r + 1) * size).toList())
        }
        // Cols
        for (c in 0 until size) {
            lists.add(List(size) { i -> i * size + c })
        }
        // Diags
        lists.add(List(size) { i -> i * size + i })
        lists.add(List(size) { i -> i * size + (size - 1 - i) })
        return lists
    }
}

class TicTacToeViewModelFactory(private val repository: GameRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TicTacToeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TicTacToeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
