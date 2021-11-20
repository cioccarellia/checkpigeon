package com.cioccarellia.checkpigeon.logic.engine

import com.cioccarellia.checkpigeon.logic.board.Board
import com.cioccarellia.checkpigeon.logic.console.bracket
import com.cioccarellia.checkpigeon.logic.console.green
import com.cioccarellia.checkpigeon.logic.console.yellow
import com.cioccarellia.checkpigeon.logic.console.yellowBackground
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
        println("Received event from Main".yellow())
        when (event) {
            is Event.Message -> {
                val reply = "Roger that, got ${event.content}"

                _outputFlow.emit(
                    Event.Message(reply)
                )
            }
            is Event.StartGame -> {
                println(
                    bracket("Engine".green()) + "Starting up game"
                )
                Status.gameStatus.onGameStarted()
                Status.gameHistory.onGameStarted()
            }
            is Event.EndGame -> {
                println(
                    bracket("Engine".green()) + "Ending game"
                )
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