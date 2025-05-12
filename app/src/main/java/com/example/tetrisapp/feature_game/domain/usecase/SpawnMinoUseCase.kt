package com.example.tetrisapp.feature_game.domain.usecase

import com.example.tetrisapp.feature_game.domain.entity.TetriMinoList
import com.example.tetrisapp.feature_game.domain.model.MinoType

class SpawnMinoUseCase {
    data class Result(
        val nextMinoType: MinoType,
        val updatedList: TetriMinoList
    )

    operator fun invoke(list: TetriMinoList): Result {
        val (nextMinoType, updatedList) = list.spawnTetriMino()
        return Result(
            nextMinoType = nextMinoType,
            updatedList = updatedList
        )
    }
}