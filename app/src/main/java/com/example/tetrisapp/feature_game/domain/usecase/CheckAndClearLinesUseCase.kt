package com.example.tetrisapp.feature_game.domain.usecase

import com.example.tetrisapp.feature_game.domain.entity.Board

class CheckAndClearLinesUseCase {
    // invokeは習慣的な名前らしい。executeとかでもいいらしい
    operator fun invoke(board: Board): Board{
        val newCells = board.checkAndClearLines()
        return newCells
    }
}