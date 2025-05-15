package com.example.tetrisapp.feature_game.domain.usecase.tetrimino

import com.example.tetrisapp.feature_game.domain.entity.Board
import com.example.tetrisapp.feature_game.domain.entity.TetriMino
import com.example.tetrisapp.feature_game.domain.usecase.game_manager.CalculateScoreUseCase
import com.example.tetrisapp.feature_game.domain.usecase.game_manager.CheckAndClearLinesUseCase

data class PlacementResult(
    val newBoard: Board,
    val newScore: Int,
    val newCombo: Int
)

class ProcessPlacementUseCase(
    private val clearLines: CheckAndClearLinesUseCase,
    private val checkTSpin: CheckIsTSpinUseCase,
    private val calcScore: CalculateScoreUseCase
) {
    operator fun invoke(
        board: Board,
        mino: TetriMino,
        currentScore: Int,
        currentCombo: Int
    ): PlacementResult {
        val isTSpin = checkTSpin(mino, board)
        val (boardAfterClear, lines) = clearLines(board)
        val (calculated, updatedCombo) = calcScore(
            lines,
            isTSpin,
            currentCombo = currentCombo,
            currentScore = currentScore
        )

        return PlacementResult(
            newBoard = boardAfterClear,
            newScore = calculated,
            newCombo = updatedCombo
        )
    }
}