package com.example.tetrisapp.feature_game.ui.game_component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Preview
@Composable
fun PauseModal(
    isPaused: Boolean = true,
    onResume: () -> Unit = {},
    onInit: () -> Unit = {},
    onChangeToMenu: () -> Unit = {}
) {
    if (isPaused) {
        Dialog(
            onDismissRequest = { onResume() }
        ) {
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .wrapContentHeight()
                    .background(Color.Transparent)
                    .border(width = 4.dp, color = Color.Black.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                        .background(Color.Transparent)
                    ,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    GameMenuButton(
                        onClick = onChangeToMenu,
                        text = "タイトルに戻る",
                        modifier = Modifier
                    )

                    Spacer(Modifier.height(8.dp))

                    GameMenuButton(
                        onClick = {
                            onInit()
                            onResume()
                        },
                        text = "やり直し",
                        modifier = Modifier
                    )

                    Spacer(Modifier.height(8.dp))

                    GameMenuButton(
                        onClick = {
                            onResume()
                        },
                        text = "再開する",
                        modifier = Modifier
                    )
                }
            }
        }
    }
}