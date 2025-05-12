package com.example.tetrisapp.feature_game.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tetrisapp.feature_game.domain.entity.Board
import com.example.tetrisapp.feature_game.domain.entity.TetriMino
import com.example.tetrisapp.feature_game.domain.model.MinoType
import com.example.tetrisapp.feature_game.domain.usecase.SideX
import com.example.tetrisapp.feature_game.ui.game_component.GameBoard
import com.example.tetrisapp.feature_game.ui.game_component.GameButtonGroup
import com.example.tetrisapp.feature_game.ui.game_component.NextMino
import com.example.tetrisapp.feature_game.ui.game_component.PauseModal
import com.example.tetrisapp.feature_game.ui.viewmodel.GameViewModel


// テトリスのゲーム画面の表示のみを行う

@Composable
fun GameScreen(gameViewModel: GameViewModel = viewModel()) {
    val board by gameViewModel.board.observeAsState(Board())
    val boardWidth = Board().cells[0].size
    val minoWith = 4
    val toCenter = (boardWidth / 2) - (minoWith / 2)
    val mino by gameViewModel.tetriMino.observeAsState(
        TetriMino(
            // ダミーデータ
            _position = Pair(toCenter, 0), _type = MinoType.T, _rotation = 0
        )
    )
    val ghostMino by gameViewModel.ghostMino.observeAsState(
        TetriMino(
            // ダミーデータ
            _position = Pair(toCenter, 0), _type = MinoType.T, _rotation = 0
        )
    )
    val isInitialized = remember { mutableStateOf(false) }
    val lastTime: MutableLongState = remember { mutableLongStateOf(System.currentTimeMillis()) }
    val prolongTimeDelayCountLimit = gameViewModel.prolongTimeDelayCountLimit.observeAsState(0)
    val score = gameViewModel.score.value
    val nextMino = gameViewModel.tetriMinoList.value?.tetriMinoList?.get(0)
    val timeDelay = gameViewModel.timeDelay
    val isPaused = gameViewModel.isPaused.observeAsState(false).value
    val combo = gameViewModel.comboCount.observeAsState(0).value


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
                    timeDelay = timeDelay,
                    board = board,
                    mino = mino,
                    lastTime = lastTime
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
                if (score != null) {
                    Text(score.toString() + "POINT")
                }

                // Combo
                Text(combo.toString() + "COMBO")

                // TODO: Time
                val time = gameViewModel.time.value?.div(1000)
                val minutesDisplay = time?.div(60)
                val secondsDisplay = time?.rem(60)
                Text(text = "%02d:%02d".format(minutesDisplay, secondsDisplay))

                // TODO: LEVEL


                if (nextMino != null && score != null) {
                    NextMino(
                        gameViewModel = gameViewModel,
                        isInitialized = isInitialized,
                        nextMino = nextMino,
                    )
                }
            }
        }


        // 操作ボタン
        GameButtonGroup(
            onMoveRight = {gameViewModel.moveX(
                sideX = SideX.RIGHT,
                board = board,
                mino = mino,
                prolongTimeDelayCountLimit = prolongTimeDelayCountLimit
            )},
            onMoveLeft = {gameViewModel.moveX(
                sideX = SideX.LEFT,
                board = board,
                mino = mino,
                prolongTimeDelayCountLimit = prolongTimeDelayCountLimit
            )},
            onSoftDrop = { gameViewModel.softDrop(
                mino = mino,
                board = board
            ) },
            onRotateClockWise = {
                gameViewModel.rotate(
                    rotateDir = 1,
                    mino = mino,
                    board = board
                )
            },
            onRotateAntiClockWise = {
                gameViewModel.rotate(
                    rotateDir = -1,
                    mino = mino,
                    board = board
                )
            },
        )
    }
}
