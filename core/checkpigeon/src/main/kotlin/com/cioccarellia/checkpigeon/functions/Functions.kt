package com.cioccarellia.checkpigeon.functions

import com.cioccarellia.checkpigeon.generator.gen_all
import com.cioccarellia.checkpigeon.logic.board.Board
import com.cioccarellia.checkpigeon.logic.model.material.Material
import com.cioccarellia.checkpigeon.logic.model.move.linear.Move
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor
import com.cioccarellia.checkpigeon.model.State


fun Utility(state: State): Int {
    assert(IsTerminal(state))

    val winner = CalcWinner(state.board)!!

    return if (winner == state.playerColor) (1) else (-1)
}


const val WEIGHT_LVL_1 = 1
const val WEIGHT_LVL_2 = 10

fun Evaluation(state: State): Int {
    fun computeMaterialIncrement(color: TileColor): Int {
        val pts =
            state.board.countPiecesWithTypeAndColor<Material.Dama>(color) to state.board.countPiecesWithTypeAndColor<Material.Damone>(
                color
            )

        return pts.first * WEIGHT_LVL_1 + pts.second * WEIGHT_LVL_2
    }

    fun computeMaterialWeight(color: TileColor): Int {
        var accumulator = 0

        state.board.enumPieces(color).forEach {
            accumulator += when (it.first) {
                is Material.Dama -> m1_eval(it.second, color)
                is Material.Damone -> m2_eval(it.second, color)
                Material.Empty -> 0
            }
        }

        return accumulator
    }


    val ownMaterialIncrement = computeMaterialIncrement(state.playerColor)
    val enemyMaterialIncrement = computeMaterialIncrement(state.playerColor.not())

    val ownPositionIncrement = computeMaterialWeight(state.playerColor)
    val enemyPositionIncrement = computeMaterialWeight(state.playerColor.not())


    return ownMaterialIncrement - enemyMaterialIncrement + ownPositionIncrement - enemyPositionIncrement
}


fun ToMove(state: State): TileColor {
    return state.playerColor
}


fun CalcWinner(board: Board): TileColor? {
    val whiteMaterial = board.countPieces(TileColor.WHITE);
    if (whiteMaterial == 0) {
        return TileColor.BLACK
    }


    val blackMaterial = board.countPieces(TileColor.WHITE);
    if (blackMaterial == 0) {
        return TileColor.WHITE
    }


    if (Actions(board, TileColor.WHITE).isEmpty()) {
        // No more valid moves: stalling
        return TileColor.BLACK
    }

    if (Actions(board, TileColor.BLACK).isEmpty()) {
        // No more valid moves: stalling
        return TileColor.WHITE
    }

    return null
}


fun IsTerminal(board: Board, playingColor: TileColor): Boolean {
    val winner = CalcWinner(board)
    return winner != null && winner != playingColor
}

fun IsTerminal(state: State): Boolean {
    return IsTerminal(state.board, state.playerColor)
}


fun Result(state: State, move: Move): State {
    assert(!state.isGameOver)

    val newBoard = state.board.copyAndApplyMove(move)

    return State(
        board = newBoard,
        playerColor = state.playerColor.not(),
        isGameOver = IsTerminal(newBoard, state.playerColor.not())
    )
}


fun Actions(board: Board, playingColor: TileColor): List<Move> {
    return gen_all(board, playingColor)
}


fun Actions(state: State): List<Move> {
    return gen_all(state.board, state.playerColor)
}




