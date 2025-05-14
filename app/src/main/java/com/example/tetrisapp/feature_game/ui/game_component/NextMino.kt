package com.example.tetrisapp.feature_game.ui.game_component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tetrisapp.feature_game.domain.model.MinoType
import com.example.tetrisapp.feature_game.ui.viewmodel.GameViewModel

@Composable
fun NextMino(
    gameViewModel: GameViewModel,
    isInitialized: MutableState<Boolean>,
    nextMino: MinoType,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                gameViewModel.swapHoldAndNext()
            }
    ) {
        Text(
            text = "NEXT", modifier = Modifier.align(Alignment.TopCenter)
        )

        if (isInitialized.value) {
            val shape = nextMino.shapes[0]
            val cellSize = 20
            val boxCenter = 50  // 100 / 2

            // 最大値と最小値の中心「位置」を求めるので、半マス分ずらす必要あるので、+1する
            val centerX = (shape.minOf { it.first } + shape.maxOf { it.first } + 1) / 2f
            val centerY = (shape.minOf { it.second } + shape.maxOf { it.second } + 1) / 2f

            for ((x, y) in shape) {
                val offsetX = ((x - centerX) * cellSize + boxCenter).dp
                val offsetY =
                    ((y - centerY) * cellSize + boxCenter + 10).dp // 10dpだけNEXTの下にずらす

                Box(
                    modifier = Modifier
                        .offset(x = offsetX, y = offsetY)
                        .size(cellSize.dp)
                        .background(nextMino.color)
                        .border(1.dp, Color.Black)
                )
            }
        }
    }
}