package com.cioccarellia.checkpigeon.logic.engine

import com.cioccarellia.checkpigeon.annotations.FromEngine
import com.cioccarellia.checkpigeon.annotations.ToEngine
import com.cioccarellia.checkpigeon.debug.CustomLogger
import com.cioccarellia.checkpigeon.debug.d
import com.cioccarellia.checkpigeon.debug.w
import com.cioccarellia.checkpigeon.ext.findPlayerWithColor
import com.cioccarellia.checkpigeon.logic.board.Board
import com.cioccarellia.checkpigeon.logic.engine.events.Event
import com.cioccarellia.checkpigeon.logic.engine.game.GameResult
import com.cioccarellia.checkpigeon.logic.engine.internal.BoardPrinter
import com.cioccarellia.checkpigeon.logic.engine.status.EngineStatus
import com.cioccarellia.checkpigeon.logic.engine.verifier.MoveVerifier
import com.cioccarellia.checkpigeon.logic.engine.verifier.VerificationResult
import com.cioccarellia.checkpigeon.logic.model.player.Player
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Checkpigeon game com.cioccarellia.checkpigeon.logic.getEngine
 * */
class Engine(
    /**
     * White and Black players
     * */
    val players: Pair<Player, Player>,

    /**
     * Input [Event]-receiving [MutableSharedFlow]
     * */
    private val inputFlow: SharedFlow<@ToEngine Event>
) {
    /**
     * Output [Event]-emitting [MutableSharedFlow]
     * */
    private val _outputFlow = MutableSharedFlow<@FromEngine Event>()
    val engineOutputFlow: SharedFlow<@FromEngine Event> = _outputFlow.asSharedFlow()

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

        CoroutineScope(Dispatchers.IO).launch {
            // forever running coroutine
            hookInputFlow()
        }

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

    /**
     * Listens on the given input flow for events.
     * */
    private suspend fun hookInputFlow() = inputFlow.collect { event ->
        engineLogger.d("Received event $event")

        when (event) {
            is Event.Message -> {
                engineLogger.d("Event is Message")
                val reply = "Roger that, got ${event.content}."

                _outputFlow.emit(
                    Event.Message(reply)
                )
            }
            is Event.StartGame -> {
                engineLogger.d("Event is StartGame")
                status.onGameStarted()
            }
            is Event.SubmissionProposal -> {
                /**
                 * We received a Move Submission Proposal.
                 * [Engine] only accepts [SubmissionRequest]s, since the engine itself is the
                 * part deciding whether moves get approved or rejected.
                 */
                when (event) {
                    is Event.SubmissionProposal.SubmissionRequest -> {
                        // A move has been played
                        when (val verification = MoveVerifier.verifyMove(event.submittedMove, board, status.gameStatus)) {
                            is VerificationResult.Passed -> {
                                status.onMoveAccepted(verification.move, board)
                                board.executeMoveForward(verification.move)

                                postSubmissionGameEngineLogic()

                                _outputFlow.emit(
                                    Event.SubmissionProposal.SubmissionAccepted(
                                        processedMove = verification.move,
                                        message = "Verified by CheckPigeon Engine"
                                    )
                                )
                            }
                            is VerificationResult.Failed -> {
                                status.onMoveRejected(verification.rejectionReason)

                                _outputFlow.emit(
                                    Event.SubmissionProposal.SubmissionRejected(
                                        rejectionReason = verification.rejectionReason,
                                        message = "Rejected by CheckPigeon Engine"
                                    )
                                )
                            }
                        }
                    }
                    else -> {
                        engineLogger.w("Received a SubmissionProposal event which should not be handled by the Engine.")
                    }
                }
            }
            is Event.Resignation -> {
                status.onGameEnded(
                    GameResult.Done(
                        winner = players.findPlayerWithColor(event.color),
                        loser = players.findPlayerWithColor(!event.color),
                    )
                )
            }
            is Event.DrawProposal -> {
                val acceptsDraw = true

                if (acceptsDraw) {
                    status.onGameEnded(
                        GameResult.Draw
                    )
                }
            }
        }
    }

    fun stdoutBoard(
        colorPerspective: TileColor = status.gameStatus.turnColor,
        highlights: Highlights = emptyList()
    ) {
        BoardPrinter.stdout(board, colorPerspective, highlights)
    }
}