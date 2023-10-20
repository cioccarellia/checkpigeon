package com.cioccarellia.checkpigeon.functions

import com.cioccarellia.checkpigeon.logic.model.board.Coordinate
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor


val matrix_1_white: Array<IntArray> = arrayOf(
    intArrayOf(100, 200, 200, 200, 200, 200, 200, 100),
    intArrayOf( 50, 100, 100, 100, 100, 100, 100,  50),
    intArrayOf( 50, 100, 100, 100, 100, 100, 100,  50),
    intArrayOf( 10,  50,  50,  50,  50,  50,  50,  10),
    intArrayOf( 10,  50,  50,  50,  50,  50,  50,  10),
    intArrayOf(  0,   0,   0,   0,   0,   0,   0,   0),
    intArrayOf(  0,   0,   0,   0,   0,   0,   0,   0),
    intArrayOf(  0,   0,   0,   0,   0,   0,   0,   0),
)


val matrix_1_black: Array<IntArray> = matrix_1_white.reversed().toTypedArray()


fun m1_eval(coordinate: Coordinate, color: TileColor): Int {
    val matrix: Array<IntArray> = if (color == TileColor.WHITE) matrix_1_white else matrix_1_black
    return matrix[coordinate.file.letter.numeric - 1][coordinate.rank.number - 1]
}



val matrix_2_white: Array<IntArray> = arrayOf(
    intArrayOf( 10,  10,  10,  10,  10,  10,  10,  10),
    intArrayOf( 10, 100, 100, 100, 100, 100, 100,  10),
    intArrayOf( 10, 100, 100, 100, 100, 100, 100,  10),
    intArrayOf( 10, 100, 100, 100, 100, 100, 100,  10),
    intArrayOf( 10, 100, 100, 100, 100, 100, 100,  10),
    intArrayOf( 10, 100, 100, 100, 100, 100, 100,  10),
    intArrayOf( 10, 100, 100, 100, 100, 100, 100,  10),
    intArrayOf( 10,  10,  10,  10,  10,  10,  10,  10),
)

val matrix_2_black = matrix_2_white


fun m2_eval(coordinate: Coordinate, color: TileColor): Int {
    val matrix: Array<IntArray> = if (color == TileColor.WHITE) matrix_2_white else matrix_2_black
    return matrix[coordinate.file.letter.numeric - 1][coordinate.rank.number - 1]
}