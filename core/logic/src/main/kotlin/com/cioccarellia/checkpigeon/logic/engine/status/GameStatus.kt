package com.cioccarellia.checkpigeon.logic.engine.status

import com.cioccarellia.checkpigeon.logic.model.move.linear.Move
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor

class GameStatus : EngineMoveReceiver {

    var hasGameStarted: Boolean = false
        private set

    var hasGameFinished: Boolean = false
        private set

    val isAlive: Boolean
        get() = hasGameStarted && !hasGameFinished

    var turnNumber: Int = 1
        private set

    var turnColor: TileColor = TileColor.WHITE
        private set

    var moveCount: Int = 0
        private set

    var winner: TileColor? = null
        private set


    override fun onMoveAccepted(move: Move) {
        // Change turn and move
        when (move.playingColor) {
            TileColor.BLACK -> {
                turnColor = TileColor.WHITE
                turnNumber++
            }
            TileColor.WHITE -> {
                turnColor = TileColor.BLACK
            }
        }

        moveCount++
    }

    override fun onGameStarted() {
        hasGameStarted = true
        hasGameFinished = false
        turnNumber = 1
        moveCount = 0
        turnColor = TileColor.WHITE
        winner = null
    }

    override fun onGameEnded() {
        TODO("Not yet implemented")
    }
}