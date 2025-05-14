package com.example.tetrisapp.feature_game.ui.game_component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tetrisapp.feature_game.domain.entity.Board
import com.example.tetrisapp.feature_game.domain.entity.TetriMino

@Composable
fun GameBoard(
    board: Board,
    ghostMino: TetriMino,
    fallingMino: TetriMino,
    isInitialized: MutableState<Boolean>,
    onHardDrop: () -> Unit
) {
    Box(modifier = Modifier
        .background(Color.Black)
        .border(3.dp, Color.Gray)
        // clickable はmodifier末尾に入れた方がいいらしい
        // 大きさとか決まった時点でクリックできる範囲を決めたいから
        .clickable {
            onHardDrop()
        }) {

        // セルが横10列が縦に20個並んでいるものを描画している
        Column {
            for (cellRow in board.cells) {
                Row {
                    for (cell in cellRow) {
                        // TODO: Cell全てに灰色のボーダーを当てているので、セル自体にボーダーの色を持たせて、セルごとに色を持たせたい。空セルは灰色で、それ以外のセルは透明でいいかも
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(cell.color)
                                .border(1.dp, Color.Gray.copy(alpha = 0.2f))
                        )
                    }
                }
            }
        }

        // ゴーストの描画
        GhostMino(isInitialized = isInitialized, ghostMino = ghostMino)

        // テトリミノの描画
        FallingMino(isInitialized = isInitialized, mino = fallingMino)
    }
}