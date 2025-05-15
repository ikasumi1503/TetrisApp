package com.example.tetrisapp.feature_game.ui.game_component.gamebuttongroup

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import com.example.tetrisapp.feature_game.ui.game_component.template.ButtonTemplate

@Composable
fun PauseButton(pause: () -> Unit) {
    ButtonTemplate(
        onClick = { pause() },
        iconImage = Icons.Filled.Menu,
        contentDescription = "MenuButton",
        isMirror = false,
        canRepeat = false
    )
}