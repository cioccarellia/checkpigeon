package com.cioccarellia.checkpigeon.logic.engine.events

import com.cioccarellia.checkpigeon.logic.model.move.linear.Move
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor

/**
 * Represents all the events that can be handled by [Engine]
 * */
sealed class Event {
    class Submission(
        val move: Move
    ) : Event()

    class Resignation(
        val color: TileColor
    ) : Event()

    class DrawRequest(
        val color: TileColor
    ) : Event()
}