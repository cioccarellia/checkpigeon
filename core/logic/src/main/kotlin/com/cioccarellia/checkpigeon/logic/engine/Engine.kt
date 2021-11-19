package com.cioccarellia.checkpigeon.logic.engine

import com.cioccarellia.checkpigeon.logic.board.Board
import com.cioccarellia.checkpigeon.logic.engine.status.FullGameHistory
import com.cioccarellia.checkpigeon.logic.engine.status.GameStatus
import com.cioccarellia.checkpigeon.logic.model.player.Player

class Engine(
    val players: Pair<Player, Player>,
) {
    /**
     * Game board
     * */
    private val board = Board()




    /**
     * Collects and preserves information about the game.
     * */
    object Status {
        val gameStatus = GameStatus()
        val gameHistory = FullGameHistory()
    }
}