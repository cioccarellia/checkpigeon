package com.cioccarellia.checkpigeon.logic.engine.status

import com.cioccarellia.checkpigeon.logic.board.Board
import com.cioccarellia.checkpigeon.logic.engine.game.GameResult
import com.cioccarellia.checkpigeon.logic.engine.verbose
import com.cioccarellia.checkpigeon.logic.engine.verifier.RejectionReason
import com.cioccarellia.checkpigeon.logic.model.move.linear.Move

/**
 * Keeps track of the game ongoing status
 * */
class EngineStatus {
    val gameStatus = GameStatus()
    val gameHistory = GameHistory()

    fun onGameStarted() {
        gameStatus.onGameStarted()
        gameHistory.onGameStarted()
    }

    fun onGameEnded(result: GameResult) {
        gameStatus.onGameEnded(result)
        gameHistory.onGameEnded(result)
    }

    /**
     * Must be called before the move is executed permanently on the board.
     * */
    fun onMoveAccepted(move: Move, board: Board) {
        gameStatus.onMoveAccepted(move)
        gameHistory.onVerboseMoveAccepted(move.verbose(board))
    }

    fun onMoveRejected(details: RejectionReason) {
        gameStatus.onMoveRejected(details)
        gameHistory.onVerboseMoveRejected(details)
    }
}
