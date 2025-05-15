package com.example.tetrisapp.feature_game.domain.usecase

import com.example.tetrisapp.feature_game.domain.entity.Cell
import com.example.tetrisapp.feature_game.domain.entity.TetriMino

class GenerateLockCellsUseCase {
    operator fun invoke(mino: TetriMino): List<Cell> {
        return mino.type.shapes[mino.rotation].map { rel ->
            Cell(
                color = mino.type.color,
                position = mino.position.first + rel.first to mino.position.second + rel.second,
                isFilled = true
            )
        }
    }
}
