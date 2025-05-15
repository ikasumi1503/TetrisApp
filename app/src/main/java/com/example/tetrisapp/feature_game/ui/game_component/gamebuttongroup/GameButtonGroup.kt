package com.example.tetrisapp.feature_game.ui.game_component.gamebuttongroup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tetrisapp.feature_game.ui.game_component.template.ButtonTemplate

@Preview
@Composable
fun GameButtonGroup(
    onMoveRight: () -> Unit = {},
    onMoveLeft: () -> Unit = {},
    onSoftDrop: () -> Unit = {},
    onRotateClockWise: () -> Unit = {},
    onRotateAntiClockWise: () -> Unit = {},
) {
    val horizontalSpacerSize = 24.dp
    val verticalSpacerSize = 12.dp

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                ButtonTemplate(
                    onMoveLeft,
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    "LeftButton",
                    isMirror = false,
                    canRepeat = true
                )
                Spacer(modifier = Modifier.size(horizontalSpacerSize))
                ButtonTemplate(
                    onMoveRight,
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    "RightButton",
                    isMirror = false,
                    canRepeat = true
                )
            }
            Spacer(modifier = Modifier.size(verticalSpacerSize))
            ButtonTemplate(
                onSoftDrop,
                Icons.Filled.KeyboardArrowDown,
                "DownButton",
                isMirror = false,
                canRepeat = true
            )
        }
        Spacer(modifier = Modifier.size(horizontalSpacerSize))

        ButtonTemplate(
            onRotateAntiClockWise,
            Icons.Filled.Refresh,
            "RotateAntiClockWiseButton",
            isMirror = true,
            canRepeat = false
        )
        Spacer(modifier = Modifier.size(horizontalSpacerSize))
        ButtonTemplate(
            onRotateClockWise,
            Icons.Filled.Refresh,
            "RotateClockWiseButton",
            isMirror = false,
            canRepeat = false
        )
    }
}