package com.example.tetrisapp.feature_game.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tetrisapp.feature_game.domain.entity.Board
import com.example.tetrisapp.feature_game.domain.entity.Cell
import com.example.tetrisapp.feature_game.domain.entity.TetriMino
import com.example.tetrisapp.feature_game.domain.model.MinoType


// なぜuiにviewModelを置いているのか？
// ui状態の管理をしたり、ドメインロジックを呼び出すため、ドメインとの橋渡しをするのでuiに置いている
// ViewModelはAndroidに依存しているので、純粋なロジックのみではないから

//　ViewModelの役割は？
// uiが欲しい状態を整えてあげる中間役
class GameViewModel : ViewModel() {

    // ここでuiで使うプロパティをどんどん入れていく

    // privateにして直接変更及び取得ができないようにする
    // ViewModelはデータを入れる箱で、LiveDataはそのデータを反映させる液晶の役割
    private val _board = MutableLiveData(Board())
    private val _tetriMino = MutableLiveData(TetriMino(_type = MinoType.T)) // TODO: 初期値をどこかでランダムに代入したい


    // ここで外部から値を取得するためのプロパティを作る
    // LiveDataは変更があったら自動的にUIにデータの内容を反映させてくれる型
    val board: LiveData<Board> = _board
    val tetriMino: LiveData<TetriMino> = _tetriMino

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
}