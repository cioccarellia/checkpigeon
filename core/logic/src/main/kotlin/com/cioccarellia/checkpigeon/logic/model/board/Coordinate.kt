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

    /**
     * Every checker coordinate has a number
     * */
    val checkersCoordinateNumber: Int
        get() {
            if (!parity) return 0

            // Rank weight
            val inverseRank = (8 - rank.value)
            val weight = 4 * inverseRank

            // Steps 2 adjacent squares unit
            val step = when (file.value) {
                Files.A, Files.B -> 1
                Files.C, Files.D -> 2
                Files.E, Files.F -> 3
                Files.G, Files.H -> 4
            }

            return weight + step
        }
}