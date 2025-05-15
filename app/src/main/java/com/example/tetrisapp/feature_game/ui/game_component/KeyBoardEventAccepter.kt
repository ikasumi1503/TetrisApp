package com.example.tetrisapp.feature_game.ui.game_component

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import com.example.tetrisapp.feature_game.domain.entity.TetriMino
import com.example.tetrisapp.feature_game.domain.usecase.input.SideX
import com.example.tetrisapp.feature_game.ui.viewmodel.GameViewModel
import kotlinx.coroutines.delay

@Composable
fun KeyBoardEvents(
    gameViewModel: GameViewModel,
    ghostMino: TetriMino,
    mino: TetriMino
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        delay(3000) // 短めのディレイ（UI構築完了待ち）
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .focusRequester(focusRequester)
            .focusable() // キーボード入力を受け取る準備をする
            .onKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown) {
                    // キーを押したときの繰り返し処理はWindowsのキー押下による繰り返し処理が反映されている。つまり、OS側を設定しないといけない
                    println("Pressed key: ${keyEvent.key}, type: ${keyEvent.type}")
                    when (keyEvent.key) {
                        Key.DirectionLeft -> {
                            gameViewModel.onMoveX(SideX.LEFT)
                            true
                        }

                        Key.DirectionRight -> {
                            gameViewModel.onMoveX(SideX.RIGHT)
                            true
                        }

                        Key.DirectionDown -> {
                            gameViewModel.onSoftDrop()
                            true
                        }

                        Key.Spacebar -> {
                            gameViewModel.hardDrop(
                                ghostMino = ghostMino,
                                mino = mino
                            )
                            true
                        }

                        Key.S -> {
                            gameViewModel.swapHoldAndNext()
                            true
                        }

                        Key.Z -> {
                            gameViewModel.onRotate(-1)
                            true
                        }

                        Key.X -> {
                            gameViewModel.onRotate(+1)
                            true
                        }

                        Key.A -> {
                            gameViewModel.onRotate(+1)
                            gameViewModel.onRotate(+1)
                            true
                        }

                        else -> false
                    }
                } else false
            }
    )
}