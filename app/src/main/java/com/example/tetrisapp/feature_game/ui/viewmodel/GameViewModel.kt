package com.example.tetrisapp.feature_game.ui.viewmodel

import GenerateLockCellsUseCase
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tetrisapp.feature_game.domain.entity.Board
import com.example.tetrisapp.feature_game.domain.entity.TetriMino
import com.example.tetrisapp.feature_game.domain.entity.TetriMinoList
import com.example.tetrisapp.feature_game.domain.usecase.CalculateScoreUseCase
import com.example.tetrisapp.feature_game.domain.usecase.CheckAndClearLinesUseCase
import com.example.tetrisapp.feature_game.domain.usecase.CheckCollisionXUseCase
import com.example.tetrisapp.feature_game.domain.usecase.CheckCollisionYUseCase
import com.example.tetrisapp.feature_game.domain.usecase.CheckGameOverUseCase
import com.example.tetrisapp.feature_game.domain.usecase.CheckIsTSpinUseCase
import com.example.tetrisapp.feature_game.domain.usecase.ComputeGhostMinoUseCase
import com.example.tetrisapp.feature_game.domain.usecase.MoveXUseCase
import com.example.tetrisapp.feature_game.domain.usecase.ProcessPlacementUseCase
import com.example.tetrisapp.feature_game.domain.usecase.RotateMinoUseCase
import com.example.tetrisapp.feature_game.domain.usecase.SideX
import com.example.tetrisapp.feature_game.domain.usecase.SideXToNumUseCase
import com.example.tetrisapp.feature_game.domain.usecase.SoftDropUseCase
import com.example.tetrisapp.feature_game.domain.usecase.SpawnMinoUseCase
import com.example.tetrisapp.feature_game.domain.usecase.SwapHoldUseCase
import com.example.tetrisapp.feature_game.ui.ScreenState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


// なぜuiにviewModelを置いているのか？
// ui状態の管理をしたり、ドメインロジックを呼び出すため、ドメインとの橋渡しをするのでuiに置いている
// ViewModelはAndroidに依存しているので、純粋なロジックのみではないから

//　ViewModelの役割は？
// 状態の管理。uiが欲しい状態を整えてあげる中間役。

