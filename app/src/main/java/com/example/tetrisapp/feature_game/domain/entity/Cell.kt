package com.example.tetrisapp.feature_game.domain.entity

import androidx.compose.ui.graphics.Color
import com.example.tetrisapp.feature_game.domain.model.CellType

data class Cell(
    override val position: Pair<Int,Int> = Pair(0,0),
    override val color: Color = Color.Transparent,
    override val isFilled: Boolean = false
):CellType