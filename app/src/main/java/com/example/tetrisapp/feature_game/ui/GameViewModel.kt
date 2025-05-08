package com.example.tetrisapp.feature_game.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tetrisapp.feature_game.domain.entity.Board
import com.example.tetrisapp.feature_game.domain.entity.Cell
import com.example.tetrisapp.feature_game.domain.entity.TetriMino
import com.example.tetrisapp.feature_game.domain.entity.TetriMinoList
import com.example.tetrisapp.feature_game.domain.model.MinoType
import com.example.tetrisapp.feature_game.domain.usecase.CheckAndClearLinesUseCase
import com.example.tetrisapp.feature_game.domain.usecase.CheckCollisionYUseCase
import com.example.tetrisapp.feature_game.domain.usecase.CheckIsTSpinUseCase
import com.example.tetrisapp.feature_game.domain.usecase.ComputeGhostMinoUseCase


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
    application: Application,
) : AndroidViewModel(application = application) {

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
    // mutableStateOfでもいいかも
    private val _highScore = MutableLiveData(0)
    private val _prolongTimeDelayCountLimit = MutableLiveData<Int>(0)

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
    val highScore : LiveData<Int> = _highScore
    val prolongTimeDelayCountLimit : LiveData<Int> = _prolongTimeDelayCountLimit

    init {
        _highScore.value = loadHighScore()
    }

    // MVVM(一つの場所に一つの責任)の原則的に、窓口であるviewModelでデータに対応するプロパティやメソッドをまとめてUIで使えるようにする。
    // つまり、UI側でboard.createBoardWithUpdateCellsとはせずにviewModelでまとめたものを使う。
    // UIがわで使うのはgameViewModel.createBoardWithUpdateCellsとする
    fun createBoardWithUpdateCells(cell: Cell){
        // newCell = cell じゃなくてcellをそのまま入れてもいいけど、newCell = cellのほうが分かりやすい・統一感ある
        _board.value = _board.value?.createBoardWithUpdateCells(newCell = cell)
    }

    fun updateTetriMino(mino: TetriMino){
        _tetriMino.value = _tetriMino.value?.updateTetriMino(mino = mino)
    }

    fun swapHoldAndNext() {
        if(isSwapped.value == true) return
        val currentMino = requireNotNull(_tetriMino.value) { "Current tetri mino is null!" }
        val minoType = _tetriMinoList.value?.tetriMinoList?.getOrNull(0)
            ?: throw IllegalStateException("Mino list is empty!")

        _tetriMinoList.value = _tetriMinoList.value?.swapHoldAndNext(mino = currentMino)
        _tetriMino.value = TetriMino(_type = minoType)
        updateIsSwapped(true)
        updateGhostMino()
    }

    fun updateIsSwapped(updatedIsSwapped: Boolean){
        _isSwapped.value = updatedIsSwapped
    }

    fun spawnTetriMino(){
        // テトリミノの一巡と次の操作するミノを呼び出して適用する
        val result = _tetriMinoList.value?.spawnTetriMino()
        if(result != null){
            val (nextMinoType, nextMinoList) = result
            _tetriMino.value = TetriMino(_type = nextMinoType)
            _tetriMinoList.value = nextMinoList
        }
        updateGhostMino()

        // ミノの生成時にミノがその位置にあればゲームオーバー
        val checkCollisionYUseCase = CheckCollisionYUseCase()
        val board = _board.value
        val mino = _tetriMino.value
        if(board != null && mino != null){
            val isCollide = checkCollisionYUseCase(
                board = board,
                mino = mino.copy(_position = Pair(mino.position.first, mino.position.second - 1))
            )
            if(isCollide){
                endGame()
            }
        }
    }

    fun checkAndClearLines(){
        // _boardの中身はLiveDataでnullになる可能性があるのでletを使ってnullになるかもしれませんよ、と書かないといけない
        val board = _board.value ?: return
        val mino = _tetriMino.value ?: return
        val (newBoard, linesCount) = checkAndClearLinesUseCase(board)
        _board.value = newBoard
        val checkIsTSpinUseCase = CheckIsTSpinUseCase()
        val isTSpinPerformed = checkIsTSpinUseCase(mino = mino, board = board)
        calculateScore(linesCleared = linesCount, isTSpinPerformed = isTSpinPerformed)
    }

    fun updateGhostMino(){
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
        if(score != null && highScore != null){
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

    fun changeToMenu(){
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

    fun setProlongTimeDelayCountLimit(count: Int){
        _prolongTimeDelayCountLimit.value = count
    }
}