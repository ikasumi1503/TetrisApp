package com.example.tetrisapp.feature_game.domain.usecase

import com.example.tetrisapp.feature_game.domain.entity.Board
import com.example.tetrisapp.feature_game.domain.entity.TetriMino

class CheckCollisionYUseCase {
    // > operator fun invokeにすると、クラスを呼ぶと返り値を関数として使える https://star-zero.medium.com/kotlin%E3%81%AEinvoke-operator-5fe733e75738
    operator fun invoke(
        board: Board,
        mino: TetriMino,
        position: Pair<Int, Int> // TODO: これをviewModelのmino.positionにしたい
    ): Boolean {
        val shape = mino.type.shapes[mino.rotation]
        val isCollideY = shape.any { relativePosition ->
            val nextX = mino.position.first + relativePosition.first
            val nextY = mino.position.second + relativePosition.second + 1

            // ミノの次の位置が壁があるまたはミノがあるならwillCollide=trueを返す
            // getOrNullにすることで範囲外の時例外が出るのを防ぐ。例えばnextY=21になると範囲外でクラッシュするのを防ぐ

            nextY >= board.cells.size || board.cells.getOrNull(nextY)
                ?.getOrNull(nextX)?.isFilled == true
        }
        println(isCollideY)
        return isCollideY
    }
}