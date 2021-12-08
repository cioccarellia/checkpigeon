package com.cioccarellia.checkpigeon.logic.engine.verifier

import com.cioccarellia.checkpigeon.logic.board.Board
import com.cioccarellia.checkpigeon.logic.engine.status.GameStatus
import com.cioccarellia.checkpigeon.logic.engine.verifier.VerificationResult.*
import com.cioccarellia.checkpigeon.logic.model.move.linear.Move

object MoveVerifier {

    fun verifyMove(
        move: Move,
        board: Board,
        gameStatus: GameStatus
    ): VerificationResult {

        if (move.playingColor != gameStatus.turnColor) {
            return Failed(RejectionDetails.PLAYER_MISMATCH)
        }


        return Passed(move = move)
    }

}