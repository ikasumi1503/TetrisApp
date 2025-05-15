package com.example.tetrisapp.feature_game.domain.usecase.game_manager

import com.example.tetrisapp.feature_game.data.GameRepository

class LoadHighScoreUseCase(
    private val repository: GameRepository
) {
    operator fun invoke(): Int {
        return repository.loadHighScore()
    }
}