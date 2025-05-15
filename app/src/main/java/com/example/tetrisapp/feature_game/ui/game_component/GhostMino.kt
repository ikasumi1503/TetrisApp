package com.example.tetrisapp.feature_game.ui.game_component

import androidx.compose.runtime.Composable
import com.example.tetrisapp.feature_game.domain.entity.TetriMino

@Composable
fun GhostMino(isInitialized: Boolean, ghostMino: TetriMino) {
    if (isInitialized) {
        for (relativePosition in ghostMino.type.shapes[ghostMino.rotation]) {
            MinoBlock(
                color = ghostMino.type.color,
                position = Pair(
                    ((ghostMino.position.first + relativePosition.first) * 20),
                    ((ghostMino.position.second + relativePosition.second - 1) * 20 + 22)
                ),
                isGhost = true
            )
        }
    }
}