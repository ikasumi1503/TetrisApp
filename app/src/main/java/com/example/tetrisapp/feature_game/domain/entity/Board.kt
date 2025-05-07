package com.example.tetrisapp.feature_game.domain.entity

import com.example.tetrisapp.feature_game.domain.model.BoardType
import com.example.tetrisapp.feature_game.domain.model.CellType

data class Board(
    private val _cells: List<List<CellType>> = List(20) { List(10) { Cell() } }
) : BoardType {
    override val cells: List<List<CellType>>
        get() = _cells

    fun createBoardWithUpdateCells(newCell: CellType): Board {
        val newCells = _cells.mapIndexed { row, cellRow ->
            cellRow.mapIndexed { col, cell ->
                if (row == newCell.position.second && col == newCell.position.first) {
                    newCell
                } else {
                    cell
                }
            }
        }

        return this.copy(_cells = newCells)
    }

    fun checkAndClearLines(): Pair<Board, Int> {

        // ボードの中身を一つ一つチェックして一行がisFilledになっているか確認
        val remainingRows = _cells.filter { row ->
            // any...一つでも当てはまるものがあればtrueを返す
            row.any { cell -> !cell.isFilled }
        }

        // 削除された行数分追加
        val clearedLinesCount = _cells.size - remainingRows.size
        val newEmptyColumns = List(clearedLinesCount) { List(_cells[0].size) { Cell() } }
        val updatedCells = newEmptyColumns + remainingRows
        return Pair(this.copy(_cells = updatedCells), clearedLinesCount)
    }
}
