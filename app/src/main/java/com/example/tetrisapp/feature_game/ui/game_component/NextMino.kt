package com.example.tetrisapp.feature_game.ui.game_component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.tetrisapp.feature_game.domain.model.MinoType

@Composable
fun NextMino(
    swapHoldAndNext: () -> Unit,
    isInitialized: Boolean,
    nextMino: MinoType,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                swapHoldAndNext()
            }
    ) {
        Text(
            text = "NEXT", modifier = Modifier.align(Alignment.TopCenter), color = Color.White
        )

        if (isInitialized) {
            val shape = nextMino.shapes[0]
            val cellSize = 20
            val boxHorizontalCenter = maxWidth.value / 2
            val boxVerticalCenter = maxHeight.value / 2

            // 最大値と最小値の中心「位置」を求めるので、半マス分ずらす必要あるので、+1する
            val centerX = (shape.minOf { it.first } + shape.maxOf { it.first } + 1) / 2f
            val centerY = (shape.minOf { it.second } + shape.maxOf { it.second } + 1) / 2f

            for ((x, y) in shape) {
                val offsetX = ((x - centerX) * cellSize + boxHorizontalCenter).toInt()
                val offsetY =
                    ((y - centerY) * cellSize + boxVerticalCenter + 10).toInt()

                MinoBlock(
                    color = nextMino.color,
                    position = Pair(offsetX, offsetY),
                    isGhost = false
                )
            }
        }
    }
}