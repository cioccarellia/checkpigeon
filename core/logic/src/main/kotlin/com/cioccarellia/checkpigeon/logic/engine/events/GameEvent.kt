package com.cioccarellia.checkpigeon.logic.engine.events

import com.cioccarellia.checkpigeon.logic.model.move.linear.Move
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor

/**
 * Represents all the events that can be handled by [Engine]
 * */
sealed class GameEvent {

    data object StartGame : GameEvent()

    data object DumpGame : GameEvent()

    data class SubmissionRequest(
        // unsafe move to execute, has to be checked
        val submittedMove: Move
    ) : GameEvent()

    data object ActionRequest : GameEvent()

}