package com.cioccarellia.checkpigeon.eval

import com.cioccarellia.checkpigeon.logic.model.board.Coordinate
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor


val matrix_1_white: Array<IntArray> = arrayOf(
    intArrayOf( 50,  55,  55,  55,  55,  55,  55,  50),
    intArrayOf( 50,  40,  30,  40,  40,  40,  40,  50),
    intArrayOf( 40,  20,  20,  20,  20,  20,  20,  40),
    intArrayOf( 15,  10,  10,  15,  15,  10,  10,  15),
    intArrayOf( 10,   5,   5,  10,  10,   5,   5,  10),
    intArrayOf(  4,   4,   4,   4,   4,   4,   4,   4),
    intArrayOf(  2,   2,   2,   2,   2,   2,   2,   2),
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