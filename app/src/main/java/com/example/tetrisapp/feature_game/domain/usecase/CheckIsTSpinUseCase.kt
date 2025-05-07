package com.example.tetrisapp.feature_game.domain.usecase

import com.example.tetrisapp.feature_game.domain.entity.Board
import com.example.tetrisapp.feature_game.domain.entity.TetriMino
import com.example.tetrisapp.feature_game.domain.model.MinoType

class CheckIsTSpinUseCase {
    operator fun invoke(mino: TetriMino, board: Board): Boolean {
        if (mino.type != MinoType.T) return false

        val x = mino.position.first + 1
        val y = mino.position.second + 1
        val corners = listOf(
            Pair(x - 1, y - 1),
            Pair(x + 1, y - 1),
            Pair(x - 1, y + 1),
            Pair(x + 1, y + 1)
        )

        val blockedCount = corners.count { (cx, cy) ->
            cx < 0 || cy < 0 || cx >= board.cells[0].size || cy >= board.cells.size || board.cells[cy][cx].isFilled
        }

        return blockedCount >= 3
    }
}