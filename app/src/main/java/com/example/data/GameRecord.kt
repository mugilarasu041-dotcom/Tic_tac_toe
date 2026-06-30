package com.example.data

import androidx.room.*

@Entity(tableName = "game_records")
data class GameRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "grid_size") val gridSize: Int,
    @ColumnInfo(name = "mode") val mode: String, // "VS_AI" or "VS_HUMAN"
    @ColumnInfo(name = "difficulty") val difficulty: String, // "EASY", "MEDIUM", "HARD", or "NONE"
    @ColumnInfo(name = "winner") val winner: String, // "PLAYER_X", "PLAYER_O", "TIE"
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
)
