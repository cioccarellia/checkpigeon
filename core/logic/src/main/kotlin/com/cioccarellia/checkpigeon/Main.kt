package com.cioccarellia.checkpigeon

import com.cioccarellia.checkpigeon.annotations.ToEngine
import com.cioccarellia.checkpigeon.logic.console.red
import com.cioccarellia.checkpigeon.logic.console.yellow
import com.cioccarellia.checkpigeon.logic.console.yellowBackground
import com.cioccarellia.checkpigeon.logic.engine.Engine
import com.cioccarellia.checkpigeon.logic.engine.events.Event
import com.cioccarellia.checkpigeon.logic.model.board.Coordinate
import com.cioccarellia.checkpigeon.logic.model.board.File
import com.cioccarellia.checkpigeon.logic.model.board.FileLetter
import com.cioccarellia.checkpigeon.logic.model.board.Rank
import com.cioccarellia.checkpigeon.logic.model.move.MoveType
import com.cioccarellia.checkpigeon.logic.model.move.linear.Move
import com.cioccarellia.checkpigeon.logic.model.player.Player
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect

val mainFlow = MutableSharedFlow<@ToEngine Event>()

val engine = Engine(
    Player("Apium", TileColor.WHITE) to Player("Titi", TileColor.BLACK),
    mainFlow
)

fun main(args: Array<String>): Unit = runBlocking {
    mainFlow.emit(Event.StartGame)

    /**
     * Async Dispatcher reading flow, never terminating
     * */
    CoroutineScope(Dispatchers.Default).launch {
        println("Initializing Main -> Engine collector".yellowBackground())
        engine.engineOutputFlow.collect {
            println("Received event from Engine".yellow())

            when (it) {
                is Event.Message -> {
                    println("Received Message \"${it.content}\"".red())
                }
                is Event.SubmissionProposal -> when (it) {
                    is Event.SubmissionProposal.SubmissionAccepted -> {
                        println("Move accepted")
                    }
                    is Event.SubmissionProposal.SubmissionRejected -> {

                    }
                    else -> {

                    }
                }
                else -> {

                }
            }
        }
    }

    mainFlow.emit(
        Event.Message("Sup")
    )

    engine.stdoutBoard()

    while (true) {
        println("White plays")

        mainFlow.emit(
            Event.SubmissionProposal.SubmissionRequest(
                Move(
                    moveType = MoveType.Movement,
                    playingColor = TileColor.WHITE,
                    start = Coordinate(
                        File(FileLetter.A), Rank(3)
                    ),
                    end = Coordinate(
                        File(FileLetter.B), Rank(4)
                    ),
                    captures = listOf(),
                    blows = null
                )
            )
        )

        delay(200)
        engine.stdoutBoard(TileColor.BLACK, highlights = listOf(File(FileLetter.B) to Rank(4)))

        break
    }
}
