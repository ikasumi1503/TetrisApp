package com.example.tetrisapp.feature_game.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tetrisapp.feature_game.domain.usecase.SideX
import com.example.tetrisapp.feature_game.ui.game_component.GameBoard
import com.example.tetrisapp.feature_game.ui.game_component.GameButtonGroup
import com.example.tetrisapp.feature_game.ui.game_component.NextMino
import com.example.tetrisapp.feature_game.ui.game_component.PauseModal
import com.example.tetrisapp.feature_game.ui.viewmodel.GameViewModel


// テトリスのゲーム画面の表示のみを行う

@Composable
fun GameScreen(gameViewModel: GameViewModel = viewModel()) {
    val state by gameViewModel.state.collectAsState()
    val board = state.board
    val mino = state.tetriMino
    val ghostMino = state.ghostMino
    val isInitialized = remember { mutableStateOf(false) }
    val prolongTimeDelayCountLimit = state.prolongTimeDelayCountLimit
    val score = state.score
    val nextMino = state.tetriMinoList.tetriMinoList[0]
    val isPaused = state.isPaused
    val combo = state.comboCount


    // LaunchedEffect内のコードは@Composable描画時に一度だけ表示される
    // 自然落下の処理
    LaunchedEffect(Unit) {
        // 最初に生成するミノの選択
        gameViewModel.spawnTetriMino()

        // ミノが選択された後にミノの描画やゴーストの表示を行いたいので、初期化されたかをtrueにする
        isInitialized.value = true
    }

    LaunchedEffect(isPaused) {
        // 自由落下処理
        if (
            !isPaused
        ) {
            while (
                true
            ) {
                gameViewModel.gravity(
                )
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            GameBoard(
                board = board,
                ghostMino = ghostMino,
                fallingMino = mino,
                isInitialized = isInitialized,
                onHardDrop = {
                    gameViewModel.hardDrop(
                        ghostMino = ghostMino,
                        mino = mino
                    )
                }
            )

            // Nextの表示
            Column {
                // Pause
                Button(
                    onClick = {
                        gameViewModel.pause()
                    }
                ) {
                    Text("Pause")
                }

                PauseModal(
                    isPaused = isPaused,
                    onChangeToMenu = { gameViewModel.changeToMenu() },
                    onInit = { gameViewModel.initGame() },
                    onResume = { gameViewModel.resume() }
                )

                // Score
                Text(score.toString() + "POINT")

                // Combo
                Text(combo.toString() + "COMBO")

                // TODO: Time
                val time = state.elapsedTime.div(1000)
                val minutesDisplay = time.div(60)
                val secondsDisplay = time.rem(60)
                Text(text = "%02d:%02d".format(minutesDisplay, secondsDisplay))

                // TODO: LEVEL


                NextMino(
                    gameViewModel = gameViewModel,
                    isInitialized = isInitialized,
                    nextMino = nextMino,
                )
            }
        }


        // 操作ボタン
        GameButtonGroup(
            onMoveRight = {
                gameViewModel.onMoveX(
                    sideX = SideX.RIGHT
                )
            },
            onMoveLeft = {
                gameViewModel.onMoveX(
                    sideX = SideX.LEFT
                )
            },
            onSoftDrop = {
                gameViewModel.onSoftDrop(

                )
            },
            onRotateClockWise = {
                gameViewModel.onRotate(
                    rotateDir = 1
                )
            },
            onRotateAntiClockWise = {
                gameViewModel.onRotate(
                    rotateDir = -1
                )
            },
        )
    }
}
