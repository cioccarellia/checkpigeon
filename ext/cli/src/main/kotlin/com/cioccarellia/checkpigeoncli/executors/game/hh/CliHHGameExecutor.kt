package com.cioccarellia.checkpigeoncli.executors.game.hh

import com.cioccarellia.checkpigeon.annotations.ToEngine
import com.cioccarellia.checkpigeon.logic.console.lightYellow
import com.cioccarellia.checkpigeon.logic.console.red
import com.cioccarellia.checkpigeon.logic.console.yellow
import com.cioccarellia.checkpigeon.logic.engine.Engine
import com.cioccarellia.checkpigeon.logic.engine.events.Event
import com.cioccarellia.checkpigeon.logic.model.board.Coordinate
import com.cioccarellia.checkpigeon.logic.model.board.File
import com.cioccarellia.checkpigeon.logic.model.board.FileLetter
import com.cioccarellia.checkpigeon.logic.model.board.Rank
import com.cioccarellia.checkpigeon.logic.model.move.MoveType
import com.cioccarellia.checkpigeon.logic.model.move.linear.Move
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor
import com.cioccarellia.checkpigeoncli.commands.Command.CreateGame.GameHumanVsHuman
import com.cioccarellia.checkpigeoncli.executors.CommandExecutor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CliHHGameExecutor(
    private val command: GameHumanVsHuman
) : CommandExecutor<GameHumanVsHuman>(command) {

    private val executorFlow = MutableSharedFlow<@ToEngine Event>()

    private val engine = Engine(
        command.whitePlayer to command.blackPlayer,
        executorFlow
    )

    suspend fun inputMove(): Move {
        delay(6_000)

        return Move(
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
    }

    override suspend fun execute() {
        executorFlow.emit(Event.StartGame)
        CoroutineScope(Dispatchers.Default).launch {
            initEngineCollector()
        }

        executorFlow.emit(
            Event.Message("Sup")
        )

        engine.stdoutBoard()

        while (true) {
            println("White plays")

            executorFlow.emit(
                Event.SubmissionProposal.SubmissionRequest(
                    inputMove()
                )
            )

            engine.stdoutBoard()
        }
    }

    private suspend fun initEngineCollector() {
        println("Initializing Main -> Engine collector".lightYellow())

        engine.engineOutputFlow.collect {
            println("Received event from Engine".yellow())

            when (it) {
                is Event.Message -> {
                    println("Received Message \"${it.content}\"".red())
                }
                is Event.SubmissionProposal -> when (it) {
                    is Event.SubmissionProposal.SubmissionAccepted -> {

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
}