package com.example.tetrisapp.feature_game.domain.entity

import com.example.tetrisapp.feature_game.domain.model.BoardType
import com.example.tetrisapp.feature_game.domain.model.CellType

data class Board(
    private val _cells: List<List<CellType>> = List(20) { List(10) { Cell() } }
) : BoardType {
    override val cells: List<List<CellType>>
        get() = _cells

    fun createBoardWithUpdateCells(newCell: CellType): Board {
        val newCells = _cells.mapIndexed{ row , cellRow ->
            cellRow.mapIndexed {
                col, cell ->
                if (row == newCell.position.second && col == newCell.position.first) {
                    newCell
                } else {
                    cell
                }
            }
        }

        return Board(newCells)
    }
}
