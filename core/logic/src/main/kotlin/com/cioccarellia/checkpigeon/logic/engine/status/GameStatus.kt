package com.cioccarellia.checkpigeon.logic.engine.status

import com.cioccarellia.checkpigeon.logic.engine.verifier.RejectionReason
import com.cioccarellia.checkpigeon.logic.model.move.linear.Move
import com.cioccarellia.checkpigeon.logic.model.player.Player
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor

sealed class GameResult {
    object Draw : GameResult()
    class Done(
        val winner: Player,
        val loser: Player
    ) : GameResult()
}

class GameStatus : EngineMoveReceiver {

    var hasGameStarted: Boolean = false
        private set

    var hasGameFinished: Boolean = false
        private set

    /**
     * Whether the game has started but not finished
     * */
    val isAlive: Boolean
        get() = hasGameStarted && !hasGameFinished


    /**
     * Player expected to play
     * */
    var turnColor: TileColor = TileColor.WHITE
        private set

    /**
     * 2 moves count as a turn. (Each time white plays
     * */
    var turnNumber: Int = 1
        private set

    /**
     * Total number of moves played so far
     * */
    var moveCount: Int = 0
        private set

    var gameResult: GameResult? = null
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

    override fun onMoveRejected(details: RejectionReason) {

    }

    override fun onGameStarted() {
        hasGameStarted = true
        hasGameFinished = false
        turnNumber = 1
        moveCount = 0
        turnColor = TileColor.WHITE
        gameResult = null
    }

    override fun onGameEnded() {
        hasGameStarted = true
        hasGameFinished = false

        // TODO
    }
}