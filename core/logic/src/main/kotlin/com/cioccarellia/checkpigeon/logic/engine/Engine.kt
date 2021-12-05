package com.cioccarellia.checkpigeon.logic.engine

import com.cioccarellia.checkpigeon.annotations.FromEngine
import com.cioccarellia.checkpigeon.annotations.ToEngine
import com.cioccarellia.checkpigeon.debug.CustomLogger
import com.cioccarellia.checkpigeon.debug.d
import com.cioccarellia.checkpigeon.debug.w
import com.cioccarellia.checkpigeon.logic.board.Board
import com.cioccarellia.checkpigeon.logic.engine.events.Event
import com.cioccarellia.checkpigeon.logic.engine.internal.BoardPrinter
import com.cioccarellia.checkpigeon.logic.engine.status.EngineStatus
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
                val reply = "Roger that, got ${event.content}."

                _outputFlow.emit(
                    Event.Message(reply)
                )
            }
            is Event.StartGame -> {
                engineLogger.d("Event is StartGame")
                status.onGameStarted()
            }
            is Event.EndGame -> {
                engineLogger.d("Event is EndGame")
                status.onGameEnded()
            }
            is Event.SubmissionProposal -> {
                when (event) {
                    is Event.SubmissionProposal.SubmissionRequest -> {
                        status.onMoveAccepted(event.move, board)
                        board.execute(event.move)

                        _outputFlow.emit(Event.SubmissionProposal.SubmissionAccepted())
                    }
                    else -> {
                        engineLogger.w("Received a SubmissionProposal event which should not be handled by the Engine.")
                    }
                }
            }
            is Event.Resignation -> {
                status.onGameEnded()
            }
            is Event.DrawProposal -> TODO()
        }
    }

    fun stdoutBoard(
        colorPerspective: TileColor = status.gameStatus.turnColor,
        highlights: Highlights = emptyList()
    ) {
        BoardPrinter.stdout(board, colorPerspective, highlights)
    }
}