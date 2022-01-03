package com.cioccarellia.checkpigeon.logic.engine.status

import com.cioccarellia.checkpigeon.logic.board.Board
import com.cioccarellia.checkpigeon.logic.engine.game.GameResult
import com.cioccarellia.checkpigeon.logic.engine.verifier.RejectionReason
import com.cioccarellia.checkpigeon.logic.model.move.verbose.VerboseMove
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor.BLACK
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor.WHITE

/**
 * Keeps tracks of all the played moves
 * */
class GameHistory : EngineVerboseMoveReceiver {
    private val whiteMoves: MutableList<VerboseMove> = mutableListOf()
    private val blackMoves: MutableList<VerboseMove> = mutableListOf()

    fun boardAt(
        presentMoveNumber: Int,
        pastMoveNumber: Int,
        board: Board,
    ): Board {
        require(presentMoveNumber > 0 && pastMoveNumber > 0 && pastMoveNumber <= presentMoveNumber) {
            "Inconsistent presentMoveNumber=$presentMoveNumber and pastMoveNumber=$pastMoveNumber values"
        }

        if (pastMoveNumber != presentMoveNumber) {
            for (descendingMoveNumber in presentMoveNumber downTo pastMoveNumber) {
                board.executeMoveBackward(getVerboseMoveForMoveNumber(descendingMoveNumber))
            }
        }

        return board
    }

    private fun getVerboseMoveForMoveNumber(moveNumber: Int) = if (moveNumber % 2 != 0) {
        // Odd moves (1, 3, 5, ...) are white's
        val index = (moveNumber - 1) / 2
        whiteMoves[index]
    } else {
        // Even moves (2, 4, 6, ...) are black's
        val index = (moveNumber / 2) - 1
        blackMoves[index]
    }

    override fun onVerboseMoveAccepted(move: VerboseMove) {
        when (move.playingColor) {
            BLACK -> blackMoves.add(move)
            WHITE -> whiteMoves.add(move)
        }
    }

    override fun onVerboseMoveRejected(details: RejectionReason) {}

    override fun onGameStarted() = Unit

    override fun onGameEnded(result: GameResult) = Unit
}