package com.example.tetrisapp.feature_game.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

val LocalGameKey = compositionLocalOf<Int> { error("No game key provided") }

val LocalGameKeyUpdater = compositionLocalOf<(Int) -> Unit> { error("No updater provided") }

val LocalGameViewModel = compositionLocalOf<GameViewModel> { error("No GameViewModel provided") }

@Composable
fun ScreenContext(content: @Composable () -> Unit) {
    // 新しいゲームを始める時にkeyを指定してviewModelを新規作成するけど、毎回古いものは破棄するので重たくならないらしい。
    // 本来はMainActivityでviewModelsを使いたかったけど、@Composableの中でしかviewModelが使えなかったので、こちらに置いた
    val gameKey = remember { mutableIntStateOf(0) }
    val app = LocalContext.current.applicationContext as Application
    val gameViewModel: GameViewModel = viewModel(
        key = "vm-${gameKey}",
        factory = GameViewModelFactory(app)
    )

    // CompositionLocal に流し込みつつ、content を呼び出し
    CompositionLocalProvider(
        LocalGameKey provides gameKey.intValue,
        LocalGameKeyUpdater provides { newKey ->
            println(gameKey.intValue)
            gameKey.intValue = newKey
        },
        LocalGameViewModel provides gameViewModel
    ) {
        content()
    }
}