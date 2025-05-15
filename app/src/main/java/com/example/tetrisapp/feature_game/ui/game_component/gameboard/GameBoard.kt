package com.example.tetrisapp.feature_game.ui.game_component.gameboard

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.tetrisapp.feature_game.domain.entity.Board
import com.example.tetrisapp.feature_game.domain.entity.TetriMino
import com.example.tetrisapp.feature_game.ui.game_component.template.MinoBlock
import kotlin.math.roundToInt

@Composable
fun GameBoard(
    board: Board,
    ghostMino: TetriMino,
    fallingMino: TetriMino,
    isInitialized: Boolean,
    onHardDrop: () -> Unit,
    isHardDropTriggered: Boolean,
) {
    val offsetY = remember { androidx.compose.animation.core.Animatable(0f) }

    LaunchedEffect(isHardDropTriggered) {
        if (isHardDropTriggered) {
            offsetY.snapTo(0f)
            offsetY.animateTo(
                targetValue = 3f,
                animationSpec = tween(durationMillis = 100)
            )
            offsetY.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 120)
            )
        }
    }

    Box(modifier = Modifier
        .background(Color.Black)
        .offset {
            IntOffset(0, offsetY.value.roundToInt())
        }
        .border(3.dp, Color.Gray)
        // clickable はmodifier末尾に入れた方がいいらしい
        // 大きさとか決まった時点でクリックできる範囲を決めたいから
        .clickable(
            indication = null, // ← リップルエフェクトを無効化
            interactionSource = remember { MutableInteractionSource() } // ← 必須：内部トラッキング用
        ) {
            onHardDrop()
        }
    ) {

        // セルが横10列が縦に20個並んでいるものを描画している
        Column {
            for (cellRow in board.cells) {
                Row {
                    for (cell in cellRow) {
                        if (cell.isFilled) {
                            MinoBlock(
                                color = cell.color,
                                position = Pair(0, 0),
                                isGhost = false
                            )
                        } else {
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
        }

        // ゴーストの描画
        GhostMino(isInitialized = isInitialized, ghostMino = ghostMino)

        // テトリミノの描画
        FallingMino(isInitialized = isInitialized, mino = fallingMino)
    }
}