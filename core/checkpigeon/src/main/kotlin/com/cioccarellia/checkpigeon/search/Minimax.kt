package com.cioccarellia.checkpigeon.search

import com.cioccarellia.checkpigeon.eval.Eval
import com.cioccarellia.checkpigeon.functions.*
import com.cioccarellia.checkpigeon.logic.model.move.MoveType
import com.cioccarellia.checkpigeon.logic.model.move.linear.Move
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor
import com.cioccarellia.checkpigeon.model.State
import kotlin.math.max
import kotlin.math.min


var nodes = 0
var cached = 0

val lookup: MutableMap<Pair<Int, TileColor>, Pair<Int, Move>> = mutableMapOf()

fun MaxValue(state: State, depth: Int, _alpha: Int, _beta: Int): Pair<Int, Move?> {
    nodes++
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

    actions = actions.sortedBy { it.moveType == MoveType.Capture }

    for (a in actions) {
        if (lookup.containsKey(state.board.hash() to state.playerColor)) {
            cached++
            return lookup[state.board.hash() to state.playerColor]!!
        }

        var (v2, a2) = MinValue(Result(state, a), depth - 1, alpha, _beta)

        if (v2 > v1) {
            v1 = v2
            move = a

            alpha = max(alpha, v1)
        }

        if (v1 >= _beta) {
            lookup[state.board.hash() to state.playerColor] = v1 to move!!
            return v1 to move
        }
    }

    if (SearchParameters.Debug.DEBUG_ALL) {
        dbg_tree(depth, v1, state.playerColor, move)
    }

    if (move != null) {
        lookup[state.board.hash() to state.playerColor] = v1 to move
    }

    return v1 to move
}


fun MinValue(state: State, depth: Int, _alpha: Int, _beta: Int): Pair<Int, Move?> {
    nodes++
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

    actions = actions.sortedBy { it.moveType == MoveType.Capture }

    for (a in actions) {
        if (lookup.containsKey(state.board.hash() to state.playerColor)) {
            cached++;
            return lookup[state.board.hash() to state.playerColor]!!
        }

        var (v2, a2) = MaxValue(Result(state, a), depth - 1, _alpha, beta)


        if (v2 < v1) {
            v1 = v2
            move = a

            beta = min(beta, v1)
        }

        if (v1 <= _alpha) {
            lookup[state.board.hash() to state.playerColor] = v1 to move!!
            return v1 to move
        }
    }


    if (SearchParameters.Debug.DEBUG_ALL) {
        dbg_tree(depth, v1, state.playerColor, move)
    }

    if (move != null) {
        lookup[state.board.hash() to state.playerColor] = v1 to move
    }
    return v1 to move
}


fun MiniMaxAlphaBeta(state: State, silent: Boolean = false): Move? {
    val hash = state.board.hash()
    if (lookup.containsKey(hash to state.playerColor)) {
        if (!silent) {
            println("Instant lookup for hash $hash, 0 nodes processed")

            println("nodes processed                [in this run]: 0")
            println("lookup table size              [cumulative ]: ${lookup.size} positions evaluated")
        }

        return lookup[hash to state.playerColor]!!.second
    }

    nodes = 0;
    cached = 0;


    val player = ToMove(state)
    val (evaluation, move) = MaxValue(state, SearchParameters.MAX_DEPTH, SearchParameters.MIN, SearchParameters.MAX)

    if (move != null) {
        if (!silent) {
            println("Saving master hash $hash to lookup")
        }
        lookup[hash to state.playerColor] = evaluation to move
    }

    if (!silent) {
        if (SearchParameters.Debug.DEBUG_ALL || SearchParameters.Debug.DEBUG_EVAL) {
            println()
            println()
            println()
            println()
            println()
            println()
            dbg_tree(0, evaluation, player, move)

            Eval((state), debug = true, "previousEval (own)")
            if (move != null) {
                Eval(Result(state, move), debug = true, "currentEval (enemy)")
            }

            println("nodes processed                [in this run]: $nodes nodes")
            println("cache hits                     [in this run]: $cached positions added to lookup table")
            println("lookup table size              [cumulative ]: ${lookup.size} positions evaluated")
            println("END_DBG_MINIMAX")
        }
    }


    return move
}