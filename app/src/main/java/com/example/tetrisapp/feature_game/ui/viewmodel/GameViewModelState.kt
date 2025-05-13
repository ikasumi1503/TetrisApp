package com.example.tetrisapp.feature_game.ui.viewmodel

import com.example.tetrisapp.feature_game.domain.entity.Board
import com.example.tetrisapp.feature_game.domain.entity.TetriMino
import com.example.tetrisapp.feature_game.domain.entity.TetriMinoList
import com.example.tetrisapp.feature_game.domain.model.MinoType
import com.example.tetrisapp.feature_game.ui.ScreenState
import com.example.tetrisapp.feature_game.util.LevelConstants

data class GameViewModelState(
    // ここで外部から値を取得するためのプロパティを作る
    // LiveDataは変更があったら自動的にUIにデータの内容を反映させてくれる型
    val board: Board = Board(),
    val tetriMinoList: TetriMinoList = TetriMinoList(),
    val tetriMino: TetriMino = TetriMino(
        _type = MinoType.T
    ),
    val ghostMino: TetriMino = TetriMino(
        _type = MinoType.T
    ),
    val isSwapped: Boolean = false,
    val score: Int = 0,
    val comboCount: Int = 0,
    val lastActionWasRotation: Boolean = false,
    val screenState: ScreenState = ScreenState.Game,
    val highScore: Int = 0,
    val prolongTimeDelayCountLimit: Int = 0,
    val timeDelay: Long = 0L,
    val isPaused: Boolean = false,
    val elapsedTime: Long = 0L,
    val delayLimit: Long = LevelConstants.levelsInfo[1]?.second ?: 1000L,
    val level: Int = 1,
    val lastTime: Long = System.currentTimeMillis(),
    val levelInfo: Map<Int, Pair<Long, Long>> = LevelConstants.levelsInfo
)