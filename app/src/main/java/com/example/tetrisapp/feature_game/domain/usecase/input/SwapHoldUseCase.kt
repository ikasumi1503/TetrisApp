package com.example.tetrisapp.feature_game.domain.usecase.input

import com.example.tetrisapp.feature_game.domain.entity.TetriMino
import com.example.tetrisapp.feature_game.domain.entity.TetriMinoList


class SwapHoldUseCase {
    data class Result(
        val newMino: TetriMino,
        val newMinoList: TetriMinoList
    )

    operator fun invoke(
        mino: TetriMino,
        tetriMinoList: TetriMinoList,
        currentIsSwapped: Boolean,
    ): Result? {
        if (currentIsSwapped) return null
        val currentMino = requireNotNull(mino) { "Current tetri mino is null!" }
        val minoType = tetriMinoList.tetriMinoList.getOrNull(0)
            ?: throw IllegalStateException("Mino list is empty!")

        val newMinoList = tetriMinoList.swapHoldAndNext(mino = currentMino)
        val newMino = TetriMino(_type = minoType)
        return Result(newMino = newMino, newMinoList = newMinoList)
    }
}