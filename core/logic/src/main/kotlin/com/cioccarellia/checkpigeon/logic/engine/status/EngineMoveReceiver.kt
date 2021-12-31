package com.cioccarellia.checkpigeon.logic.engine.status

import com.cioccarellia.checkpigeon.logic.engine.game.GameResult
import com.cioccarellia.checkpigeon.logic.engine.verifier.RejectionReason
import com.cioccarellia.checkpigeon.logic.model.move.linear.Move
import com.cioccarellia.checkpigeon.logic.model.move.verbose.VerboseMove

/**
 * Implements a simple move receiver & status holder (only current state)
 * */
interface EngineMoveReceiver {
    fun onGameStarted()
    fun onGameEnded(result: GameResult)
    fun onMoveAccepted(move: Move)
    fun onMoveRejected(details: RejectionReason)
}

/**
 * Implements a verbose move receiver & status holder (access to previous states)
 * */
interface EngineVerboseMoveReceiver {
    fun onGameStarted()
    fun onGameEnded(result: GameResult)
    fun onVerboseMoveAccepted(move: VerboseMove)
    fun onVerboseMoveRejected(details: RejectionReason)
}