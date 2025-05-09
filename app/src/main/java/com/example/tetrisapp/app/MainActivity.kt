package com.example.tetrisapp.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.tetrisapp.core.ui.theme.TetrisAppTheme
import com.example.tetrisapp.feature_game.ui.MainScreen
import com.example.tetrisapp.feature_game.ui.viewmodel.ScreenContext

// MainActivityの役割は？
// ライフサイクルの設定と画面の表示
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TetrisAppTheme {
                ScreenContext {
                    MainScreen()
                }
            }
        }
    }
}
