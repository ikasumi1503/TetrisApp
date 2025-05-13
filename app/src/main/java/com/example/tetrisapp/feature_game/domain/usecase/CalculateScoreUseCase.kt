package com.example.tetrisapp.feature_game.domain.usecase

class CalculateScoreUseCase {
    data class Result(
        val newScore: Int,
        val newCombo: Int
    )

    operator fun invoke(
        linesCleared: Int,
        isTSpinPerformed: Boolean,
        currentScore: Int,
        currentCombo: Int
    ): Result {
        if (linesCleared <= 0) {

            return Result(
                newScore = currentScore,
                newCombo = 0
            )
        }

        val scoreIncrement = when {
            isTSpinPerformed && linesCleared == 1 -> 500
            isTSpinPerformed && linesCleared == 2 -> 800
            isTSpinPerformed && linesCleared == 3 -> 1200

            linesCleared == 1 -> 100
            linesCleared == 2 -> 300
            linesCleared == 3 -> 500
            linesCleared == 4 -> 800
            else -> 0
        }

        val updatedScore = currentScore + scoreIncrement + currentCombo * 50
        val updatedCombo = currentCombo + 1

        return Result(
            newScore = updatedScore,
            newCombo = updatedCombo
        )
    }
}