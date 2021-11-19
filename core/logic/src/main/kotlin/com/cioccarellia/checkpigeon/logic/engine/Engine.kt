package com.cioccarellia.checkpigeon.logic.engine

import com.cioccarellia.checkpigeon.logic.board.Board
import com.cioccarellia.checkpigeon.logic.engine.events.Event
import com.cioccarellia.checkpigeon.logic.engine.status.FullGameHistory
import com.cioccarellia.checkpigeon.logic.engine.status.GameStatus
import com.cioccarellia.checkpigeon.logic.model.player.Player
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Checkpigeon game engine
 * */
class Engine(
    val players: Pair<Player, Player>,
    val inputFlow: SharedFlow<Event>
) {
    /**
     * Game board
     * */
    private val board = Board()

    init {
        check(players.first.color == TileColor.WHITE && players.second.color == TileColor.BLACK)

        CoroutineScope(Dispatchers.IO).launch {
            hookInputFlow()
        }

    }

    public val outputFlow = MutableSharedFlow<Event>()


    private suspend fun hookInputFlow() = inputFlow.collect { event ->
        when (event) {
            is Event.DrawRequest -> TODO()
            is Event.Resignation -> TODO()
            is Event.Submission -> TODO()
        }
    }


    /**
     * Collects and preserves information about the game.
     * */
    object Status {
        val gameStatus = GameStatus()
        val gameHistory = FullGameHistory()
    }
}