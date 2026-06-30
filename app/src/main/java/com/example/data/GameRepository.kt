package com.example.data

import kotlinx.coroutines.flow.Flow

class GameRepository(private val gameDao: GameDao) {
    val allRecords: Flow<List<GameRecord>> = gameDao.getAllRecordsFlow()

    suspend fun insertRecord(record: GameRecord) {
        gameDao.insertRecord(record)
    }

    suspend fun clearHistory() {
        gameDao.deleteAllRecords()
    }
}
