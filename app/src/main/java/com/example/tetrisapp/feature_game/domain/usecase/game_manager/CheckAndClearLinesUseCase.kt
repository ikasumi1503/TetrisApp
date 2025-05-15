package com.example.tetrisapp.feature_game.domain.usecase.game_manager

import com.example.tetrisapp.feature_game.domain.entity.Board

class CheckAndClearLinesUseCase {
    // invokeは習慣的な名前らしい。executeとかでもいいらしい
    operator fun invoke(board: Board): Pair<Board, Int> {
        val (newCells, linesCount) = board.checkAndClearLines()
        return Pair(newCells, linesCount)
    }
}