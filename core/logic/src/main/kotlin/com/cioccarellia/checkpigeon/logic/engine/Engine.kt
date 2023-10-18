package com.cioccarellia.checkpigeon.logic.engine

import com.cioccarellia.checkpigeon.debug.CustomLogger
import com.cioccarellia.checkpigeon.debug.d
import com.cioccarellia.checkpigeon.debug.w
import com.cioccarellia.checkpigeon.ext.findPlayerWithColor
import com.cioccarellia.checkpigeon.logic.board.Board
import com.cioccarellia.checkpigeon.logic.engine.events.GameEvent
import com.cioccarellia.checkpigeon.logic.engine.game.GameResult
import com.cioccarellia.checkpigeon.logic.engine.internal.BoardPrinter
import com.cioccarellia.checkpigeon.logic.engine.status.EngineStatus
import com.cioccarellia.checkpigeon.logic.engine.verifier.MoveVerifier
import com.cioccarellia.checkpigeon.logic.engine.verifier.VerificationResult
import com.cioccarellia.checkpigeon.logic.model.player.Player
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor

/**
 * Checkpigeon game com.cioccarellia.checkpigeon.logic.getEngine
 * */
class Engine(
    /**
     * White and Black players
     * */
    val players: Pair<Player, Player>
) {
    /**
     * Output [GameEvent]-emitting [MutableSharedFlow]
     * */

    /**
     * Game board
     * */
    private val board = Board()

    /**
     * Engine Logger
     * */
    private val engineLogger = CustomLogger(tag = "Engine")

    /**
     * Game status
     * */
    val status = EngineStatus()

    init {
        check(players.first.color == TileColor.WHITE && players.second.color == TileColor.BLACK)

        status.onGameStarted()
    }

    private fun postSubmissionGameEngineLogic() {
        when {
            board.whitePieceCount == 0 -> {
                status.onGameEnded(
                    GameResult.Done(
                        winner = players.findPlayerWithColor(TileColor.BLACK),
                        loser = players.findPlayerWithColor(TileColor.WHITE),
                    )
                )
            }

            board.blackPieceCount == 0 -> {
                status.onGameEnded(
                    GameResult.Done(
                        winner = players.findPlayerWithColor(TileColor.WHITE),
                        loser = players.findPlayerWithColor(TileColor.BLACK),
                    )
                )
            }
        }
    }


    fun emit(event: GameEvent): GameEvent? {
        engineLogger.d("Received event $event")

        when (event) {
            is GameEvent.Message -> {
                engineLogger.d("Event is Message")
                val reply = "Roger that, got ${event.content}."

                return GameEvent.Message(reply)
            }

            is GameEvent.StartGame -> {
                engineLogger.d("Event is StartGame")
                status.onGameStarted()
            }

            is GameEvent.SubmissionProposal -> {
                /**
                 * We received a Move Submission Proposal.
                 * [Engine] only accepts [SubmissionRequest]s, since the engine itself is the
                 * part deciding whether moves get approved or rejected.
                 */
                when (event) {
                    is GameEvent.SubmissionProposal.SubmissionRequest -> {
                        // A move has been played
                        when (val verification =
                            MoveVerifier.verifyMove(event.submittedMove, board, status.gameStatus)) {
                            is VerificationResult.Passed -> {
                                status.onMoveAccepted(verification.move, board)
                                board.executeMoveForward(verification.move)

                                postSubmissionGameEngineLogic()

                                return GameEvent.SubmissionProposal.SubmissionAccepted(
                                    processedMove = verification.move,
                                    message = "Verified by CheckPigeon Engine"
                                )
                            }

                            is VerificationResult.Failed -> {
                                status.onMoveRejected(verification.rejectionReason)

                                return GameEvent.SubmissionProposal.SubmissionRejected(
                                    rejectionReason = verification.rejectionReason,
                                    message = "Rejected by CheckPigeon Engine"
                                )
                            }
                        }
                    }

                    else -> {
                        engineLogger.w("Received a SubmissionProposal event which should not be handled by the Engine.")
                    }
                }
            }

            is GameEvent.Resignation -> {
                status.onGameEnded(
                    GameResult.Done(
                        winner = players.findPlayerWithColor(event.color),
                        loser = players.findPlayerWithColor(!event.color),
                    )
                )
            }

            is GameEvent.DrawProposal -> {
                val acceptsDraw = true

                if (acceptsDraw) {
                    status.onGameEnded(
                        GameResult.Draw
                    )
                }
            }
        }

        TODO()
    }

    fun stdoutBoard(
        colorPerspective: TileColor = status.gameStatus.turnColor,
        highlights: Highlights = emptyList()
    ) {
        BoardPrinter.stdout(board, colorPerspective, highlights)
    }

    fun expose() = board
}