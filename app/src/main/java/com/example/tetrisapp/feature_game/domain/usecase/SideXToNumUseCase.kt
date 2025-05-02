package com.example.tetrisapp.feature_game.domain.usecase

enum class SideX {
    LEFT, RIGHT
}

class SideXToNumUseCase {
    operator fun invoke(sideX: SideX): Int{
        val sideXToNum = when(sideX){
            SideX.LEFT -> -1
            SideX.RIGHT -> 1
        }
        return sideXToNum
    }
}