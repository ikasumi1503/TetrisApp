package com.example.tetrisapp.feature_game.domain.model

import androidx.compose.ui.graphics.Color


// interfaceじゃなくopen classやabstract classでも代用可能
interface CellType{
    val position: Pair<Int, Int>
    val color: Color
    val isFilled: Boolean
}
