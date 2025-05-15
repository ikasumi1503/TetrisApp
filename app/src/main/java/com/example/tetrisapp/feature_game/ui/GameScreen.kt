package com.example.tetrisapp.feature_game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tetrisapp.feature_game.domain.usecase.SideX
import com.example.tetrisapp.feature_game.ui.game_component.GameBoard
import com.example.tetrisapp.feature_game.ui.game_component.GameButtonGroup
import com.example.tetrisapp.feature_game.ui.game_component.GameStatsGroup
import com.example.tetrisapp.feature_game.ui.game_component.PauseButton
import com.example.tetrisapp.feature_game.ui.viewmodel.GameViewModel


// テトリスのゲーム画面の表示のみを行う
// TODO: ESLintで整形できるようにしたい。・引数の中身の改行分け
@Composable
fun GameScreen(gameViewModel: GameViewModel = viewModel()) {
    val state by gameViewModel.state.collectAsState()
    val board = state.board
    val mino = state.tetriMino
    val ghostMino = state.ghostMino
    val isInitialized = state.isInitialized
    val score = state.score
    val nextMino = state.tetriMinoList.tetriMinoList[0]
    val isPaused = state.isPaused
    val combo = state.comboCount
    val level = state.level
    val elapsedTime = state.elapsedTime
    val isHardDropTriggered = state.hardDropTrigger


    // LaunchedEffect内のコードは@Composable描画時に一度だけ表示される
    // 自然落下の処理
    LaunchedEffect(Unit) {
        // 最初に生成するミノの選択
        gameViewModel.spawnTetriMino()

        // ミノが選択された後にミノの描画やゴーストの表示を行いたいので、初期化されたかをtrueにする
        gameViewModel.updateIsInitialized(true)
    }

    LaunchedEffect(isPaused) {
        // 自由落下処理
        if (!isPaused) {
            while (true) {
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 32.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            PauseButton(pause = { gameViewModel.pause() })
        }

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
                },
                isHardDropTriggered = isHardDropTriggered
            )

            Column(
                modifier = Modifier
                    .width(100.dp)
                    .height(400.dp)
                    .background(Color.Black)
                    .border(3.dp, Color.Gray),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                // Pause
                GameStatsGroup(
                    changeToMenu = { gameViewModel.changeToMenu() },
                    initGame = { gameViewModel.initGame() },
                    swapHoldAndNext = { gameViewModel.swapHoldAndNext() },
                    resume = { gameViewModel.resume() },
                    isPaused = isPaused,
                    score = score,
                    combo = combo,
                    elapsedTime = elapsedTime,
                    level = level,
                    isInitialized = isInitialized,
                    nextMino = nextMino
                )
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

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
