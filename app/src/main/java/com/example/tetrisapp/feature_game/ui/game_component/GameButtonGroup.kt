package com.example.tetrisapp.feature_game.ui.game_component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tetrisapp.feature_game.domain.entity.Board
import com.example.tetrisapp.feature_game.domain.entity.TetriMino
import com.example.tetrisapp.feature_game.domain.usecase.SideX

@Preview
@Composable
fun GameButtonGroup(
    onMoveRight: () -> Unit = {},
    onMoveLeft: () -> Unit = {},
    onSoftDrop: () -> Unit = {},
    onRotateClockWise: () -> Unit = {},
    onRotateAntiClockWise: () -> Unit = {},
    ) {
    Row {

        Column(
            modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {

                IconButton(onClick = {
                    onMoveLeft()
                }) {
                    Icon(
                        Icons.Filled.KeyboardArrowLeft,
                        contentDescription = "Left",
                        modifier = Modifier.size(48.dp)
                    )
                }
                IconButton(onClick = {
                    onMoveRight()
                }) {
                    Icon(
                        Icons.Filled.KeyboardArrowRight,
                        contentDescription = "Right",
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            IconButton(onClick = {
                onSoftDrop()
            }) {
                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    contentDescription = "SoftDrop",
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        IconButton(onClick = {
            onRotateAntiClockWise()
        }) {
            Icon(
                Icons.Filled.Refresh,
                contentDescription = "RotateAntiClockWise",
                modifier = Modifier
                    .size(48.dp)
                    // 左右反転にしてる
                    .graphicsLayer(scaleX = -1f)
            )
        }
        IconButton(onClick = {
            onRotateClockWise()
        }) {
            Icon(
                Icons.Filled.Refresh,
                contentDescription = "RotateClockWise",
                modifier = Modifier.size(48.dp)
            )
        }
    }
}