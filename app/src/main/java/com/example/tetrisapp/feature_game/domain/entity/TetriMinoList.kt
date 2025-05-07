package com.example.tetrisapp.feature_game.domain.entity

import com.example.tetrisapp.feature_game.domain.model.MinoType
import com.example.tetrisapp.feature_game.domain.model.TetriMinoListType

data class TetriMinoList(
    private val _tetriMinoList: List<MinoType> = MinoType.entries.shuffled(),
    private val _nextTetriMinoList: List<MinoType> = MinoType.entries.shuffled()
    // TODO: 次のミノのリストも用意しておく
): TetriMinoListType {
    override val tetriMinoList: List<MinoType>
        get() = _tetriMinoList
    override val nextTetriMinoList: List<MinoType>
        get() = _nextTetriMinoList

    // TODO: 新しくミノを生成する処理
    fun spawnTetriMino(): Pair<MinoType,TetriMinoList>{
        // ミノのリストの中からミノを新しく選択
        val nextMino = _tetriMinoList.firstOrNull() ?: throw IllegalStateException("Mino list is empty")
        // ミノのリストからそのミノを抜き出す
        val updatedList = _tetriMinoList.drop(1)
        // もしもミノが無くなったら次のミノを追加する
        val nextList = if(updatedList.isEmpty()){
            TetriMinoList(_tetriMinoList = _nextTetriMinoList)
        }else{
            TetriMinoList(_tetriMinoList = updatedList)
        }

        return nextMino to nextList
    }

    fun swapHoldAndNext(mino: TetriMino): TetriMinoList {
        val updatedList = listOf(mino.type) + _tetriMinoList.drop(1) // 先頭だけ入れ替え
        return this.copy(_tetriMinoList = updatedList) // 新しいオブジェクトを作成
    }


    // TODO: ミノのリストを生成する処理

}