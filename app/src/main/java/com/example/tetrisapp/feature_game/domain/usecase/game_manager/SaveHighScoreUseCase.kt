package com.example.tetrisapp.feature_game.domain.usecase.game_manager

import com.example.tetrisapp.feature_game.data.GameRepository

class SaveHighScoreUseCase(
    private val repository: GameRepository
) {
    operator fun invoke(score: Int) {
        repository.saveHighScore(
            score = score
        )
    }
}