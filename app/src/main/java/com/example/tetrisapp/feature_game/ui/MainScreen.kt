package com.example.tetrisapp.feature_game.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import com.example.tetrisapp.feature_game.ui.viewmodel.LocalGameViewModel


enum class ScreenState {
    Menu,
    Game,
    GameOver
}

@Composable
fun MainScreen() {
    val gameSessionId = remember { mutableIntStateOf(0) }
    val gameViewModel = LocalGameViewModel.current
    val screenState = gameViewModel.screenState.observeAsState(ScreenState.Menu).value

    screenState.let {
        // このitはscreenStateを参照している
        when (it) {
            ScreenState.Game -> key(gameSessionId) {
                GameScreen(gameViewModel = gameViewModel)
            }

            ScreenState.GameOver -> GameOverScreen(gameViewModel = gameViewModel)
            ScreenState.Menu -> MenuScreen(gameViewModel = gameViewModel, onStartGame = {
                gameViewModel.initGame()
                gameSessionId.intValue++
                gameViewModel.setScreenState(ScreenState.Game)
            })
        }
    }
}