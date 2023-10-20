package com.cioccarellia.checkpigeon.eval

import com.cioccarellia.checkpigeon.logic.model.board.Coordinate
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor


const val P_FACTOR_1 = 1F
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
    val value = matrix[coordinate.file.letter.numeric - 1][coordinate.rank.number - 1]

    return Math.round(value * P_FACTOR_1)
}



const val P_FACTOR_2 = 2F

const val P = 1
const val N = 1

val matrix_2_white: Array<IntArray> = arrayOf(
    intArrayOf( -50 * N, -30 * N, -50 * N, -30 * N, -30 * N, -50 * N, -30 * N,  -50 * N),
    intArrayOf( -30 * N, -30 * N,   0 * P,   0 * P,  0  * P,   0 * P, -30 * N,  -30 * N),
    intArrayOf( -50 * N,   0 * P,  25 * P,  35 * P,  35 * P,  25 * P,   0 * P,  -50 * N),
    intArrayOf( -30 * N,   0 * P,  35 * P,  50 * P,  50 * P,  35 * P,   0 * P,  -30 * N),
    intArrayOf( -30 * N,   0 * P,  35 * P,  50 * P,  50 * P,  35 * P,   0 * P,  -30 * N),
    intArrayOf( -50 * N,   0 * P,  25 * P,  35 * P,  35 * P,  25 * P,   0 * P,  -50 * N),
    intArrayOf( -30 * N, -30 * N,   0 * P,   0 * P,   0 * P,   0 * P, -30 * N,  -30 * N),
    intArrayOf( -50 * N, -30 * N, -50 * N, -30 * N, -30 * N, -50 * N, -30 * N,  -50 * N),
)

val matrix_2_black = matrix_2_white



fun m2_eval(coordinate: Coordinate, color: TileColor): Int {
    val matrix: Array<IntArray> = if (color == TileColor.WHITE) matrix_2_white else matrix_2_black
    return Math.round(matrix[coordinate.file.letter.numeric - 1][coordinate.rank.number - 1] * P_FACTOR_2)
}