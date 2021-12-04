package com.cioccarellia.checkpigeon.logic.model.board

import com.cioccarellia.checkpigeon.logic.model.board.ext.plus

/**
 * Represents a single board tile, with its file and rank.
 * */
data class Coordinate(
    val file: File,
    val rank: Rank,
) {

    /**
     * Even square <=> Black
     * Odd square  <=> White
     * */
    val parity: Boolean
        get() = (file + rank) % 2 == 0

    val highlight
        get() = file to rank

    /**
     * Every checker coordinate has a number
     * */
    val checkersCoordinateNumber: Int
        get() {
            if (!parity) return 0

            // Rank weight
            val inverseRank = (8 - rank.number)
            val weight = 4 * inverseRank

            // Steps 2 adjacent squares unit
            val step = when (file.letter) {
                FileLetter.A, FileLetter.B -> 1
                FileLetter.C, FileLetter.D -> 2
                FileLetter.E, FileLetter.F -> 3
                FileLetter.G, FileLetter.H -> 4
            }

            return weight + step
        }
}