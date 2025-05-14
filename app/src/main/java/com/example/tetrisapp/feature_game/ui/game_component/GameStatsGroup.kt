package com.example.tetrisapp.feature_game.ui.game_component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tetrisapp.feature_game.domain.model.MinoType

@Composable
fun GameStatsGroup(
    pause: () -> Unit,
    changeToMenu: () -> Unit,
    initGame: () -> Unit,
    swapHoldAndNext: () -> Unit,
    resume: () -> Unit,
    isPaused: Boolean,
    score: Int,
    combo: Int,
    elapsedTime: Long,
    level: Int,
    isInitialized: Boolean,
    nextMino: MinoType
) {
    val textColor = Color.White

    @Composable
    // RowScopeにしないと、子要素がRowの各要素と認識されなくて間が開かない
    fun DisplayBox(content: @Composable RowScope.() -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(3.dp, Color.Gray)
                .padding(vertical = 16.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }

    // TODO: こういうのESLintで整形したい
    Button(
        onClick = {
            pause()
        }
    ) {
        Text("Pause")
    }

    PauseModal(
        isPaused = isPaused,
        onChangeToMenu = { changeToMenu() },
        onInit = { initGame() },
        onResume = { resume() }
    )


    // Score
    DisplayBox {
        Text("POINT:", color = textColor)
        Text("$score", color = textColor)
    }

    // Combo
    DisplayBox {
        Text("COMBO:", color = textColor)
        Text("$combo", color = textColor)
    }

    val time = elapsedTime.div(1000)
    val minutesDisplay = time.div(60)
    val secondsDisplay = time.rem(60)

    DisplayBox {
        Text(
            text = "%02d:%02d".format(minutesDisplay, secondsDisplay),
            color = textColor
        )
    }

    DisplayBox {
        Text("Level:", color = textColor)
        Text("$level", color = textColor)
    }

    // TODO: 左寄りになっているので直したい
    DisplayBox {
        NextMino(
            swapHoldAndNext = swapHoldAndNext,
            isInitialized = isInitialized,
            nextMino = nextMino,
        )
    }
}