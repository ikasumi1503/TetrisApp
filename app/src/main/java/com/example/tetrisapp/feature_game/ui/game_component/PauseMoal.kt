package com.example.tetrisapp.feature_game.ui.game_component

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun PauseModal(
    isPaused: Boolean,
    onResume: () -> Unit,
    onInit: () -> Unit,
    onChangeToMenu: () -> Unit
) {
    if (isPaused) {
        Dialog(onDismissRequest = { onResume() }) {
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            onChangeToMenu()
                            onResume()
                        }
                    ) {
                        Text("タイトルに戻る")
                    }

                    Button(
                        onClick = {
                            onInit()
                            onResume()
                        }
                    ) {
                        Text("やり直し")
                    }

                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { onResume() }
                    ) {
                        Text("再開する")
                    }
                }
            }
        }
    }
}