package com.cioccarellia.checkpigeon

import com.cioccarellia.checkpigeon.logic.console.red
import com.cioccarellia.checkpigeon.logic.console.yellow
import com.cioccarellia.checkpigeon.logic.engine.Engine
import com.cioccarellia.checkpigeon.logic.engine.events.GameEvent
import com.cioccarellia.checkpigeon.logic.model.board.Coordinate
import com.cioccarellia.checkpigeon.logic.model.board.File
import com.cioccarellia.checkpigeon.logic.model.board.FileLetter
import com.cioccarellia.checkpigeon.logic.model.board.Rank
import com.cioccarellia.checkpigeon.logic.model.move.MoveType
import com.cioccarellia.checkpigeon.logic.model.move.linear.Move
import com.cioccarellia.checkpigeon.logic.model.player.Player
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor
import kotlinx.coroutines.*

val engine = Engine(Player("Apium", TileColor.WHITE) to Player("Titi", TileColor.BLACK))


fun onResponse(responeSEvent: GameEvent) {
    println("Received event from Engine".yellow())

    when (responeSEvent) {
        is GameEvent.Message -> {
            println("Received Message \"${responeSEvent.content}\"".red())
        }

        is GameEvent.SubmissionProposal -> when (responeSEvent) {
            is GameEvent.SubmissionProposal.SubmissionAccepted -> {
                println("Move accepted")
            }

            is GameEvent.SubmissionProposal.SubmissionRejected -> {
                println("Move rejected")
            }

            else -> {

            }
        }

        else -> {

        }
    }
}


fun main(args: Array<String>): Unit = runBlocking {

    engine.emit(
        GameEvent.Message("Sup")
    )

    engine.stdoutBoard()

    while (true) {
        println("White plays")

        engine.emit(
            GameEvent.SubmissionProposal.SubmissionRequest(
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
        engine.stdoutBoard(
            TileColor.BLACK, highlights = listOf(
                File(FileLetter.B) to Rank(4)
            )
        )

        break
    }
}
