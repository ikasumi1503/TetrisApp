package com.example.tetrisapp.feature_game.ui.viewmodel

import GenerateLockCellsUseCase
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.tetrisapp.feature_game.domain.entity.Board
import com.example.tetrisapp.feature_game.domain.entity.Cell
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


// なぜuiにviewModelを置いているのか？
// ui状態の管理をしたり、ドメインロジックを呼び出すため、ドメインとの橋渡しをするのでuiに置いている
// ViewModelはAndroidに依存しているので、純粋なロジックのみではないから

//　ViewModelの役割は？
// uiが欲しい状態を整えてあげる中間役
// TODO: 今はUI(View) → ViewModelまではいいんだけど、UseCase → Repository → データ層がごちゃごちゃになってるから、
// TODO: UI(View) → ViewModel → UseCase → Repository → データ層 にしておく
class GameViewModel(
    // TODO: 直接渡すと
    private val checkAndClearLinesUseCase: CheckAndClearLinesUseCase = CheckAndClearLinesUseCase(),
    private val swapHoldUseCase: SwapHoldUseCase = SwapHoldUseCase(),
    private val checkCollisionYUseCase: CheckCollisionYUseCase = CheckCollisionYUseCase(),
    private val spawnMinoUseCase: SpawnMinoUseCase = SpawnMinoUseCase(),
    private val checkGameOverUseCase: CheckGameOverUseCase = CheckGameOverUseCase(
        checkCollisionY = checkCollisionYUseCase
    ),
    private val checkIsTSpinUseCase: CheckIsTSpinUseCase = CheckIsTSpinUseCase(),
    private val calculateScoreUseCase: CalculateScoreUseCase = CalculateScoreUseCase(),
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

    // TODO: UseCase使う
    // TODO: 使う値を引数から持ってくる

    // viewModelの書き方のルール
    // ①ViewModel は UseCase を呼び、返り値で State（UIState）を更新
    // ②ViewModel内のものはUIで使うメソッドのみにしておく。ドメインロジックはすべて UseCase の中にいれる。
    // ③UseCase 同士の組み合わせはドメイン層（UseCase内）で OK
    // ④ViewModel ↔ UseCase の依存は一方向（ViewModel → UseCase）のみ
    // ⑤一つのfunには一つの役割

    // viewModelの書き方(反省)
    // ①viewModel内の値を変更する処理があるならviewModel内に書く
    // ②実際の処理の内容はUseCaseに書いていく
    // ③大きなUseCaseどうしの共通処理など、小さな部品が作りたいなら、小さなUseCaseを作る

    // !!は使わない方向
    // UseCase内でviewModelの関数を使わないようにしている

    // MVVM(一つの場所に一つの責任)の原則的に、窓口であるviewModelでデータに対応するプロパティやメソッドをまとめてUIで使えるようにする。
    // つまり、UI側でboard.createBoardWithUpdateCellsとはせずにviewModelでまとめたものを使う。
    // UIがわで使うのはgameViewModel.createBoardWithUpdateCellsとする
    fun createBoardWithUpdateCells(cell: Cell) {
        // newCell = cell じゃなくてcellをそのまま入れてもいいけど、newCell = cellのほうが分かりやすい・統一感ある
        state.update { it.copy(board = it.board.createBoardWithUpdateCells(newCell = cell)) }
    }

    // 単純なものはviewModelでOK
    fun pause() {
        state.update { it.copy(isPaused = true) }
    }

    fun resume() {
        state.update { it.copy(lastTime = System.currentTimeMillis(), isPaused = false) }
    }

    fun updateTetriMino(mino: TetriMino) {
        state.update { it.copy(tetriMino = it.tetriMino.updateTetriMino(mino = mino)) }
    }

    fun updateTimeDelay(newTimeDelay: Long) {
        state.update { it.copy(timeDelay = newTimeDelay) }

    }

    fun swapHoldAndNext() {
        swapHoldUseCase(
            mino = state.value.tetriMino,
            tetriMinoList = state.value.tetriMinoList,
            currentIsSwapped = state.value.isSwapped
        )?.let { result ->
            state.update { it.copy(tetriMino = result.newMino, tetriMinoList = result.newMinoList) }
            updateIsSwapped(true)
            updateGhostMino()
        }
    }

    fun updateIsSwapped(updatedIsSwapped: Boolean) {
        state.update { it.copy(isSwapped = updatedIsSwapped) }
    }

    fun spawnTetriMino() {
        // テトリミノの一巡と次の操作するミノを呼び出して適用する
        val result = spawnMinoUseCase(state.value.tetriMinoList)
        val (nextMinoType, nextMinoList) = result
        state.update { it.copy(tetriMino = TetriMino(_type = nextMinoType)) }
        state.update { it.copy(tetriMinoList = nextMinoList) }
        updateGhostMino()
        checkGameOverAndEnd()
        // ミノの生成時にミノがその位置にあればゲームオーバー
    }

    fun checkGameOverAndEnd() {
        // state.value から現在のボードとテトリミノを取得
        val board = state.value.board
        val mino = state.value.tetriMino
        if (checkGameOverUseCase(board, mino)) {
            endGame()
        }
    }

    // UIから呼ぶものをviewModelの関数に置くので、checkAndClearLinesとcheckIsTSpinを統合してcheckAndClearLinesにした
    fun processPlacement() {
        val board = state.value.board
        val mino = state.value.tetriMino
        val currentScore = state.value.score
        val currentCombo = state.value.comboCount
        val (newBoard, newScore, newCombo) = processPlacementUseCase(
            board, mino, currentScore, currentCombo
        )

        state.update { it.copy(board = newBoard, score = newScore, comboCount = newCombo) }
    }

    fun updateGhostMino() {
        state.update {
            it.copy(ghostMino = computeGhostMinoUseCase(it.tetriMino, it.board))
        }
    }

    fun markRotation(mark: Boolean) {
        state.update {
            it.copy(lastActionWasRotation = mark)
        }
    }

    fun endGame() {
        state.update {
            var updatedHighScore = it.highScore // highScore を更新する可能性があるため var にする

            if (it.score > it.highScore) {
                updatedHighScore = it.score // ハイスコアを更新
                saveHighScore(it.score) // ハイスコアを保存
            }

            // ゲームオーバー状態と更新されたハイスコアを新しい状態にコピーして返す
            it.copy(
                screenState = ScreenState.GameOver, highScore = updatedHighScore
            )
        }
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

    fun setProlongTimeDelayCountLimit(count: Int) {
        state.update {
            it.copy(prolongTimeDelayCountLimit = count)
        }
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

        // 1. ミノ位置更新
        state.update { it.copy(tetriMino = result.movedMino) }

        // 2. ゴーストミノ更新
        state.update {
            it.copy(
                ghostMino = computeGhostMinoUseCase(result.movedMino, state.value.board)
            )
        }

        // 3. 回転フラグリセット
        state.update { it.copy(lastActionWasRotation = false) }

        // 4. delay リセット＆prolong カウント更新
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
        updateTetriMino(newMino)

        // フラグの初期化
        setProlongTimeDelayCountLimit(0)
        markRotation(false)
        updateIsSwapped(false)
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

        // ② ロックダウンされたら一連の処理へ
        if (result.didLock) {
            onCollisionY()    // 既存のロックダウン処理メソッドを呼ぶ
        }
        // ③ そうでなければ一段下げたミノを反映
        else {
            state.update {
                it.copy(
                    tetriMino = result.newMino!!,
                    lastActionWasRotation = false,
                    timeDelay = 0L   // drop ごとに delay リセット
                )
            }
        }
    }

    fun hardDrop(ghostMino: TetriMino, mino: TetriMino) {
        val newMino = mino.copy(
            _position = ghostMino.position
        )
        updateTetriMino(newMino)
        onCollisionY()
        updateGhostMino()
        updateTimeDelay(1000L)
    }

    suspend fun gravity(
        // TODO: 内容をUseCaseに切り出す
    ) {
        // TODO: もしもすぐ下に壁やミノがあるなら時間延長
        val currentTime = System.currentTimeMillis()
        // StateFlowで実装した。
        // 最初はファイルの上部にvalueやobserveAsStateでアクセスしていたけど、ループ内でviewModelで取得した初期値がcurrentDelayに入っていた。
        // つまり、最初の値が参照されていて変更が検知されなかった。
        // minoを読み込むときにはオブジェクトの値を読みに行ってて、ミノの生成ごとにそれに対応するオブジェクトが生成されていたから、読み込むことができていた
        val currentDelay = state.value.timeDelay
        val delayLimit = state.value.delayLimit
        val time = state.value.elapsedTime
        val level = state.value.level
        val lastTime = state.value.lastTime
        val mino = state.value.tetriMino
        val board = state.value.board

        if (currentDelay >= delayLimit) {
            // 壁への当たり判定
            val willCollideY: Boolean = checkCollisionYUseCase(board = board, mino = mino)

            if (willCollideY) {
                // 衝突するならそこにミノを設置して新しいミノを作成
                onCollisionY()
            } else {
                // 衝突してないならミノを一つ下に落とす
                val newMino = mino.copy(
                    _position = Pair(mino.position.first, mino.position.second + 1)
                )
                updateTetriMino(newMino)
                markRotation(false)
            }

            // TimeDelayを0にする
            updateTimeDelay(0)
        } else {
            // TimeDelayにcurrentTime-lastTimeを足す
            updateTimeDelay(currentDelay + currentTime - lastTime)
        }

        // レベル1で時間が20秒以上になったら
        // レベルを1上げる
        // そのときの落下スピードを決める
        if (time >= (state.value.levelInfo[level]?.first
                ?: 0L) && level < state.value.levelInfo.size + 1
        ) {
            val nextLevelInfo = state.value.levelInfo[level + 1] ?: Pair(0L, 1000L)
            state.update { it.copy(delayLimit = nextLevelInfo.second, level = it.level + 1) }
        }

        state.update {
            it.copy(
                elapsedTime = it.elapsedTime.plus(currentTime - lastTime), lastTime = currentTime
            )
        }

        delay(16L) // 60fpsくらい
    }
}