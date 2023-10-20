package com.cioccarellia.checkpigeon.eval

import com.cioccarellia.checkpigeon.logic.model.board.Coordinate
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor


val matrix_1_white: Array<IntArray> = arrayOf(
    intArrayOf( 10,  10,  10,  10,  10,  10,  10,  10),
    intArrayOf(  8,   7,   7,   7,   7,   7,   7,   8),
    intArrayOf(  6,   5,   5,   5,   5,   5,   5,   6),
    intArrayOf(  5,   4,   4,   4,   4,   4,   4,   5),
    intArrayOf(  4,   3,   3,   3,   3,   3,   3,   4),
    intArrayOf(  2,   2,   2,   2,   2,   2,   2,   1),
    intArrayOf(  1,   1,   1,   1,   1,   1,   1,   1),
    intArrayOf(  0,   0,   0,   0,   0,   0,   0,   0),
)


val matrix_1_black: Array<IntArray> = matrix_1_white.reversed().toTypedArray()


fun m1_eval(coordinate: Coordinate, color: TileColor): Int {
    val matrix: Array<IntArray> = if (color == TileColor.WHITE) matrix_1_white else matrix_1_black
    return matrix[coordinate.file.letter.numeric - 1][coordinate.rank.number - 1]
}



val matrix_2_white: Array<IntArray> = arrayOf(
    intArrayOf(  -1,  -1,  -1,  -1,  -1,  -1,  -1,   -1),
    intArrayOf(  -1,   0,   0,   0,   0,   0,   0,   -1),
    intArrayOf(  -1,   0,   0,   0,   0,   0,   0,   -1),
    intArrayOf(  -1,   0,   0,   0,   0,   0,   0,   -1),
    intArrayOf(  -1,   0,   0,   0,   0,   0,   0,   -1),
    intArrayOf(  -1,   0,   0,   0,   0,   0,   0,   -1),
    intArrayOf(  -1,   0,   0,   0,   0,   0,   0,   -1),
    intArrayOf(  -1,  -1,  -1,  -1,  -1,  -1,  -1,   -1),
)

val matrix_2_black = matrix_2_white


fun m2_eval(coordinate: Coordinate, color: TileColor): Int {
    val matrix: Array<IntArray> = if (color == TileColor.WHITE) matrix_2_white else matrix_2_black
    return matrix[coordinate.file.letter.numeric - 1][coordinate.rank.number - 1]
}