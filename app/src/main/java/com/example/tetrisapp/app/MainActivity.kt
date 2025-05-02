package com.example.tetrisapp.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.tetrisapp.core.ui.theme.TetrisAppTheme
import com.example.tetrisapp.feature_game.ui.GameViewModel
import com.example.tetrisapp.feature_game.ui.MainScreen

// MainActivityの役割は？
// ライフサイクルの設定と画面の表示
class MainActivity : ComponentActivity() {
    private val gameViewModel: GameViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TetrisAppTheme {
                MainScreen(gameViewModel)
            }
        }
    }
}
