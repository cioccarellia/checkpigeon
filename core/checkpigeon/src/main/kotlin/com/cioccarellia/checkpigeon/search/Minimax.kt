package com.cioccarellia.checkpigeon.search

import com.cioccarellia.checkpigeon.eval.Eval
import com.cioccarellia.checkpigeon.functions.*
import com.cioccarellia.checkpigeon.logic.console.blue
import com.cioccarellia.checkpigeon.logic.console.green
import com.cioccarellia.checkpigeon.logic.console.red
import com.cioccarellia.checkpigeon.logic.console.yellow
import com.cioccarellia.checkpigeon.logic.model.move.linear.Move
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor
import com.cioccarellia.checkpigeon.model.State
import kotlin.math.max
import kotlin.math.min



fun MaxValue(state: State, depth: Int, _alpha: Int, _beta: Int): Pair<Int, Move?> {
    var alpha = _alpha

        if (depth <= 0 || IsTerminal(state)) {
            return Evaluation(state) to null
        }


    var v1 = SearchParameters.MIN
    var move: Move? = null

    var actions = Actions(state)

    if (SearchParameters.RANDOMIZED) {
        actions = actions.shuffled(SearchParameters.seed())
    }

    for (a in actions) {
        val (v2, a2) = MinValue(Result(state, a), depth - 1, alpha, _beta)

        if (v2 > v1) {
            v1 = v2
            move = a

            alpha = max(alpha, v1)
        }

        if (v1 >= _beta) {
            return v1 to move
        }
    }

    if (SearchParameters.Debug.DEBUG_ALL) {
        dbg_tree(depth, v1, state.playerColor, move)
    }

    return v1 to move
}


fun MinValue(state: State, depth: Int, _alpha: Int, _beta: Int): Pair<Int, Move?> {
    var beta = _beta

        if (depth <= 0 || IsTerminal(state)) {
            return Evaluation(state) to null
        }

    var v1 = SearchParameters.MAX
    var move: Move? = null

    var actions = Actions(state)

    if (SearchParameters.RANDOMIZED) {
        actions = actions.shuffled(SearchParameters.seed())
    }

    for (a in actions) {
        val (v2, a2) = MaxValue(Result(state, a), depth - 1, _alpha, beta)

        if (v2 < v1) {
            v1 = v2
            move = a

            beta = min(beta, v1)
        }

        if (v1 <= _alpha) {
            return v1 to move
        }
    }


    if (SearchParameters.Debug.DEBUG_ALL) {
        dbg_tree(depth, v1, state.playerColor, move)
    }

    return v1 to move
}


fun MiniMaxAlphaBeta(state: State): Move? {
    val player = ToMove(state)
    val (utility, move) = MaxValue(state, SearchParameters.MAX_DEPTH, SearchParameters.MIN, SearchParameters.MAX)

    if (SearchParameters.Debug.DEBUG_ALL || SearchParameters.Debug.DEBUG_EVAL) {
        println()
        println()
        println()
        println()
        println()
        println()
        dbg_tree(0, utility, player, move)

        Eval((state), debug = true, "previousEval")
        Eval((Result(state, move!!)), debug = true, "currentEval")
        println("END_DBG_MINIMAX")
    }

    return move
}