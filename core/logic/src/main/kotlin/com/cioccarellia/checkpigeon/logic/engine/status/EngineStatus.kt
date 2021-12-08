package com.cioccarellia.checkpigeon.logic.engine.status

import com.cioccarellia.checkpigeon.logic.board.Board
import com.cioccarellia.checkpigeon.logic.engine.verbose
import com.cioccarellia.checkpigeon.logic.engine.verifier.RejectionDetails
import com.cioccarellia.checkpigeon.logic.model.move.linear.Move

class EngineStatus {
    val gameStatus = GameStatus()
    val gameHistory = FullGameHistory()

    fun onGameStarted() {
        gameStatus.onGameStarted()
        gameHistory.onGameStarted()
    }

    fun onGameEnded() {
        gameStatus.onGameEnded()
        gameHistory.onGameEnded()
    }

    /**
     * Must be called before the move is executed permanently on the board.
     * */
    fun onMoveAccepted(move: Move, board: Board) {
        gameStatus.onMoveAccepted(move)
        gameHistory.onVerboseMoveAccepted(move.verbose(board))
    }

    fun onMoveRejected(details: RejectionDetails) {
        gameStatus.onMoveRejected(details)
        gameHistory.onVerboseMoveRejected(details)
    }
}
