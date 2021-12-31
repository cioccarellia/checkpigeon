package com.cioccarellia.checkpigeon.logic.engine.status

import com.cioccarellia.checkpigeon.logic.engine.verifier.RejectionReason
import com.cioccarellia.checkpigeon.logic.model.move.verbose.VerboseMove
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor.BLACK
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor.WHITE

/**
 * Keeps tracks of all the played moves
 * */
class GameHistory : EngineVerboseMoveReceiver {
    private val whiteMoves: MutableList<VerboseMove> = mutableListOf()
    private val blackMoves: MutableList<VerboseMove> = mutableListOf()

    override fun onVerboseMoveAccepted(move: VerboseMove) {
        when (move.playingColor) {
            BLACK -> blackMoves.add(move)
            WHITE -> whiteMoves.add(move)
        }
    }

    override fun onVerboseMoveRejected(details: RejectionReason) {}

    override fun onGameStarted() = Unit

    override fun onGameEnded() = Unit
}