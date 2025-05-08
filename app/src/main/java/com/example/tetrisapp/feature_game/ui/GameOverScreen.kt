package com.example.tetrisapp.feature_game.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun GameOverScreen(gameViewModel: GameViewModel){
    Column {
        Text(text = "${gameViewModel.score.value}")
        Text(text = "${gameViewModel.highScore.value}")

        Button (onClick = { gameViewModel.changeToMenu()}){
            Text("メニューに戻る")
        }
    }
}