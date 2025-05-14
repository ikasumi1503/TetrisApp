package com.example.tetrisapp.feature_game.ui.game_component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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

    // TODO: Pauseボタンを変える
    // TODO: スコアなどの数値を大きくする
    @Composable
    // RowScopeにしないと、子要素がRowの各要素と認識されなくて間が開かない
    fun DisplayBox(content: @Composable ColumnScope.() -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(3.dp, Color.Gray)
                .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            content()
        }
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