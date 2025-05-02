package com.example.tetrisapp.feature_game.domain.usecase

import androidx.compose.runtime.MutableState
import com.example.tetrisapp.feature_game.domain.entity.Cell
import com.example.tetrisapp.feature_game.domain.entity.TetriMino
import com.example.tetrisapp.feature_game.ui.GameViewModel
import com.example.tetrisapp.feature_game.util.GameConstants.TO_CENTER

class OnCollisionYUseCase( // TODO: ぶつかった時に一マス移動させる処理とかあるのでこれ名前変更したい
    private val gameViewModel: GameViewModel
) {
    operator fun invoke(
        mino: TetriMino,
        ) {
        for (relativePosition in mino.type.shapes[mino.rotation]) {
            // ミノの配置
            val newCell = Cell(
                color = mino.type.color,
                position = Pair(
                    mino.position.first + relativePosition.first,
                    mino.position.second + relativePosition.second
                ),
                isFilled = true
            )
            gameViewModel.createBoardWithUpdateCells(newCell)
        }
        // 新しいミノの生成
        val newMino = mino.copy(_position = Pair(TO_CENTER, 0))
        gameViewModel.updateTetriMino(newMino)
    }
}