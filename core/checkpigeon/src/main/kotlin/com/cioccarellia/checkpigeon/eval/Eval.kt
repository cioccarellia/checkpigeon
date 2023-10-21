package com.cioccarellia.checkpigeon.eval

import com.cioccarellia.checkpigeon.functions.enumPieces
import com.cioccarellia.checkpigeon.logic.board.Board
import com.cioccarellia.checkpigeon.logic.model.material.Material
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor
import com.cioccarellia.checkpigeon.model.State
import com.cioccarellia.checkpigeon.search.grad


data class EvalParameters(
    // material
    val weightGeneralMaterialScale: Float,
    val weightGeneralMaterialMul: Int,
    val weightOwnMaterial: Int,
    val weightEnemyMaterial: Int,

    // position
    val weightGeneralPositionScale: Float,
    val weightGeneralPositionMul: Int,
    val weightOwnPosition: Int,
    val weightEnemyPosition: Int,

    // pieces
    val weightPiece1: Int,
    val weightPiece2: Int
)


val stdParams = EvalParameters(
    // material
    weightGeneralMaterialScale = 1F,
    weightGeneralMaterialMul = 1,
    weightOwnMaterial = 1,
    weightEnemyMaterial = 5,

    // position
    weightGeneralPositionScale = 1F,
    weightGeneralPositionMul = 1,
    weightOwnPosition = 1,
    weightEnemyPosition = 1,

    // pieces
    weightPiece1 = 20,
    weightPiece2 = 50
)

fun EvalParams(s: State) = stdParams

fun computeMaterialIncrement(parameters: EvalParameters, board: Board, color: TileColor): Int {
    val ownPoints1 = board.countPiecesWithTypeAndColor<Material.Dama>(color)
    val ownPoints2 = board.countPiecesWithTypeAndColor<Material.Damone>(color)

    return ownPoints1 * parameters.weightPiece1 + ownPoints2 * parameters.weightPiece2
}

fun computeMaterialWeight(parameters: EvalParameters, board: Board, color: TileColor): Int {
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
    val parameters = EvalParams(state)

    val ownMaterialIncrement = parameters.weightOwnMaterial * computeMaterialIncrement(parameters, state.board, state.playerColor)
    val enemyMaterialIncrement = parameters.weightEnemyMaterial * computeMaterialIncrement(parameters, state.board, state.playerColor.not())

    val ownPositionIncrement = parameters.weightOwnPosition * computeMaterialWeight(parameters, state.board, state.playerColor)
    val enemyPositionIncrement = parameters.weightEnemyPosition * computeMaterialWeight(parameters, state.board, state.playerColor.not())

    val eval = (        parameters.weightGeneralMaterialScale * parameters.weightGeneralMaterialMul * (ownMaterialIncrement - enemyMaterialIncrement)
                    +   parameters.weightGeneralPositionScale * parameters.weightGeneralPositionMul * (ownPositionIncrement - enemyPositionIncrement)).toInt()

    if (debug) {
        println("eval = w1 * (ownMaterial - enemyMaterial) = ${parameters.weightGeneralMaterialScale * parameters.weightGeneralMaterialMul} * (${ownMaterialIncrement.grad()} - ${enemyMaterialIncrement.grad(inverse = true)}) = ${(parameters.weightGeneralMaterialScale * parameters.weightGeneralMaterialMul * (ownMaterialIncrement - enemyMaterialIncrement)).toInt().grad()}")
        println("     + w2 * (ownPosition - enemyPosition) = ${parameters.weightGeneralPositionScale * parameters.weightGeneralPositionMul} * (${ownPositionIncrement.grad()} - ${enemyPositionIncrement.grad(inverse = true)}) = ${(parameters.weightGeneralPositionScale * parameters.weightGeneralPositionMul * (ownPositionIncrement - enemyPositionIncrement)).toInt().grad()}")
        println("     = ${eval.grad()}                                                            " + if (label.isNotBlank()) "[$label]" else "")
    }

    return eval
}