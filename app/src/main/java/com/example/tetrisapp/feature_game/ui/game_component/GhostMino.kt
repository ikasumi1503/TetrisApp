package com.example.tetrisapp.feature_game.ui.game_component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tetrisapp.feature_game.domain.entity.TetriMino

@Composable
fun GhostMino(isInitialized: MutableState<Boolean>, ghostMino: TetriMino) {
    if (isInitialized.value) {
        for (relativePosition in ghostMino.type.shapes[ghostMino.rotation]) {
            Box(
                modifier = Modifier
                    // sizeとbackgroundを先においてしまうと、先に色がついて正しく表示されない
                    .offset(
                        x = ((ghostMino.position.first + relativePosition.first) * 20).dp,
                        // 上方向になぜかズレてたので修正した
                        y = ((ghostMino.position.second + relativePosition.second - 1) * 20 + 22).dp
                    )
                    .size(20.dp)
                    .background(ghostMino.type.color.copy(alpha = 0.3f))
                    .border(1.dp, ghostMino.type.color.copy(alpha = 0.4f))
                    .drawBehind {
                        // 左上にハイライト
                        drawRect(
                            color = Color.White.copy(alpha = 0.1f), // ハイライト
                            size = size.copy(width = size.width / 2, height = size.height / 2)
                        )
                        // 右下に影
                        drawRect(
                            color = Color.Black.copy(alpha = 0.2f),
                            topLeft = androidx.compose.ui.geometry.Offset(
                                size.width / 2,
                                size.height / 2
                            ),
                            size = size.copy(width = size.width / 2, height = size.height / 2)
                        )
                    }
            )
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