package com.cioccarellia.checkpigeoncli.executors.game.hh

import com.cioccarellia.checkpigeon.annotations.ToEngine
import com.cioccarellia.checkpigeon.logic.console.lightYellow
import com.cioccarellia.checkpigeon.logic.console.red
import com.cioccarellia.checkpigeon.logic.console.yellow
import com.cioccarellia.checkpigeon.logic.engine.Engine
import com.cioccarellia.checkpigeon.logic.engine.events.Event
import com.cioccarellia.checkpigeoncli.commands.Command.CreateGame.GameHumanVsHuman
import com.cioccarellia.checkpigeoncli.commands.readCLI
import com.cioccarellia.checkpigeoncli.executors.CommandExecutor
import com.cioccarellia.checkpigeoncli.executors.game.input.MoveParser
import com.cioccarellia.checkpigeoncli.executors.game.input.ParsedMove
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

    private suspend fun processInputMove(): ParsedMove = with(engine.status.gameStatus) {
        val input = readCLI("Move $turnNumber for $turnColor")
        return@with MoveParser.convert(input, turnColor)
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
            delay(250)

            when (val parsedMove = processInputMove()) {
                is ParsedMove.Success -> {
                    executorFlow.emit(
                        Event.SubmissionProposal.SubmissionRequest(
                            move = parsedMove.move
                        )
                    )

                    engine.stdoutBoard()
                }
                is ParsedMove.Failure -> {
                    println("Error: ${parsedMove.message}")
                }
            }
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