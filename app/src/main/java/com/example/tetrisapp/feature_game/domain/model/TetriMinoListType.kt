package com.example.tetrisapp.feature_game.domain.model

interface TetriMinoListType {
    val tetriMinoList: List<MinoType>
    val nextTetriMinoList: List<MinoType>
}