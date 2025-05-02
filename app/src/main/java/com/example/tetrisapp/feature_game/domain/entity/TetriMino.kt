package com.example.tetrisapp.feature_game.domain.entity

import androidx.compose.ui.unit.dp
import com.example.tetrisapp.feature_game.domain.model.MinoType
import com.example.tetrisapp.feature_game.domain.model.TetriMinoType


data class TetriMino(
    override val position: Pair<Int, Int> = Pair(3, 0),
    override val rotation: Int = 0,
    override val type: MinoType
): TetriMinoType