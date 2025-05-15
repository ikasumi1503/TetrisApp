package com.example.tetrisapp.feature_game.ui.game_component

import androidx.compose.runtime.Composable
import com.example.tetrisapp.feature_game.domain.entity.TetriMino

@Composable
fun FallingMino(isInitialized: Boolean, mino: TetriMino) {
    // テトリミノの描画
    if (isInitialized) {
        for (relativePosition in mino.type.shapes[mino.rotation]) {
            MinoBlock(
                color = mino.type.color,
                position = Pair(
                    ((mino.position.first + relativePosition.first) * 20),
                    ((mino.position.second + relativePosition.second - 1) * 20 + 22)
                ),
                isGhost = false
            )
        }
    }
}