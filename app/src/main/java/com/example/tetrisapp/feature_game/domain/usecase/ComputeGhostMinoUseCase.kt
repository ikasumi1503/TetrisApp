package com.example.tetrisapp.feature_game.domain.usecase

import com.example.tetrisapp.feature_game.domain.entity.Board
import com.example.tetrisapp.feature_game.domain.entity.TetriMino

class ComputeGhostMinoUseCase {
    operator fun invoke(mino: TetriMino,board: Board): TetriMino{
        val checkCollisionYUseCase = CheckCollisionYUseCase()

        // generateSequence(初期値){}...初期値から始めて、次の値である返り値(next)をSequenceオブジェクトに入れていく処理
        // JSでいうと、for文内で配列にpushしていくような処理
        return generateSequence(mino) { current ->
            val next = current.copy(
                _position = Pair(current.position.first, current.position.second + 1)
            )
            // nullを返すと終了する
            if (checkCollisionYUseCase(board, current)) null else next
            // ゴーストが一番下に来る最後の要素を取得する
        }.last()
    }
}