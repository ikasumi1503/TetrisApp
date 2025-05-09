package com.example.tetrisapp.feature_game.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.tetrisapp.feature_game.ui.viewmodel.GameViewModel

@Composable
// Unit ... voidに相当する
fun MenuScreen(gameViewModel: GameViewModel, onStartGame: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "最高得点")

        Button(onClick = { onStartGame() }) {
            Text(text = "Game Start")
        }

        Button(onClick = {}) {
            Text(text = "Impossible")
        }

    }
}