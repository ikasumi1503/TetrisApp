package com.example.tetrisapp.feature_game.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.tetrisapp.feature_game.ui.viewmodel.GameViewModel

@Composable
fun GameOverScreen(gameViewModel: GameViewModel) {
    val gameState = gameViewModel.state.collectAsState()
    Column {
        Text(text = "${gameState.value.score}")
        Text(text = "${gameState.value.highScore}")

        Button(onClick = { gameViewModel.changeToMenu() }) {
            Text("メニューに戻る")
        }
    }
}