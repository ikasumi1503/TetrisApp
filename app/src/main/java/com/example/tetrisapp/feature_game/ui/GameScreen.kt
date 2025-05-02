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
import kotlinx.coroutines.delay


// テトリスのゲーム画面の表示のみを行う


@Composable
fun GameScreen(gameViewModel: GameViewModel) {
    // TODO: git管理しておく
    val boardWidth = Board().cells[0].size
    val minoWith = 4
    val toCenter = (boardWidth / 2) - (minoWith / 2)

    var minoPosition by remember { mutableStateOf(Pair(toCenter, 0)) } // viewModelで実装したい
    val mino = TetriMino(type = MinoType.O)
    val shape = mino.type.shapes[mino.rotation]

    // LaunchedEffect内のコードは@Composable描画時に一度だけ表示される
    LaunchedEffect(Unit) {
        while (true) {
            delay(500)

            // TODO: これをviewModelに変えておく
            // 壁への当たり判定
            val nextMinoPosition = Pair(minoPosition.first, minoPosition.second + 1)

            // TODO: ミノの相対配置それぞれについて、nextMinoPositionにすでにミノ・壁があればミノ設置

            val willCollideY: Boolean = shape.any { relativePosition ->
                val nextX = minoPosition.first + relativePosition.first
                val nextY = minoPosition.second + relativePosition.second + 1

                // ミノの次の位置が壁があるまたはミノがあるならwillCollide=trueを返す
                // getOrNullにすることで範囲外の時例外が出るのを防ぐ。例えばnextY=21になると範囲外でクラッシュするのを防ぐ


                nextY >= Board().cells.size || gameViewModel.board.value?.cells?.getOrNull(nextY)
                    ?.getOrNull(nextX)?.isFilled == true
            }

            // 衝突するならそこにミノを設置
            if (willCollideY) {
                for (relativePosition in mino.type.shapes[mino.rotation]) {
                    // ミノの配置
                    val newCell = Cell(
                        color = mino.type.color,
                        position = Pair(
                            minoPosition.first + relativePosition.first,
                            minoPosition.second + relativePosition.second
                        ),
                        isFilled = true
                    )
                    gameViewModel.createBoardWithUpdateCells(newCell)
                }
                // 新しいミノの生成
                minoPosition = Pair(toCenter, 0)
            } else {
                minoPosition = nextMinoPosition
            }

            // TODO: もしも地面に着いたら次のミノを表示させる

            // TODO: もしもミノの出現位置に既にミノがあればゲームオーバーにする
        }
    }

    // TODO: スワイプによってミノの位置を変える(横)
    val board by gameViewModel.board.observeAsState(Board())

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
                            x = ((minoPosition.first + relativePosition.first) * 20).dp,
                            y = ((minoPosition.second + relativePosition.second) * 20).dp
                        )
                        .size(20.dp)
                        .background(mino.type.color)
                )
            }

        }
    }
}
