package com.example.tetrisapp.feature_game.ui.game_component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

fun Color.lighten(factor: Float): Color {
    return Color(
        red = (red + (1 - red) * factor).coerceIn(0f, 1f),
        green = (green + (1 - green) * factor).coerceIn(0f, 1f),
        blue = (blue + (1 - blue) * factor).coerceIn(0f, 1f),
        alpha = alpha
    )
}

@Composable
fun MinoBlock(color: Color, position: Pair<Int, Int>, isGhost: Boolean) {
    val backGroundColor = if (isGhost) color.lighten(0.2f) else color
    val backGroundAlpha = if (isGhost) 0.3f else 1f
    val borderAlpha = if (isGhost) 0.35f else 0.8f

    Box(
        modifier = Modifier
            // sizeとbackgroundを先においてしまうと、先に色がついて正しく表示されない
            .offset(
                x = position.first.dp,
                // 上方向にズレてたので修正した。GameBoardのColumn直下においたので、Columnのborderのdp分下がったのかもしれない。
                // 横はcolumnの方向的にズレなかったのかもしれないが、わからない
                y = position.second.dp
            )
            .size(20.dp)
            .background(backGroundColor.copy(alpha = backGroundAlpha))
            .border(1.dp, color.copy(alpha = borderAlpha))
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
}