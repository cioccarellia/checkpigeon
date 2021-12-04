package com.cioccarellia.checkpigeon.logic.board

import com.cioccarellia.checkpigeon.logic.model.board.Coordinate
import com.cioccarellia.checkpigeon.logic.model.board.File
import com.cioccarellia.checkpigeon.logic.model.board.Rank
import com.cioccarellia.checkpigeon.logic.model.material.Material
import com.cioccarellia.checkpigeon.logic.model.move.MoveType
import com.cioccarellia.checkpigeon.logic.model.move.linear.Move
import com.cioccarellia.checkpigeon.logic.model.tile.Tile
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor

class Board {
    val matrix: Array<Array<Tile>> = Array(8) { rankIndex ->
        val rankNumber = 8 - (rankIndex + 1) + 1

        Array(8) { fileIndex ->
            val file = File.from(fileIndex + 1)
            val rank = Rank.from(rankNumber)

            Tile(
                Coordinate(file, rank), Material.Empty
            )
        }
    }

    init {
        matrix.forEachIndexed { i, rank ->
            val rankIndex = (8 - i)

            // Populates board
            when {
                rankIndex >= 6 -> {
                    // Top with black pieces
                    rank.forEach {
                        if (it.color == TileColor.BLACK) {
                            it.material = Material.Dama(TileColor.BLACK)
                        }
                    }
                }
                rankIndex <= 3 -> {
                    // Bottom with white pieces
                    rank.forEach {
                        if (it.color == TileColor.BLACK) {
                            it.material = Material.Dama(TileColor.WHITE)
                        }
                    }
                }
            }
        }
    }

    /**
     * Applies changes for a given move
     * */
    internal fun execute(validatedMove: Move) = with(validatedMove) {
        when (moveType) {
            MoveType.Capture -> {
                blows?.toList()?.forEach(::remove)
                captures.toList().forEach(::remove)
                swap(start, end)
            }
            MoveType.Movement -> {
                swap(start, end)
                //set(end, get(start))
                //set(start, Material.Empty)
            }
        }
    }

    /**
     * Returns the material on a given [Coordinate]
     * */
    operator fun get(coord: Coordinate): Material = tile(coord).material

    private fun tile(coord: Coordinate): Tile = matrix[8 - coord.rank.number][coord.file.letter.numeric - 1]

    private fun set(coord: Coordinate, material: Material) {
        matrix[coord.rank.number - 1][coord.file.letter.numeric - 1].material = material
    }

    private fun remove(coord: Coordinate) = set(coord, Material.Empty)

    private fun swap(X: Coordinate, Y: Coordinate) {
        val tmpX = tile(X).material

        tile(X).material = tile(Y).material
        tile(Y).material = tmpX
    }
}