package com.cioccarellia.checkpigeon.logic.engine.internal

import com.cioccarellia.checkpigeon.logic.board.Board
import com.cioccarellia.checkpigeon.logic.console.cyan
import com.cioccarellia.checkpigeon.logic.console.lightCyan
import com.cioccarellia.checkpigeon.logic.console.lightGreen
import com.cioccarellia.checkpigeon.logic.engine.Highlights
import com.cioccarellia.checkpigeon.logic.model.board.Coordinate
import com.cioccarellia.checkpigeon.logic.model.board.File
import com.cioccarellia.checkpigeon.logic.model.board.Rank
import com.cioccarellia.checkpigeon.logic.model.tile.Tile
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor

typealias GameMatrix = Array<Array<Tile>>

/**
 * Board-printing utility
 * */
object BoardPrinter {

    /**
     * Prints board to standard output and returns it
     * */

    /*
     * white:
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
     * black:
     *          1	|   | ● |   | ● |   | ● |   | ● |
     *          2	| ● |   | ● |   | ● |   | ● |   |
     *          3	|   | ● |   | ● |   | ● |   | ● |
     *          4	|   |   |   |   |   |   |   |   |
     *          5	|   |   |   |   |   |   |   |   |
     *          6	| ○ |   | ○ |   | ○ |   | ○ |   |
     *          7	|   | ○ |   | ○ |   | ○ |   | ○ |
     *          8	| ○ |   | ○ |   | ○ |   | ○ |   |
     *                H   G   F   E   D   C   B   A
     * */
    fun stdoutc(
        board: Board,
        color: TileColor,
        highlights: List<Coordinate> = listOf()
    ) = stdout(board, color, highlights.map { it.file to it.rank })

    fun stdout(
        board: Board,
        color: TileColor,
        highlights: List<Pair<File, Rank>> = listOf()
    ) = buildString {
        val transposedMatrix: GameMatrix = Array(8) { rowIndex ->
            Array(8) { columnIndex ->
                board.matrix[columnIndex][rowIndex]
            }
        }

        fun generateBoardText() = buildString {
            transposedMatrix.forEachIndexed { rankIndex, rankTiles ->
                // [rankIndex] stays between 0 and 7.
                // We can begin printing for white (we start from rank 8 through 1)
                // or for black (we start from rank 1 through 8)
                val rankNumber = if (color == TileColor.WHITE) 8 - rankIndex else rankIndex + 1
                val rank = Rank.from(rankNumber)

                append(
                    board.printRank(rank, rankTiles, color, highlights)
                )

                appendBar()
                appendLine()
            }

            appendTabAndSpace()
        }

        fun generateFileLetters() = buildString {
            val highlightedFiles = highlights.map { it.first }

            repeat(8) { index ->
                val value = if (color == TileColor.WHITE) index + 1 else 8 - index
                val file = File.from(value)

                if (highlightedFiles.contains(file)) {
                    appendSpace()
                    append(file.letter.toString().cyan())
                    appendDoubleSpace()
                } else {
                    appendSpace()
                    append(file.letter.toString().lightGreen())
                    appendDoubleSpace()
                }
            }

            appendLine()
        }

        append(
            generateBoardText(),
            generateFileLetters()
        )
    }.also {
        // This makes the whole operation atomic
        print(it)
    }

    private const val SPACE = " "
    private const val TAB = "\t"
    private const val BAR = "|"

    private fun StringBuilder.appendSpace() = append(SPACE)
    private fun StringBuilder.appendDoubleSpace() = append(SPACE + SPACE)
    private fun StringBuilder.appendBar() = append(BAR)
    private fun StringBuilder.appendBarAndSpace() = append("$BAR$SPACE")
    private fun StringBuilder.appendTab() = append(TAB)
    private fun StringBuilder.appendTabAndSpace() = append("$TAB$SPACE")

    private fun Board.printRank(
        rank: Rank,
        rankTiles: Array<Tile>,
        color: TileColor,
        highlights: Highlights
    ) = buildString {
        val highlightedRanks = highlights.map { it.second }

        if (highlightedRanks.contains(rank)) {
            append("${rank.number}".cyan())
        } else {
            append("${rank.number}".lightGreen())
        }

        append(TAB)

        rankTiles.forEachIndexed { fileIndex, tile ->
            val fileNumber = if (color == TileColor.WHITE) fileIndex + 1 else 8 - fileIndex
            val file = File.from(fileNumber)

            val coords = Coordinate(file, rank)

            appendBarAndSpace()

            if (highlights.contains(coords.highlight)) {
                append(get(coords).toString().lightCyan())
            } else {
                append(get(coords))
            }

            appendSpace()
        }
    }
}