package com.cioccarellia.checkpigeon.logic.board

import com.cioccarellia.checkpigeon.logic.model.board.Coordinate
import com.cioccarellia.checkpigeon.logic.model.board.File
import com.cioccarellia.checkpigeon.logic.model.board.Rank
import com.cioccarellia.checkpigeon.logic.model.material.Material
import com.cioccarellia.checkpigeon.logic.model.move.MoveType
import com.cioccarellia.checkpigeon.logic.model.move.linear.Move
import com.cioccarellia.checkpigeon.logic.model.move.verbose.VerboseMove
import com.cioccarellia.checkpigeon.logic.model.tile.Tile
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor

data class Board(
    val matrix: Array<Array<Tile>> = Array(8) { fileIndex ->
        // We fix the file (A through H)
        val fileNumber = fileIndex + 1
        val file = File.from(fileNumber)

        Array(8) { rankIndex ->
            /**
             * Creates the final [Tile]s fixing a single file and sliding ranks up from A to H
             * */
            val slidingRank = Rank.from(rankIndex + 1)

            //val file = File.from(fileIndex + 1)

            Tile(
                Coordinate(file, slidingRank), Material.Empty
            )
        }
    }
) {
    /**
     * Board matrix, representing the game state.
     *
     * A chessboard is indexed using [File]s (columns) and [Rank]s (rows), one-based.
     * A coordinate contains file and rank information, therefore a chessboard can be
     * indexed with a [Coordinate]
     *
     * The board matrix is built symmetrically, from the bottom to the top: while creating the [matrix],
     * for each file (from 1 to 8) the [File] is fixed, and the [Rank] is shifting (A through H), 8 times.
     *
     * Therefore, at matrix[0] we have the first [Rank], the A rank with A1, A2, ..., A8.
     * */

    /*
     *          8	|   | ○ |   | ○ |   | ○ |   | ○ |
     *          7	| ○ |   | ○ |   | ○ |   | ○ |   |
     *          6	|   | ○ |   | ○ |   | ○ |   | ○ |
     *          5	|   |   |   |   |   |   |   |   |
     *          4	|   |   |   |   |   |   |   |   |
     *          3	| ● |   | ● |   | ● |   | ● |   |
     *          2	|   | ● |   | ● |   | ● |   | ● |
     *          1	| ● |   | ● |   | ● |   | ● |   |
     *                A   B   C   D   E   F   G   H
     *
     *
     *          1	|   | ● |   | ● |   | ● |   | ● |
     *          2	| ● |   | ● |   | ● |   | ● |   |
     *          3	|   | ● |   | ● |   | ● |   | ● |
     *          4	|   |   |   |   |   |   |   |   |
     *          5	|   |   |   |   |   |   |   |   |
     *          6	| ○ |   | ○ |   | ○ |   | ○ |   |
     *          7	|   | ○ |   | ○ |   | ○ |   | ○ |
     *          8	| ○ |   | ○ |   | ○ |   | ○ |   |
     *                H   G   F   E   D   C   B   A
     *
     *
     *
     *          7	|   | ○ |   | ○ |   | ○ |   | ○ |
     *          6	| ○ |   | ○ |   | ○ |   | ○ |   |
     *          5	|   | ○ |   | ○ |   | ○ |   | ○ |
     *          4	|   |   |   |   |   |   |   |   |
     *          3	|   |   |   |   |   |   |   |   |
     *          2	| ● |   | ● |   | ● |   | ● |   |
     *          1	|   | ● |   | ● |   | ● |   | ● |
     *          0	| ● |   | ● |   | ● |   | ● |   |
     *                0   1   2   3   4   5   6   7
     *
     *          matrix[0][2] = A3
     *          matrix[2][0] = C1
     * */


    init {
        // Populates board
        matrix.forEach { file ->
            // File 1 [ranks 1 to 8]

            file.forEachIndexed { i, it ->
                when {
                    i >= 5 -> {
                        if (it.color == TileColor.BLACK) {
                            it.material = Material.Dama(TileColor.BLACK)
                        }
                    }
                    i <= 2 -> {
                        if (it.color == TileColor.BLACK) {
                            it.material = Material.Dama(TileColor.WHITE)
                        }
                    }
                }
            }
        }

        // Debugging
        // matrix[4][2].material = Material.Damone(TileColor.WHITE)
    }

    val whitePieceCount: Int
        get() = countPieces(TileColor.WHITE)

    val blackPieceCount: Int
        get() = countPieces(TileColor.BLACK)

    fun countPieces(color: TileColor) = matrix.sumOf {
        it.count { tile ->
            when (val material = tile.material) {
                is Material.Dama -> material.color == color
                is Material.Damone -> material.color == color
                else -> false
            }
        }
    }

    /**
     * Applies changes to the game matrix for a given move forward in time
     * */
    fun executeMoveForward(validatedMove: Move) = with(validatedMove) {
        when (moveType) {
            MoveType.Capture -> {
                captures.toList().forEach(::remove)
                blows?.let { remove(it.first) }
                swap(start, end)
            }
            MoveType.Movement -> {
                swap(start, end)
            }
        }

        promotion?.let {
            set(it, get(it).promoted())
        }
    }


    /**
     * Applies changes to the game matrix for a given move backward in time
     * */
    fun executeMoveBackward(verboseMove: VerboseMove) = with(verboseMove) {
        when (moveType) {
            MoveType.Capture -> {
                captureCoordsMaterialPairList().forEach {
                    set(it.first, it.second)
                }
                blows?.let {
                    set(it.first, blownMaterial!!)
                }

                swap(start, end)
            }
            MoveType.Movement -> {
                swap(start, end)
            }
        }

        promotion?.let {
            set(it, get(it).demoted())
        }
    }

    /**
     * Returns the material on a given [Coordinate]
     * */
    operator fun get(coord: Coordinate): Material = tile(coord).material

    private fun tile(coord: Coordinate): Tile = matrix[coord.file.letter.numeric - 1][coord.rank.number - 1]

    internal fun set(coord: Coordinate, material: Material) {
        matrix[coord.file.letter.numeric - 1][coord.rank.number - 1].material = material
    }

    private fun remove(coord: Coordinate) = set(coord, Material.Empty)

    private fun swap(X: Coordinate, Y: Coordinate) {
        val tmpX = tile(X).material

        tile(X).material = tile(Y).material
        tile(Y).material = tmpX
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Board

        if (!matrix.contentDeepEquals(other.matrix)) return false

        return true
    }

    override fun hashCode(): Int {
        return matrix.contentDeepHashCode()
    }
}