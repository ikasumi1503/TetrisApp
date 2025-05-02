package com.example.tetrisapp.feature_game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tetrisapp.feature_game.domain.entity.Board
import com.example.tetrisapp.feature_game.domain.entity.Cell
import com.example.tetrisapp.feature_game.domain.entity.TetriMino
import com.example.tetrisapp.feature_game.domain.model.MinoType
import com.example.tetrisapp.feature_game.domain.usecase.CheckCollisionYUseCase
import com.example.tetrisapp.feature_game.domain.usecase.OnCollisionYUseCase
import kotlinx.coroutines.delay


// テトリスのゲーム画面の表示のみを行う


@Composable
fun GameScreen(gameViewModel: GameViewModel) {
    // TODO: git管理しておく
    val board by gameViewModel.board.observeAsState(Board())
    val boardWidth = Board().cells[0].size
    val minoWith = 4
    val toCenter = (boardWidth / 2) - (minoWith / 2)

    val minoPosition: MutableState<Pair<Int, Int>> = remember { mutableStateOf(Pair(toCenter, 0)) } // TODO: viewModelで実装したい
    val mino = TetriMino(type = MinoType.O)
    val shape = mino.type.shapes[mino.rotation]

    // LaunchedEffect内のコードは@Composable描画時に一度だけ表示される
    LaunchedEffect(Unit) {
        while (true) {
            delay(300)

            // TODO: これをviewModelに変えておく
            // 壁への当たり判定
            val nextMinoPosition = Pair(minoPosition.value.first, minoPosition.value.second + 1)

            // TODO: ミノの相対配置それぞれについて、nextMinoPositionにすでにミノ・壁があればミノ設置

            val checkCollisionYUseCase = CheckCollisionYUseCase()
            val willCollideY: Boolean = checkCollisionYUseCase(board = board, mino = mino, position = minoPosition.value)

            // 衝突するならそこにミノを設置
            if (willCollideY) {
                val onCollisionYUseCase = OnCollisionYUseCase(gameViewModel = gameViewModel)
                onCollisionYUseCase(mino = mino, position = minoPosition)
            } else {
                minoPosition.value = nextMinoPosition
            }


            // TODO: もしもミノの出現位置に既にミノがあればゲームオーバーにする
        }
    }

    // TODO: スワイプによってミノの位置を変える(横)


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Box(modifier = Modifier) {
            // セルが横10列が縦に20個並んでいるものを描画している
            Column {
                for (cellRow in board.cells) {
                    Row {
                        for (cell in cellRow) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .border(1.dp, Color.Gray)
                                    .background(cell.color)
                            )
                        }
                    }
                }
            }


            // テトリミノの描画
            for (relativePosition in mino.type.shapes[0]) {
                Box(
                    modifier = Modifier
                        // sizeとbackgroundを先においてしまうと、先に色がついて正しく表示されない
                        .offset(
                            x = ((minoPosition.value.first + relativePosition.first) * 20).dp,
                            y = ((minoPosition.value.second + relativePosition.second) * 20).dp
                        )
                        .size(20.dp)
                        .background(mino.type.color)
                )
            }

        }
    }
}
