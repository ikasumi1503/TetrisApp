package com.example.tetrisapp.feature_game.data

import android.content.Context

//GameRepositoryImplって何？
// GameRepositoryImplでは、GameRepositoryで使うと宣言したものを実際にどうやって実装していくかを書いた場所
class GameRepositoryImpl(
    private val context: Context
) : GameRepository {
    override fun saveHighScore(score: Int) {
        // ここで保持されたデータはアプリを終了しても残るみたい
        // また、再ビルドしてもデータが残る
        // ただし、アンインストールしたりキャッシュを削除すると消える
        val prefs = context.getSharedPreferences("tetris_prefs", Context.MODE_PRIVATE)
        prefs.edit().putInt("high_score", score).apply()
    }

    override fun loadHighScore(): Int {
        val prefs = context.getSharedPreferences("tetris_prefs", Context.MODE_PRIVATE)
        return prefs.getInt("high_score", 0)
    }
}