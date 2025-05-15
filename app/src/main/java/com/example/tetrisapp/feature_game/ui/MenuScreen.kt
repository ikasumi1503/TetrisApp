package com.example.tetrisapp.feature_game.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tetrisapp.feature_game.ui.game_component.template.GameMenuButtonTemplate
import com.example.tetrisapp.feature_game.ui.viewmodel.GameViewModel

@Composable
// Unit ... voidに相当する
fun MenuScreen(gameViewModel: GameViewModel, onStartGame: () -> Unit) {
    val state = gameViewModel.state.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .wrapContentHeight()
                .border(
                    width = 4.dp,
                    color = Color.Black.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "最高得点: ${state.value.highScore}")
                GameMenuButtonTemplate(
                    onClick = { onStartGame() },
                    text = "ゲームスタート"
                )
            }
        }
    }
}