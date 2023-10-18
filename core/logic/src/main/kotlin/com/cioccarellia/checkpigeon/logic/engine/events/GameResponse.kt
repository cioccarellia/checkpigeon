package com.cioccarellia.checkpigeon.logic.engine.events

import com.cioccarellia.checkpigeon.logic.board.Board
import com.cioccarellia.checkpigeon.logic.engine.Engine
import com.cioccarellia.checkpigeon.logic.engine.status.EngineStatus
import com.cioccarellia.checkpigeon.logic.engine.verifier.RejectionReason
import com.cioccarellia.checkpigeon.logic.model.move.linear.Move
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor

sealed class GameResponse {


    data object Ready : GameResponse()

    data class GameInfo(
        val board: Board,
        val status: EngineStatus
    ) : GameResponse()


    data object Ack : GameResponse()


    data object MoveEvent : GameResponse()

}