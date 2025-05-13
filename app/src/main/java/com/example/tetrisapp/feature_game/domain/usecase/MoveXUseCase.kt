package com.example.tetrisapp.feature_game.domain.usecase

import com.example.tetrisapp.feature_game.domain.entity.Board
import com.example.tetrisapp.feature_game.domain.entity.TetriMino

data class MoveXResult(
    val movedMino: TetriMino?,      // 当たってなければ新ポジション、当たってたら null
    val didMove: Boolean,           // 移動したかどうか
    val didResetDelay: Boolean,     // 地面で操作したとき delay をリセットすべきか
    val incrementProlongCount: Boolean  // プロロングカウントを増やすべきか
)

class MoveXUseCase(
    private val sideXToNum: SideXToNumUseCase,
    private val checkCollisionX: CheckCollisionXUseCase,
    private val checkCollisionY: CheckCollisionYUseCase
) {
    operator fun invoke(
        sideX: SideX,
        board: Board,
        mino: TetriMino,
        currentProlongCount: Int,
        maxProlongCount: Int = 10
    ): MoveXResult {
        // 1. 横当たり判定
        if (checkCollisionX(board, mino, sideX)) {
            return MoveXResult(
                movedMino = null,
                didMove = false,
                didResetDelay = false,
                incrementProlongCount = false
            )
        }

        // 2. 実際に移動したミノを計算
        val dx = sideXToNum(sideX)
        val newMino = mino.copy(
            _position = mino.position.let { (x, y) -> x + dx to y }
        )

        // 3. 地面（Y方向）でもう一度当たり判定して時間延長するか判定
        val willCollideY = checkCollisionY(board, newMino)
        val shouldResetDelay = willCollideY && currentProlongCount < maxProlongCount
        val shouldIncrement = willCollideY && currentProlongCount < maxProlongCount

        return MoveXResult(
            movedMino = newMino,
            didMove = true,
            didResetDelay = shouldResetDelay,
            incrementProlongCount = shouldIncrement
        )
    }
}
