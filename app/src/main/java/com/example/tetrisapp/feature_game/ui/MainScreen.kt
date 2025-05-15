package com.example.tetrisapp.feature_game.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.tetrisapp.R
import com.example.tetrisapp.feature_game.ui.viewmodel.LocalGameKey
import com.example.tetrisapp.feature_game.ui.viewmodel.LocalGameKeyUpdater
import com.example.tetrisapp.feature_game.ui.viewmodel.LocalGameViewModel


enum class ScreenState {
    Menu,
    Game,
    GameOver
}

@Composable
fun MainScreen() {
    val gameKey = LocalGameKey.current
    val gameKeyUpdater = LocalGameKeyUpdater.current
    val gameSessionId = remember { mutableIntStateOf(0) }
    val gameViewModel = LocalGameViewModel.current
    val screenState = gameViewModel.state.collectAsState().value.screenState

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Imageを最初に置くと、後から入る要素は上に重ねられていく
        Image(
            painter = painterResource(id = R.drawable.pia07906_large),
            contentDescription = null,
            // ContentScale.Crop ... 縦横比を保ちつつ、View全体を覆うように拡大・切り取りする
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }

    screenState.let {
        // このitはscreenStateを参照している
        when (it) {
            ScreenState.Game -> key(gameSessionId) {
                GameScreen(gameViewModel = gameViewModel)
            }

            ScreenState.GameOver -> GameOverScreen(gameViewModel = gameViewModel)
            ScreenState.Menu -> MenuScreen(gameViewModel = gameViewModel, onStartGame = {
                gameKeyUpdater(gameKey + 1)
                gameViewModel.setScreenState(ScreenState.Game)
            })
        }
    }
}