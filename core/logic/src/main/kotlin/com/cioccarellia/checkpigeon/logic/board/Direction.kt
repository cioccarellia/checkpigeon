package com.cioccarellia.checkpigeon.logic.board

import com.cioccarellia.checkpigeon.logic.model.board.Coordinate

/**
 * Board-dependent cardinal points.
 * */
enum class Direction {
    NE, SE, SW, NW;

    /**
     * Unsafe if coordinate is not checked to be legal for a given direction
     * */
    fun shiftedCoordinateBy1Diagonally(coordinate: Coordinate): Coordinate = when (this) {
        NE -> Coordinate.raw(coordinate.rawFile() + 1, coordinate.rawRank() + 1)
        SE -> Coordinate.raw(coordinate.rawFile() + 1, coordinate.rawRank() - 1)
        SW -> Coordinate.raw(coordinate.rawFile() - 1, coordinate.rawRank() - 1)
        NW -> Coordinate.raw(coordinate.rawFile() - 1, coordinate.rawRank() + 1)
    }

    /**
     * Unsafe if coordinate is not checked to be (double jump) legal for a given direction
     * */
    fun shiftedCoordinateBy2Diagonally(coordinate: Coordinate): Coordinate = when (this) {
        NE -> Coordinate.raw(coordinate.rawFile() + 2, coordinate.rawRank() + 2)
        SE -> Coordinate.raw(coordinate.rawFile() + 2, coordinate.rawRank() - 2)
        SW -> Coordinate.raw(coordinate.rawFile() - 2, coordinate.rawRank() - 2)
        NW -> Coordinate.raw(coordinate.rawFile() - 2, coordinate.rawRank() + 2)
    }

    companion object {
        fun infer(
            start: Coordinate,
            end: Coordinate
        ): Direction = when {
            end.rawFile() - start.rawFile() > 0 && end.rawRank() - start.rawRank() > 0 -> NE
            end.rawFile() - start.rawFile() > 0 && end.rawRank() - start.rawRank() < 0 -> SE
            end.rawFile() - start.rawFile() < 0 && end.rawRank() - start.rawRank() < 0 -> SW
            end.rawFile() - start.rawFile() < 0 && end.rawRank() - start.rawRank() > 0 -> NW
            else -> throw IllegalArgumentException("Can not determine direction between $start and $end. Are they on a rook line? (0 or infty tangent)")
        }
    }
}