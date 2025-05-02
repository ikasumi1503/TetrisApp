package com.example.tetrisapp.feature_game.data

import com.example.tetrisapp.feature_game.domain.entity.TetriMino

// Implとの関係性は？
// Implで使うメソッドなどの型を決めておく「抽象」的な部分を宣言する役割

// GameRepositoryの役割は？
// ゲームのドメインロジックやデータソースをimportしてまとめてViewModelで使う窓口

//
interface GameRepository {
    fun moveDown(): TetriMino
}

