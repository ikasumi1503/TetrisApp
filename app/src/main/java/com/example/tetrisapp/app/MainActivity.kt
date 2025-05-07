package com.example.tetrisapp.app

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tetrisapp.core.ui.theme.TetrisAppTheme
import com.example.tetrisapp.feature_game.ui.GameViewModel
import com.example.tetrisapp.feature_game.ui.MainScreen

// MainActivityの役割は？
// ライフサイクルの設定と画面の表示
//class MainActivity : ComponentActivity() {
//    private val gameViewModel: GameViewModel by viewModels()
//    override fun onCreate(savedInstanceState: Bundle?) {
//
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            TetrisAppTheme {
//                MainScreen(gameViewModel)
//            }
//        }
//    }
//}

class GameViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(application = application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


class MainActivity : ComponentActivity() {

    private val gameViewModel: GameViewModel by viewModels {
        GameViewModelFactory(application)
    }

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
