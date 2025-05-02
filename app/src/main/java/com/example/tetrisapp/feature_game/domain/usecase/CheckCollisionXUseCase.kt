package com.example.tetrisapp.feature_game.domain.usecase

import com.example.tetrisapp.feature_game.domain.entity.Board
import com.example.tetrisapp.feature_game.domain.entity.TetriMino



class CheckCollisionXUseCase {
    // > operator fun invokeにすると、クラスを呼ぶと返り値を関数として使える https://star-zero.medium.com/kotlin%E3%81%AEinvoke-operator-5fe733e75738
    operator fun invoke(
        board: Board,
        mino: TetriMino,
        sideX: SideX
    ): Boolean {
        val shape = mino.type.shapes[mino.rotation]
        val sideXToNum = when(sideX){
            SideX.LEFT -> -1
            SideX.RIGHT -> 1
        }

        val isCollideX = shape.any { relativePosition ->
            val nextX = mino.position.first + relativePosition.first + sideXToNum
            val nextY = mino.position.second + relativePosition.second

            // ミノの次の位置が壁があるまたはミノがあるならwillCollide=trueを返す
            // getOrNullにすることで範囲外の時例外が出るのを防ぐ。例えばnextY=21になると範囲外でクラッシュするのを防ぐ
            // sideToNumのwhenと合体させる方法も考えたが、こっちのほうがきれいに書けそうなのでこう書いた
            when(sideX){
                SideX.RIGHT -> {
                    nextX >= board.cells[0].size || board.cells.getOrNull(nextY)
                        ?.getOrNull(nextX)?.isFilled == true
                }
                SideX.LEFT -> {
                    nextX < 0 || board.cells.getOrNull(nextY)
                        ?.getOrNull(nextX)?.isFilled == true
                }
            }
        }
        return isCollideX
    }
}