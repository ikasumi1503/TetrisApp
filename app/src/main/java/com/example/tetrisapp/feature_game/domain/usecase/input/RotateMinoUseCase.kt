package com.example.tetrisapp.feature_game.domain.usecase.input

import com.example.tetrisapp.feature_game.domain.entity.Board
import com.example.tetrisapp.feature_game.domain.entity.TetriMino
import com.example.tetrisapp.feature_game.domain.model.MinoType
import com.example.tetrisapp.feature_game.domain.usecase.tetrimino.CheckCollisionYUseCase
import com.example.tetrisapp.feature_game.util.GameConstants

data class RotateMinoResult(
    val rotatedMino: TetriMino?,   // 衝突なしで回転できたら新しいミノ、できなければ null
    val didRotate: Boolean,         // 回転に成功したか
    val didResetDelay: Boolean,     // 接地時の操作で delay をリセットすべきか
    val incrementProlongCount: Boolean // prolongTimeDelayCountLimit を増やすべきか
)

class RotateMinoUseCase(
    private val checkCollisionY: CheckCollisionYUseCase
) {
    operator fun invoke(
        rotateDir: Int,
        mino: TetriMino,
        board: Board,
        currentProlongCount: Int,
        maxProlongCount: Int = 10
    ): RotateMinoResult {
        // 1. 新しい回転インデックスを計算
        val newRotation =
            (mino.type.shapes.size + mino.rotation + rotateDir) % mino.type.shapes.size
        val candidate = mino.copy(_rotation = newRotation)

        // 2. SRS キックテーブルの取得
        val key = Pair(mino.rotation, newRotation)
        val kickOffsets = if (mino.type == MinoType.I)
            GameConstants.I_KickTable[key] ?: emptyList()
        else
            GameConstants.JLSTZ_KickTable[key] ?: emptyList()

        // 3. 各オフセットを試し、最初に衝突しないものを採用
        for ((dx, dy) in kickOffsets) {
            val moved = candidate.copy(
                _position = Pair(mino.position.first + dx, mino.position.second + dy)
            )

            // 壁／ミノ重なり判定
            val isCollided =
                moved.type.shapes[moved.rotation].any { (rx, ry) ->
                    val x = moved.position.first + rx
                    val y = moved.position.second + ry
                    x < 0 || x >= board.cells[0].size ||
                            y < 0 || y >= board.cells.size ||
                            board.cells[y][x].isFilled
                }
            if (!isCollided) {
                // 4. 接地時の delay リセット判定
                val onGround = checkCollisionY(board, moved)
                val shouldReset = onGround && currentProlongCount < maxProlongCount
                return RotateMinoResult(
                    rotatedMino = moved,
                    didRotate = true,
                    didResetDelay = shouldReset,
                    incrementProlongCount = shouldReset
                )
            }
        }

        // 5. どれも無理なら回転失敗
        return RotateMinoResult(
            rotatedMino = null,
            didRotate = false,
            didResetDelay = false,
            incrementProlongCount = false
        )
    }
}
