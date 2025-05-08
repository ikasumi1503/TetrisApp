package com.example.tetrisapp.app

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.key
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tetrisapp.core.ui.theme.TetrisAppTheme
import com.example.tetrisapp.feature_game.ui.GameViewModel
import com.example.tetrisapp.feature_game.ui.MainScreen

// MainActivityの役割は？
// ライフサイクルの設定と画面の表示

// Factory ... viewModelの作り方の設計図
class GameViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    // T : ViewModel> ... TはViewModelを継承している型(クラス)と宣言している
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // 作るviewModelがGameViewModelだった場合に次の処理を実行
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {

            // 「安全な型の当てはめかどうかわからないから危険！」というエラーをSuppressで抑えている
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(application = application) as T
        }
        // GameViewModel以外のViewModelだったらエラーを投げる
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


class MainActivity : ComponentActivity() {

//    private val gameViewModel: GameViewModel by viewModels {
//        // applicationはComponentActivityから自動的に継承されているプロパティ
//        key { "" }
//        GameViewModelFactory(application)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TetrisAppTheme {
                MainScreen()
            }
        }
    }
}
