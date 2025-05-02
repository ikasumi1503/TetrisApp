package com.example.tetrisapp.feature_game.domain.model

interface BoardType {
    val cells: List<List<CellType>>;
}