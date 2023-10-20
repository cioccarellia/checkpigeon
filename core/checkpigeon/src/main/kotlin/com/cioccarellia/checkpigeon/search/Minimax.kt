package com.cioccarellia.checkpigeon.search

import com.cioccarellia.checkpigeon.functions.*
import com.cioccarellia.checkpigeon.logic.model.move.linear.Move
import com.cioccarellia.checkpigeon.model.State
import kotlin.math.max
import kotlin.math.min


const val USE_EVAL = true

const val MAX = +1_000_000
const val MIN = -1_000_000

const val MAX_DEPTH = 2
const val RANDOMIZED = true




fun MaxValue(state: State, depth: Int, _alpha: Int, _beta: Int): Pair<Int, Move?> {
    var alpha = _alpha

    // if (IsTerminal(state)) {
    //     return Utility(state) to null
    // }
    if (USE_EVAL) {
        if (depth <= 0 || IsTerminal(state)) {
            return Evaluation(state) to null
        }
    } else {
        if (IsTerminal(state)) {
            return Utility(state) to null
        }
    }



    var v1 = MIN
    var move: Move? = null

    val actions = Actions(state).apply {
        if (RANDOMIZED) {
            this.shuffled()
        }
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

    return v1 to move
}


fun MinValue(state: State, depth: Int, _alpha: Int, _beta: Int): Pair<Int, Move?> {
    var beta = _beta

    //if (IsTerminal(state)) {
    //    return Utility(state) to null
    //}
    if (USE_EVAL) {
        if (depth <= 0 || IsTerminal(state)) {
            return Evaluation(state) to null
        }
    } else {
        if (IsTerminal(state)) {
            return Utility(state) to null
        }
    }

    var v1 = MAX
    var move: Move? = null

    val actions = Actions(state).apply {
        if (RANDOMIZED) {
            this.shuffled()
        }
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

    return v1 to move
}



fun MiniMaxAlphaBeta(state: State): Move? {
    val player = ToMove(state)
    val (utility, move) = MaxValue(state, MAX_DEPTH, MIN, MAX)

    return move
}