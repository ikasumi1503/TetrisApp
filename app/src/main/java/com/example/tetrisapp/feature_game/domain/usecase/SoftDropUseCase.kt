// domain/usecase/SoftDropUseCase.kt
package com.example.tetrisapp.feature_game.domain.usecase

import com.example.tetrisapp.feature_game.domain.entity.Board
import com.example.tetrisapp.feature_game.domain.entity.TetriMino


data class SoftDropResult(
    val newMino: TetriMino?,
    val didLock: Boolean
)

class SoftDropUseCase(
    private val checkCollisionY: CheckCollisionYUseCase
) {

    operator fun invoke(mino: TetriMino, board: Board): SoftDropResult {
        val willCollideY = checkCollisionY(board, mino)
        return if (willCollideY) {
            // ロックダウン
            SoftDropResult(newMino = null, didLock = true)
        } else {
            // 一段下げ
            val dropped = mino.copy(
                _position = mino.position.let { (x, y) -> x to (y + 1) }
            )
            SoftDropResult(newMino = dropped, didLock = false)
        }
    }
}
