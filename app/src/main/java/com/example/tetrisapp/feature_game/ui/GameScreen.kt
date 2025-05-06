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
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tetrisapp.feature_game.domain.entity.Board
import com.example.tetrisapp.feature_game.domain.entity.TetriMino
import com.example.tetrisapp.feature_game.domain.model.MinoType
import com.example.tetrisapp.feature_game.domain.usecase.CheckCollisionXUseCase
import com.example.tetrisapp.feature_game.domain.usecase.CheckCollisionYUseCase
import com.example.tetrisapp.feature_game.domain.usecase.OnCollisionYUseCase
import com.example.tetrisapp.feature_game.domain.usecase.SideX
import com.example.tetrisapp.feature_game.domain.usecase.SideXToNumUseCase
import com.example.tetrisapp.feature_game.util.GameConstants
import kotlinx.coroutines.delay


// テトリスのゲーム画面の表示のみを行う


@Composable
fun GameScreen(gameViewModel: GameViewModel) {
    // TODO: git管理しておく
    val board by gameViewModel.board.observeAsState(Board())
    val boardWidth = Board().cells[0].size
    val minoWith = 4
    val toCenter = (boardWidth / 2) - (minoWith / 2)

    val minoPosition: MutableState<Pair<Int, Int>> =
        remember { mutableStateOf(Pair(toCenter, 0)) } // TODO: viewModelで実装したい
    val mino by gameViewModel.tetriMino.observeAsState(
        TetriMino(
            _position = Pair(toCenter, 0), _type = MinoType.T, _rotation = 0
        )
    )

    // LaunchedEffect内のコードは@Composable描画時に一度だけ表示される
    // 自然落下の処理
    LaunchedEffect(Unit) { // TODO: ViewModel内で使うようにする
        while (true) {
            delay(400)
            // TODO: これをviewModelに変えておく
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
            for (relativePosition in mino.type.shapes[mino.rotation]) {
                Box(
                    modifier = Modifier
                        // sizeとbackgroundを先においてしまうと、先に色がついて正しく表示されない
                        .offset(
                            x = ((mino.position.first + relativePosition.first) * 20).dp,
                            y = ((mino.position.second + relativePosition.second) * 20).dp
                        )
                        .size(20.dp)
                        .background(mino.type.color)
                )
            }
        }

        fun moveX(sideX: SideX) { // TODO: useCaseにしておく
            val sideToNumUseCase = SideXToNumUseCase()
            val sideToNum = sideToNumUseCase(sideX = sideX)

            val checkCollisionXUseCase = CheckCollisionXUseCase()
            // CheckCollisionXUseCaseのなかのwhenが分かりやすいのでsideToNumではなくsideを渡している
            val willCollideX = checkCollisionXUseCase(board = board, mino = mino, sideX = sideX)

            // 壁やミノと当たったら動かさない
            if (willCollideX) {
                return
            } else {
                // なににもあたらなければ左に動かす
                val newMino = mino.copy(
                    _position = Pair(
                        mino.position.first + sideToNum, mino.position.second
                    )
                )
                gameViewModel.updateTetriMino(newMino)
            }
        }

        fun rotate() {
            // TODO: もしもミノと壁とかの位置がかぶったらpositionずらす
            val newRotation = (mino.rotation + 1) % mino.type.shapes.size

            val rotatedMino = mino.copy(_rotation = newRotation)

            // SRSルールというテトリミノの回転ルールを適用している

            val kickOffsets =
                if (mino.type == MinoType.I) GameConstants.I_KickTable[Pair(mino.rotation, rotatedMino.rotation)]
                    ?: listOf(Pair(0, 0)) else GameConstants.JLSTZ_KickTable[Pair(
                    mino.rotation, rotatedMino.rotation
                )] ?: listOf(Pair(0, 0))

            // それぞれのオフセットを適用
            for (kickOffset in kickOffsets) {
                val newPosition = Pair(
                    mino.position.first + kickOffset.first, mino.position.second + kickOffset.second
                )
                // オフセットを適用したとき、回転後のミノで被っているものがないか確認
                val kickedRotatedMino = rotatedMino.copy(_position = newPosition)

                val isCollided =
                    kickedRotatedMino.type.shapes[kickedRotatedMino.rotation].any { relativePosition ->
                        val kickedRotatedMinoPartsX =
                            kickedRotatedMino.position.first + relativePosition.first
                        val kickedRotatedMinoPartsY =
                            kickedRotatedMino.position.second + relativePosition.second

                        // 画面外になっているかどうか
                        val isOutOfBounds =
                            kickedRotatedMinoPartsX < 0 || kickedRotatedMinoPartsX >= board.cells[0].size || kickedRotatedMinoPartsY < 0 || kickedRotatedMinoPartsY >= board.cells.size
                        // 他のミノと被っているかどうか
                        val isOverlapping =
                            board.cells.getOrNull(kickedRotatedMinoPartsY)?.getOrNull(
                                kickedRotatedMinoPartsX
                            )?.isFilled == true

                        isOutOfBounds || isOverlapping
                    }

                // 回転後のミノで被っていなければ確定
                if (!isCollided) {
                    println(kickedRotatedMino.position)
                    gameViewModel.updateTetriMino(kickedRotatedMino)
                    break
                }
            }
        }

        Row {
            Button(onClick = { moveX(sideX = SideX.LEFT) }) { Text("左") }
            Button(onClick = { moveX(sideX = SideX.RIGHT) }) { Text("右") }
            Button(onClick = { rotate() }) { Text("右回転") } // TODO: 左右の回転にしておきたい
        }
    }

}