// TODO: UseCaseの中でUseCaseを使っているときがあるので、直しておく
class GameViewModel(
    private val swapHoldUseCase: SwapHoldUseCase = SwapHoldUseCase(),
    private val checkCollisionYUseCase: CheckCollisionYUseCase = CheckCollisionYUseCase(),
    private val spawnMinoUseCase: SpawnMinoUseCase = SpawnMinoUseCase(),
    private val checkGameOverUseCase: CheckGameOverUseCase = CheckGameOverUseCase(
        checkCollisionY = checkCollisionYUseCase
    ),
    private val computeGhostMinoUseCase: ComputeGhostMinoUseCase = ComputeGhostMinoUseCase(),
    private val processPlacementUseCase: ProcessPlacementUseCase = ProcessPlacementUseCase(
        clearLines = CheckAndClearLinesUseCase(),
        checkTSpin = CheckIsTSpinUseCase(),
        calcScore = CalculateScoreUseCase()
    ),
    private val moveXUseCase: MoveXUseCase = MoveXUseCase(
        sideXToNum = SideXToNumUseCase(),
        checkCollisionX = CheckCollisionXUseCase(),
        checkCollisionY = CheckCollisionYUseCase()
    ),
    private val rotateMinoUseCase: RotateMinoUseCase = RotateMinoUseCase(
        checkCollisionY = CheckCollisionYUseCase()
    ),
    private val generateLockCellsUseCase: GenerateLockCellsUseCase = GenerateLockCellsUseCase(),
    private val softDropUseCase: SoftDropUseCase = SoftDropUseCase(
        checkCollisionY = CheckCollisionYUseCase()
    ),

    application: Application,
) : AndroidViewModel(application = application) {
    val state = MutableStateFlow(GameViewModelState())

    init {
        state.update { it.copy(highScore = loadHighScore()) }
    }

    // viewModelの書き方のルール
    // ①ViewModel は UseCase を呼び、返り値で State（UIState）を更新
    // ②ViewModel内のものはUIで使うメソッドのみにしておく。ドメインロジックはすべて UseCase の中にいれる。
    // ③ViewModel ↔ UseCase の依存は一方向（ViewModel → UseCase）のみ
    // ④一つのfunには一つの動作

    // viewModelの書き方(反省)
    // ①viewModel内の値を変更する処理があるならviewModel内に書く
    // ②実際の処理の内容はUseCaseに書いていく
    // ③UseCase内でUseCaseを使わないようにしていく
    // ④UseCaseを定義するなら、viewModelのfunの中の順次処理の部品をまとめる要領で書く

    // !!は使わない方向
    // UseCase内でviewModelの関数を使わないようにしている
    // 役割としてはViewModelの状態を変更してUIに渡す関数を用意したりするが、他の人のレポジトリ見る限りprivateな関数を定義してもいいみたい。

    // MVVMでは、窓口であるviewModelでデータに対応するプロパティやメソッドをまとめてUIで使えるようにする。
    // つまり、UI側でboard.createBoardWithUpdateCellsとはせずにviewModelでまとめたものを使う。
    // UIがわで使うのはgameViewModel.createBoardWithUpdateCellsとする

    // state.update { it.copy() }はそのまま使っている。funに一つの関数としてまとめてカプセル化しないようにしている。
    // updateする関数が複数乱立したら見にくくなるので、今回のアプリに限っては、この方針。
    // ただし、UIで使うものはupdate関数を作っている。

    fun updateIsInitialized(newIsInitialized: Boolean) {
        state.update { it.copy(isInitialized = newIsInitialized) }
    }

    fun pause() {
        state.update { it.copy(isPaused = true) }
    }

    fun resume() {
        state.update { it.copy(lastTime = System.currentTimeMillis(), isPaused = false) }
    }

    fun swapHoldAndNext() {
        swapHoldUseCase(
            mino = state.value.tetriMino,
            tetriMinoList = state.value.tetriMinoList,
            currentIsSwapped = state.value.isSwapped
        )?.let { result ->
            state.update {
                it.copy(
                    tetriMino = result.newMino,
                    tetriMinoList = result.newMinoList,
                    isSwapped = true
                )
            }
            updateGhostMino()
        }
    }

    fun spawnTetriMino() {
        // テトリミノの一巡と次の操作するミノを呼び出して適用する
        val result = spawnMinoUseCase(state.value.tetriMinoList)
        val (nextMinoType, nextMinoList) = result
        state.update {
            it.copy(
                tetriMino = TetriMino(_type = nextMinoType),
                tetriMinoList = nextMinoList
            )
        }
        updateGhostMino()
        checkGameOverAndEnd()
        // ミノの生成時にミノがその位置にあればゲームオーバー
    }

    private fun checkGameOverAndEnd() {
        // state.value から現在のボードとテトリミノを取得
        val board = state.value.board
        val mino = state.value.tetriMino
        if (checkGameOverUseCase(board, mino)) {
            endGame()
        }
    }

    // UIから呼ぶものをviewModelの関数に置くので、checkAndClearLinesとcheckIsTSpinを統合してcheckAndClearLinesにした
    private fun processPlacement() {
        val (newBoard, newScore, newCombo) = processPlacementUseCase(
            state.value.board,
            state.value.tetriMino,
            state.value.score,
            state.value.comboCount
        )
        state.update { it.copy(board = newBoard, score = newScore, comboCount = newCombo) }
    }

    private fun updateGhostMino() {
        val stateValue = state.value
        val newGhostMino = computeGhostMinoUseCase(stateValue.tetriMino, stateValue.board)
        state.update {
            it.copy(ghostMino = newGhostMino)
        }
    }

    private fun endGame() {
        val newState = state.value.let {
            val newHighScore = maxOf(it.highScore, it.score)

            if (it.score > it.highScore) {
                saveHighScore(it.score)
            }

            it.copy(
                screenState = ScreenState.GameOver,
                highScore = newHighScore
            )
        }

        // updateの中で更新以外の処理入れないほうがいい
        state.update { newState }
    }

    fun setScreenState(screenState: ScreenState) {
        state.update {
            it.copy(screenState = screenState)
        }
    }

    fun initGame() {
        state.update {
            // ゲームの初期状態を新しい状態にコピーして返す
            it.copy(
                board = Board(), // 新しいボードを作成
                tetriMinoList = TetriMinoList(), // 新しいテトリミノリストを作成
                score = 0, // スコアをリセット
                comboCount = 0, // コンボカウントをリセット
                elapsedTime = 0L, // 時間をリセット
                isSwapped = false, // ホールドスワップフラグをリセット
                lastActionWasRotation = false // 最終アクションフラグをリセット
            )
        }
        // 状態更新後にミノを生成し、ゴーストミノを更新
        spawnTetriMino()
        updateGhostMino()
    }

    fun changeToMenu() {
        state.update {
            it.copy(screenState = ScreenState.Menu)
        }
    }

    // TODO: これRepositoryの中に入れた方がいい
    private fun loadHighScore(): Int {
        val context: Context = getApplication<Application>().applicationContext
        val prefs = context.getSharedPreferences("tetris_prefs", Context.MODE_PRIVATE)
        return prefs.getInt("high_score", 0)
    }

    private fun saveHighScore(score: Int) {
        val context: Context = getApplication<Application>().applicationContext
        // ここで保持されたデータはアプリを終了しても残るみたい
        // また、再ビルドしてもデータが残る
        // ただし、アンインストールしたりキャッシュを削除すると消える
        val prefs = context.getSharedPreferences("tetris_prefs", Context.MODE_PRIVATE)
        prefs.edit().putInt("high_score", score).apply()
    }

    fun onMoveX(sideX: SideX) {
        val stateValue = state.value
        val result = moveXUseCase(
            sideX = sideX,
            board = stateValue.board,
            mino = stateValue.tetriMino,
            currentProlongCount = stateValue.prolongTimeDelayCountLimit
        )

        if (!result.didMove || result.movedMino == null) return

        // 1. ミノ・ゴースト・フラグ更新
        state.update {
            it.copy(
                tetriMino = result.movedMino,
                ghostMino = computeGhostMinoUseCase(result.movedMino, state.value.board),
                lastActionWasRotation = false
            )
        }

        // 2. delay リセット＆prolong カウント更新
        if (result.didResetDelay) {
            state.update {
                it.copy(
                    timeDelay = 0L,
                    prolongTimeDelayCountLimit = state.value.prolongTimeDelayCountLimit + 1
                )
            }
        }
    }

    private fun onCollisionY() {

        val stateValue = state.value
        // fold ... Listの各要素を受け取って蓄積する
        // それぞれのセルにに対して処理を行う。
        // boardAccに更新された一つ一つのセルが入る
        val newBoard = generateLockCellsUseCase(stateValue.tetriMino)
            .fold(stateValue.board) { boardAcc, cell ->
                boardAcc.createBoardWithUpdateCells(newCell = cell)
            }

        state.update { it.copy(board = newBoard) }
        // ライン削除
        processPlacement()

        // 新しいミノの生成
        spawnTetriMino()
        val newMino = state.value.tetriMino
        state.update { it.copy(tetriMino = it.tetriMino.updateTetriMino(mino = newMino)) }

        // フラグの初期化
        val levelInfo = state.value.levelInfo[stateValue.level] ?: Pair(0L, 1000L)
        state.update {
            it.copy(
                timeDelay = 0L,
                isSwapped = false,
                lastActionWasRotation = false,
                prolongTimeDelayCountLimit = 0,
                delayLimit = levelInfo.second,
                level = it.level
            )
        }
        state.update { it.copy(delayLimit = levelInfo.second, level = it.level) }

        // アニメーションフラグ
        state.update { it.copy(hardDropTrigger = true) }
        viewModelScope.launch {
            delay(500)
            state.update { it.copy(hardDropTrigger = false) }
        }
    }

    fun onRotate(rotateDir: Int) {
        val stateValue = state.value
        val result = rotateMinoUseCase(
            rotateDir = rotateDir,
            mino = stateValue.tetriMino,
            board = stateValue.board,
            currentProlongCount = stateValue.prolongTimeDelayCountLimit
        )

        if (!result.didRotate || result.rotatedMino == null) return

        // 1) ミノとゴーストミノの更新
        state.update {
            it.copy(
                tetriMino = result.rotatedMino,
                ghostMino = computeGhostMinoUseCase(result.rotatedMino, it.board),
                lastActionWasRotation = true
            )
        }

        // 2) delay／prolongCount の更新
        if (result.didResetDelay) {
            state.update {
                it.copy(
                    timeDelay = 0L, prolongTimeDelayCountLimit = it.prolongTimeDelayCountLimit + 1
                )
            }
        }
    }

    fun onSoftDrop() {
        val stateValue = state.value
        val result = softDropUseCase(
            mino = stateValue.tetriMino,
            board = stateValue.board
        )

        // 下に衝突したら衝突処理
        if (result.didLock) {
            onCollisionY()
        }
        // そうでなければ一段下げたミノを反映
        else {
            state.update {
                it.copy(
                    tetriMino = result.newMino,
                    lastActionWasRotation = false,
                    timeDelay = 0L,
                )
            }
        }
    }

    fun hardDrop(ghostMino: TetriMino, mino: TetriMino) {
        val newMino = mino.copy(
            _position = ghostMino.position
        )
        state.update { it.copy(tetriMino = it.tetriMino.updateTetriMino(mino = newMino)) }
        onCollisionY()
        updateGhostMino()
    }

    suspend fun gravity(
    ) {
        val currentTime = System.currentTimeMillis()
        val lastTime = state.value.lastTime

        handleFalling(currentTime = currentTime)
        handleLevelUp()

        // 時間経過計算処理とdelayは一つのUIを変更する動作になっていないからそのままにしておいた
        state.update {
            it.copy(
                elapsedTime = it.elapsedTime.plus(currentTime - lastTime), lastTime = currentTime
            )
        }

        delay(16L) // 60fpsくらい
    }

    private fun handleFalling(currentTime: Long) {
        val stateValue = state.value
        val currentDelay = stateValue.timeDelay
        val delayLimit = stateValue.delayLimit
        val lastTime = stateValue.lastTime
        val mino = stateValue.tetriMino
        val board = stateValue.board
        // レベル1で時間が次のレベルの時間以上になったら
        // レベルを1上げる
        // そのときの落下スピードを決める

        // TODO: もしもすぐ下に壁やミノがあるなら時間延長したい
        if (currentDelay >= delayLimit) {
            // 壁への当たり判定
            val willCollideY: Boolean = checkCollisionYUseCase(board = board, mino = mino)

            if (willCollideY) {
                // 衝突するならそこにミノを設置して新しいミノを作成
                onCollisionY()
            } else {
                // 衝突してないならミノを一つ下に落とす
                onSoftDrop()

                // 早すぎると移動の前にミノが固定されてしまうので、防止する
                val isGrounded: Boolean =
                    checkCollisionYUseCase(board = state.value.board, mino = state.value.tetriMino)
                if (isGrounded && delayLimit < 200) {
                    state.update { it.copy(delayLimit = 200) }
                }
            }

            // TimeDelayを0にする
            state.update { it.copy(timeDelay = 0L) }

        } else {
            // TimeDelayにcurrentTime-lastTimeを足す
            state.update { it.copy(timeDelay = currentDelay + currentTime - lastTime) }
        }
    }

    private fun handleLevelUp() {
        val time = state.value.elapsedTime
        val level = state.value.level

        val nextLevelInfo = state.value.levelInfo[level + 1] ?: return

        if (time >= (nextLevelInfo.first)
        ) {
            state.update { it.copy(delayLimit = nextLevelInfo.second, level = it.level + 1) }
        }
    }
}