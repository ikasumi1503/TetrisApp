package com.example.tetrisapp.feature_game.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.State
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tetrisapp.feature_game.domain.entity.Board
import com.example.tetrisapp.feature_game.domain.entity.Cell
import com.example.tetrisapp.feature_game.domain.entity.TetriMino
import com.example.tetrisapp.feature_game.domain.entity.TetriMinoList
import com.example.tetrisapp.feature_game.domain.model.MinoType
import com.example.tetrisapp.feature_game.domain.usecase.CheckAndClearLinesUseCase
import com.example.tetrisapp.feature_game.domain.usecase.CheckCollisionXUseCase
import com.example.tetrisapp.feature_game.domain.usecase.CheckCollisionYUseCase
import com.example.tetrisapp.feature_game.domain.usecase.CheckGameOverUseCase
import com.example.tetrisapp.feature_game.domain.usecase.CheckIsTSpinUseCase
import com.example.tetrisapp.feature_game.domain.usecase.ComputeGhostMinoUseCase
import com.example.tetrisapp.feature_game.domain.usecase.OnCollisionYUseCase
import com.example.tetrisapp.feature_game.domain.usecase.SideX
import com.example.tetrisapp.feature_game.domain.usecase.SideXToNumUseCase
import com.example.tetrisapp.feature_game.domain.usecase.SpawnMinoUseCase
import com.example.tetrisapp.feature_game.domain.usecase.SwapHoldUseCase
import com.example.tetrisapp.feature_game.ui.ScreenState
import com.example.tetrisapp.feature_game.util.GameConstants
import com.example.tetrisapp.feature_game.util.LevelConstants
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


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


    application: Application,
) : AndroidViewModel(application = application) {
    private val levelInfo = LevelConstants.levelsInfo

    // ここでuiで使うプロパティをどんどん入れていく

    // privateにして直接変更及び取得ができないようにする
    // ViewModelはデータを入れる箱で、LiveDataはそのデータを反映させる液晶の役割
    private val _board = MutableLiveData(Board())
    private val _tetriMinoList = MutableLiveData(TetriMinoList())
    private val _tetriMino = MutableLiveData(TetriMino(_type = MinoType.T))
    private val _ghostMino = MutableLiveData<TetriMino>()
    private val _isSwapped = MutableLiveData<Boolean>(false)
    private val _score = MutableLiveData<Int>(0)
    private val _comboCount = MutableLiveData(0)
    private val _lastActionWasRotation = MutableLiveData(false)
    private val _screenState = MutableLiveData(ScreenState.Game)
    private val _isPaused = MutableLiveData(false)
    private val _time = MutableLiveData(0L)
    private val _delayLimit = MutableLiveData(levelInfo[1]?.second ?: 1000L)
    private val _level = MutableLiveData(1)

    // mutableStateOfでもいいかも
    private val _highScore = MutableLiveData(0)
    private val _prolongTimeDelayCountLimit = MutableLiveData<Int>(0)
    private val _timeDelay = MutableStateFlow<Long>(0)

    // ここで外部から値を取得するためのプロパティを作る
    // LiveDataは変更があったら自動的にUIにデータの内容を反映させてくれる型
    val board: LiveData<Board> = _board
    val tetriMinoList: LiveData<TetriMinoList> = _tetriMinoList
    val tetriMino: LiveData<TetriMino> = _tetriMino
    val ghostMino: LiveData<TetriMino> = _ghostMino
    val isSwapped: LiveData<Boolean> = _isSwapped
    val score: LiveData<Int> = _score
    val comboCount: LiveData<Int> = _comboCount
    val lastActionWasRotation: LiveData<Boolean> = _lastActionWasRotation
    val screenState: LiveData<ScreenState> = _screenState
    val highScore: LiveData<Int> = _highScore
    val prolongTimeDelayCountLimit: LiveData<Int> = _prolongTimeDelayCountLimit
    val timeDelay: StateFlow<Long> = _timeDelay
    val isPaused: LiveData<Boolean> = _isPaused
    val time: LiveData<Long> = _time
    val delayLimit: LiveData<Long> = _delayLimit
    val level: LiveData<Int> = _level

    init {
        _highScore.value = loadHighScore()
    }

    // MVVM(一つの場所に一つの責任)の原則的に、窓口であるviewModelでデータに対応するプロパティやメソッドをまとめてUIで使えるようにする。
    // つまり、UI側でboard.createBoardWithUpdateCellsとはせずにviewModelでまとめたものを使う。
    // UIがわで使うのはgameViewModel.createBoardWithUpdateCellsとする
    fun createBoardWithUpdateCells(cell: Cell) {
        // newCell = cell じゃなくてcellをそのまま入れてもいいけど、newCell = cellのほうが分かりやすい・統一感ある
        _board.value = _board.value?.createBoardWithUpdateCells(newCell = cell)
    }

    // 単純なものはviewModelでOK
    fun pause() {
        _isPaused.value = true
    }

    fun resume() {
        _isPaused.value = false
    }

    fun updateTetriMino(mino: TetriMino) {
        _tetriMino.value = _tetriMino.value?.updateTetriMino(mino = mino)
    }

    fun updateTimeDelay(newTimeDelay: Long) {
        _timeDelay.value = newTimeDelay
    }

    fun swapHoldAndNext() {
        val current = _tetriMino.value ?: return
        val list = _tetriMinoList.value ?: return
        val swapped = _isSwapped.value ?: false

        swapHoldUseCase(
            mino = current,
            tetriMinoList = list,
            currentIsSwapped = swapped
        )?.let { result ->
            _tetriMino.value = result.newMino
            _tetriMinoList.value = result.newMinoList
            updateIsSwapped(true)
            updateGhostMino()
        }
    }

    fun updateIsSwapped(updatedIsSwapped: Boolean) {
        _isSwapped.value = updatedIsSwapped
    }

    fun spawnTetriMino() {
        // テトリミノの一巡と次の操作するミノを呼び出して適用する
        val result = _tetriMinoList.value?.let { spawnMinoUseCase(it) }
        if (result != null) {
            val (nextMinoType, nextMinoList) = result
            _tetriMino.value = TetriMino(_type = nextMinoType)
            _tetriMinoList.value = nextMinoList
        }
        updateGhostMino()

        // ミノの生成時にミノがその位置にあればゲームオーバー
        val board = _board.value
        val mino = _tetriMino.value
        if (checkGameOverUseCase(board, mino)) {
            endGame()
        }
    }

    fun checkAndClearLines() {
        // _boardの中身はLiveDataでnullになる可能性があるのでletを使ってnullになるかもしれませんよ、と書かないといけない
        val board = _board.value ?: return
        val mino = _tetriMino.value ?: return
        val (newBoard, linesCount) = checkAndClearLinesUseCase(board)
        val isTSpinPerformed = checkIsTSpinUseCase(mino = mino, board = board)

        calculateScore(linesCleared = linesCount, isTSpinPerformed = isTSpinPerformed)
        _board.value = newBoard
    }

    fun updateGhostMino() {
        // ?: return ... もしもnullならreturn してね、という意味
        val mino = _tetriMino.value ?: return
        val board = _board.value ?: return
        val computeGhostMinoUseCase = ComputeGhostMinoUseCase()
        _ghostMino.value = computeGhostMinoUseCase(mino, board)
    }

    fun calculateScore(linesCleared: Int, isTSpinPerformed: Boolean): Int {
        if (linesCleared <= 0) {
            _comboCount.value = 0
            return _score.value ?: 0 // 変更なしの場合は現在のスコアを返す
        }

        val scoreIncrement = when {
            isTSpinPerformed && linesCleared == 1 -> 500
            isTSpinPerformed && linesCleared == 2 -> 800
            isTSpinPerformed && linesCleared == 3 -> 1200

            linesCleared == 1 -> 100
            linesCleared == 2 -> 300
            linesCleared == 3 -> 500
            linesCleared == 4 -> 800
            else -> 0
        }

        val updatedScore = (_score.value ?: 0) + scoreIncrement + (_comboCount.value ?: 0) * 50
        _score.value = updatedScore
        _comboCount.value = (_comboCount.value ?: 0) + 1

        return updatedScore
    }

    fun markRotation(mark: Boolean) {
        _lastActionWasRotation.value = mark
    }

    fun endGame() {
        val score = _score.value
        val highScore = _highScore.value
        if (score != null && highScore != null) {
            if (score > highScore) {
                _highScore.value = score
                saveHighScore(score)
            }
        }

        _screenState.value = ScreenState.GameOver
    }

    fun setScreenState(state: ScreenState) {
        _screenState.value = state
    }

    fun initGame() {
        _board.value = Board()
        _tetriMinoList.value = TetriMinoList()
        _score.value = 0
        _comboCount.value = 0
        _isSwapped.value = false
        _lastActionWasRotation.value = false
        spawnTetriMino()
        updateGhostMino()
    }

    fun changeToMenu() {
        _screenState.value = ScreenState.Menu
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
        _prolongTimeDelayCountLimit.value = count
    }

    fun moveX(
        sideX: SideX,
        board: Board,
        mino: TetriMino,
        prolongTimeDelayCountLimit: State<Int>
    ) { // TODO: useCaseにしておく
        val sideToNumUseCase = SideXToNumUseCase()
        val sideToNum = sideToNumUseCase(sideX = sideX)

        val checkCollisionXUseCase = CheckCollisionXUseCase()
        // CheckCollisionXUseCaseのなかのwhenが分かりやすいのでsideToNumではなくsideを渡している
        val willCollideX = checkCollisionXUseCase(board = board, mino = mino, sideX = sideX)

        // 壁やミノと当たったら動かさない
        if (willCollideX) {
            return
        } else {
            val x = mino.position.first
            val y = mino.position.second
            // なににもあたらなければ左に動かす
            val newMino = mino.copy(
                _position = Pair(
                    x + sideToNum, y
                )
            )
            updateTetriMino(newMino)
            updateGhostMino()
            markRotation(false)

            // TODO: ここuseCaseでまとめた方がいいかも
            // 接地時点で操作したら落下しない処理
            val checkCollisionYUseCase = CheckCollisionYUseCase()
            val willCollideY = checkCollisionYUseCase(board = board, mino = mino)
            if (willCollideY && prolongTimeDelayCountLimit.value <= 10) {
                updateTimeDelay(0)
                setProlongTimeDelayCountLimit(prolongTimeDelayCountLimit.value + 1)
            }

        }

    }

    fun rotate(rotateDir: Int, mino: TetriMino, board: Board) {
        // 左回転の時でmino.rotation=0の時、newRotationが+3になってほしいので、mino.type.shapes.sizeを足しておく
        val newRotation =
            (mino.type.shapes.size + mino.rotation + rotateDir) % mino.type.shapes.size
        val rotatedMino = mino.copy(_rotation = newRotation)

        // SRSルールというテトリミノの回転ルールを適用している
        // https://tetrisch.github.io/main/srs.html

        val key = Pair(mino.rotation, rotatedMino.rotation)

        val kickOffsets = if (mino.type == MinoType.I)
            GameConstants.I_KickTable[key] ?: error("Missing kick data for I: $key")
        else
            GameConstants.JLSTZ_KickTable[key] ?: error("Missing kick data for JLSTZ: $key")


        // それぞれのオフセットを適用
        for (kickOffset in kickOffsets) {
            val newPosition = Pair(
                mino.position.first + kickOffset.first, mino.position.second + kickOffset.second
            )

            // オフセットを適用したとき、回転後のミノで壁やミノと被っているものがないか確認
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

                // 接地時点で回転したら落下しない処理
                val checkCollisionYUseCase = CheckCollisionYUseCase()
                val willCollideY =
                    checkCollisionYUseCase(board = board, mino = kickedRotatedMino)
                if (willCollideY && prolongTimeDelayCountLimit.value <= 10) {
                    updateTimeDelay(0)
                    setProlongTimeDelayCountLimit(prolongTimeDelayCountLimit.value + 1)
                }
                updateTetriMino(kickedRotatedMino)
                updateGhostMino()
                markRotation(true)
                break
            }
        }


    }

    fun softDrop(mino: TetriMino, board: Board) {
        // 壁への当たり判定
        val checkCollisionYUseCase = CheckCollisionYUseCase()
        val willCollideY: Boolean = checkCollisionYUseCase(board = board, mino = mino)

        if (willCollideY) {
            // 衝突するならそこにミノを設置して新しいミノを作成
            val onCollisionYUseCase = OnCollisionYUseCase(gameViewModel = this)
            onCollisionYUseCase(mino = mino)
        } else {
            // 衝突してないならミノを一つ下に落とす
            val newMino = mino.copy(
                _position = Pair(mino.position.first, mino.position.second + 1)
            )
            updateTetriMino(newMino)
            markRotation(false)
        }

        updateTimeDelay(0)
    }

    fun hardDrop(ghostMino: TetriMino, mino: TetriMino) {
        val newMino = mino.copy(
            _position = ghostMino.position
        )
        updateTetriMino(newMino)
        OnCollisionYUseCase(gameViewModel = this)
        updateGhostMino()
        updateTimeDelay(1000L)
    }

    suspend fun gravity(
        timeDelay: StateFlow<Long>,
        board: Board,
        mino: TetriMino,
        lastTime: MutableLongState
    ) {

        val currentTime = System.currentTimeMillis()
        // StateFlowで実装した。
        // 最初はファイルの上部にvalueやobserveAsStateでアクセスしていたけど、ループ内でviewModelで取得した初期値がcurrentDelayに入っていた。
        // つまり、最初の値が参照されていて変更が検知されなかった。
        // minoを読み込むときにはオブジェクトの値を読みに行ってて、ミノの生成ごとにそれに対応するオブジェクトが生成されていたから、読み込むことができていた
        val currentDelay = timeDelay.value
        val delayLimit = _delayLimit.value ?: return
        val time = _time.value ?: return
        val level = _level.value ?: return

        println(delayLimit)

        if (currentDelay >= delayLimit
        ) {

            // 壁への当たり判定
            val checkCollisionYUseCase = CheckCollisionYUseCase()
            val willCollideY: Boolean = checkCollisionYUseCase(board = board, mino = mino)

            if (willCollideY) {
                // 衝突するならそこにミノを設置して新しいミノを作成
                val onCollisionYUseCase = OnCollisionYUseCase(gameViewModel = this)
                onCollisionYUseCase(mino = mino)
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
            updateTimeDelay(currentDelay + currentTime - lastTime.longValue)
        }

        // レベル1で時間が20秒以上になったら
        // レベルを1上げる
        // そのときの落下スピードを決める
        if (time >= (levelInfo[level]?.first ?: 0L) && level < levelInfo.size + 1) {
            _delayLimit.value = levelInfo[level + 1]?.second ?: 1000L
            _level.value = level + 1
        }

        _time.value = _time.value?.plus(currentTime - lastTime.longValue)
        lastTime.longValue = currentTime

        delay(16L) // 60fpsくらい
    }
}