package com.cioccarellia.checkpigeon.logic.engine

import com.cioccarellia.checkpigeon.logic.board.Board
import com.cioccarellia.checkpigeon.logic.model.move.linear.Move
import com.cioccarellia.checkpigeon.logic.model.move.verbose.VerboseMove

@OptIn(ExperimentalStdlibApi::class)
internal fun Move.verbose(board: Board) = VerboseMove(
    moveType = moveType,
    playingColor = playingColor,

    start = start,
    startMaterial = board[start],

    end = end,
    endMaterial = board[end],

    captures = captures,
    capturedMaterial = buildList(captures.size) {
        captures.forEachIndexed { index, coordinate ->
            add(index, board[coordinate])
        }
    },

    blows = blows,
    blownMaterial = if (blows == null) null else board[blows.first]
)