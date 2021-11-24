package com.cioccarellia.checkpigeon.logic.engine

import com.cioccarellia.checkpigeon.debug.CustomLogger
import com.cioccarellia.checkpigeon.debug.d
import com.cioccarellia.checkpigeon.logic.board.Board
import com.cioccarellia.checkpigeon.logic.engine.events.Event
import com.cioccarellia.checkpigeon.logic.engine.status.FullGameHistory
import com.cioccarellia.checkpigeon.logic.engine.status.GameStatus
import com.cioccarellia.checkpigeon.logic.model.move.linear.Move
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
    private val inputFlow: SharedFlow<Event>
) {
    /**
     * Output [Event]-emitting [MutableSharedFlow]
     * */
    private val _outputFlow = MutableSharedFlow<Event>()
    val engineOutputFlow: SharedFlow<Event> = _outputFlow.asSharedFlow()

    /**
     * Game board
     * */
    private val board = Board()

    /**
     * Logger
     * */
    private val engineLogger = CustomLogger(tag = "Engine")

    init {
        check(players.first.color == TileColor.WHITE && players.second.color == TileColor.BLACK)

        CoroutineScope(Dispatchers.IO).launch {
            hookInputFlow()
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
                val reply = "Roger that, got ${event.content}"

                _outputFlow.emit(
                    Event.Message(reply)
                )
            }
            is Event.StartGame -> {
                engineLogger.d("Event is StartGame")
                Status.gameStatus.onGameStarted()
                Status.gameHistory.onGameStarted()
            }
            is Event.EndGame -> {
                engineLogger.d("Event is EndGame")
                Status.gameStatus.onGameEnded()
                Status.gameHistory.onGameEnded()
            }
            is Event.SubmissionProposal -> {
                when (event) {
                    is Event.SubmissionProposal.SubmissionRequest -> {
                        board.execute(event.move)
                    }
                    is Event.SubmissionProposal.SubmissionAccepted -> {

                    }
                    is Event.SubmissionProposal.SubmissionRejected -> {

                    }
                }
            }
            is Event.Resignation -> {
                Status.gameStatus.onGameEnded()
                Status.gameHistory.onGameEnded()
            }
            is Event.DrawProposal -> TODO()
        }
    }

    /**
     * Object which collects and preserves information
     * and status data about the game.
     * */
    internal object Status {
        val gameStatus = GameStatus()
        val gameHistory = FullGameHistory()
    }

    /**
     * Called whenever a [Move] gets verified and dispatched
     * for execution to the [Board].
     * */
    internal fun onMoveAccepted(move: Move) {
        Status.gameStatus.onMoveAccepted(move)
        Status.gameHistory.onVerboseMoveAccepted(move.verbose(board))
    }
}