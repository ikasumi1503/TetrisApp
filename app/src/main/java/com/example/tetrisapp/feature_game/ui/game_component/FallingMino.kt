package com.example.tetrisapp.feature_game.ui.game_component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tetrisapp.feature_game.domain.entity.TetriMino

@Composable
fun FallingMino(isInitialized: MutableState<Boolean>, mino: TetriMino) {
    // テトリミノの描画
    if (isInitialized.value) {
        for (relativePosition in mino.type.shapes[mino.rotation]) {
            Box(
                modifier = Modifier
                    // sizeとbackgroundを先においてしまうと、先に色がついて正しく表示されない
                    .offset(
                        x = ((mino.position.first + relativePosition.first) * 20).dp,
                        // 上方向にズレてたので修正した。GameBoardのColumn直下においたので、Columnのborderのdp分下がったのかもしれない。
                        // 横はcolumnの方向的にズレなかったのかもしれないが、わからない
                        y = ((mino.position.second + relativePosition.second - 1) * 20 + 22).dp
                    )
                    .size(20.dp)
                    .background(mino.type.color)
                    .border(1.dp, Color.Transparent)
            )
        }
    }
}