package com.example.tetrisapp.feature_game.domain.usecase

import com.example.tetrisapp.feature_game.domain.entity.Board
import com.example.tetrisapp.feature_game.domain.entity.TetriMino

// ドメイン層：生成直後の配置で衝突してないかを判定する
class CheckGameOverUseCase(
    private val checkCollisionY: CheckCollisionYUseCase
) {
    operator fun invoke(
        board: Board,
        newMino: TetriMino
    ): Boolean {
        // 生成位置は mino.position のすぐ上：Y-1
        val testMino = newMino.copy(
            _position = newMino.position.copy(second = newMino.position.second - 1)
        )
        return checkCollisionY(board, testMino)
    }
}
