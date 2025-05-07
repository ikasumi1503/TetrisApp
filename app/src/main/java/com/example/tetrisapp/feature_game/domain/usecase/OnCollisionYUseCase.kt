package com.example.tetrisapp.feature_game.domain.usecase

import com.example.tetrisapp.feature_game.domain.entity.Cell
import com.example.tetrisapp.feature_game.domain.entity.TetriMino
import com.example.tetrisapp.feature_game.ui.GameViewModel

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

        // ライン削除
        gameViewModel.checkAndClearLines()

        // 新しいミノの生成
        gameViewModel.spawnTetriMino()
        val newMino = gameViewModel.tetriMino.value
        if (newMino != null) {
            gameViewModel.updateTetriMino(newMino)
        }

        // ミノについての初期化
        gameViewModel.markRotation(false)
        gameViewModel.updateIsSwapped(false)
    }
}