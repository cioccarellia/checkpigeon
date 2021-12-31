package com.cioccarellia.checkpigeoncli.executors.game.hh

import com.cioccarellia.checkpigeon.annotations.ToEngine
import com.cioccarellia.checkpigeon.ext.findPlayerWithColor
import com.cioccarellia.checkpigeon.logic.console.cyan
import com.cioccarellia.checkpigeon.logic.console.green
import com.cioccarellia.checkpigeon.logic.console.lightYellow
import com.cioccarellia.checkpigeon.logic.console.red
import com.cioccarellia.checkpigeon.logic.engine.Engine
import com.cioccarellia.checkpigeon.logic.engine.events.Event
import com.cioccarellia.checkpigeoncli.commands.Command.CreateGame.GameHumanVsHuman
import com.cioccarellia.checkpigeoncli.commands.readCLI
import com.cioccarellia.checkpigeoncli.executors.CommandExecutor
import com.cioccarellia.checkpigeoncli.executors.game.input.CLICommand
import com.cioccarellia.checkpigeoncli.executors.game.input.CommandParser
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

    private suspend fun processInputMove(): CLICommand = with(engine.status.gameStatus) {
        val currentPlayer = engine.players.findPlayerWithColor(turnColor)
        val input = readCLI("Turn $turnNumber for $turnColor (${currentPlayer.alias}) | command")

        return@with CommandParser.parseCommand(input, turnColor)
    }

    override suspend fun execute() {
        CoroutineScope(Dispatchers.Default).launch {
            initEngineCollector()
        }

        executorFlow.emit(Event.StartGame)
        engine.stdoutBoard()

        while (engine.status.gameStatus.isAlive) {
            delay(200)

            when (val parsedMove = processInputMove()) {
                is CLICommand.Move -> when (parsedMove.parsedMove) {
                    is ParsedMove.Success -> {
                        executorFlow.emit(
                            Event.SubmissionProposal.SubmissionRequest(
                                submittedMove = parsedMove.parsedMove.move
                            )
                        )

                        delay(50)
                        engine.stdoutBoard()
                    }
                    is ParsedMove.Failure -> {
                        println("Error: ${parsedMove.parsedMove.message}")
                    }
                }
                CLICommand.Resignation -> {
                    executorFlow.emit(
                        Event.Resignation(color = engine.status.gameStatus.turnColor)
                    )
                }
                CLICommand.DrawOffer -> {
                    executorFlow.emit(
                        Event.DrawProposal.DrawRequest(color = engine.status.gameStatus.turnColor)
                    )
                }
                CLICommand.UnknownCommand -> {
                    println("Unknown command".red())
                }
            }
        }

        println("Engine died. Bye bye".cyan())
    }

    private suspend fun initEngineCollector() {
        println("Initializing Main -> Engine collector".lightYellow())

        engine.engineOutputFlow.collect {
            when (it) {
                is Event.Message -> {
                    println("Received Message \"${it.content}\"".red())
                }
                is Event.SubmissionProposal -> when (it) {
                    is Event.SubmissionProposal.SubmissionAccepted -> {
                        println("Engine accepted move".green())
                    }
                    is Event.SubmissionProposal.SubmissionRejected -> {
                        println("Move rejected: ${it.rejectionReason}.".red())
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