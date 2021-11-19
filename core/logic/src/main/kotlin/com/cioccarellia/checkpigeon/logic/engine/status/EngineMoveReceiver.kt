package com.cioccarellia.checkpigeon.logic.engine.status

import com.cioccarellia.checkpigeon.logic.model.move.Move
import com.cioccarellia.checkpigeon.logic.model.move.VerboseMove

interface EngineMoveReceiver {
    fun onMoveAccepted(move: Move)
}

interface EngineVerboseMoveReceiver {
    fun onVerboseMoveAccepted(move: VerboseMove)
}