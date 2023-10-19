package com.cioccarellia.checkpigeon.logic.engine.events

import com.cioccarellia.checkpigeon.logic.board.Board
import com.cioccarellia.checkpigeon.logic.engine.status.EngineStatus

sealed class GameResponse {


    data object Ready : GameResponse()

    data class GameInfo(
        val board: Board,
        val status: EngineStatus
    ) : GameResponse()


    data object Ack : GameResponse()


    data object MoveEvent : GameResponse()

}