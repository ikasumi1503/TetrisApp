package com.example.tetrisapp.feature_game.ui

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tetrisapp.app.GameViewModelFactory


enum class ScreenState {
    Menu,
    Game,
    GameOver
}

@Composable
fun MainScreen(){
    val gameSessionId = remember { mutableIntStateOf(0) }

    // 新しいゲームを始める時にkeyを指定してviewModelを新規作成するけど、毎回古いものは破棄するので重たくならないらしい。
    // 本来はMainActivityでviewModelsを使いたかったけど、@Composableの中でしかviewModelが使えなかったので、こちらに置いた
    val gameViewModel: GameViewModel = viewModel(
        key = "GameViewModel-${gameSessionId.intValue}",
        factory = GameViewModelFactory(LocalContext.current.applicationContext as Application)
    )
    val screenState = gameViewModel.screenState.observeAsState().value

    screenState?.let {
        when (it) {
            ScreenState.Game -> key (gameSessionId){
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