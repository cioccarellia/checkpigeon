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
    val weightPiece2: Int,

    // boostrs
    val granularEnemyMaterialLeft: Int,
)


val stdParams = EvalParameters(
    // material
    weightGeneralMaterialScale = 1F,
    weightGeneralMaterialMul = 1,
    weightOwnMaterial = 1,
    weightEnemyMaterial = 1,

    // position
    weightGeneralPositionScale = .5F,
    weightGeneralPositionMul = 0,
    weightOwnPosition = 1,
    weightEnemyPosition = 1,

    // pieces
    weightPiece1 = 1,
    weightPiece2 = 5,

    // boost
    granularEnemyMaterialLeft = 0,
)


val xtraParams = EvalParameters(
    // material
    weightGeneralMaterialScale = 1F,
    weightGeneralMaterialMul = 1,
    weightOwnMaterial = 1,
    weightEnemyMaterial = 1,

    // position
    weightGeneralPositionScale = 1F,
    weightGeneralPositionMul = 1,
    weightOwnPosition = 1,
    weightEnemyPosition = 1,

    // pieces
    weightPiece1 = 10,
    weightPiece2 = 100,

    // boost
    granularEnemyMaterialLeft = 0,
)

fun EvalParams(s: State) = stdParams // if (s.playerColor == TileColor.WHITE) stdParams else xtraParams

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


    fun attributeEnemyLeftoverIncrement(params: EvalParameters, piece1: Int, piece2: Int): Int {
        val initialN = 12

        return when (val leftover = initialN - (piece1 + piece2)) {
            1 -> leftover * params.granularEnemyMaterialLeft * 4
            in 1..2 -> leftover * params.granularEnemyMaterialLeft * 3
            in 3..5 -> leftover * params.granularEnemyMaterialLeft * 2
            in 6..8 -> leftover * params.granularEnemyMaterialLeft * 1
            in 9..11 -> leftover * params.granularEnemyMaterialLeft * 1
            else -> 0
        }
    }

    // val ownPiece1 = state.board.countPiecesWithTypeAndColor<Material.Dama>(state.playerColor)
    // val ownPiece2 = state.board.countPiecesWithTypeAndColor<Material.Damone>(state.playerColor)

    val enemyPiece1 = state.board.countPiecesWithTypeAndColor<Material.Dama>(state.playerColor.not())
    val enemyPiece2 = state.board.countPiecesWithTypeAndColor<Material.Damone>(state.playerColor.not())

    // val selfTotalPieces = ownPiece1 + ownPiece2
    val enemyTotalPieces = enemyPiece1 + enemyPiece2



    var eval = (        parameters.weightGeneralMaterialScale * parameters.weightGeneralMaterialMul * (ownMaterialIncrement - enemyMaterialIncrement)
                    +   parameters.weightGeneralPositionScale * parameters.weightGeneralPositionMul * (ownPositionIncrement - enemyPositionIncrement)
                    +   attributeEnemyLeftoverIncrement(parameters, enemyPiece1, enemyPiece2)
    ).toInt()


    eval = 10 * ownMaterialIncrement - 9 * enemyMaterialIncrement


    if (debug) {
        println("eval = w1 * (ownMaterial - enemyMaterial) = ${parameters.weightGeneralMaterialScale * parameters.weightGeneralMaterialMul} * (${ownMaterialIncrement.grad()} - ${enemyMaterialIncrement.grad(inverse = true)}) = ${(parameters.weightGeneralMaterialScale * parameters.weightGeneralMaterialMul * (ownMaterialIncrement - enemyMaterialIncrement)).toInt().grad()}")
        println("     + w2 * (ownPosition - enemyPosition) = ${parameters.weightGeneralPositionScale * parameters.weightGeneralPositionMul} * (${ownPositionIncrement.grad()} - ${enemyPositionIncrement.grad(inverse = true)}) = ${(parameters.weightGeneralPositionScale * parameters.weightGeneralPositionMul * (ownPositionIncrement - enemyPositionIncrement)).toInt().grad()}")
        println("     + enemyLeftoverMaterialBoost(dame=$enemyPiece1, damoni=$enemyPiece2) = ${(attributeEnemyLeftoverIncrement(parameters, enemyPiece1, enemyPiece2)).grad()}")
        println("     = ${eval.grad()}                                                            " + if (label.isNotBlank()) "[$label]" else "")
    }

    return eval
}