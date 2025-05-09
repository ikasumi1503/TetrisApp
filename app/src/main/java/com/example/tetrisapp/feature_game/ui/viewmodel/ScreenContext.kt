package com.example.tetrisapp.feature_game.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

// 1. CompositionLocal を定義（必要に応じて）
val LocalGameViewModel = compositionLocalOf<GameViewModel> {
    error("No GameViewModel provided")
}

@Composable
fun ScreenContext(content: @Composable () -> Unit) {
    // 新しいゲームを始める時にkeyを指定してviewModelを新規作成するけど、毎回古いものは破棄するので重たくならないらしい。
    // 本来はMainActivityでviewModelsを使いたかったけど、@Composableの中でしかviewModelが使えなかったので、こちらに置いた
    val app = LocalContext.current.applicationContext as Application
    val gameViewModel: GameViewModel = viewModel(
        key = "SharedGameVM",
        factory = GameViewModelFactory(app)
    )

    // CompositionLocal に流し込みつつ、content を呼び出し
    CompositionLocalProvider(
        LocalGameViewModel provides gameViewModel
    ) {
        content()
    }
}