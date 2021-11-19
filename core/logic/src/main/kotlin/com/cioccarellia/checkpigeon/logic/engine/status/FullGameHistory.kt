package com.cioccarellia.checkpigeon.logic.engine.status

import com.cioccarellia.checkpigeon.logic.model.move.VerboseMove
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor.BLACK
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor.WHITE

data class FullGameHistory(
    val whiteMoves: MutableList<VerboseMove> = mutableListOf(),
    val blackMoves: MutableList<VerboseMove> = mutableListOf(),
) : EngineVerboseMoveReceiver {
    override fun onVerboseMoveAccepted(move: VerboseMove) {
        when (move.playingColor) {
            BLACK -> blackMoves.add(move)
            WHITE -> whiteMoves.add(move)
        }
    }
}