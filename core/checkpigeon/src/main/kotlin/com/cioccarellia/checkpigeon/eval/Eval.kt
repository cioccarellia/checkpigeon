package com.cioccarellia.checkpigeon.eval

import com.cioccarellia.checkpigeon.functions.enumPieces
import com.cioccarellia.checkpigeon.logic.board.Board
import com.cioccarellia.checkpigeon.logic.model.material.Material
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor
import com.cioccarellia.checkpigeon.model.State
import com.cioccarellia.checkpigeon.search.grad


const val WEIGHT_LVL_1 = 30
const val WEIGHT_LVL_2 = 60


fun computeMaterialIncrement(board: Board, color: TileColor): Int {
    val ownPoints1 = board.countPiecesWithTypeAndColor<Material.Dama>(color)
    val ownPoints2 = board.countPiecesWithTypeAndColor<Material.Damone>(color)

    return ownPoints1 * WEIGHT_LVL_1 + ownPoints2 * WEIGHT_LVL_2
}

fun computeMaterialWeight(board: Board, color: TileColor): Int {
    var accumulator = 0

    board.enumPieces(color).forEach {
        accumulator += when (it.first) {
            is Material.Dama -> m1_eval(it.second, color)
            is Material.Damone -> m2_eval(it.second, color)
            Material.Empty -> 0
        }
    }

    return accumulator
}





fun Eval(state: State, debug: Boolean = false, label: String = ""): Int {
    val ownMaterialIncrement = computeMaterialIncrement(state.board, state.playerColor)
    val enemyMaterialIncrement = computeMaterialIncrement(state.board, state.playerColor.not())

    val ownPositionIncrement = computeMaterialWeight(state.board, state.playerColor)
    val enemyPositionIncrement = computeMaterialWeight(state.board, state.playerColor.not())

    val eval = ownMaterialIncrement - enemyMaterialIncrement + ownPositionIncrement - enemyPositionIncrement

    if (debug) {
        println("eval = ownMaterial - enemyMaterial = ${ownMaterialIncrement.grad()} - ${enemyMaterialIncrement.grad(inverse = true)} = ${(ownMaterialIncrement - enemyMaterialIncrement).grad()}")
        println("     + ownPosition - enemyPosition = ${ownPositionIncrement.grad()} - ${enemyPositionIncrement.grad(inverse = true)} = ${(ownPositionIncrement - enemyPositionIncrement).grad()}")
        println("     = ${eval.grad()}                                                            " + if (label.isNotBlank()) "[$label]" else "")
    }

    return eval

}