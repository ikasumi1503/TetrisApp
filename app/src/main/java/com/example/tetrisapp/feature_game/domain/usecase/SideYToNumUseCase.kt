package com.example.tetrisapp.feature_game.domain.usecase

enum class SideY {
    UP, DOWN
}

class SideYToNumUseCase {
    operator fun invoke(sideY: SideY): Int{
        val sideYToNum = when(sideY){
            SideY.UP -> -1
            SideY.DOWN -> 1
        }
        return sideYToNum
    }
}