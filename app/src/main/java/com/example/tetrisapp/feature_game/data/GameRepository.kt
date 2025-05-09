package com.example.tetrisapp.feature_game.data

import com.example.tetrisapp.feature_game.domain.entity.TetriMino

// Implとの関係性は？
// Implで使うメソッドなどの型を決めておく「抽象」的な部分を宣言する役割

// GameRepositoryの役割は？
// 外部とのやり取りをする窓口

// やることの例
// APIで受け取った物をuseCaseに渡す
// ローカルにデータを保存したり、読み込んだりする処理
// データ型の変換(ドメインロジックで頻繁にデータの変換を行う場合はUseCase内で、API通信などのためにデータの型を変換する場合にはレポジトリ内で変換を行う)
interface GameRepository {
    fun moveDown(): TetriMino
}

