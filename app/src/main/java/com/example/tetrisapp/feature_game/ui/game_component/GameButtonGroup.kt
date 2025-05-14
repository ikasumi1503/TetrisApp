package com.example.tetrisapp.feature_game.ui.game_component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun GameButtonGroup(
    onMoveRight: () -> Unit = {},
    onMoveLeft: () -> Unit = {},
    onSoftDrop: () -> Unit = {},
    onRotateClockWise: () -> Unit = {},
    onRotateAntiClockWise: () -> Unit = {},
) {
    val buttonSize = 64.dp
    val buttonContentSize = 56.dp
    val horizontalSpacerSize = 24.dp
    val verticalSpacerSize = 12.dp

    @Composable
    fun GeneralButton(
        onButtonPressed: () -> Unit,
        buttonImage: ImageVector,
        isMirror: Boolean,
        contentDescription: String
    ) {
        val isMirrorToNum = if (isMirror) -1f else +1f
        IconButton(
            onClick = onButtonPressed,
            modifier = Modifier
                .size(buttonSize)
                .shadow(elevation = 6.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(Color.Gray)
        ) {
            Icon(
                imageVector = buttonImage,
                contentDescription = contentDescription,
                tint = Color.White, // アイコンのコンテンツの色
                modifier = Modifier
                    .size(buttonContentSize)
                    .graphicsLayer(scaleX = isMirrorToNum)
            )
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                GeneralButton(
                    onMoveLeft,
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    false,
                    "LeftButton"
                )
                Spacer(modifier = Modifier.size(horizontalSpacerSize))
                GeneralButton(
                    onMoveRight,
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    false,
                    "RightButton"
                )
            }
            Spacer(modifier = Modifier.size(verticalSpacerSize))
            // TODO: 長押しでどんどん落ちていくようにしたい
            GeneralButton(onSoftDrop, Icons.Filled.KeyboardArrowDown, false, "DownButton")
        }
        Spacer(modifier = Modifier.size(horizontalSpacerSize))

        GeneralButton(
            onRotateAntiClockWise,
            Icons.Filled.Refresh,
            true,
            "RotateAntiClockWiseButton"
        )
        Spacer(modifier = Modifier.size(horizontalSpacerSize))
        GeneralButton(onRotateClockWise, Icons.Filled.Refresh, false, "RotateClockWiseButton")
    }
}