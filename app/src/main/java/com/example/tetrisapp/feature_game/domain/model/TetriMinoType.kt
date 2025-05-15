package com.example.tetrisapp.feature_game.domain.model

import android.icu.text.Transliterator.Position
import androidx.compose.ui.graphics.Color


enum class MinoType(
    val color: Color,
    val shapes: List<List<Pair<Int, Int>>>,
) {
    // 回転に対して右回り
    I(
        Color.Cyan, listOf(
            listOf(Pair(0, 1), Pair(1, 1), Pair(2, 1), Pair(3, 1)), // 0°
            listOf(Pair(2, 0), Pair(2, 1), Pair(2, 2), Pair(2, 3)), // 90°
            listOf(Pair(0, 2), Pair(1, 2), Pair(2, 2), Pair(3, 2)), // 180°
            listOf(Pair(1, 0), Pair(1, 1), Pair(1, 2), Pair(1, 3))  // 270°
        )
    ),

    O(
        Color.Yellow, listOf(
            listOf(Pair(1, 1), Pair(1, 2), Pair(2, 1), Pair(2, 2)) // 回転不要
        )
    ),

    T(
        Color.Magenta, listOf(
            listOf(Pair(1, 0), Pair(0, 1), Pair(1, 1), Pair(2, 1)), // 0°
            listOf(Pair(1, 0), Pair(1, 1), Pair(1, 2), Pair(2, 1)), // 90°
            listOf(Pair(0, 1), Pair(1, 1), Pair(2, 1), Pair(1, 2)), // 180°
            listOf(Pair(1, 0), Pair(0, 1), Pair(1, 1), Pair(1, 2))  // 270°
        )
    ),

    S(
        Color.Green, listOf(
            listOf(Pair(1, 1), Pair(2, 1), Pair(0, 2), Pair(1, 2)), // 0°
            listOf(Pair(1, 1), Pair(1, 2), Pair(2, 2), Pair(2, 3)), // 90°
            listOf(Pair(1, 2), Pair(2, 2), Pair(0, 3), Pair(1, 3)), // 180°
            listOf(Pair(0, 1), Pair(0, 2), Pair(1, 2), Pair(1, 3))  // 270°
        )
    ),

    Z(
        Color.Red, listOf(
            listOf(Pair(0, 1), Pair(1, 1), Pair(1, 2), Pair(2, 2)), // 0°
            listOf(Pair(2, 1), Pair(2, 2), Pair(1, 2), Pair(1, 3)),  // 90°
            listOf(Pair(0, 2), Pair(1, 2), Pair(1, 3), Pair(2, 3)), // 180°
            listOf(Pair(1, 1), Pair(1, 2), Pair(0, 2), Pair(0, 3)) // 270°
        )
    ),

    J(
        Color.Blue, listOf(
            listOf(Pair(0, 0), Pair(0, 1), Pair(1, 1), Pair(2, 1)), // 0°
            listOf(Pair(1, 0), Pair(1, 1), Pair(1, 2), Pair(2, 0)), // 90°
            listOf(Pair(0, 1), Pair(1, 1), Pair(2, 1), Pair(2, 2)), // 180°
            listOf(Pair(0, 2), Pair(1, 0), Pair(1, 1), Pair(1, 2))  // 270°
        )
    ),

    L(
        Color.Gray, listOf(
            listOf(Pair(2, 0), Pair(0, 1), Pair(1, 1), Pair(2, 1)), // 0°
            listOf(Pair(1, 0), Pair(1, 1), Pair(1, 2), Pair(2, 2)), // 90°
            listOf(Pair(0, 1), Pair(1, 1), Pair(2, 1), Pair(0, 2)), // 180°
            listOf(Pair(0, 0), Pair(1, 0), Pair(1, 1), Pair(1, 2))  // 270°
        )
    )
}

interface TetriMinoType {
    val rotation: Int
    val type: MinoType
    val position: Pair<Int, Int>
}