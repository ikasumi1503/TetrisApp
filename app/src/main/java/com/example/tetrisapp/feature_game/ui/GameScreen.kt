package com.example.tetrisapp.feature_game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.tetrisapp.feature_game.domain.entity.Board
import com.example.tetrisapp.feature_game.domain.entity.TetriMino
import com.example.tetrisapp.feature_game.domain.model.MinoType
import com.example.tetrisapp.feature_game.domain.usecase.CheckCollisionYUseCase
import com.example.tetrisapp.feature_game.domain.usecase.OnCollisionYUseCase
import com.example.tetrisapp.feature_game.domain.usecase.SideX
import com.example.tetrisapp.feature_game.ui.game_component.FallingMino
import com.example.tetrisapp.feature_game.ui.game_component.GhostMino
import com.example.tetrisapp.feature_game.ui.game_component.NextMino
import kotlinx.coroutines.delay


// テトリスのゲーム画面の表示のみを行う


@Composable
fun GameScreen(gameViewModel: GameViewModel) {
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


    // LaunchedEffect内のコードは@Composable描画時に一度だけ表示される
    // 自然落下の処理
    LaunchedEffect(Unit) { // TODO: ViewModel内で使うようにする

        // 最初に生成するミノの選択
        gameViewModel.spawnTetriMino()
        isInitialized.value = true


        while (true) {
            val currentTime = System.currentTimeMillis()
            // StateFlowで実装した。
            // 最初はファイルの上部にvalueやobserveAsStateでアクセスしていたけど、ループ内でviewModelで取得した初期値がcurrentDelayに入っていた。
            // つまり、最初の値が参照されていて変更が検知されなかった。
            // minoを読み込むときにはオブジェクトの値を読みに行ってて、ミノの生成ごとにそれに対応するオブジェクトが生成されていたから、読み込むことができていた
            val currentDelay = gameViewModel.timeDelay.value
            if (currentDelay >= 1000L
            ) {

                // 壁への当たり判定
                val checkCollisionYUseCase = CheckCollisionYUseCase()
                val willCollideY: Boolean = checkCollisionYUseCase(board = board, mino = mino)

                if (willCollideY) {
                    // 衝突するならそこにミノを設置して新しいミノを作成
                    val onCollisionYUseCase = OnCollisionYUseCase(gameViewModel = gameViewModel)
                    onCollisionYUseCase(mino = mino)
                } else {
                    // 衝突してないならミノを一つ下に落とす
                    val newMino = mino.copy(
                        _position = Pair(mino.position.first, mino.position.second + 1)
                    )
                    gameViewModel.updateTetriMino(newMino)
                    gameViewModel.markRotation(false)
                }

                // TimeDelayを0にする
                gameViewModel.updateTimeDelay(0)
            } else {
                // TimeDelayにcurrentTime-lastTimeを足す
                gameViewModel.updateTimeDelay(currentDelay + currentTime - lastTime.longValue)
            }
            lastTime.longValue = currentTime


            delay(16L) // 60fpsくらい
            // TODO: もしもミノの出現位置に既にミノがあればゲームオーバーにする
        }
    }

    // TODO: スワイプによってミノの位置を変える(横)


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier
                .border(1.dp, Color.Gray)
                // clickable はmodifier末尾に入れた方がいいらしい
                // 大きさとか決まった時点でクリックできる範囲を決めたいから
                .clickable {
                    gameViewModel.hardDrop(
                        ghostMino = ghostMino,
                        mino = mino
                    )
                }) {

                // セルが横10列が縦に20個並んでいるものを描画している
                Column {
                    for (cellRow in board.cells) {
                        Row {
                            for (cell in cellRow) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(cell.color)
                                )
                            }
                        }
                    }
                }

                // ゴーストの描画
                GhostMino(isInitialized = isInitialized, ghostMino = ghostMino)

                // テトリミノの描画
                FallingMino(isInitialized = isInitialized, mino = mino)
            }

            // Nextの表示
            if (nextMino != null && score != null) {
                NextMino(
                    gameViewModel = gameViewModel,
                    isInitialized = isInitialized,
                    nextMino = nextMino,
                    score = score
                )
            }
        }

        Row {

            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {

                    IconButton(
                        onClick = {
                            gameViewModel.moveX(
                                sideX = SideX.LEFT,
                                mino = mino,
                                board = board,
                                prolongTimeDelayCountLimit = prolongTimeDelayCountLimit
                            )
                        }
                    ) {
                        Icon(
                            Icons.Filled.KeyboardArrowLeft,
                            contentDescription = "Left",
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            gameViewModel.moveX(
                                sideX = SideX.RIGHT,
                                mino = mino,
                                board = board,
                                prolongTimeDelayCountLimit = prolongTimeDelayCountLimit
                            )
                        }
                    ) {
                        Icon(
                            Icons.Filled.KeyboardArrowRight,
                            contentDescription = "Right",
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                IconButton(
                    onClick = {
                        gameViewModel.softDrop(
                            mino = mino,
                            board = board
                        )
                    }
                ) {
                    Icon(
                        Icons.Filled.KeyboardArrowDown,
                        contentDescription = "SoftDrop",
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            IconButton(
                onClick = {
                    gameViewModel.rotate(
                        rotateDir = 1,
                        mino = mino,
                        board = board
                    )
                }
            ) {
                Icon(
                    Icons.Filled.Refresh,
                    contentDescription = "RotateClockWise",
                    modifier = Modifier.size(48.dp)
                )
            }
            IconButton(
                onClick = {
                    gameViewModel.rotate(
                        rotateDir = -1,
                        mino = mino,
                        board = board
                    )
                }
            ) {
                Icon(
                    Icons.Filled.Refresh,
                    contentDescription = "RotateAntiClockWise",
                    modifier = Modifier
                        .size(48.dp)
                        // 左右反転にしてる
                        .graphicsLayer(scaleX = -1f)
                )
            }
        }
    }
}
