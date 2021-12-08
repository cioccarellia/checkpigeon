package com.cioccarellia.checkpigeon.logic.engine.status

import com.cioccarellia.checkpigeon.logic.engine.verifier.RejectionDetails
import com.cioccarellia.checkpigeon.logic.model.move.linear.Move
import com.cioccarellia.checkpigeon.logic.model.move.verbose.VerboseMove

interface EngineMoveReceiver {
    fun onGameStarted()
    fun onGameEnded()
    fun onMoveAccepted(move: Move)
    fun onMoveRejected(details: RejectionDetails)
}

interface EngineVerboseMoveReceiver {
    fun onGameStarted()
    fun onGameEnded()
    fun onVerboseMoveAccepted(move: VerboseMove)
    fun onVerboseMoveRejected(details: RejectionDetails)
}