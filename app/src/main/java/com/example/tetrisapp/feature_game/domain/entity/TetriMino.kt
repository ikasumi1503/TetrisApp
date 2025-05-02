package com.example.tetrisapp.feature_game.domain.entity

import androidx.compose.ui.unit.dp
import com.example.tetrisapp.feature_game.domain.model.MinoType
import com.example.tetrisapp.feature_game.domain.model.TetriMinoType


data class TetriMino(
    private val _position: Pair<Int, Int> = Pair(3, 0),
    private val _rotation: Int = 0,
    private val _type: MinoType
): TetriMinoType {
    override val position: Pair<Int,Int>
        get() = _position
    override val rotation: Int
        get() = _rotation
    override val type: MinoType
        get() = _type

    // update以外にも設置することを見越してclassの中で新しくインスタンスを作成する
    fun updateTetriMino(mino: TetriMino): TetriMino {
        return this.copy(_position = mino._position, _rotation = mino._rotation, _type = mino._type)
    }
}