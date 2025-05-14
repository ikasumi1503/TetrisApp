package com.example.tetrisapp.feature_game.ui.game_component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ButtonTemplate(
    onClick: () -> Unit,
    iconImage: ImageVector,
    contentDescription: String,
    isMirror: Boolean,
    canRepeat: Boolean
) {
    val isPressed = remember { mutableStateOf(false) }
    val isMirrorToNum = if (isMirror) -1f else +1f
    val buttonSize = 64.dp
    val buttonContentSize = 56.dp
    val scope = rememberCoroutineScope()

    // 長押し中の連打処理
    if (canRepeat) {
        LaunchedEffect(isPressed.value) {
            if (isPressed.value) {
                while (isPressed.value) {
                    onClick()
                    delay(100)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .size(buttonSize)
            .graphicsLayer(scaleX = isMirrorToNum)
            .shadow(8.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFAAAAAA), Color(0xFF444444))
                )
            )
            .border(2.dp, Color.Black.copy(alpha = 0.5f), shape = CircleShape)
            .pointerInput(Unit) {
                detectTapGestures(onPress = {
                    onClick()

                    // scope.launchはCoroutineの中で動くから、並列処理
                    val job = scope.launch {
                        delay(250) // 待機時間後に長押し処理
                        if (canRepeat) isPressed.value = true
                    }

                    try {
                        tryAwaitRelease()
                    } finally {
                        // もし長押しキャンセルされたら長押し処理キャンセル
                        job.cancel()
                        isPressed.value = false
                    }
                })
            },

        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = iconImage,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(buttonContentSize)
        )
    }
}
