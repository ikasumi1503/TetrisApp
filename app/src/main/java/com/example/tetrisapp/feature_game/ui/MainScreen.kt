package com.example.tetrisapp.feature_game.ui

import androidx.compose.runtime.Composable

enum class ScreenState {
    Menu,
    Game,
    GameOver
}

@Composable
fun MainScreen(gameViewModel: GameViewModel){
    // TODO: メニュー画面の作成
    val screenState = gameViewModel.screenState.value // Unwrap the LiveData

    screenState?.let { // Ensure screenState is not null
        when (it) {
            ScreenState.Game -> GameScreen(gameViewModel = gameViewModel)
            ScreenState.GameOver -> GameScreen(gameViewModel = gameViewModel)
            ScreenState.Menu -> GameScreen(gameViewModel = gameViewModel)
        }
    }
}