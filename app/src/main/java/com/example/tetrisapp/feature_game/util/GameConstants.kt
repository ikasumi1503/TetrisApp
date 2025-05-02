package com.example.tetrisapp.feature_game.util

// object classはインスタンスを一つだけ作る
// クラスに属さない共通で使うものをまとめる時に使ったりするみたい
object GameConstants {
    private const val MINO_WIDTH = 4
    private const val BOARD_WIDTH = 10
    const val TO_CENTER = (BOARD_WIDTH / 2) - (MINO_WIDTH / 2)
    // constはString型とかシンプルな型にしか適用できない
    val JLSTZ_KickTable = mapOf(
        // rotationが 0 → 1 のとき
        Pair(0, 1) to listOf(
            Pair(0, 0), Pair(-1, 0), Pair(-1, +1), Pair(0, -2), Pair(-1, -2)
        ), // TODO: 説明を追加しておく
        // rotationが 1 → 0 のとき
        Pair(1, 0) to listOf(
            Pair(0, 0), Pair(+1, 0), Pair(+1, -1), Pair(0, +2), Pair(+1, +2)
        ),
        // rotationが 1 → 2 のとき
        Pair(1, 2) to listOf(
            Pair(0, 0), Pair(+1, 0), Pair(+1, -1), Pair(0, +2), Pair(+1, +2)
        ),
        // rotationが 2 → 1 のとき
        Pair(2, 1) to listOf(
            Pair(0, 0), Pair(-1, 0), Pair(-1, +1), Pair(0, -2), Pair(-1, -2)
        ),
        Pair(2, 3) to listOf(
            Pair(0, 0), Pair(+1, 0), Pair(+1, +1), Pair(0, -2), Pair(+1, -2)
        ),
        Pair(3, 2) to listOf(
            Pair(0, 0), Pair(-1, 0), Pair(-1, -1), Pair(0, +2), Pair(-1, +2)
        ),
        Pair(3, 0) to listOf(
            Pair(0, 0), Pair(-1, 0), Pair(-1, -1), Pair(0, +2), Pair(-1, +2)
        ),
        Pair(0, 3) to listOf(
            Pair(0, 0), Pair(+1, 0), Pair(+1, +1), Pair(0, -2), Pair(+1, -2)
        ),
    )

    val I_KickTable = mapOf(
        Pair(0, 1) to listOf(
            Pair(0, 0), Pair(-2, 0), Pair(+1, 0), Pair(-2, -1), Pair(+1, +2)
        ), Pair(1, 0) to listOf(
            Pair(0, 0), Pair(+2, 0), Pair(-1, 0), Pair(+2, +1), Pair(-1, -2)
        ), Pair(1, 2) to listOf(
            Pair(0, 0), Pair(-1, 0), Pair(+2, 0), Pair(-1, +2), Pair(+2, -1)
        ), Pair(2, 1) to listOf(
            Pair(0, 0), Pair(+1, 0), Pair(-2, 0), Pair(+1, -2), Pair(-2, +1)
        ), Pair(2, 3) to listOf(
            Pair(0, 0), Pair(+2, 0), Pair(-1, 0), Pair(+2, +1), Pair(-1, -2)
        ), Pair(3, 2) to listOf(
            Pair(0, 0), Pair(-2, 0), Pair(+1, 0), Pair(-2, -1), Pair(+1, +2)
        ), Pair(3, 0) to listOf(
            Pair(0, 0), Pair(+1, 0), Pair(-2, 0), Pair(+1, -2), Pair(-2, +1)
        ), Pair(0, 3) to listOf(
            Pair(0, 0), Pair(-1, 0), Pair(+2, 0), Pair(-1, +2), Pair(+2, -1)
        )
    )
}

