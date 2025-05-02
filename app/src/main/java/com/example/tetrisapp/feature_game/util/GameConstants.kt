package com.example.tetrisapp.feature_game.util

// object classはインスタンスを一つだけ作る
// クラスに属さない共通で使うものをまとめる時に使ったりするみたい
object GameConstants {
    private const val MINO_WIDTH = 4
    private const val BOARD_WIDTH = 10
    const val TO_CENTER = (BOARD_WIDTH / 2) - (MINO_WIDTH / 2)
}